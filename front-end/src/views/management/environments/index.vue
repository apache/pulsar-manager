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
    <el-button v-if="superUser" type="primary" icon="el-icon-plus" @click="handleCreateEnvironment">{{ $t('env.buttonNewEnv') }}</el-button>

    <el-row :gutter="24">
      <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}" style="margin-top:15px">
        <el-table
          v-loading="environmentListLoading"
          :key="environmentTableKey"
          :data="environmentList"
          border
          fit
          highlight-current-row
          style="width: 100%;">
          <el-table-column :label="$t('env.colHeadingEnv')" min-width="50px" align="center">
            <template slot-scope="scope">
              <router-link :to="'#'" class="link-type" @click.native="handleSetEnvironment(scope.row.environment)">
                <span>{{ scope.row.environment }}</span>
              </router-link>
            </template>
          </el-table-column>
          <el-table-column :label="$t('env.colHeadingServiceUrl')" align="center" min-width="100px">
            <template slot-scope="scope">
              <span>{{ scope.row.broker }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('env.colHeadingBookieUrl')" align="center" min-width="100px">
            <template slot-scope="scope">
              <span>{{ scope.row.bookie }}</span>
            </template>
          </el-table-column>
          <el-table-column v-if="superUser" :label="$t('table.actions')" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button type="primary" size="mini" @click="handleUpdateEnvironment(scope.row)">{{ $t('table.edit') }}</el-button>
              <el-button size="mini" type="danger" @click="handleDeleteEnvironment(scope.row)">{{ $t('table.delete') }}</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="30%">
      <el-form ref="form" :rules="rules" :model="form" label-position="top">
        <el-form-item v-if="dialogStatus==='create'" :label="$t('env.newEnvNameLabel')" prop="environment">
          <el-input v-model="form.environment" :placeholder="$t('env.newEnvNamePlaceHolder')"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" :label="$t('env.newEnvServiceUrlLabel')" prop="broker">
          <el-input v-model="form.broker" :placeholder="$t('env.newEnvServiceUrlPlaceHolder')"/>
        </el-form-item>

        <el-form-item v-if="dialogStatus==='create'" :label="$t('env.newEnvBookieUrlLabel')" prop="bookie">
          <el-input v-model="form.bookie" :placeholder="$t('env.newEnvBookieUrlPlaceHolder')"/>
        </el-form-item>

        <el-form-item v-if="dialogStatus==='update'" :label="$t('env.updateEnvNameLabel')">
          <el-tag type="primary" size="medium">{{ form.environment }}</el-tag>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" :label="$t('env.updateEnvServiceUrlLabel')" prop="broker">
          <el-input v-model="form.broker" :placeholder="$t('env.updateEnvServiceUrlPlaceHolder')"/>
        </el-form-item>

        <el-form-item v-if="dialogStatus==='update'" :label="$t('env.updateEnvBookieUrlLabel')" prop="bookie">
          <el-input v-model="form.bookie" :placeholder="$t('env.updateEnvBookieUrlPlaceHolder')"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='delete'">
          <h4>{{ $t('env.deleteEnvDialogText') }}</h4>
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
  import { putEnvironment, fetchEnvironments, deleteEnvironment, updateEnvironment } from '@/api/environments'
  import { setEnvironment } from '@/utils/environment'
  import store from '@/store'

  export default {
    name: 'EnvironmentInfo',
    data() {
      return {
        environmentList: [],
        environmentTableKey: 0,
        environmentListLoading: false,
        textMap: {
          create: this.$i18n.t('env.newEnvDialogCaption'),
          delete: this.$i18n.t('env.deleteEnvDialogCaption'),
          update: this.$i18n.t('env.updateEnvDialogCaption')
        },
        dialogFormVisible: false,
        dialogStatus: '',
        form: {
          environment: '',
          broker: '',
          bookie: ''
        },
        temp: {
          'name': '',
          'broker': '',
          'bookie': ''
        },
        superUser: false,
        roles: [],
        rules: {
          environment: [{ required: true, message: this.$i18n.t('env.envNameIsRequired'), trigger: 'blur' }],
          broker: [{ required: true, message: this.$i18n.t('env.serviceUrlIsRequired'), trigger: 'blur' }],
          bookie: [{ required: true, message: this.$i18n.t('env.bookieUrlIsRequired'), trigger: 'blur' }]
        }
      }
    },
    created() {
      this.getEnvironments()
      this.roles = store.getters && store.getters.roles
      if (this.roles.includes('super')) {
        this.superUser = true
      } else {
        this.superUser = false
      }
    },
    methods: {
      getEnvironments() {
        fetchEnvironments().then(response => {
          if (!response.data) return
          this.environmentList = []
          for (var i = 0; i < response.data.data.length; i++) {
            this.environmentList.push({
              'environment': response.data.data[i].name,
              'broker': response.data.data[i].broker,
              'bookie': response.data.data[i].bookie
            })
          }
        })
      },
      handleCreateEnvironment() {
        this.form.environment = ''
        this.form.broker = ''
        this.dialogFormVisible = true
        this.dialogStatus = 'create'
      },
      handleDeleteEnvironment(row) {
        this.temp.name = row.environment
        this.temp.broker = row.broker
        this.dialogFormVisible = true
        this.dialogStatus = 'delete'
      },
      handleUpdateEnvironment(row) {
        this.form.environment = row.environment
        this.form.broker = row.broker
        this.form.bookie = row.bookie
        this.dialogFormVisible = true
        this.dialogStatus = 'update'
      },
      handleOptions() {
        this.$refs['form'].validate((valid) => {
          if (valid) {
            switch (this.dialogStatus) {
              case 'create':
                this.createEnvironment()
                break
              case 'delete':
                this.deleteEnvironment()
                break
              case 'update':
                this.updateEnvironment()
                break
            }
          }
        })
      },
      createEnvironment() {
        const data = {
          'name': this.form.environment,
          'broker': this.form.broker,
          'bookie': this.form.bookie
        }
        putEnvironment(data).then(response => {
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
            message: this.$i18n.t('env.addEnvSuccessNotification'),
            type: 'success',
            duration: 2000
          })
          this.dialogFormVisible = false
          this.getEnvironments()
        })
      },
      deleteEnvironment() {
        const data = {
          'name': this.temp.name,
          'broker': this.temp.broker
        }
        deleteEnvironment(data).then(response => {
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
            message: this.$i18n.t('env.deleteEnvSuccessNotification'),
            type: 'success',
            duration: 2000
          })
          this.getEnvironments()
          this.dialogFormVisible = false
        })
      },
      updateEnvironment() {
        const data = {
          'name': this.form.environment,
          'broker': this.form.broker,
          'bookie': this.form.bookie
        }
        updateEnvironment(data).then(response => {
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
            message: this.$i18n.t('env.updateEnvSuccessNotification'),
            type: 'success',
            duration: 2000
          })
          this.getEnvironments()
          this.dialogFormVisible = false
        })
      },
      handleSetEnvironment(environment) {
        setEnvironment(environment)
        if (this.roles.includes('super')) {
          this.$router.push({ path: '/management/tenants' })
        } else {
          this.$router.push({ path: '/management/admin/tenants/tenantInfo' })
        }
      }
    }
  }
</script>
