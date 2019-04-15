<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input :placeholder="$t('table.cluster')" v-model="listQuery.cluster" style="width: 200px;" class="filter-item" @keyup.enter.native="handleFilter"/>
      <el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">{{ $t('table.search') }}</el-button>
      <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleCreate">{{ $t('table.add') }}</el-button>
      <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleUpdateClusterPeer">updatePeer</el-button>
      <el-dropdown class="filter-item" style="margin-left: 10px;" @command="handleCommand">
        <el-button type="primary">
          domainName<i class="el-icon-arrow-down el-icon--right"/>
        </el-button>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="domain-name-list">list</el-dropdown-item>
          <el-dropdown-item command="domain-name-get">get</el-dropdown-item>
          <el-dropdown-item command="domain-name-delete">delete</el-dropdown-item>
          <el-dropdown-item command="domain-name-update">update</el-dropdown-item>
          <el-dropdown-item command="domain-name-create">create</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>

    <el-row :gutter="8">
      <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 12}" :xl="{span: 12}" style="padding-right:8px;margin-bottom:30px;">
        <el-table
          v-loading="listLoading"
          :key="tableKey"
          :data="list"
          border
          fit
          highlight-current-row
          style="width: 100%;"
          @row-click="getCurrentRow">
          <el-table-column :label="$t('table.cluster')" min-width="150px" align="center">
            <template slot-scope="scope">
              <span>{{ scope.row.cluster }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('table.config')" min-width="150px" align="center">
            <template slot-scope="scope">
              <span class="link-type" @click="handleGetConfig(scope.row)">config</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('table.actions')" align="center" width="230" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button type="primary" size="mini" @click="handleUpdate(scope.row)">{{ $t('table.edit') }}</el-button>
              <el-button type="danger" size="mini" @click="handleDelete(scope.row)">{{ $t('table.delete') }}</el-button>
            </template>
          </el-table-column>
        </el-table>

        <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getClusters" />
      </el-col>
      <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 12}" :xl="{span: 12}" style="margin-bottom:30px;">
        <jsonEditor :value="jsonValue"/>
      </el-col>
    </el-row>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form ref="temp" :rules="rules" :model="temp" label-position="left" label-width="130px" style="width: 400px; margin-left:50px;">
        <div v-if="dialogStatus==='create'||dialogStatus==='update'">
          <div v-if="dialogStatus==='create'">
            <el-form-item :label="$t('table.cluster')" prop="cluster">
              <el-input v-model="temp.cluster"/>
            </el-form-item>
          </div>
          <div v-if="dialogStatus==='update'">
            <el-form-item :label="$t('table.cluster')">
              <span>{{ temp.cluster }}</span>
            </el-form-item>
            <el-form-item :label="$t('table.serviceUrl')" prop="serviceUrl">
              <el-input v-model="temp.serviceUrl"/>
            </el-form-item>
            <el-form-item label="serviceUrlTls" prop="serviceUrlTls">
              <el-input v-model="temp.serviceUrlTls"/>
            </el-form-item>
            <el-form-item :label="$t('table.brokerServiceUrl')" prop="brokerServiceUrl">
              <el-input v-model="temp.brokerServiceUrl"/>
            </el-form-item>
            <el-form-item label="brokerUrlTls" prop="brokerServiceUrlTls">
              <el-input v-model="temp.brokerServiceUrlTls"/>
            </el-form-item>
          </div>
        </div>
        <div v-if="dialogStatus==='updatePeer'">
          <el-form-item label="peerClusters" prop="peerClusters">
            <el-drag-select v-model="temp.peerClusters" style="width:300px;" multiple placeholder="Please select">
              <el-option v-for="item in peerClustersListOptions" :label="item.label" :value="item.value" :key="item.value" />
            </el-drag-select>
          </el-form-item>
        </div>
        <div v-if="dialogStatus==='domain-name-get'||dialogStatus==='domain-name-delete'">
          <el-form-item label="domainNames" prop="domainNames">
            <el-select v-model="temp.domainNames" style="width:300px;" placeholder="Please select">
              <el-option v-for="(item,index) in domainNamesListOptions" :key="item+index" :label="item" :value="item"/>
            </el-select>
          </el-form-item>
        </div>
        <div v-if="dialogStatus==='domain-name-create'||dialogStatus==='domain-name-update'">
          <div v-if="dialogStatus==='domain-name-create'">
            <el-form-item label="domainName" prop="domainName">
              <el-input v-model="temp.domainName"/>
            </el-form-item>
          </div>
          <div v-if="dialogStatus==='domain-name-update'">
            <el-form-item label="domainNames" prop="domainNames">
              <el-select v-model="temp.domainNames" style="width:300px;" placeholder="Please select">
                <el-option v-for="(item,index) in domainNamesListOptions" :key="item+index" :label="item" :value="item"/>
              </el-select>
            </el-form-item>
          </div>
          <el-form-item label="brokerList" prop="brokerList">
            <el-input v-model="temp.brokerList"/>
          </el-form-item>
        </div>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">{{ $t('table.cancel') }}</el-button>
        <el-button type="primary" @click="handleOptions()">{{ $t('table.confirm') }}</el-button>
        <!-- <el-button type="primary" @click="dialogStatus==='create'?createData():updateData()">{{ $t('table.confirm') }}</el-button> -->
      </div>
    </el-dialog>

  </div>
