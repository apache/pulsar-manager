<!--

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

-->
<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input :placeholder="$t('cluster.searchClusters')" v-model="listQuery.cluster" style="width: 200px;" class="filter-item" @keyup.enter.native="handleFilter"/>
      <el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter"/>
      <!-- <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-plus" @click="handleCreate">{{ $t('cluster.addCluster') }}</el-button> -->
    </div>

    <el-row :gutter="24">
      <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}" style="padding-right:8px;margin-bottom:30px;">
        <el-table
          v-loading="listLoading"
          :key="tableKey"
          :data="list"
          border
          fit
          highlight-current-row
          style="width: 100%;">
          <el-table-column :label="$t('cluster.name')" min-width="150px" align="center">
            <template slot-scope="scope">
              <router-link :to="'/management/clusters/' + scope.row.cluster + '/cluster?tab=config'" class="link-type">
                <span>{{ scope.row.cluster }}</span>
              </router-link>
            </template>
          </el-table-column>
          <el-table-column :label="$t('broker.brokerNumber')" min-width="150px" align="center">
            <template slot-scope="scope">
              <router-link :to="'/management/clusters/' + scope.row.cluster + '/cluster?tab=brokers'" class="link-type">
                <span>{{ scope.row.brokers }}</span>
              </router-link>
            </template>
          </el-table-column>
          <el-table-column :label="$t('cluster.serviceUrl')" min-width="150px" align="center">
            <template slot-scope="scope">
              <span v-if="scope.row.brokerServiceUrl !== ''">
                <i class="el-icon-sort" style="margin-right: 2px"/>
                <router-link :to="'/management/clusters/' + scope.row.cluster + '/cluster?tab=brokers'" class="link-type">
                  {{ scope.row.brokerServiceUrl }}
                </router-link>
              </span>
              <br>
              <span v-if="scope.row.serviceUrl !== ''">
                <i class="el-icon-setting" style="margin-right: 2px"/>
                <router-link :to="'/management/clusters/' + scope.row.cluster + '/cluster?tab=config'" class="link-type">
                  {{ scope.row.serviceUrl }}
                </router-link>
              </span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('table.actions')" align="center" width="240" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <router-link :to="'/management/clusters/' + scope.row.cluster + '/cluster?tab=config'">
                <el-button type="primary" class="el-icon-edit-outline" size="small">{{ $t('table.edit') }}</el-button>
              </router-link>
              <!-- <el-button size="small" class="el-icon-delete" type="danger" @click="handleDelete(scope.row)">{{ $t('table.delete') }}</el-button> -->
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form
        ref="temp"
        :rules="rules"
        :model="temp"
        label-position="top"
        style="margin-left:30%; margin-right:10%">
        <div v-if="dialogStatus==='create'">
          <el-form-item :label="$t('cluster.name')" prop="cluster">
            <el-input v-model="temp.cluster"/>
          </el-form-item>
          <el-form-item :label="$t('cluster.webServiceUrlPrefix')" prop="serviceUrl">
            <el-input v-model="temp.serviceUrl" placeholder="http://"/>
          </el-form-item>
          <el-form-item :label="$t('cluster.webServiceUrlTlsPrefix')" prop="serviceUrlTls">
            <el-input v-model="temp.serviceUrlTls" placeholder="https://"/>
          </el-form-item>
          <el-form-item :label="$t('cluster.brokerServiceUrlPrefix')" prop="brokerServiceUrl">
            <el-input v-model="temp.brokerServiceUrl" placeholder="pulsar://"/>
          </el-form-item>
          <el-form-item :label="$t('cluster.brokerServiceUrlTlsPrefix')" prop="brokerServiceUrlTls">
            <el-input v-model="temp.brokerServiceUrlTls" placeholder="pulsar+ssl://"/>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleOptions()">{{ $t('table.confirm') }}</el-button>
            <el-button @click="dialogFormVisible = false">{{ $t('table.cancel') }}</el-button>
          </el-form-item>
        </div>
        <div v-if="dialogStatus==='delete'">
          <el-form-item>
            <h4>{{ deleteClusterMessage }}</h4>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="deleteCluster()">{{ $t('table.confirm') }}</el-button>
            <el-button @click="dialogFormVisible = false">{{ $t('table.cancel') }}</el-button>
          </el-form-item>
        </div>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import {
  fetchClusters,
  putCluster,
  fetchClusterConfig,
  deleteCluster
} from '@/api/clusters'
import waves from '@/directive/waves' // Waves directive
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import jsonEditor from '@/components/JsonEditor'
import { validateEmpty, validateServiceUrl } from '@/utils/validate'
import MdInput from '@/components/MDinput'

export default {
  name: 'Clusters',
  components: {
    Pagination,
    jsonEditor,
    MdInput
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
        delete: this.$i18n.t('cluster.deleteCluster'),
        create: this.$i18n.t('cluster.addCluster')
      },
      dialogPvVisible: false,
      rules: {
        cluster: [
          { required: true, message: this.$i18n.t('cluster.clusterNameIsRequired'), trigger: 'change' }
        ],
        serviceUrl: [
          { required: true, message: this.$i18n.t('cluster.serviceUrlIsRequired'), trigger: 'change' },
          { validator: validateServiceUrl('http:', false), trigger: 'blur' }
        ],
        serviceUrlTls: [
          { validator: validateServiceUrl('https:', true), trigger: 'blur' }
        ],
        brokerServiceUrl: [
          { required: true, message: this.$i18n.t('cluster.serviceUrlIsRequired'), trigger: 'change' },
          { validator: validateServiceUrl('pulsar:', false), trigger: 'blur' }
        ],
        brokerServiceUrlTls: [
          { validator: validateServiceUrl('pulsar+ssl:', true), trigger: 'blur' }
        ],
        domainName: [{ required: true, message: 'domainName is required', trigger: 'change' }],
        domainNames: [{ required: true, message: 'domainNames is required', trigger: 'change' }]
      },
      deleteClusterMessage: this.$i18n.t('cluster.deleteClusterMessage')
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
          for (var i = 0; i < response.data.data.length; i++) {
            this.localList.push({
              'cluster': response.data.data[i]['cluster'],
              'brokers': response.data.data[i]['brokers'],
              'serviceUrl': response.data.data[i]['serviceUrl'],
              'brokerServiceUrl': response.data.data[i]['brokerServiceUrl']
            })
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
          'serviceUrlTls': response.data.serviceUrlTls,
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
        serviceUrlTls: '',
        brokerServiceUrl: '',
        brokerServiceUrlTls: ''
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
        this.dialogFormVisible = false
        this.localList = []
        this.getClusters()
        this.$notify({
          title: 'success',
          message: this.$i18n.t('cluster.addClusterSuccessNotification'),
          type: 'success',
          duration: 2000
        })
      })
    },
    handleOptions() {
      this.$refs['temp'].validate((valid) => {
        if (valid) {
          switch (this.dialogStatus) {
            case 'create':
              this.createData()
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
        case 'domain-name-create':
          this.handleDomainNameCreate()
          break
      }
    },
    handleDelete(row) {
      this.dialogStatus = 'delete'
      this.dialogFormVisible = true
      this.temp.cluster = row.cluster
    },
    deleteCluster() {
      deleteCluster(this.temp.cluster).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('cluster.deleteClusterSuccessNotification'),
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
        this.localList = []
        this.getClusters()
      })
    }
  }
}
</script>

<style>
.el-dialog {
  width: 35%;
}
.el-form {
  margin-left: 0% !important;
  margin-right: 0% !important;
}
</style>
