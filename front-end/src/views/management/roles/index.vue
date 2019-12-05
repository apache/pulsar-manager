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
    <el-button type="primary" icon="el-icon-plus" @click="handleCreateRole">Create a role</el-button>

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
          <el-table-column label="Role Name" min-width="50px" align="center">
            <template slot-scope="scope">
              <span>{{ scope.row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column label="Role Description" align="center" min-width="100px">
            <template slot-scope="scope">
              <span>{{ scope.row.description }}</span>
            </template>
          </el-table-column>
          <el-table-column label="Resource Type" align="center" min-width="100px">
            <template slot-scope="scope">
              <span>{{ scope.row.resourceType }}</span>
            </template>
          </el-table-column>
          <el-table-column label="Resource Name" align="center" min-width="100px">
            <template slot-scope="scope">
              <span>{{ scope.row.resourceName }}</span>
            </template>
          </el-table-column>
          <el-table-column label="Resource Verbs" align="center" min-width="100px">
            <template slot-scope="scope">
              <span>{{ scope.row.resourceVerbs }}</span>
            </template>
          </el-table-column>
          <el-table-column v-if="superUser" label="Role Source" align="center" min-width="100px">
            <template slot-scope="scope">
              <span>{{ scope.row.roleSource }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('table.actions')" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button type="primary" size="mini" @click="handleUpdateRole(scope.row)">{{ $t('table.edit') }}</el-button>
              <el-button size="mini" type="danger" @click="handleDeleteRole(scope.row)">{{ $t('table.delete') }}</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="30%">
      <el-form ref="form" :rules="rules" :model="form" label-position="top">
        <el-form-item v-if="dialogStatus==='create'" label="Role Name" prop="name">
          <el-input v-model="form.name" placeholder="Please input role name"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" label="Resource Name">
          <el-input :rows="2" v-model="form.description" placeholder="Please input role name"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" label="Resource Type">
          <el-select
            v-model="form.resourceType"
            placeholder="Please select resource type"
            style="width:100%"
            @change="handleChangeResourceType(form.resourceType)">
            <el-option
              v-for="item in resourceTypeListOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" label="Resource">
          <el-select v-model="form.resource" placeholder="Please select resource" style="width:100%">
            <el-option
              v-for="item in resourceListOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" label="Resource Verbs">
          <el-select
            v-model="form.resourceVerbs"
            multiple
            placeholder="Please select role verbs"
            style="width:100%">
            <el-option
              v-for="item in resourceVerbsListOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" label="Role Description">
          <el-input :rows="2" v-model="form.description" placeholder="Please input role description" type="textarea"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" label="Role Name">
          <span>{{ form.name }} </span>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" label="Resource Name">
          <el-input :rows="2" v-model="form.description" placeholder="Please input role name"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" label="Resource Type">
          <el-select v-model="resourceTypeValue" placeholder="Please select resource type" style="width:100%">
            <el-option
              v-for="item in resourceTypeListOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" label="Resource">
          <el-select v-model="form.resource" placeholder="Please select resource" style="width:100%">
            <el-option
              v-for="item in resourceListOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" label="Resource Verbs" style="width:100%">
          <el-select
            v-model="form.resourceVerbs"
            multiple
            placeholder="Please select role verbs">
            <el-option
              v-for="item in resourceVerbsListOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" label="Role Description">
          <el-input :rows="2" v-model="form.description" placeholder="Please input role description" type="textarea"/>
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
import { fetchRoles, putRole, getResourceType, getResource, getResourceVerbs } from '@/api/roles'

export default {
  name: 'RolesInfo',
  data() {
    return {
      roleList: [],
      roleTableKey: 0,
      roleListLoading: false,
      textMap: {
        create: 'New Role',
        delete: 'Delete Role',
        update: 'Update Role'
      },
      dialogFormVisible: false,
      dialogStatus: '',
      form: {
        name: '',
        description: '',
        resourceType: '',
        resourceName: '',
        resourceVerbs: [],
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
        name: [{ required: true, message: 'The name can not is empty', trigger: 'blur' }]
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
    handleCreateRole() {
      this.form.name = ''
      this.form.description = ''
      this.form.resourceType = ''
      this.form.resourceName = ''
      this.form.resourceVerbs = ''
      this.dialogFormVisible = true
      this.dialogStatus = 'create'
    },
    handleDeleteRole(row) {
      this.temp.name = row.name
      this.temp.description = row.description
      this.temp.resourceType = row.resourceType
      this.temp.resourceName = row.resourceName
      this.temp.resourceVerbs = row.resourceVerbs
      this.dialogFormVisible = true
      this.dialogStatus = 'delete'
    },
    handleUpdateRole(row) {
      this.form.name = row.name
      this.form.description = row.description
      this.form.resourceType = row.resourceType
      this.form.resourceName = row.resourceName
      this.form.resourceVerbs = row.resourceVerbs
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
        'resourceName': this.form.sourceName,
        'resourceVerbs': this.form.resourceVerbs,
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
          message: 'Create role success',
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
        this.getRoles()
      })
    },
    handleChangeResourceType(resourceType) {
      getResource(resourceType).then(response => {
        if (!response.data) return
        this.resourceListOptions = []
        for (var i = 0; i < response.data.data.length; i++) {
          this.resourceListOptions.push({
            'value': response.data.data[i],
            'label': response.data.data[i]
          })
        }
      })
      getResourceVerbs(resourceType).then(response => {
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
