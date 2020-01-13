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
          <el-select v-model="postForm.tenant" :placeholder="$t('tenant.selectTenantMessage')" @change="getNamespacesList(postForm.tenant)">
            <el-option v-for="(item,index) in tenantsListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <el-tabs v-model="activeName">
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
import { fetchTenants } from '@/api/tenants'
import { getTenant } from '@/utils/tenant'
import {
  fetchNamespaces,
  putNamespace,
  deleteNamespace
} from '@/api/namespaces'
import { validateEmpty } from '@/utils/validate'

const defaultForm = {
  tenant: getTenant()
}

export default {
  name: 'TenantInfo',
  data() {
    return {
      postForm: Object.assign({}, defaultForm),
      adminRoles: '',
      tenantsListOptions: [],
      listQuery: {
        namespace: '',
        page: 1,
        limit: 20
      },
      activeName: 'namespaces',
      listLoading: false,
      tableKey: 0,
      total: 0,
      listNamespaces: [],
      temp: {
        tenant: '',
        namespace: '',
        limit: ''
      },
      dialogFormVisible: false,
      dialogStatus: '',
      rules: {
        namespace: [{ required: true, message: this.$i18n.t('namespace.namespaceNameIsRequired'), trigger: 'blur' }]
      },
      currentTabName: '',
      textMap: {
        createNamespace: this.$i18n.t('namespace.newNamespace'),
        deleteNamespace: this.$i18n.t('namespace.deleteNamespace')
      },
      tempNamespacesList: [],
      searchNamespace: '',
      searchList: []
    }
  },
  created() {
    this.getRemoteTenantsList()
    this.getNamespacesList()
  },
  methods: {
    getRemoteTenantsList() {
      fetchTenants().then(response => {
        if (!response.data) return
        for (var i = 0; i < response.data.total; i++) {
          this.tenantsListOptions.push(response.data.data[i].tenant)
        }
      })
    },
    getNamespacesList() {
      fetchNamespaces(this.postForm.tenant, this.listQuery).then(response => {
        if (!response.data.data) return
        this.listNamespaces = []
        for (var i = 0; i < response.data.data.length; i++) {
          this.listNamespaces.push({
            'tenant': this.postForm.tenant,
            'namespace': response.data.data[i].namespace,
            'topics': response.data.data[i].topics
          })
        }
        this.total = this.listNamespaces.length
        // Just to simulate the time of the request
        setTimeout(() => {
          this.listLoading = false
        }, 1.5 * 1000)
      })
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
