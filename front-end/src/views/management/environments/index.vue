<template>
  <div class="app-container">
    <el-button type="primary" icon="el-icon-plus" @click="handleCreateEnvironment">New Environment</el-button>

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
          <el-table-column label="Environments" min-width="50px" align="center">
            <template slot-scope="scope">
              <router-link :to="'/management/tenants'" class="link-type" @click.native="handleSetEnvironment(scope.row.environment)">
                <span>{{ scope.row.environment }}</span>
              </router-link>
            </template>
          </el-table-column>
          <el-table-column label="broker" align="center" min-width="100px">
            <template slot-scope="scope">
              <span>{{ scope.row.broker }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('table.actions')" align="center" class-name="small-padding fixed-width">
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
        <el-form-item v-if="dialogStatus==='create'" label="New Environment" prop="environment">
          <el-input v-model="form.environment" placeholder="Please input name"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='create'" label="New Broker" prop="broker">
          <el-input v-model="form.broker" placeholder="Please input broker"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" label="Environment">
          <span>{{ form.environment }}</span>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='update'" label="Broker" prop="broker">
          <el-input v-model="form.broker" placeholder="Please input broker"/>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='delete'">
          <h4>Are you sure you want to delete this environment?</h4>
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

export default {
  name: 'EnvironmentInfo',
  data() {
    return {
      environmentList: [],
      environmentTableKey: 0,
      environmentListLoading: false,
      textMap: {
        create: 'New Environment',
        delete: 'Delete Environment',
        update: 'Update Environemnt'
      },
      dialogFormVisible: false,
      dialogStatus: '',
      form: {
        environment: '',
        broker: ''
      },
      temp: {
        'name': '',
        'broker': ''
      },
      rules: {
        environment: [{ required: true, message: 'Environment Name is required', trigger: 'blur' }],
        broker: [{ required: true, message: 'Broker is required', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getEnvironments()
  },
  methods: {
    getEnvironments() {
      fetchEnvironments().then(response => {
        if (!response.data) return
        this.environmentList = []
        for (var i = 0; i < response.data.data.length; i++) {
          this.environmentList.push({
            'environment': response.data.data[i].name,
            'broker': response.data.data[i].broker
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
        'broker': this.form.broker
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
          message: 'Add environment success',
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
          message: 'Delete environment success',
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
        'broker': this.form.broker
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
          message: 'Update environment success',
          type: 'success',
          duration: 2000
        })
        this.getEnvironments()
        this.dialogFormVisible = false
      })
    },
    handleSetEnvironment(environment) {
      setEnvironment(environment)
    }
  }
}
</script>
