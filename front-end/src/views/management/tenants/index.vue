<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input :placeholder="$t('tenant.searchTenant')" v-model="listQuery.tenant" style="width: 200px;" @keyup.enter.native="handleFilter"/>
      <el-button type="primary" icon="el-icon-search" @click="handleFilter"/>
      <el-button type="primary" icon="el-icon-plus" @click="handleCreate">{{ $t('tenant.newTenant') }}</el-button>
    </div>

    <el-row :gutter="24">
      <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}">
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
              <router-link :to="'/management/tenants/tenantInfo/' + scope.row.tenant + '?tab=namespaces'" class="link-type">
                <span>{{ scope.row.tenant }}</span>
              </router-link>
            </template>
          </el-table-column>
          <el-table-column :label="$t('table.namespace')" align="center" min-width="100px">
            <template slot-scope="scope">
              <router-link :to="'/management/tenants/tenantInfo/' + scope.row.tenant + '?tab=namespaces'" class="link-type">
                <span>{{ scope.row.namespace }}</span>
              </router-link>
            </template>
          </el-table-column>
          <el-table-column :label="$t('table.allowedClusters')" align="center" min-width="100px">
            <template slot-scope="scope">
              <span>{{ scope.row.allowedClusters }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('table.adminRoles')" align="center" min-width="100px">
            <template slot-scope="scope">
              <span>{{ scope.row.adminRoles }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('table.actions')" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <router-link :to="'/management/tenants/tenantInfo/' + scope.row.tenant">
                <el-button type="primary" size="mini">{{ $t('table.edit') }}</el-button>
              </router-link>
              <el-button size="mini" type="danger" @click="handleDelete(scope.row)">{{ $t('table.delete') }}</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="30%">
      <el-form ref="form" :rules="rules" :model="form" label-position="top">
        <el-form-item v-if="dialogStatus==='create'" :label="$t('table.tenant')" prop="tenant">
          <el-input v-model="form.tenant" placeholder="Please input tenant"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" label="Allowed Clusters" prop="clusters">
          <el-select v-model="form.clusters" multiple placeholder="Please select clusters" style="width:100%">
            <el-option v-for="item in clusterListOptions" :label="item.label" :value="item.value" :key="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" label="Allowed Roles" prop="roles">
          <el-tag
            v-for="tag in form.dynamicRoles"
            :key="tag"
            :disable-transitions="false"
            closable
            @close="handleClose(tag)">
            {{ tag }}
          </el-tag>
          <el-input
            v-if="inputVisible"
            ref="saveTagInput"
            v-model="inputValue"
            size="small"
            class="input-new-tag"
            @keyup.enter.native="handleInputConfirm"/>
          <el-button v-else class="button-new-tag" size="small" @click="showInput">+ New Role</el-button>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='delete'">
          <h4>Are you sure you want to delete this tenant?</h4>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleOptions()">{{ $t('table.confirm') }}</el-button>
          <el-button @click="dialogFormVisible = false">{{ $t('table.cancel') }}</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import {
  putTenant,
  fetchTenants,
  deleteTenant
} from '@/api/tenants'
import { fetchClusters } from '@/api/clusters'
import { validateEmpty } from '@/utils/validate'
const defaultForm = {
  cluster: ''
}
export default {
  name: 'Tenants',
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
      listQuery: {
        tenant: '',
        page: 1,
        limit: 10
      },
      form: {
        tenant: '',
        clusters: [],
        dynamicRoles: []
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        create: 'New Tenant',
        delete: 'Delete Tenant'
      },
      rules: {
        tenant: [{ required: true, message: 'Tenant is required', trigger: 'blur' }],
        clusters: [{ required: true, message: 'Cluster is required', trigger: 'blur' }]
      },
      inputVisible: false,
      inputValue: ''
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
          for (var i = 0; i < response.data.total; i++) {
            let allowedClusters = '-'
            let adminRoles = '-'
            if (response.data.data[i]['allowedClusters'].length > 0) {
              allowedClusters = response.data.data[i]['allowedClusters']
            }
            if (response.data.data[i]['adminRoles'].length > 0) {
              adminRoles = response.data.data[i]['adminRoles']
            }
            this.localList.push({
              'tenant': response.data.data[i]['tenant'],
              'namespace': response.data.data[i]['namespaces'],
              'allowedClusters': allowedClusters,
              'adminRoles': adminRoles
            })
          }
          this.total = response.data.total
          this.listQuery.page = response.data.pageNum
          this.listQuery.limit = response.data.pageSize
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
      this.listLoading = false
    },
    handleFilter() {
      this.getTenants()
    },
    resetForm() {
      this.form = {
        tenant: '',
        adminRoles: '',
        allowedClusters: '',
        clusters: []
      }
    },
    handleOptions() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          switch (this.dialogStatus) {
            case 'create':
              this.createData()
              break
            case 'delete':
              this.deleteData()
              break
          }
        }
      })
    },
    handleCreate() {
      this.resetForm()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.form.clusters = []
      this.clusterListOptions = []
      this.form.dynamicRoles = []
      fetchClusters(this.listQuery).then(response => {
        for (var i = 0; i < response.data.data.length; i++) {
          this.clusterListOptions.push({ 'value': response.data.data[i].cluster, 'label': response.data.data[i].cluster })
        }
      })
      this.$nextTick(() => {
        this.$refs['form'].clearValidate()
      })
    },
    createData() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          const data = {
            allowedClusters: this.form.clusters,
            adminRoles: this.form.dynamicRoles
          }
          putTenant(this.form.tenant, data).then((response) => {
            this.form.adminRoles = 'empty'
            this.form.allowedClusters = 'empty'
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
    handleDelete(row) {
      this.dialogStatus = 'delete'
      this.dialogFormVisible = true
      this.form.tenant = row.tenant
    },
    deleteData() {
      deleteTenant(this.form.tenant).then((response) => {
        this.$notify({
          title: 'success',
          message: 'delete success',
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
        this.localList = []
        this.getTenants()
      })
    },
    handleClose(tag) {
      this.form.dynamicRoles.splice(this.form.dynamicRoles.indexOf(tag), 1)
    },
    showInput() {
      this.inputVisible = true
      this.$nextTick(_ => {
        this.$refs.saveTagInput.$refs.input.focus()
      })
    },
    handleInputConfirm() {
      const inputValue = this.inputValue
      if (inputValue) {
        if (this.form.dynamicRoles.indexOf(this.inputValue) >= 0) {
          this.$notify({
            title: 'error',
            message: 'Role is exists',
            type: 'error',
            duration: 2000
          })
          return
        }
        this.form.dynamicRoles.push(inputValue)
      }
      this.inputVisible = false
      this.inputValue = ''
    }
  }
}
</script>

<style>
.el-form {
  margin-left: 0% !important;
  margin-right: 0% !important;
}
</style>
