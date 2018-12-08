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
        <el-form-item v-if="dialogStatus==='update'" :label="$t('table.tenant')">
          <span>{{ temp.tenant }}</span>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" :label="$t('table.role')">
          <el-input v-model="temp.adminRoles"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" :label="$t('table.clusters')">
          <el-input v-model="temp.allowedClusters"/>
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
import { fetchTenantsInfo, updateTenant, putTenant, fetchTenants } from '@/api/tenants'
import waves from '@/directive/waves' // Waves directive
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import jsonEditor from '@/components/JsonEditor'

export default {
  name: 'Tenants',
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
      total: 0,
      listLoading: true,
      jsonValue: {},
      listQuery: {
        page: 1,
        limit: 20
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
        tenant: [{ required: true, message: 'tenant is required', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getTenants()
  },
  methods: {
    getTenants() {
      this.listLoading = true
      fetchTenantsInfo(this.listQuery).then(response => {
        this.list = response.items
        this.total = response.total
        // Just to simulate the time of the request
        setTimeout(() => {
          this.listLoading = false
        }, 1.5 * 1000)
      })
    },
    handleGetConfig(row) {
      this.temp = Object.assign({}, row) // copy obj
      fetchTenants(this.temp.tenant).then(response => {
        // response
        this.jsonValue = {
          'adminRoles': response.adminRoles,
          'allowedClusters': response.allowedClusters
        }
      })
    },
    getTenantsConfig() {
    },
    handleFilter() {
      this.listQuery.page = 1
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
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          putTenant(this.temp.tenant, this.temp).then(() => {
            this.temp.adminRoles = 'empty'
            this.temp.allowedClusters = 'empty'
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
      fetchTenants(this.temp.tenant).then(response => {
        this.temp.adminRoles = response.adminRoles.join(',')
        this.temp.allowedClusters = response.allowedClusters.join(',')
      })
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const tempData = Object.assign({}, this.temp)
          updateTenant(this.temp.tenant, tempData).then(() => {
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
    JumpToNamespace(tenant) {

    }
  }
}
</script>
