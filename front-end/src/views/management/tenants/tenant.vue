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
    <div class="createPost-container">
      <el-form :inline="true" :model="postForm" class="form-container">
        <el-form-item class="postInfo-container-item" label="Tenant">
          <el-select v-model="postForm.tenant" :placeholder="$t('tenant.selectTenantMessage')" filterable @change="getNamespacesList(postForm.tenant)">
            <el-option v-for="(item,index) in tenantsListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane :label="$t('tabs.namespace')" name="namespaces">
        <div class="filter-container">
          <el-input
            :placeholder="$t('namespace.searchNamespaces')"
            v-model="searchNamespace"
            style="width: 200px;"
            @keyup.enter.native="handleFilterNamespace"/>
          <el-button type="primary" icon="el-icon-search" @click="handleFilterNamespace"/>
          <el-button type="primary" icon="el-icon-plus" @click="handleCreateNamespace">{{ $t('namespace.newNamespace') }}</el-button>
        </div>
        <el-row :gutter="24">
          <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}">
            <el-table
              v-loading="listLoading"
              :key="tableKey"
              :data="listNamespaces"
              border
              fit
              highlight-current-row
              style="width: 100%;">
              <el-table-column :label="$t('namespace.name')" align="center">
                <template slot-scope="scope">
                  <router-link :to="'/management/namespaces/' + scope.row.tenant +'/' + scope.row.namespace + '/namespace?tab=overview'" class="link-type">
                    <span>{{ scope.row.namespace }}</span>
                  </router-link>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.topicNumber')" align="center">
                <template slot-scope="scope">
                  <router-link :to="'/management/namespaces/' + scope.row.tenant + '/' + scope.row.namespace + '/namespace?tab=topics'" class="link-type">
                    <span>{{ scope.row.topics }}</span>
                  </router-link>
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
                  <router-link :to="'/management/namespaces/' + scope.row.tenant +'/' + scope.row.namespace + '/namespace'">
                    <el-button type="primary" size="mini">{{ $t('table.edit') }}</el-button>
                  </router-link>
                  <el-button v-if="scope.row.status!='deleted'" size="mini" type="danger" @click="handleDeleteNamespace(scope.row)">{{ $t('table.delete') }}</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane :label="$t('tabs.configuration')" name="configuration">
        <el-row :gutter="24">
          <el-col :span="24">
            <div slot="header" class="clearfix">
              <h2>{{ $t('tenant.tenantInfo') }}</h2>
            </div>
            <h4>Clusters</h4>
            <hr class="split-line">
            <div class="component-item">
              <div class="section-title">
                <span>{{ $t('tenant.allowedClustersLabel') }}</span>
                <el-tooltip :content="allowedClustersContent" class="item" effect="dark" placement="top">
                  <i class="el-icon-info"/>
                </el-tooltip>
              </div>
              <el-select
                v-model="clusterValue"
                :placeholder="$t('cluster.selectCluster')"
                style="width:500px;margin-top:20px"
                multiple
                @change="handleSelectClusters()">
                <el-option v-for="item in clusterOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </div>
            <h4>{{ $t('tenant.permissions') }}</h4>
            <hr class="split-line">
            <div class="component-item">
              <div class="section-title">
                <span>{{ $t('tenant.adminRolesLabel') }}</span>
                <el-tooltip :content="adminRolesContent" class="item" effect="dark" placement="top">
                  <i class="el-icon-info"/>
                </el-tooltip>
              </div>
              <div class="section-content">
                <el-tag
                  v-for="tag in dynamicRoles"
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
                <el-button v-else class="button-new-tag" size="small" icon="el-icon-plus" @click="showInput">{{ $t('tenant.newRole') }}</el-button>
              </div>
            </div>
            <h4 style="color:#E57470">{{ $t('common.dangerZone') }}</h4>
            <hr class="danger-line">
            <el-button type="danger" class="button" @click="handleDeleteTenant">{{ $t('tenant.deleteTenant') }}</el-button>
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="30%">
      <el-form ref="temp" :model="temp" :rules="rules" label-position="top">
        <el-form-item v-if="dialogStatus==='createNamespace'" :label="$t('table.namespace')" prop="namespace">
          <el-input v-model="temp.namespace" :placeholder="$t('namespace.inputNamespaceMessage')"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='deleteNamespace'">
          <h4>{{ $t('namespace.deleteNamespaceMessage') }}</h4>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='deleteTenant'">
          <h4>{{ $t('tenant.deleteTenantMessage') }}</h4>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleOptions()">{{ $t('table.confirm') }}</el-button>
          <el-button @click="dialogFormVisible=false">{{ $t('table.cancel') }}</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import { fetchClusters } from '@/api/clusters'
