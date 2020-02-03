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
    <el-button type="primary" icon="el-icon-plus" @click="handleCreateRoleBinding">Create RoleBinding</el-button>

    <el-row :gutter="24">
      <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}" style="margin-top:15px">
        <el-table
          v-loading="roleBindingListLoading"
          :key="roleBindingTableKey"
          :data="roleBindingList"
          border
          fit
          highlight-current-row
          style="width: 100%;">
          <el-table-column label="Role Binding Name" min-width="50px" align="center">
            <template slot-scope="scope">
              <span>{{ scope.row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column label="Role name" align="center" min-width="100px">
            <template slot-scope="scope">
              <span>{{ scope.row.roleName }}</span>
            </template>
          </el-table-column>
          <el-table-column label="User Name" align="center" min-width="100px">
            <template slot-scope="scope">
              <span>{{ scope.row.userName }}</span>
            </template>
          </el-table-column>
          <el-table-column label="Binding Description" align="center" min-width="100px">
            <template slot-scope="scope">
              <span>{{ scope.row.description }}</span>
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
        <el-form-item v-if="dialogStatus==='create'" label="Name" prop="name">
          <el-input v-model="form.name" placeholder="Please input name"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" label="Description">
          <el-input :rows="2" v-model="form.description" placeholder="Please input description" type="textarea"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" label="Role Name" prop="roleName">
          <el-select
            v-model="form.roleName"
            placeholder="Please select role name"
            style="width:100%">
            <el-option
              v-for="item in roleListOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" label="User Name" prop="userName">
          <el-input :rows="2" v-model="form.userName" placeholder="Please input user name"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" label="Role binding name">
          <span>{{ form.name }} </span>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" label="Role Name">
          <el-select v-model="form.roleName" placeholder="Please select role" style="width:100%">
            <el-option
              v-for="item in roleListOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" label="User Name">
          <el-input :rows="2" v-model="form.userName" placeholder="Please input user name"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" label="Description">
          <el-input :rows="2" v-model="form.description" placeholder="Please input description" type="textarea"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='delete'">
          <h4>Delete Role Binding {{ form.name }}</h4>
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
  fetchRoleBinding,
  putRoleBinding,
  updateRoleBinding,
  deleteRoleBinding
} from '@/api/roleBinding'

import { fetchRoles } from '@/api/roles'

export default {
  name: 'RoleBinding',
  data() {
    return {
      roleBindingList: [],
      roleBindingTableKey: 0,
      roleBindingListLoading: false,
      textMap: {
        create: 'New role binding',
        delete: 'Delete role binding',
        update: 'Update role binding'
      },
      dialogFormVisible: false,
      dialogStatus: '',
      form: {
        name: '',
        description: '',
        roleName: '',
        roleId: 0,
        userName: '',
        userId: 0
      },
      roleListOptions: [],
      roleNameValue: '',
      temp: {
        'name': '',
        'description': '',
        'roleName': '',
        'roleId': 0,
        'userName': '',
        'userId': 0
      },
      superUser: false,
      description: '',
      rules: {
        name: [{ required: true, message: 'Name is required', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getRoleBinding()
  },
  methods: {
    getRoleBinding() {
      fetchRoleBinding().then(response => {
        if (!response.data) return
        this.roleBindingList = []
        for (var i = 0; i < response.data.data.length; i++) {
          this.roleBindingList.push({
            'name': response.data.data[i].name,
            'description': response.data.data[i].description,
            'roleName': response.data.data[i].roleName,
            'roleId': response.data.data[i].roleId,
            'userName': response.data.data[i].userName,
            'userId': response.data.data[i].userId
          })
        }
      })
    },
    handleCreateRoleBinding() {
      this.form.name = ''
      this.form.description = ''
      this.form.roleName = ''
      this.form.roleId = 0
      this.form.userName = ''
      this.form.userId = 0
      this.dialogFormVisible = true
      this.handleGetRoles()
      this.dialogStatus = 'create'
    },
    handleDeleteRole(row) {
      this.temp.name = row.name
      this.temp.roleId = row.roleId
      this.temp.userId = row.userId
      this.dialogFormVisible = true
      this.dialogStatus = 'delete'
    },
    handleUpdateRole(row) {
      this.form.name = row.name
      this.form.description = row.description
      this.form.roleName = row.roleName
      this.form.roleId = row.roleId
      this.form.userName = row.userName
      this.form.userId = row.userId
      this.handleGetRoles()
      this.dialogFormVisible = true
      this.dialogStatus = 'update'
    },
    handleOptions() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          switch (this.dialogStatus) {
            case 'create':
              this.createRoleBinding()
              break
            case 'delete':
              this.deleteRoleBinding()
              break
            case 'update':
              this.updateRoleBinding()
              break
          }
        }
      })
    },
    createRoleBinding() {
      const data = {
        'name': this.form.name,
        'description': this.form.description,
        'roleId': this.form.roleId,
        'userId': 0,
        'roleSource': 'placeholder'
      }
      this.roleBindingList = []
      putRoleBinding(this.form.roleName, this.form.userName, data).then(response => {
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
          message: 'Create role binding success',
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
        this.getRoleBinding()
      })
    },
    updateRoleBinding() {
      const data = {
        'description': this.form.description,
        'roleId': this.form.roleId,
        'userId': this.form.userId,
        'name': this.form.name
      }
      updateRoleBinding(this.form.roleName, this.form.userName, data).then(response => {
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
          message: 'Update role binding success',
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
        this.getRoleBinding()
      })
    },
    deleteRoleBinding() {
      const data = {
        'name': this.temp.name,
        'roleId': this.temp.roleId,
        'userId': this.temp.userId
      }
      deleteRoleBinding(data).then(response => {
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
        this.getRoleBinding()
      })
    },
    handleGetRoles() {
      fetchRoles().then(response => {
        if (!response.data) return
        this.roleListOptions = []
        for (var i = 0; i < response.data.data.length; i++) {
          this.roleListOptions.push({
            'value': response.data.data[i].roleName,
            'label': response.data.data[i].roleName
          })
        }
      })
    }
  }
}
</script>
