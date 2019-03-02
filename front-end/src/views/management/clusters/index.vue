<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input :placeholder="$t('table.name')" v-model="listQuery.name" style="width: 200px;" class="filter-item" @keyup.enter.native="handleFilter"/>
      <el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">{{ $t('table.search') }}</el-button>
      <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleCreate">{{ $t('table.add') }}</el-button>
      <el-button v-waves :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">{{ $t('table.export') }}</el-button>
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
          style="width: 100%;">
          <el-table-column :label="$t('table.name')" min-width="150px" align="center">
            <template slot-scope="scope">
              <span>{{ scope.row.name }}</span>
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
      <el-form ref="dataForm" :rules="rules" :model="temp" label-position="left" label-width="130px" style="width: 400px; margin-left:50px;">
        <el-form-item v-if="dialogStatus==='create'" :label="$t('table.name')" prop="name">
          <el-input v-model="temp.name"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" :label="$t('table.name')">
          <span>{{ temp.name }}</span>
        </el-form-item>
        <el-form-item :label="$t('table.serviceUrl')" prop="serviceUrl">
          <el-input v-model="temp.serviceUrl"/>
        </el-form-item>
        <el-form-item :label="$t('table.brokerServiceUrl')" prop="brokerServiceUrl">
          <el-input v-model="temp.brokerServiceUrl"/>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">{{ $t('table.cancel') }}</el-button>
        <el-button type="primary" @click="dialogStatus==='create'?createData():updateData()">{{ $t('table.confirm') }}</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { fetchClusters, putCluster, updateCluster, fetchClusterConfig } from '@/api/clusters'
import waves from '@/directive/waves' // Waves directive
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import jsonEditor from '@/components/JsonEditor'
import { validateEmpty } from '@/utils/validate'

export default {
  name: 'Clusters',
  components: {
    Pagination,
    jsonEditor
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
      searchList: [],
      total: 0,
      listLoading: true,
      jsonValue: {},
      listQuery: {
        name: '',
        page: 1,
        limit: 10
      },
      temp: {
        id: undefined,
        name: '',
        serviceUrl: '',
        brokerServiceUrl: ''
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: 'Edit',
        create: 'Create'
      },
      dialogPvVisible: false,
      rules: {
        name: [{ required: true, message: 'name is required', trigger: 'change' }],
        serviceUrl: [{ required: true, message: 'serviceUrl is required', trigger: 'change' }],
        brokerServiceUrl: [{ required: true, message: 'brokerServiceUrl is required', trigger: 'blur' }]
      },
      downloadLoading: false
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
          this.localList = response.items
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
      if (!validateEmpty(this.listQuery.name)) {
        this.searchList = []
        for (var i = 0; i < this.localList.length; i++) {
          if (this.localList[i]['name'].indexOf(this.listQuery.name) !== -1) {
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
      fetchClusterConfig(this.temp.tenant).then(response => {
        this.jsonValue = {
          'serviceUrl': response.serviceUrl,
          'brokerServiceUrl': response.brokerServiceUrl
        }
      })
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getClusters()
    },
    resetTemp() {
      this.temp = {
        name: '',
        serviceUrl: '',
        brokerServiceUrl: ''
      }
    },
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          putCluster(this.temp.name, this.temp).then(response => {
            this.list.unshift(this.temp)
            this.dialogFormVisible = false
            this.$notify({
              title: 'success',
              message: 'create success',
              type: 'success',
              duration: 2000
            })
          })
        }
      })
    },
    handleUpdate(row) {
      this.temp = Object.assign({}, row) // copy obj
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const tempData = Object.assign({}, this.temp)
          fetchClusterConfig(this.temp.tenant).then(response => {
            this.tempData.ServiceUrl = response.serviceUrl
            this.tempData.BrokerServiceUrl = response.brokerServiceUrl
          })
          updateCluster(this.temp.name, tempData).then(() => {
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
        }
      })
    },
    handleDelete(row) {
      this.$notify({
        title: 'success',
        message: 'delete success',
        type: 'success',
        duration: 2000
      })
      const index = this.list.indexOf(row)
      this.list.splice(index, 1)
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = ['id', 'name', 'serviceUrl', 'brokerServiceUrl']
        const filterVal = ['id', 'name', 'serviceUrl', 'brokerServiceUrl']
        const data = this.formatJson(filterVal, this.list)
        excel.export_json_to_excel({
          header: tHeader,
          data,
          filename: 'clusters-list'
        })
        this.downloadLoading = false
      })
    },
    formatJson(filterVal, jsonData) {
      return jsonData.map(v => filterVal.map(j => {
        return v[j]
      }))
    }
  }
}
</script>
