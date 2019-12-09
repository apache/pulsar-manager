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
    <el-button type="primary" icon="el-icon-plus" @click="handleCreateRole">{{ $t('role.buttonNewRole') }}</el-button>

    <el-row :gutter="24">
      <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}" style="margin-top:15px">
        <el-table
          v-loading="roleListLoading"
          :key="roleTableKey"
          :data="roleList"
          border
          fit
          highlight-current-row
          style="width: 100%;">
          <el-table-column :label="$t('role.colRoleName')" min-width="50px" align="center">
            <template slot-scope="scope">
              <span>{{ scope.row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('role.colRoleDesc')" align="center" min-width="100px">
            <template slot-scope="scope">
              <span>{{ scope.row.description }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('role.colResourceType')" align="center" min-width="100px">
            <template slot-scope="scope">
              <span>{{ scope.row.resourceType }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('role.colResourceName')" align="center" min-width="100px">
            <template slot-scope="scope">
              <span>{{ scope.row.resourceName }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('role.colResourceVerbs')" align="center" min-width="100px">
            <template slot-scope="scope">
              <span>{{ scope.row.resourceVerbs }}</span>
            </template>
          </el-table-column>
          <el-table-column v-if="superUser" :label="$t('role.colRoleSource')" align="center" min-width="100px">
            <template slot-scope="scope">
              <span>{{ scope.row.roleSource }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('table.actions')" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button :disabled="scope.row.resourceType=='TENANTS'" type="primary" size="mini" @click="handleUpdateRole(scope.row)">{{ $t('table.edit') }}</el-button>
              <el-button :disabled="scope.row.resourceType=='TENANTS'" size="mini" type="danger" @click="handleDeleteRole(scope.row)">{{ $t('table.delete') }}</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="30%">
      <el-form ref="form" :rules="rules" :model="form" label-position="top">
        <el-form-item v-if="dialogStatus==='create'" :label="$t('role.colRoleName')" prop="name">
          <el-input v-model="form.name" :placeholder="$t('role.roleNamePlaceHolder')"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" :label="$t('role.colRoleDesc')">
          <el-input :rows="2" v-model="form.description" :placeholder="$t('role.roleDescPlaceHolder')" type="textarea"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" :label="$t('role.colResourceName')" prop="resourceName">
          <el-input :rows="2" v-model="form.resourceName" :placeholder="$t('role.resourceNamePlaceHolder')"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" :label="$t('role.colResourceType')" prop="resourceType">
          <el-select
            v-model="form.resourceType"
            :placeholder="$t('role.resourceTypePlaceHolder')"
            style="width:100%"
            @change="handleChangeResourceType(form.resourceType)">
            <el-option
              v-for="item in resourceTypeListOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" :label="$t('role.colResource')" prop="resource">
          <el-select v-model="form.resource" :placeholder="$t('role.resourcePlaceHolder')" style="width:100%">
            <el-option
              v-for="item in resourceListOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" :label="$t('role.colResourceVerbs')" prop="resourceVerbs">
          <el-select
            v-model="form.resourceVerbs"
            :placeholder="$t('role.resourceVerbsPlaceHolder')"
            multiple
            style="width:100%">
            <el-option
              v-for="item in resourceVerbsListOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" :label="$t('role.colRoleName')">
          <span>{{ form.name }} </span>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" :label="$t('role.colResourceType')">
          <el-select v-model="form.resourceType" :placeholder="$t('role.resourceTypePlaceHolder')" style="width:100%">
            <el-option
              v-for="item in resourceTypeListOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" :label="$t('role.colResource')">
          <el-select v-model="form.resource" :placeholder="$t('role.resourcePlaceHolder')" style="width:100%">
            <el-option
              v-for="item in resourceListOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" :label="$t('role.colResourceVerbs')">
          <el-select
            v-model="form.resourceVerbs"
            :placeholder="$t('role.resourceVerbsPlaceHolder')"
            multiple
            style="width:100%">
            <el-option
              v-for="item in resourceVerbsListOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" :label="$t('role.colRoleDesc')">
          <el-input :rows="2" v-model="form.description" :placeholder="$t('role.roleDescPlaceHolder')" type="textarea"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='delete'">
          <h4>Delete Role {{ form.name }}</h4>
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
  fetchRoles,
  putRole,
  getResourceType,
  getResource,
  getResourceVerbs,
  updateRole,
  deleteRole
} from '@/api/roles'

export default {
  name: 'RolesInfo',
  data() {
    return {
      roleList: [],
      roleTableKey: 0,
      roleListLoading: false,
      textMap: {
        create: this.$i18n.t('role.newRole'),
        delete: this.$i18n.t('role.deleteRole'),
        update: this.$i18n.t('role.updateRole')
      },
      dialogFormVisible: false,
      dialogStatus: '',
      form: {
        name: '',
        description: '',
        resourceType: '',
        resourceName: '',
        resourceVerbs: [],
        resource: '',
        roleSource: '',
        resourceId: ''
      },
      resourceTypeListOptions: [],
      resourceTypeValue: '',
      temp: {
        'name': '',
        'description': '',
        'resourceType': '',
        'resourceName': '',
        'resourceVerbs': ''
      },
      resource: '',
      resourceListOptions: [],
      resourceVerbs: '',
      resourceVerbsListOptions: [],
      superUser: false,
      description: '',
      rules: {
        name: [{ required: true, message: this.$i18n.t('role.roleNameIsRequired'), trigger: 'blur' }],
        resourceName: [{ required: true, message: 'Please input resource name', trigger: 'blur' }],
        resourceType: [{ required: true, message: 'Please select resource name', trigger: 'blur' }],
        resource: [{ required: true, message: 'Please select resource', trigger: 'blur' }],
        resourceVerbs: [{ required: true, message: 'Please select resource verbs', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getRoles()
    this.getResourceType()
  },
  methods: {
    getResourceType() {
      getResourceType().then(response => {
        if (!response.data) return
        for (var i = 0; i < response.data.resourceType.length; i++) {
          this.resourceTypeListOptions.push({
            'value': response.data.resourceType[i],
            'label': response.data.resourceType[i]
          })
        }
      })
    },
    getRoles() {
      fetchRoles().then(response => {
        if (!response.data) return
        this.roleList = []
        for (var i = 0; i < response.data.data.length; i++) {
          this.roleList.push({
            'name': response.data.data[i].roleName,
            'description': response.data.data[i].description,
            'resource': response.data.data[i].resource,
            'resourceType': response.data.data[i].resourceType,
            'resourceName': response.data.data[i].resourceName,
            'resourceVerbs': response.data.data[i].resourceVerbs,
            'resourceId': response.data.data[i].resourceId,
            'roleSource': response.data.data[i].roleSource,
            'flag': response.data.data[i].flag
          })
        }
      })
    },
    getRoleByRoleName() {
    },
    handleCreateRole() {
      this.form.name = ''
      this.form.description = ''
      this.form.resourceType = ''
      this.form.resourceName = ''
      this.form.resourceVerbs = ''
      this.form.resource = ''
      this.dialogFormVisible = true
      this.dialogStatus = 'create'
    },
    handleDeleteRole(row) {
      this.temp.name = row.name
      this.dialogFormVisible = true
      this.dialogStatus = 'delete'
    },
    handleUpdateRole(row) {
      this.form.name = row.name
      this.form.description = row.description
      this.form.resource = row.resource
      this.form.resourceId = row.resourceId
      this.form.resourceType = row.resourceType
      this.form.resourceName = row.resourceName
      this.form.resourceVerbs = row.resourceVerbs.split(',')
      this.handleChangeResourceType(row)
      this.dialogFormVisible = true
      this.dialogStatus = 'update'
    },
    handleOptions() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          switch (this.dialogStatus) {
            case 'create':
              this.createRole()
              break
            case 'delete':
              this.deleteRole()
              break
            case 'update':
              this.updateRole()
              break
          }
        }
      })
    },
    createRole() {
      const data = {
        'roleName': this.form.name,
        'description': this.form.description,
        'resourceType': this.form.resourceType,
        'resourceName': this.form.resourceName,
        'resourceId': this.form.resource,
        'resourceVerbs': this.form.resourceVerbs.join(','),
        'roleSource': 'placeholder'
      }
      this.resourceTypeListOptions = []
      putRole(data).then(response => {
        if (!response.data) return
        if (response.data.hasOwnProperty('error')) {
          this.$notify({
            title: 'error',
            message: response.data.error,
            type: 'error',
            duration: 2000
          })
          return
        }
        this.$notify({
          title: 'success',
          message: this.$i18n.t('role.creatRoleNotification'),
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
        this.getRoles()
      })
    },
    updateRole() {
      const data = {
        'roleName': this.form.name,
        'description': this.form.description,
        'resourceType': this.form.resourceType,
        'resourceName': this.form.resourceName,
        'resourceId': this.form.resourceId,
        'resourceVerbs': this.form.resourceVerbs.join(','),
        'roleSource': 'placeholder'
      }
      updateRole(data).then(response => {
        if (!response.data) return
        if (response.data.hasOwnProperty('error')) {
          this.$notify({
            title: 'error',
            message: response.data.error,
            type: 'error',
            duration: 2000
          })
          return
        }
        this.$notify({
          title: 'success',
          message: 'Update role success',
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
        this.getRoles()
      })
    },
    deleteRole() {
      const data = {
        'roleName': this.temp.name
      }
      deleteRole(data).then(response => {
        if (!response.data) return
        if (response.data.hasOwnProperty('error')) {
          this.$notify({
            title: 'error',
            message: response.data.error,
            type: 'error',
            duration: 2000
          })
          return
        }
        this.$notify({
          title: 'success',
          message: 'Delete role success',
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
        this.getRoles()
      })
    },
    handleChangeResourceType(row) {
      getResource(row.resourceType).then(response => {
        if (!response.data) return
        this.resourceListOptions = []
        for (var i = 0; i < response.data.data.length; i++) {
          if (row.resourceId === response.data.data[i].id) {
            this.form.resource = response.data.data[i].name
          }
          this.resourceListOptions.push({
            'value': response.data.data[i].id,
            'label': response.data.data[i].name
          })
        }
      })
      getResourceVerbs(row.resourceType).then(response => {
        if (!response.data) return
        this.resourceVerbsListOptions = []
        for (var i = 0; i < response.data.data.length; i++) {
          this.resourceVerbsListOptions.push({
            'value': response.data.data[i],
            'label': response.data.data[i]
          })
        }
      })
    }
  }
}
</script>
