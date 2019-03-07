<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input :placeholder="$t('table.tenant')" v-model="listQuery.tenant" style="width: 200px;" class="filter-item" @keyup.enter.native="handleFilter"/>
      <el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">{{ $t('table.search') }}</el-button>
      <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleCreate">{{ $t('table.add') }}</el-button>
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
          <el-table-column :label="$t('table.tenant')" min-width="50px" align="center">
            <template slot-scope="scope">
              <router-link :to="'/management/namespaces/' + scope.row.tenant" class="link-type">
                <span>{{ scope.row.tenant }}</span>
              </router-link>
            </template>
          </el-table-column>
          <el-table-column :label="$t('table.config')" align="center" min-width="100px">
            <template slot-scope="scope">
              <span class="link-type" @click="handleGetConfig(scope.row)">config</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('table.actions')" align="center" width="240" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button type="primary" size="mini" @click="handleUpdate(scope.row)">{{ $t('table.edit') }}</el-button>
              <el-button v-if="scope.row.status!='deleted'" size="mini" type="danger" @click="handleDelete(scope.row)">{{ $t('table.delete') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getTenants" />
      </el-col>
      <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 12}" :xl="{span: 12}" style="margin-bottom:30px;">
        <jsonEditor :value="jsonValue"/>
      </el-col>
    </el-row>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :rules="rules" :model="temp" label-position="left" label-width="70px" style="width: 400px; margin-left:50px;">
        <el-form-item v-if="dialogStatus==='create'" :label="$t('table.tenant')" prop="tenant">
          <el-input v-model="temp.tenant"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" :label="$t('table.clusters')" prop="tenant">
          <el-drag-select v-model="clusters" style="width:330px;" multiple placeholder="Please select">
            <el-option v-for="item in clusterListOptions" :label="item.label" :value="item.value" :key="item.value" />
          </el-drag-select>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" :label="$t('table.tenant')">
          <span>{{ temp.tenant }}</span>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" :label="$t('table.role')">
          <el-input v-model="temp.adminRoles"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" :label="$t('table.clusters')">
          <el-drag-select v-model="clusters" style="width:330px;" multiple placeholder="Please select">
            <el-option v-for="item in clusterListOptions" :label="item.label" :value="item.value" :key="item.value" />
          </el-drag-select>
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
import {
  updateTenant,
  putTenant,
  fetchTenants,
  fetchTenantsInfo,
  deleteTenant
} from '@/api/tenants'
import { fetchClusters } from '@/api/clusters'
import waves from '@/directive/waves' // Waves directive
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import jsonEditor from '@/components/JsonEditor'
import { validateEmpty } from '@/utils/validate'
import ElDragSelect from '@/components/DragSelect' // base on element-ui

const defaultForm = {
  cluster: ''
}

export default {
  name: 'Tenants',
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
      postForm: Object.assign({}, defaultForm),
      clusterListOptions: [],
      tableKey: 0,
      list: null,
      localList: [],
      searchList: [],
      total: 0,
      listLoading: true,
      clusters: [],
      jsonValue: {},
      listQuery: {
        tenant: '',
        page: 1,
        limit: 10
      },
      temp: {
        tenant: '',
        adminRoles: '',
        allowedClusters: ''
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: 'Edit',
        create: 'Create'
      },
      rules: {
        tenant: [{ required: true, message: 'required', trigger: 'blur' }],
        clusters: [{ required: true, message: 'required', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getTenants()
  },
  methods: {
    getTenants() {
      if (this.localList.length > 0) {
        setTimeout(() => {
          this.localPaging()
        }, 0)
      } else {
        this.listLoading = true
        fetchTenants().then(response => {
          for (var i = 0; i < response.data.length; i++) {
            this.localList.push({ 'tenant': response.data[i] })
          }
          this.total = this.localList.length
          this.list = this.localList.slice((this.listQuery.page - 1) * this.listQuery.limit, this.listQuery.limit * this.listQuery.page)
          // this.localPaging()
          // Just to simulate the time of the request
          setTimeout(() => {
            this.listLoading = false
          }, 1.5 * 1000)
        })
      }
    },
    localPaging() {
      this.listLoading = true
      if (!validateEmpty(this.listQuery.tenant)) {
        this.searchList = []
        console.log(this.localList)
        for (var i = 0; i < this.localList.length; i++) {
          if (this.localList[i]['tenant'].indexOf(this.listQuery.tenant) !== -1) {
            this.searchList.push(this.localList[i])
          }
        }
        this.total = this.searchList.length
        this.list = this.searchList.slice((this.listQuery.page - 1) * this.listQuery.limit, this.listQuery.limit * this.listQuery.page)
      } else {
        this.total = this.localList.length
        this.list = this.localList.slice((this.listQuery.page - 1) * this.listQuery.limit, this.listQuery.limit * this.listQuery.page)
      }
      console.log(this.searchList)
      this.listLoading = false
    },
    handleGetConfig(row) {
      this.temp = Object.assign({}, row) // copy obj
      fetchTenantsInfo(this.temp.tenant).then(response => {
        // response
        this.jsonValue = {
          'adminRoles': response.data.adminRoles,
          'allowedClusters': response.data.allowedClusters
        }
      })
    },
    handleFilter() {
      this.getTenants()
    },
    resetTemp() {
      this.temp = {
        tenant: '',
        adminRoles: '',
        allowedClusters: ''
      }
    },
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.clusters = []
      this.clusterListOptions = []
      fetchClusters(this.listQuery).then(response => {
        for (var i = 0; i < response.data.length; i++) {
          this.clusterListOptions.push({ 'value': response.data[i], 'label': response.data[i] })
        }
      })
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const data = { allowedClusters: this.clusters }
          putTenant(this.temp.tenant, data).then((response) => {
            this.temp.adminRoles = 'empty'
            this.temp.allowedClusters = 'empty'
            this.localList = []
            this.getTenants()
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
      fetchTenantsInfo(this.temp.tenant).then(response => {
        this.temp.adminRoles = response.data.adminRoles.join(',')
        this.clusters = response.data.allowedClusters
      })
      this.clusterListOptions = []
      fetchClusters(this.listQuery).then(response => {
        for (var i = 0; i < response.data.length; i++) {
          this.clusterListOptions.push({ 'value': response.data[i], 'label': response.data[i] })
        }
      })
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const tempData = Object.assign({}, this.temp)
          const data = {}
          if (this.clusters.length > 0) {
            data.allowedClusters = this.clusters
          }
          if (tempData.adminRoles.length > 0) {
            data.adminRoles = tempData.adminRoles.split(',')
          }
          if (this.clusters.length > 0 || tempData.adminRoles.length > 0) {
            updateTenant(this.temp.tenant, data).then(() => {
              this.dialogFormVisible = false
              this.$notify({
                title: 'success',
                message: 'update success',
                type: 'success',
                duration: 2000
              })
            })
          } else {
            this.$notify({
              title: 'success',
              message: 'no need update',
              type: 'success',
              duration: 2000
            })
          }
        }
      })
    },
    handleDelete(row) {
      this.temp = Object.assign({}, row) // copy obj
      deleteTenant(this.temp.tenant).then((response) => {
        this.$notify({
          title: 'success',
          message: 'delete success',
          type: 'success',
          duration: 2000
        })
        this.localList = []
        this.getTenants()
      })
    }
  }
}
</script>
