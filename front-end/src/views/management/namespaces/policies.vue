<template>
  <div class="app-container">

    <el-row :gutter="8">
      <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 12}" :xl="{span: 12}" style="padding-right:8px;margin-bottom:30px;">
        <el-table
          :key="tableKey"
          :data="list"
          border
          fit
          highlight-current-row
          style="width: 100%;">
          <el-table-column :label="$t('table.policies')" min-width="150px" align="center">
            <template slot-scope="scope">
              <span class="link-type" @click="getNamespacePolicie()">{{ scope.row.policies }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('table.description')" min-width="150px" align="center">
            <template slot-scope="scope">
              <span>{{ scope.row.description }}</span>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="pagePolicies" />
      </el-col>
      <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 12}" :xl="{span: 12}" style="margin-bottom:30px;">
        <jsonEditor :value="jsonValue"/>
        <template>
          <el-button type="primary" style="float: right; margin-top: 10px" @click="handleDelete()">{{ $t('table.update') }}
          </el-button>
        </template>
      </el-col>
    </el-row>

    <el-dialog :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :rules="rules" :model="temp" label-position="left" label-width="70px" style="width: 400px; margin-left:50px;">
        <el-form-item :label="$t('table.namespace')" prop="namespace">
          <el-input v-model="temp.namespace"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">{{ $t('table.cancel') }}</el-button>
        <el-button type="primary" @click="createData()">{{ $t('table.confirm') }}</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { updateTenant } from '@/api/tenants'
import waves from '@/directive/waves' // Waves directive
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import JsonEditor from '@/components/JsonEditor'

export default {
  name: 'Namespaces',
  components: {
    Pagination,
    JsonEditor
  },
  directives: { waves },
  data() {
    return {
      tableKey: 0,
      jsonValue: {},
      tenant: '',
      namespace: '',
      list: null,
      policiesList: policiesList,
      total: 0,
      listQuery: {
        page: 1,
        limit: 10
      },
      temp: {
        namespace: ''
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: 'Edit',
        create: 'Create'
      },
      rules: {
        namespace: [{ required: true, message: 'namespace is required', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.tenant = this.$route.params && this.$route.params.tenant
    this.namespace = this.$route.params && this.$route.params.namespace
    this.pagePolicies()
    // this.getNamespaces()
    // this.getNamespacePolicies()
  },
  methods: {
    pagePolicies() {
      this.total = this.policiesList.length
      this.list = this.policiesList.slice((this.listQuery.page - 1) * 10, this.listQuery.page * 10)
    },
    handleUpdate(row) {
      this.temp = Object.assign({}, row) // copy obj
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    getNamespacePolicie() {

    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const tempData = Object.assign({}, this.temp)
          updateTenant(this.temp.tenant, tempData).then(() => {
            for (const v of this.list) {
              if (v.tenant === this.temp.tenant) {
                const index = this.list.indexOf(v)
                this.list.splice(index, 1, this.temp)
                break
              }
            }
            this.dialogFormVisible = false
            this.$notify({
              title: 'success',
              message: 'update success',
              type: 'success',
              duration: 2000
            })
          })
        }
      })
    },
    handleDelete(row) {
      this.$notify({
        title: 'success',
        message: 'delete success',
        type: 'success',
        duration: 2000
      })
      const index = this.list.indexOf(row)
      this.list.splice(index, 1)
    }
  }
}
const policiesList = [
  {
    'policies': 'maxConsumersPerSubscription',
    'description': 'maxConsumersPerSubscription'
  },
  {
    'policies': 'compactionThreshold',
    'description': 'compactionThreshold'
  },
  {
    'policies': 'antiAffinity',
    'description': 'antiAffinity'
  },
  {
    'policies': 'backlogQuota',
    'description': 'backlogQuota'
  },
  {
    'policies': 'clearBacklog',
    'description': 'clearBacklog'
  },
  {
    'policies': 'deduplication',
    'description': 'deduplication'
  },
  {
    'policies': 'dispatchRate',
    'description': 'dispatchRate'
  },
  {
    'policies': 'encryptionRequired',
    'description': 'encryptionRequired'
  },
  {
    'policies': 'maxConsumersPerTopic',
    'description': 'maxConsumersPerTopic'
  },
  {
    'policies': 'maxProducersPerTopic',
    'description': 'maxProducersPerTopic'
  },
  {
    'policies': 'messageTTL',
    'description': 'messageTTL'
  },
  {
    'policies': 'offloadDeletionLagMs',
    'description': 'offloadDeletionLagMs'
  },
  {
    'policies': 'offloadThreshold',
    'description': 'offloadThreshold'
  },
  {
    'policies': 'permissions',
    'description': 'permissions'
  },
  {
    'policies': 'persistence',
    'description': 'persistence'
  },
  {
    'policies': 'replication',
    'description': 'replication'
  },
  {
    'policies': 'retention',
    'description': 'retention'
  },
  {
    'policies': 'schemaAutoUpdateCompatibilityStrategy',
    'description': 'schemaAutoUpdateCompatibilityStrategy'
  },
  {
    'policies': 'subscribeRate',
    'description': 'subscribeRate'
  },
  {
    'policies': 'subscriptionAuthMode',
    'description': 'subscriptionAuthMode'
  },
  {
    'policies': 'subscriptionDispatchRate',
    'description': 'subscriptionDispatchRate'
  },
  {
    'policies': 'unload',
    'description': 'unload'
  },
  {
    'policies': 'unsubscribe',
    'description': 'unsubscribe'
  },
  {
    'policies': 'bundle',
    'description': 'bundle'
  }
]
</script>