import { fetchTenants, fetchTenantsInfo, updateTenant, deleteTenant } from '@/api/tenants'
import {
  fetchNamespaces,
  putNamespace,
  deleteNamespace
} from '@/api/namespaces'
import { validateEmpty } from '@/utils/validate'
import { formatBytes } from '@/utils/index'
import { numberFormatter } from '@/filters/index'

const defaultForm = {
  tenant: ''
}

export default {
  name: 'TenantInfo',
  data() {
    return {
      postForm: Object.assign({}, defaultForm),
      dynamicRoles: [],
      inputVisible: false,
      inputValue: '',
      adminRoles: '',
      allowedClustersContent: this.$i18n.t('tenant.allowedClustersContent'),
      adminRolesContent: this.$i18n.t('tenant.adminRolesContent'),
      clusterValue: [],
      clusterOptions: [],
      activeName: 'configuration',
      tenantsListOptions: [],
      listQuery: {
        namespace: '',
        page: 1,
        limit: 20
      },
      listLoading: false,
      tableKey: 0,
      total: 0,
      listNamespaces: [],
      temp: {
        tenant: '',
        namespace: '',
        limit: '',
        actions: [],
        clusters: []
      },
      dialogFormVisible: false,
      dialogStatus: '',
      rules: {
        namespace: [{ required: true, message: this.$i18n.t('namespace.namespaceNameIsRequired'), trigger: 'blur' }]
      },
      currentTabName: '',
      textMap: {
        createNamespace: this.$i18n.t('namespace.newNamespace'),
        deleteNamespace: this.$i18n.t('namespace.deleteNamespace'),
        deleteTenant: this.$i18n.t('tenant.deleteTenant')
      },
      tempNamespacesList: [],
      searchNamespace: '',
      searchList: []
    }
  },
  created() {
    this.postForm.tenant = this.$route.params && this.$route.params.tenant
    if (this.$route.query && this.$route.query.tab) {
      this.activeName = this.$route.query.tab
      this.currentTabName = this.$route.query.tab
    }
    this.getRemoteTenantsList()
    this.getNamespacesList()
    this.getClustersList()
    this.getTenantsInfo()
  },
  methods: {
    handleClose(tag) {
      this.dynamicRoles.splice(this.dynamicRoles.indexOf(tag), 1)
      const data = {}
      data.adminRoles = this.dynamicRoles
      data.allowedClusters = this.clusterValue
      updateTenant(this.postForm.tenant, data).then(() => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('tenant.updateRolesSuccessNotification'),
          type: 'success',
          duration: 2000
        })
      })
    },
    showInput() {
      this.inputVisible = true
      this.$nextTick(_ => {
        this.$refs.saveTagInput.$refs.input.focus()
      })
    },
    getRemoteTenantsList() {
      fetchTenants().then(response => {
        if (!response.data) return
        for (var i = 0; i < response.data.total; i++) {
          this.tenantsListOptions.push(response.data.data[i].tenant)
        }
      })
    },
    handleInputConfirm() {
      const inputValue = this.inputValue
      if (inputValue) {
        if (this.dynamicRoles.indexOf(this.inputValue) >= 0) {
          this.$notify({
            title: 'error',
            message: this.$i18n.t('tenant.roleAlreadyExists'),
            type: 'error',
            duration: 2000
          })
          return
        }
        this.dynamicRoles.push(inputValue)
      }
      this.inputVisible = false
      this.inputValue = ''
      const data = {}
      data.adminRoles = this.dynamicRoles
      data.allowedClusters = this.clusterValue
      updateTenant(this.postForm.tenant, data).then(() => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('tenant.updateRolesSuccessNotification'),
          type: 'success',
          duration: 2000
        })
      })
    },
    getClustersList() {
      fetchClusters().then(response => {
        for (var i in response.data.data) {
          this.clusterOptions.push({
            value: response.data.data[i].cluster,
            label: response.data.data[i].cluster
          })
        }
      })
    },
    getTenantsInfo() {
      fetchTenantsInfo(this.postForm.tenant).then(response => {
        this.clusterValue = response.data.allowedClusters
        this.dynamicRoles = response.data.adminRoles
      })
    },
    handleSelectClusters() {
      const data = {}
      data.allowedClusters = this.clusterValue
      data.adminRoles = this.dynamicRoles
      updateTenant(this.postForm.tenant, data).then(() => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('tenant.updateClustersSuccessNotification'),
          type: 'success',
          duration: 2000
        })
      })
    },
    handleDeleteTenant() {
      this.dialogStatus = 'deleteTenant'
      this.dialogFormVisible = true
    },
    deleteTenant() {
      deleteTenant(this.postForm.tenant).then((response) => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('tenant.deleteTenantSuccessNotification'),
          type: 'success',
          duration: 2000
        })
        this.$router.push({ path: '/management/tenants' })
      })
    },
    getNamespacesList() {
      fetchNamespaces(this.postForm.tenant, this.listQuery).then(response => {
        this.listNamespaces = []
        for (var i = 0; i < response.data.data.length; i++) {
          const data = {
            'tenant': this.postForm.tenant,
            'namespace': response.data.data[i].namespace,
            'topics': response.data.data[i].topics,
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
          this.listNamespaces.push(data)
        }
        this.total = this.listNamespaces.length
        // Just to simulate the time of the request
        setTimeout(() => {
          this.listLoading = false
        }, 1.5 * 1000)
      })
    },
    handleClick(tab, event) {
      this.currentTabName = tab.name
      this.$router.push({ query: { 'tab': tab.name }})
    },
    handleFilterNamespace() {
      if (this.tempNamespacesList.length <= 0) {
        for (var t = 0; t < this.listNamespaces.length; t++) {
          this.tempNamespacesList.push(this.listNamespaces[t])
        }
      }
      if (!validateEmpty(this.searchNamespace)) {
        this.searchList = []
        for (var i = 0; i < this.listNamespaces.length; i++) {
          if (this.listNamespaces[i]['namespace'].indexOf(this.searchNamespace) !== -1) {
            this.searchList.push(this.listNamespaces[i])
          }
        }
        this.listNamespaces = this.searchList
      } else {
        this.listNamespaces = this.tempNamespacesList
      }
    },
    handleCreateNamespace() {
      this.temp.namespace = ''
      this.dialogStatus = 'createNamespace'
      this.dialogFormVisible = true
    },
    createNamespace() {
      putNamespace(this.postForm.tenant, this.temp.namespace, this.temp).then(() => {
        this.listNamespaces = []
        this.getNamespacesList()
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: this.$i18n.t('namespace.createNsSuccessNotification'),
          type: 'success',
          duration: 2000
        })
      })
    },
    handleOptions() {
      this.$refs['temp'].validate((valid) => {
        if (valid) {
          switch (this.dialogStatus) {
            case 'createNamespace':
              this.createNamespace()
              break
            case 'deleteNamespace':
              this.deleteNamespace()
              break
            case 'deleteTenant':
              this.deleteTenant()
              break
          }
        }
      })
    },
    handleDeleteNamespace(row) {
      this.dialogStatus = 'deleteNamespace'
      this.dialogFormVisible = true
      this.temp.tenant = row.tenant
      this.temp.namespace = row.namespace
    },
    deleteNamespace() {
      var tenantNamespace = this.temp.tenant + '/' + this.temp.namespace
      deleteNamespace(tenantNamespace).then((response) => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('namespace.deleteNsSuccessNotification'),
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
        this.listNamespaces = []
        this.getNamespacesList()
      })
    }
  }
}
</script>

<style scoped>
.el-tag + .el-tag {
    margin-left: 10px;
  }
.button-new-tag {
  margin-left: 10px;
  height: 32px;
  line-height: 30px;
  padding-top: 0;
  padding-bottom: 0;
}
.input-new-tag {
  width: 90px;
  margin-left: 10px;
  vertical-align: bottom;
}
.section-content {
  margin-top: 15px;
}
.confirm-button {
  margin-top: 10px;
}
.section-title {
  color: rgba(0,0,0,.45);
  font-size: 16px;
}
.component-item{
  min-height: 60px;
}
.split-line {
  background: #e6e9f3;
  border: none;
  height: 1px;
}
.danger-line {
  background: red;
  border: none;
  height: 1px;
}
</style>
