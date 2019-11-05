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
    <el-button type="primary" icon="el-icon-plus" @click="handleCreateToken">{{ $t('token.buttonNewToken') }}</el-button>

    <el-row :gutter="24">
      <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}" style="margin-top:15px">
        <el-table
          v-loading="tokenListLoading"
          :key="tokenTableKey"
          :data="tokenList"
          border
          fit
          highlight-current-row
          style="width: 100%;">
          <el-table-column :label="$t('token.colHeadingRole')" min-width="50px" align="center">
            <template slot-scope="scope">
              <span>{{ scope.row.role }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('token.colHeadingToken')" align="center" min-width="100px">
            <template slot-scope="scope">
              <el-link @click="handleGetToken(scope.row)">{{ scope.row.token }}<i class="el-icon-view el-icon--right"/></el-link>
            </template>
          </el-table-column>
          <el-table-column :label="$t('token.colHeadingDesc')" align="center" min-width="100px">
            <template slot-scope="scope">
              <span>{{ scope.row.description }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('table.actions')" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button type="primary" size="mini" @click="handleUpdateToken(scope.row)">{{ $t('table.edit') }}</el-button>
              <el-button size="mini" type="danger" @click="handleDeleteToken(scope.row)">{{ $t('table.delete') }}</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="30%">
      <el-form ref="form" :rules="rules" :model="form" label-position="top">
        <el-form-item v-if="dialogStatus==='create'" :label="$t('token.colHeadingRole')" prop="role">
          <el-input v-model="form.role" :placeholder="$t('token.newTokenRolePlaceholder')"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" :label="$t('token.colHeadingDesc')">
          <el-input :rows="2" v-model="form.description" :placeholder="$t('token.newTokenDescPlaceholder')" type="textarea"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" :label="$t('token.colHeadingRole')">
          <el-tag type="primary" size="medium">{{ form.role }}</el-tag>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" :label="$t('token.colHeadingDesc')">
          <el-input v-model="form.description" :placeholder="$t('token.newTokenDescPlaceholder')" type="textarea"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='delete'">
          <h4>Delete Role {{ form.role }}</h4>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='get'" :label="$t('token.colHeadingRole')">
          <el-tag type="primary" size="medium">{{ form.role }}</el-tag>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='get'" :label="$t('token.colHeadingToken')">
          <span type="primary" size="medium">{{ form.token }}</span>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='get'" :label="$t('token.colHeadingDesc')">
          <span>{{ form.description }}</span>
        </el-form-item>
        <el-form-item v-if="dialogStatus!=='get'">
          <el-button type="primary" @click="handleOptions()">{{ $t('table.confirm') }}</el-button>
          <el-button @click="dialogFormVisible = false">{{ $t('table.cancel') }}</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import { putToken, fetchTokens, updateToken, deleteToken, getToken } from '@/api/tokens'

export default {
  name: 'TokensInfo',
  data() {
    return {
      tokenList: [],
      tokenTableKey: 0,
      tokenListLoading: false,
      textMap: {
        create: 'New Token',
        delete: 'Delete Token',
        update: 'Update Token'
      },
      dialogFormVisible: false,
      dialogStatus: '',
      form: {
        token: '',
        role: '',
        description: ''
      },
      temp: {
        'token': '',
        'role': '',
        'description': ''
      },
      description: '',
      rules: {
        token: [{ required: true, message: this.$i18n.t('token.newTokenRequiredMessage'), trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getTokens()
  },
  methods: {
    getTokens() {
      fetchTokens().then(response => {
        if (!response.data) return
        this.tokenList = []
        for (var i = 0; i < response.data.data.length; i++) {
          this.tokenList.push({
            'token': 'view',
            'role': response.data.data[i].role,
            'description': response.data.data[i].description
          })
        }
      })
    },
    handleCreateToken() {
      this.form.token = ''
      this.form.role = ''
      this.form.description = ''
      this.dialogFormVisible = true
      this.dialogStatus = 'create'
    },
    handleDeleteToken(row) {
      this.temp.token = row.token
      this.temp.role = row.role
      this.temp.description = row.description
      this.dialogFormVisible = true
      this.dialogStatus = 'delete'
    },
    handleUpdateToken(row) {
      this.form.role = row.role
      this.form.description = row.description
      this.dialogFormVisible = true
      this.dialogStatus = 'update'
    },
    handleOptions() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          switch (this.dialogStatus) {
            case 'create':
              this.createToken()
              break
            case 'delete':
              this.deleteToken()
              break
            case 'update':
              this.updateToken()
              break
          }
        }
      })
    },
    createToken() {
      const data = {
        'role': this.form.role,
        'token': '',
        'description': this.form.description
      }
      putToken(data).then(response => {
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
          message: this.$i18n.t('token.addTokenSuccessNotification'),
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
        this.getTokens()
      })
    },
    deleteToken() {
      deleteToken(this.temp.role).then(response => {
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
          message: this.$i18n.t('token.deleteTokenSuccessNotification'),
          type: 'success',
          duration: 2000
        })
        this.getTokens()
        this.dialogFormVisible = false
      })
    },
    updateToken() {
      const data = {
        'role': this.form.role,
        'description': this.form.description
      }
      updateToken(data).then(response => {
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
          message: this.$i18n.t('token.updateTokenSucccessNotification'),
          type: 'success',
          duration: 2000
        })
        this.getTokens()
        this.dialogFormVisible = false
      })
    },
    handleGetToken(row) {
      getToken(row.role).then(response => {
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
        this.form.token = response.data.token
        this.form.role = response.data.role
        this.form.description = response.data.description
        this.dialogFormVisible = true
        this.dialogStatus = 'get'
      })
    }
  }
}
</script>
