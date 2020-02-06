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
    <el-button type="primary" icon="el-icon-plus" @click="handleCreateUser">{{ $t('user.buttonNewUser') }}</el-button>

    <el-row :gutter="24">
      <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}" style="margin-top:15px">
        <el-table
          v-loading="userListLoading"
          :key="userTableKey"
          :data="userList"
          border
          fit
          highlight-current-row
          style="width: 100%;">
          <el-table-column :label="$t('user.colUserName')" min-width="50px" align="center">
            <template slot-scope="scope">
              <span>{{ scope.row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('user.colUserDesc')" align="center" min-width="100px">
            <template slot-scope="scope">
              <span>{{ scope.row.description }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('user.colUserEmail')" align="center" min-width="100px">
            <template slot-scope="scope">
              <span>{{ scope.row.email }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('user.colUserPhone')" align="center" min-width="100px">
            <template slot-scope="scope">
              <span>{{ scope.row.phoneNumber }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('user.colUserLocation')" align="center" min-width="100px">
            <template slot-scope="scope">
              <span>{{ scope.row.location }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('user.colUserCompany')" align="center" min-width="100px">
            <template slot-scope="scope">
              <span>{{ scope.row.company }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('table.actions')" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button type="primary" size="mini" @click="handleUpdateUser(scope.row)">{{ $t('table.edit') }}</el-button>
              <el-button size="mini" type="danger" @click="handleDeleteUser(scope.row)">{{ $t('table.delete') }}</el-button>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getUsers" />
      </el-col>
    </el-row>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="30%">
      <el-form ref="form" :rules="rules" :model="form" label-position="top">
        <el-form-item v-if="dialogStatus==='create'" :label="$t('user.colUserName')" prop="name">
          <el-input v-model="form.name" :placeholder="$t('user.userNamePlaceHolder')"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" :label="$t('user.colUserDesc')">
          <el-input :rows="2" v-model="form.description" :placeholder="$t('user.userDescPlaceHolder')" type="textarea"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" :label="$t('user.colUserEmail')" prop="email">
          <el-input :rows="2" v-model="form.email" :placeholder="$t('user.userEmailPlaceHolder')"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" :label="$t('user.colUserPhone')">
          <el-input :rows="2" v-model="form.phoneNumber" :placeholder="$t('user.userPhonePlaceHolder')"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" :label="$t('user.colUserLocation')">
          <el-input :rows="2" v-model="form.location" :placeholder="$t('user.userLocationPlaceHolder')"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" :label="$t('user.colUserCompany')">
          <el-input :rows="2" v-model="form.company" :placeholder="$t('user.userCompnayPlaceHolder')"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" :label="$t('uesr.colUserPassword')" prop="password">
          <el-input :rows="2" v-model="form.password" :placeholder="$t('user.userPasswordPlaceHolder')" type="password"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" :label="$t('user.colUserPasswordRepeat')" prop="repeatPassword">
          <el-input :rows="2" v-model="form.repeatPassword" :placeholder="$t('user.userPasswordPlaceHolder')" type="password"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" :label="$t('user.colUserName')">
          <span>{{ form.name }}</span>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" :label="$t('user.colUserDesc')">
          <el-input :rows="2" v-model="form.description" :placeholder="$t('user.userDescPlaceHolder')" type="textarea"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" :label="$t('user.colUserEmail')" prop="email">
          <el-input :rows="2" v-model="form.email" :placeholder="$t('user.userEamilPlaceHolder')"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" :label="$t('user.colUserPhone')">
          <el-input :rows="2" v-model="form.phoneNumber" :placeholder="$t('user.userPhonePlaceHolder')"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" :label="$t('user.colUserLocation')">
          <el-input :rows="2" v-model="form.location" :placeholder="$t('user.userLocationPlaceHolder')"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" :label="$t('user.colUserCompany')">
          <el-input :rows="2" v-model="form.company" :placeholder="$t('user.userCompnayPlaceHolder')"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='delete'">
          <h4>Delete User {{ form.name }}</h4>
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
import { fetchUsers, putUser, deleteUser, updateUser } from '@/api/users'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'UsersInfo',
  components: {
    Pagination
  },
  data() {
    return {
      userList: [],
      userTableKey: 0,
      userListLoading: false,
      textMap: {
        create: this.$i18n.t('user.newUser'),
        delete: this.$i18n.t('user.deleteUser'),
        update: this.$i18n.t('user.updateUser')
      },
      dialogFormVisible: false,
      dialogStatus: '',
      form: {
        name: '',
        description: '',
        email: '',
        phoneNumber: '',
        location: '',
        company: '',
        password: '',
        repeatPassword: ''
      },
      resourceTypeListOptions: [],
      resourceTypeValue: '',
      temp: {
        'name': '',
        'description': '',
        'phoneNumber': '',
        'location': '',
        'company': '',
        'password': ''
      },
      description: '',
      rules: {
        name: [{ required: true, message: this.$i18n.t('user.userNameIsRequired'), trigger: 'blur' }],
        email: [{ required: true, message: this.$i18n.t('user.userEmailIsRequired'), trigger: 'blur' }],
        password: [{ required: true, message: this.$i18n.t('user.userPasswordIsRequired'), trigger: 'blur' }],
        repeatPassword: [{ required: true, message: this.$i18n.t('user.userPasswordIsRequired'), trigger: 'blur' }]
      },
      total: 0,
      listQuery: {
        page: 1,
        limit: 10
      }
    }
  },
  created() {
    this.getUsers()
  },
  methods: {
    getUsers() {
      fetchUsers(this.listQuery.page, this.listQuery.limit).then(response => {
        if (!response.data) return
        this.userList = []
        this.total = response.data.total
        for (var i = 0; i < response.data.data.length; i++) {
          this.userList.push({
            'name': response.data.data[i].name,
            'description': response.data.data[i].description,
            'email': response.data.data[i].email,
            'phoneNumber': response.data.data[i].phoneNumber,
            'location': response.data.data[i].location,
            'company': response.data.data[i].company
          })
        }
      })
    },
    handleCreateUser() {
      this.form.name = ''
      this.form.description = ''
      this.form.email = ''
      this.form.phoneNumber = ''
      this.form.location = ''
      this.form.company = ''
      this.form.password = ''
      this.dialogFormVisible = true
      this.dialogStatus = 'create'
    },
    handleDeleteUser(row) {
      this.form.name = row.name
      this.temp.name = row.name
      this.temp.description = row.description
      this.temp.email = row.email
      this.temp.phoneNumber = row.phoneNumber
      this.temp.location = row.location
      this.temp.company = row.company
      this.dialogFormVisible = true
      this.dialogStatus = 'delete'
    },
    handleUpdateUser(row) {
      this.form.name = row.name
      this.form.description = row.description
      this.form.email = row.email
      this.form.phoneNumber = row.phoneNumber
      this.form.location = row.location
      this.form.company = row.company
      this.dialogFormVisible = true
      this.dialogStatus = 'update'
    },
    handleOptions() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          switch (this.dialogStatus) {
            case 'create':
              this.createUser()
              break
            case 'delete':
              this.deleteUser()
              break
            case 'update':
              this.updateUser()
              break
          }
        }
      })
    },
    createUser() {
      const data = {
        'name': this.form.name,
        'description': this.form.description,
        'email': this.form.email,
        'phoneNumber': this.form.phoneNumber,
        'location': this.form.location,
        'company': this.form.company,
        'password': this.form.password
      }
      if (this.form.password !== this.form.repeatPassword) {
        this.$notify({
          title: 'error',
          message: this.$i18n.t('uesr.passwordNotification'),
          type: 'error',
          duration: 2000
        })
        return
      }
      putUser(data).then(response => {
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
          message: this.$i18n.t('user.creatUserNotification'),
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
        this.getUsers()
      })
    },
    deleteUser() {
      const data = {
        'name': this.temp.name,
        'description': this.temp.description,
        'email': this.temp.email,
        'phoneNumber': this.temp.phoneNumber,
        'location': this.temp.location,
        'company': this.temp.company,
        'password': this.temp.password
      }
      deleteUser(data).then(response => {
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
          message: this.$i18n.t('user.deleteUserNotification'),
          type: 'success',
          duration: 2000
        })
        this.getUsers()
        this.dialogFormVisible = false
      })
    },
    updateUser() {
      const data = {
        'name': this.form.name,
        'description': this.form.description,
        'email': this.form.email,
        'phoneNumber': this.form.phoneNumber,
        'location': this.form.location,
        'company': this.form.company,
        'password': this.form.password
      }
      updateUser(data).then(response => {
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
          message: this.$i18n.t('user.updateUserNotification'),
          type: 'success',
          duration: 2000
        })
        this.getUsers()
        this.dialogFormVisible = false
      })
    }
  }
}
</script>
