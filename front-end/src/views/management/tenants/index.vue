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
      <el-input :placeholder="$t('tenant.searchTenant')" v-model="listQuery.tenant" style="width: 200px;" @keyup.enter.native="handleFilter"/>
      <el-button type="primary" icon="el-icon-search" @click="handleFilter"/>
      <el-button type="primary" icon="el-icon-plus" @click="handleCreate">{{ $t('tenant.newTenant') }}</el-button>
    </div>

    <el-row :gutter="24">
      <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}">
        <el-table
          v-loading="listLoading"
          :key="tableKey"
          :data="currentList"
          border
          fit
          highlight-current-row
          style="width: 100%;">
          <el-table-column :label="$t('tenant.name')" min-width="50px" align="center">
            <template slot-scope="scope">
              <router-link :to="'/management/tenants/tenantInfo/' + scope.row.tenant + '?tab=namespaces'" class="link-type">
                <span>{{ scope.row.tenant }}</span>
              </router-link>
            </template>
          </el-table-column>
          <el-table-column :label="$t('namespace.namespaceNumber')" align="center" min-width="100px">
            <template slot-scope="scope">
              <router-link :to="'/management/tenants/tenantInfo/' + scope.row.tenant + '?tab=namespaces'" class="link-type">
                <span>{{ scope.row.namespace }}</span>
              </router-link>
            </template>
          </el-table-column>
          <el-table-column :label="$t('tenant.allowedClustersLabel')" align="center" min-width="100px">
            <template slot-scope="scope">
              <span
                v-for="tag in scope.row.allowedClusters"
                :key="tag"
                class="list-el-tag">
                <router-link :to="'/management/clusters/' + tag + '/cluster?tab=brokers'" class="link-type">
                  {{ tag }}
                </router-link>
              </span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('tenant.adminRolesLabel')" align="center" min-width="100px">
            <template slot-scope="scope">
              <el-tag
                v-for="tag in scope.row.adminRoles"
                :key="tag"
                effect="dark"
                class="list-el-tag">
                {{ tag }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column :label="$t('common.inMsg')" align="center">
            <template slot-scope="scope">
              <i class="el-icon-download" style="margin-right: 2px"/><span>{{ scope.row.inMsg }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('common.outMsg')" align="center">
            <template slot-scope="scope">
              <i class="el-icon-upload2" style="margin-right: 2px"/><span>{{ scope.row.outMsg }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('common.inBytes')" align="center">
            <template slot-scope="scope">
              <i class="el-icon-download" style="margin-right: 2px"/><span>{{ scope.row.inBytes }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('common.outBytes')" align="center">
            <template slot-scope="scope">
              <i class="el-icon-upload2" style="margin-right: 2px"/><span>{{ scope.row.outBytes }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('common.storageSize')" align="center">
            <template slot-scope="scope">
              <span>{{ scope.row.storageSize }}</span>
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
    <el-row>
      <el-pagination
        :current-page.sync="currentPage"
        :page-size="pageSize"
        :total="list.length"
        background
        layout="total, prev, pager, next"
        @current-change="handleCurrentChange"
      />
    </el-row>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="30%">
      <el-form ref="form" :rules="rules" :model="form" label-position="top">
        <el-form-item v-if="dialogStatus==='create'" :label="$t('table.tenant')" prop="tenant">
          <el-input v-model="form.tenant" :placeholder="$t('tenant.selectTenantMessage')"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" :label="$t('tenant.allowedClustersLabel')" prop="clusters">
          <el-select v-model="form.clusters" :placeholder="$t('cluster.selectClusterMessage')" multiple style="width:100%">
            <el-option v-for="item in clusterListOptions" :label="item.label" :value="item.value" :key="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" :label="$t('tenant.adminRolesLabel')" prop="roles">
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
          <h4>{{ deleteTenantMessage }}</h4>
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
import { formatBytes } from '@/utils/index'
import { numberFormatter } from '@/filters/index'

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
      currentPage: 0,
      pageSize: 10,
      list: [],
      currentList: [],
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
        create: this.$i18n.t('tenant.newTenant'),
        delete: this.$i18n.t('tenant.deleteTenant')
      },
      rules: {
        tenant: [{ required: true, message: this.$i18n.t('tenant.tenantIsRequired'), trigger: 'blur' }],
        clusters: [{ required: true, message: this.$i18n.t('tenant.clusterIsRequired'), trigger: 'blur' }]
      },
      inputVisible: false,
      inputValue: '',
      deleteTenantMessage: this.$i18n.t('tenant.deleteTenantMessage')
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
            let allowedClusters = ''
            let adminRoles = ''
            if (response.data.data[i]['allowedClusters'].length > 0) {
              allowedClusters = response.data.data[i]['allowedClusters']
            }
            if (response.data.data[i]['adminRoles'].length > 0) {
              adminRoles = response.data.data[i]['adminRoles']
            }
            let adminRolesArray = []
            if (adminRoles !== '') {
              adminRolesArray = adminRoles.split(',')
            }
            let allowedClustersArray = []
            if (allowedClusters !== '') {
              allowedClustersArray = allowedClusters.split(',')
            }
            const data = {
              'tenant': response.data.data[i]['tenant'],
              'namespace': response.data.data[i]['namespaces'],
              'allowedClusters': allowedClustersArray,
              'adminRoles': adminRolesArray,
              'inMsg': numberFormatter(0, 2),
              'outMsg': numberFormatter(0, 2),
              'inBytes': formatBytes(0),
              'outBytes': formatBytes(0),
              'storageSize': formatBytes(0, 0)
            }
            if (response.data.data[i].hasOwnProperty('inMsg')) {
              data['inMsg'] = numberFormatter(response.data.data[i]['inMsg'], 2)
              data['outMsg'] = numberFormatter(response.data.data[i]['outMsg'], 2)
              data['inBytes'] = numberFormatter(response.data.data[i]['inBytes'])
              data['outBytes'] = numberFormatter(response.data.data[i]['outBytes'])
              data['storageSize'] = numberFormatter(response.data.data[i]['storageSize'], 2)
            }
            this.localList.push(data)
          }
          this.total = response.data.total
          this.listQuery.page = response.data.pageNum
          this.listQuery.limit = response.data.pageSize
          this.list = this.localList.slice((this.listQuery.page - 1) * this.listQuery.limit, this.listQuery.limit * this.listQuery.page)
          this.pageInit()
          // this.localPaging()
          // Just to simulate the time of the request
          setTimeout(() => {
            this.listLoading = false
          }, 1.5 * 1000)
        })
      }
    },
    pageInit() {
      this.currentPage = 0
      if (this.list.length <= this.pageSize) {
        this.currentList = this.list
      } else {
        this.currentList = this.list.slice(0, this.currentPage + this.pageSize)
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
              message: this.$i18n.t('tenant.createTenantSuccessNotification'),
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
          message: this.$i18n.t('tenant.deleteTenantSuccessNotification'),
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
            message: this.$i18n.t('tenant.roleAlreadyExists'),
            type: 'error',
            duration: 2000
          })
          return
        }
        this.form.dynamicRoles.push(inputValue)
      }
      this.inputVisible = false
      this.inputValue = ''
    },
    handleCurrentChange() {
      var startList = (this.currentPage - 1) * this.pageSize
      var endList = this.currentPage * this.pageSize
      this.currentList = this.list.slice(startList, endList)
    }
  }
}
</script>

<style>
.list-el-tag {
  margin-left: 2px;
  margin-right: 2px;
}
.el-form {
  margin-left: 0% !important;
  margin-right: 0% !important;
}
</style>