</template>

<script>
import {
  fetchClusters,
  putCluster,
  deleteCluster,
  updateCluster,
  fetchClusterConfig,
  updateClusterPeer,
  listClusterDomainName,
  getClusterDomainName,
  createClusterDomainName,
  deleteClusterDomainName,
  updateClusterDomainName
  // getClusterPeer
} from '@/api/clusters'
import waves from '@/directive/waves' // Waves directive
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import jsonEditor from '@/components/JsonEditor'
import { validateEmpty } from '@/utils/validate'
import ElDragSelect from '@/components/DragSelect' // base on element-ui
import { trim } from '@/utils/index'

export default {
  name: 'Clusters',
  components: {
    Pagination,
    jsonEditor,
    ElDragSelect
  },
  directives: { waves },
  filters: {
    statusFilter(status) {
      const statusMap = {
        published: 'success',
        draft: 'info',
        deleted: 'danger'
      }
      return statusMap[status]
    }
  },
  data() {
    return {
      tableKey: 0,
      list: null,
      localList: [],
      peerClustersListOptions: [],
      domainNamesListOptions: [],
      searchList: [],
      total: 0,
      listLoading: true,
      jsonValue: {},
      listQuery: {
        cluster: '',
        page: 1,
        limit: 10
      },
      currentCluster: '',
      currentCommand: '',
      temp: {
        cluster: '',
        serviceUrl: '',
        serviceUrlTls: '',
        brokerServiceUrl: '',
        brokerServiceUrlTls: '',
        peerClusters: [],
        domainNames: '',
        brokerList: '',
        domainName: ''
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: 'Edit',
        create: 'Create'
      },
      dialogPvVisible: false,
      rules: {
        cluster: [{ required: true, message: 'cluster name is required', trigger: 'change' }],
        serviceUrl: [{ required: true, message: 'serviceUrl is required', trigger: 'change' }],
        domainName: [{ required: true, message: 'domainName is required', trigger: 'change' }],
        domainNames: [{ required: true, message: 'domainNames is required', trigger: 'change' }]
      }
    }
  },
  created() {
    this.getClusters()
  },
  methods: {
    getClusters() {
      if (this.localList.length > 0) {
        setTimeout(() => {
          this.localPaging()
        }, 0.5 * 1000)
      } else {
        this.listLoading = true
        fetchClusters(this.listQuery).then(response => {
          for (var i = 0; i < response.data.length; i++) {
            this.localList.push({ 'cluster': response.data[i] })
          }
          this.total = this.localList.length
          this.list = this.localList.slice((this.listQuery.page - 1) * this.listQuery.limit, this.listQuery.limit * this.listQuery.page)
          // Just to simulate the time of the request
          setTimeout(() => {
            this.listLoading = false
          }, 1.5 * 1000)
        })
      }
    },
    localPaging() {
      this.listLoading = true
      if (!validateEmpty(this.listQuery.cluster)) {
        this.searchList = []
        for (var i = 0; i < this.localList.length; i++) {
          if (this.localList[i]['cluster'].indexOf(this.listQuery.cluster) !== -1) {
            this.searchList.push(this.localList[i])
          }
        }
        this.total = this.searchList.length
        this.list = this.searchList.slice((this.listQuery.page - 1) * this.listQuery.limit, this.listQuery.limit * this.listQuery.page)
      } else {
        this.total = this.localList.length
        this.list = this.localList.slice((this.listQuery.page - 1) * this.listQuery.limit, this.listQuery.limit * this.listQuery.page)
      }
      this.listLoading = false
    },
    handleGetConfig(row) {
      this.temp = Object.assign({}, row) // copy obj
      fetchClusterConfig(this.temp.cluster).then(response => {
        this.jsonValue = {
          'serviceUrl': response.data.serviceUrl,
          'serviceUrlTls': response.data.serverUrlTls,
          'brokerServiceUrl': response.data.brokerServiceUrl,
          'brokerServiceUrlTsl': response.data.brokerServiceUrlTls
        }
      })
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getClusters()
    },
    resetTemp() {
      this.temp = {
        cluster: '',
        serviceUrl: '',
        brokerServiceUrl: ''
      }
    },
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['temp'].clearValidate()
      })
    },
    createData() {
      putCluster(this.temp.cluster, this.temp).then(response => {
        this.list.unshift(this.temp)
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'create success',
          type: 'success',
          duration: 2000
        })
      })
    },
    handleUpdate(row) {
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['temp'].clearValidate()
      })
      this.temp.cluster = row.cluster
      fetchClusterConfig(row.cluster).then(response => {
        this.temp.serviceUrl = response.data.serviceUrl
        this.temp.serviceUrlTls = response.data.serviceUrlTls
        this.temp.brokerServiceUrl = response.data.brokerServiceUrl
        this.temp.brokerServiceUrlTls = response.data.brokerServiceUrlTls
      })
    },
    updateData() {
      updateCluster(this.temp.cluster, this.temp).then(() => {
        for (const v of this.list) {
          if (v.id === this.temp.id) {
            const index = this.list.indexOf(v)
            this.list.splice(index, 1, this.temp)
            break
          }
        }
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'update success',
          type: 'success',
          duration: 2000
        })
      })
    },
    handleDelete(row) {
      deleteCluster(row.cluster).then(response => {
        this.$notify({
          title: 'success',
          message: 'delete success',
          type: 'success',
          duration: 2000
        })
        const index = this.list.indexOf(row)
        this.list.splice(index, 1)
      })
    },
    formatJson(filterVal, jsonData) {
      return jsonData.map(v => filterVal.map(j => {
        return v[j]
      }))
    },
    getCurrentRow(item) {
      this.currentCluster = item.cluster
    },
    handleUpdateClusterPeer() {
      if (this.currentCluster.length <= 0) {
        this.$notify({
          title: 'error',
          message: 'Please select any cluster in table',
          type: 'error',
          duration: 2000
        })
        return
      }
      this.temp.peerClusters = []
      this.peerClustersListOptions = []
      fetchClusters(this.listQuery).then(response => {
        for (var i = 0; i < response.data.length; i++) {
          if (response.data[i] !== this.currentCluster) {
            this.peerClustersListOptions.push({ 'label': response.data[i], 'value': response.data[i] })
          }
        }
      })
      this.dialogStatus = 'updatePeer'
      this.dialogFormVisible = true
    },
    confirmUpdateClusterPeer() {
      updateClusterPeer(this.currentCluster, this.temp.peerClusters).then(response => {
        this.$notify({
          title: 'success',
          message: 'Update peer clusters success',
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
      })
    },
    handleOptions() {
      this.$refs['temp'].validate((valid) => {
        if (valid) {
          switch (this.dialogStatus) {
            case 'create':
              this.createData()
              break
            case 'update':
              this.updateData()
              break
            case 'updatePeer':
              this.confirmUpdateClusterPeer()
              break
            case 'domain-name-get':
              this.confirmGetDomainName()
              break
            case 'domain-name-create':
              this.confirmDomainNameCreate()
              break
            case 'domain-name-delete':
              this.confirmDomainNameDelete()
              break
            case 'domain-name-update':
              this.confirmDomainNameUpdate()
              break
          }
        }
      })
    },
    handleCommand(command) {
      if (this.currentCluster.length <= 0) {
        this.$notify({
          title: 'error',
          message: 'Please select any cluster in table',
          type: 'error',
          duration: 2000
        })
        return
      }
      this.currentCommand = command
      switch (this.currentCommand) {
        case 'domain-name-list':
          this.confirmListDomainName()
          break
        case 'domain-name-get':
          this.handleDomainNameGet()
          break
        case 'domain-name-create':
          this.handleDomainNameCreate()
          break
        case 'domain-name-update':
          this.handleDomainNameUpdate()
          break
        case 'domain-name-delete':
          this.handleDomainNameDelete()
          break
      }
    },
    handleDomainNameGet() {
      this.dialogStatus = 'domain-name-get'
      this.dialogFormVisible = true
      this.domainNamesListOptions = []
      this.temp.domainNames = ''
      listClusterDomainName(this.currentCluster).then(response => {
        for (var key in response.data) {
          this.domainNamesListOptions.push(key)
        }
      })
    },
    handleDomainNameCreate() {
      this.dialogStatus = 'domain-name-create'
      this.dialogFormVisible = true
    },
    handleDomainNameUpdate() {
      this.dialogStatus = 'domain-name-update'
      this.dialogFormVisible = true
      this.domainNamesListOptions = []
      this.temp.domainNames = ''
      listClusterDomainName(this.currentCluster).then(response => {
        for (var key in response.data) {
          this.domainNamesListOptions.push(key)
        }
      })
    },
    handleDomainNameDelete() {
      this.dialogStatus = 'domain-name-delete'
      this.dialogFormVisible = true
      this.domainNamesListOptions = []
      this.temp.domainNames = ''
      listClusterDomainName(this.currentCluster).then(response => {
        for (var key in response.data) {
          this.domainNamesListOptions.push(key)
        }
      })
    },
    confirmListDomainName() {
      listClusterDomainName(this.currentCluster).then(response => {
        this.jsonValue = response.data
      })
    },
    confirmGetDomainName() {
      getClusterDomainName(this.currentCluster, this.temp.domainNames).then(response => {
        this.jsonValue = response.data
        this.dialogFormVisible = false
      })
    },
    confirmDomainNameCreate() {
      const data = []
      const brokerList = this.temp.brokerList.split(',')
      for (var i = 0; i < brokerList.length; i++) {
        data.push(trim(brokerList[i]))
      }
      createClusterDomainName(this.currentCluster, this.temp.domainName, { brokers: data }).then(response => {
        this.$notify({
          title: 'success',
          message: 'create success',
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
      })
    },
    confirmDomainNameDelete() {
      deleteClusterDomainName(this.currentCluster, this.temp.domainNames).then(response => {
        this.$notify({
          title: 'success',
          message: 'delete success',
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
      })
    },
    confirmDomainNameUpdate() {
      const data = []
      const brokerList = this.temp.brokerList.split(',')
      for (var i = 0; i < brokerList.length; i++) {
        data.push(trim(brokerList[i]))
      }
      updateClusterDomainName(this.currentCluster, this.temp.domainNames, { brokers: data }).then(response => {
        this.$notify({
          title: 'success',
          message: 'update success',
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
      })
    }
  }
}
</script>
