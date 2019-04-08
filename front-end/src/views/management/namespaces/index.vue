<template>
  <div class="app-container">
    <div class="createPost-container">
      <el-form ref="postForm" :model="postForm" class="form-container">
        <div class="createPost-main-container">
          <el-row>
            <el-col :span="12">
              <div class="postInfo-container">
                <el-row>
                  <el-col :span="8">
                    <el-form-item class="postInfo-container-item">
                      <el-select v-model="postForm.tenant" placeholder="select tenant" @change="getNamespacesList(postForm.tenant)">
                        <el-option v-for="(item,index) in tenantsListOptions" :key="item+index" :label="item" :value="item"/>
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>
              </div>
            </el-col>
          </el-row>
        </div>
      </el-form>
    </div>
    <div class="filter-container">
      <el-input :placeholder="$t('table.namespace')" v-model="listQuery.namespace" style="width: 200px;" class="filter-item" @keyup.enter.native="handleFilter"/>
      <el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">{{ $t('table.search') }}</el-button>
      <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleCreate">{{ $t('table.add') }}</el-button>
      <el-autocomplete
        v-model="postForm.otherOptions"
        :fetch-suggestions="querySearch"
        class="filter-item inline-input"
        style="margin-left: 10px; width:400px"
        placeholder="select options"
        clearable
        @select="moreListOptionsChange"
      />
    </div>

    <div>
      <el-row :gutter="8">
        <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 14}" :xl="{span: 14}" style="padding-right:8px;margin-bottom:30px;">
          <el-table
            v-loading="listLoading"
            :key="tableKey"
            :data="list"
            border
            fit
            highlight-current-row
            style="width: 100%;"
            @row-click="getCurrentRow">
            <el-table-column :label="$t('table.namespace')" min-width="50px" align="center">
              <template slot-scope="scope">
                <router-link :to="'/management/tenantNamespace/' + scope.row.namespace" class="link-type">
                  <span>{{ scope.row.namespace }}</span>
                </router-link>
              </template>
            </el-table-column>
            <el-table-column :label="$t('table.policies')" min-width="30px" align="center">
              <template slot-scope="scope">
                <span class="link-type" @click="getNamespacePolicies(scope.row.namespace)">stats</span>
              </template>
            </el-table-column>
            <el-table-column :label="$t('table.actions')" align="center" width="150" class-name="small-padding fixed-width">
              <template slot-scope="scope">
                <el-button v-if="scope.row.status!='deleted'" size="mini" type="danger" @click="handleDelete(scope.row)">{{ $t('table.delete') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getNamespaces" />
        </el-col>
        <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 10}" :xl="{span: 10}" style="margin-bottom:30px;">
          <jsonEditor :value="jsonValue"/>
        </el-col>
      </el-row>

    </div>

    <el-dialog :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :rules="rules" :model="temp" label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
        <div v-if="dialogStatus==='create'">
          <el-form-item :label="$t('table.namespace')" prop="namespace">
            <el-input v-model="temp.namespace"/>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='grant-permission'">
          <el-form-item :label="$t('table.namespace')" prop="namespace">
            <span>{{ currentNamespace }}</span>
          </el-form-item>
          <el-form-item :label="$t('table.grant')" prop="grant">
            <el-drag-select v-model="temp.actions" style="width:300px;" multiple placeholder="Please select">
              <el-option v-for="item in actionsListOptions" :label="item.label" :value="item.value" :key="item.value" />
            </el-drag-select>
          </el-form-item>
          <el-form-item :label="$t('table.role')" prop="role">
            <el-input v-model="temp.role" style="width:300px;" />
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='revoke-permission'">
          <el-form-item :label="$t('table.namespace')" prop="namespace">
            <span>{{ currentNamespace }}</span>
          </el-form-item>
          <el-form-item :label="$t('table.role')" prop="role">
            <el-input v-model="temp.role"/>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='set-clusters'">
          <el-form-item :label="$t('table.namespace')" prop="namespace">
            <span>{{ currentNamespace }}</span>
          </el-form-item>
          <el-form-item :label="$t('table.clusters')" prop="clusters">
            <el-drag-select v-model="temp.clusters" style="width:330px;" multiple placeholder="Please select">
              <el-option v-for="item in clusterListOptions" :label="item.label" :value="item.value" :key="item.value" />
            </el-drag-select>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='set-backlog-quota'">
          <el-form-item :label="$t('table.namespace')" prop="namespace">
            <span>{{ currentNamespace }}</span>
          </el-form-item>
          <el-form-item :label="$t('table.limit')" prop="limit">
            <el-input v-model="temp.limit"/>
          </el-form-item>
          <el-form-item :label="$t('table.policies')" prop="policies">
            <el-select v-model="temp.policy" style="width:330px;" placeholder="Please select">
              <el-option v-for="item in policiesListOptions" :label="item.label" :value="item.value" :key="item.value" />
            </el-select>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='remove-backlog-quota'">
          <el-form-item :label="$t('table.namespace')" prop="namespace">
            <span>{{ currentNamespace }}</span>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='set-persistence'">
          <el-form-item :label="$t('table.namespace')" prop="namespace">
            <span>{{ currentNamespace }}</span>
          </el-form-item>
          <el-form-item label="ackQuorum" prop="ackQuorum">
            <el-input v-model="temp.ackQuorum"/>
          </el-form-item>
          <el-form-item label="ensemble" prop="ensemble">
            <el-input v-model="temp.ensemble"/>
          </el-form-item>
          <el-form-item label="writeQuorum" prop="writeQuorum">
            <el-input v-model="temp.writeQuorum"/>
          </el-form-item>
          <el-form-item label="deleteMaxRate" prop="deleteMaxRate">
            <el-input v-model="temp.deleteMaxRate"/>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='set-message-ttl'">
          <el-form-item :label="$t('table.namespace')" prop="namespace">
            <span>{{ currentNamespace }}</span>
          </el-form-item>
          <el-form-item label="messageTTL" prop="messageTTL">
            <el-input v-model="temp.messageTTL"/>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='set-anti-affinity-group'">
          <el-form-item :label="$t('table.namespace')" prop="namespace">
            <span>{{ currentNamespace }}</span>
          </el-form-item>
          <el-form-item label="group" prop="group">
            <el-input v-model="temp.group"/>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='delete-anti-affinity-group'">
          <el-form-item :label="$t('table.namespace')" prop="namespace">
            <span>{{ currentNamespace }}</span>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='set-deduplication'">
          <el-form-item :label="$t('table.namespace')" prop="namespace">
            <span>{{ currentNamespace }}</span>
          </el-form-item>
          <el-form-item label="deduplication">
            <el-switch
              v-model="temp.deduplication"
              active-color="#13ce66"
              inactive-color="#ff4949"/>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='set-retention'">
          <el-form-item :label="$t('table.namespace')" prop="namespace">
            <span>{{ currentNamespace }}</span>
          </el-form-item>
          <el-form-item label="retentionSize" prop="retentionSize">
            <el-input v-model="temp.retentionSize"/>
          </el-form-item>
          <el-form-item label="retentionTime" prop="retentionTime">
            <el-input v-model="temp.retentionTime"/>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='unload'">
          <el-form-item :label="$t('table.namespace')" prop="namespace">
            <span>{{ currentNamespace }}</span>
          </el-form-item>
          <el-form-item label="unloadBundle" prop="unloadBundle">
            <el-input v-model="temp.unloadBundle"/>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='split-bundle'">
          <el-form-item :label="$t('table.namespace')" prop="namespace">
            <span>{{ currentNamespace }}</span>
          </el-form-item>
          <el-form-item label="splitBundle" prop="splitBundle">
            <el-input v-model="temp.splitBundle"/>
          </el-form-item>
          <el-form-item label="splitUnload">
            <el-switch
              v-model="temp.splitUnload"
              active-color="#13ce66"
              inactive-color="#ff4949"/>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='set-dispatch-rate'">
          <el-form-item :label="$t('table.namespace')" prop="namespace">
            <span>{{ currentNamespace }}</span>
          </el-form-item>
          <el-form-item label="byteDispatchRate" prop="byteDispatchRate">
            <el-input v-model="temp.byteDispatchRate"/>
          </el-form-item>
          <el-form-item label="dispatchRatePeriod" prop="dispatchRatePeriod">
            <el-input v-model="temp.dispatchRatePeriod"/>
          </el-form-item>
          <el-form-item label="msgDispatchRate" prop="msgDispatchRate">
            <el-input v-model="temp.msgDispatchRate"/>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='clear-backlog'">
          <el-form-item :label="$t('table.namespace')" prop="namespace">
            <span>{{ currentNamespace }}</span>
          </el-form-item>
          <el-form-item label="clearBundle" prop="clearBundle">
            <el-input v-model="temp.clearBundle"/>
          </el-form-item>
          <el-form-item label="clearForce">
            <el-switch
              v-model="temp.clearForce"
              active-color="#13ce66"
              inactive-color="#ff4949"/>
          </el-form-item>
          <el-form-item label="clearSub" prop="clearSub">
            <el-input v-model="temp.clearSub"/>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='unsubscribe'">
          <el-form-item :label="$t('table.namespace')" prop="namespace">
            <span>{{ currentNamespace }}</span>
          </el-form-item>
          <el-form-item label="unsubBundle" prop="unsubBundle">
            <el-input v-model="temp.unsubBundle"/>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='set-encryption-required'">
          <el-form-item :label="$t('table.namespace')" prop="namespace">
            <span>{{ currentNamespace }}</span>
          </el-form-item>
          <el-form-item label="encryption">
            <el-switch
              v-model="temp.encryption"
              active-color="#13ce66"
              inactive-color="#ff4949"/>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='set-subscription-auth-mode'">
          <el-form-item :label="$t('table.namespace')" prop="namespace">
            <span>{{ currentNamespace }}</span>
          </el-form-item>
          <el-form-item label="auth-mode" prop="auth-mode">
            <el-select v-model="temp.subscriptionAuthMode" style="width:330px;" placeholder="Please select">
              <el-option v-for="item in authModeListOptions" :label="item.label" :value="item.value" :key="item.value" />
            </el-select>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='set-max-producers-per-topic'">
          <el-form-item :label="$t('table.namespace')" prop="namespace">
            <span>{{ currentNamespace }}</span>
          </el-form-item>
          <el-form-item label="maxProducersPerTopic" prop="maxProducersPerTopic">
            <el-input v-model="temp.maxProducersPerTopic"/>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='set-max-consumers-per-topic'">
          <el-form-item :label="$t('table.namespace')" prop="namespace">
            <span>{{ currentNamespace }}</span>
          </el-form-item>
          <el-form-item label="maxConsumersPerTopic" prop="maxConsumersPerTopic">
            <el-input v-model="temp.maxConsumersPerTopic"/>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='set-max-consumers-per-subscription'">
          <el-form-item :label="$t('table.namespace')" prop="namespace">
            <span>{{ currentNamespace }}</span>
          </el-form-item>
          <el-form-item label="maxConsumersPerSub" prop="maxConsumersPerSub">
            <el-input v-model="temp.maxConsumersPerSub"/>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='set-compaction-threshold'">
          <el-form-item :label="$t('table.namespace')" prop="namespace">
            <span>{{ currentNamespace }}</span>
          </el-form-item>
          <el-form-item label="threshold" prop="threshold">
            <el-input v-model="temp.threshold"/>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='set-offload-threshold'">
          <el-form-item :label="$t('table.namespace')" prop="namespace">
            <span>{{ currentNamespace }}</span>
          </el-form-item>
          <el-form-item label="thresholdSize" prop="thresholdSize">
            <el-input v-model="temp.thresholdSize"/>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='set-offload-deletion-lag'">
          <el-form-item :label="$t('table.namespace')" prop="namespace">
            <span>{{ currentNamespace }}</span>
          </el-form-item>
          <el-form-item label="deletionLag" prop="deletionLag">
            <el-input v-model="temp.deletionLag"/>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='clear-offload-deletion-lag'">
          <el-form-item :label="$t('table.namespace')" prop="namespace">
            <span>{{ currentNamespace }}</span>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='set-schema-autoupdate-strategy'">
          <el-form-item :label="$t('table.namespace')" prop="namespace">
            <span>{{ currentNamespace }}</span>
          </el-form-item>
          <el-form-item label="compatibility" prop="compatibility">
            <el-select v-model="temp.compatibility" style="width:330px;" placeholder="Please select">
              <el-option v-for="item in compatibilityListOptions" :label="item.label" :value="item.value" :key="item.value" />
            </el-select>
          </el-form-item>
        </div>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">{{ $t('table.cancel') }}</el-button>
        <el-button type="primary" @click="handleOptions()">{{ $t('table.confirm') }}</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import {
  fetchNamespaces,
  fetchNamespacePolicies,
  putNamespace,
  deleteNamespace,
  grantPermissions,
  revokePermissions,
  setClusters,
  setBacklogQuota,
  removeBacklogQuota,
  setPersistence,
  setMessageTtl,
  setAntiAffinityGroup,
  deleteAntiAffinityGroup,
  setDeduplication,
  setRetention,
  unloadBundle,
  splitBundle,
  setDispatchRate,
  clearBundleBacklogForSubscription,
  unsubscribe,
  setEncryptionRequired,
  setSubscriptionAuthMode,
  setMaxProducersPerTopic,
  setMaxConsumersPerTopic,
  setMaxConsumersPerSubscription,
  setCompactionThreshold,
  setOffloadThreshold,
  setOffloadDeletionLag,
  clearOffloadDeletionLag,
  setSchemaAutoupdateStrategy
} from '@/api/namespaces'
import { fetchTenants } from '@/api/tenants'
import { fetchClusters } from '@/api/clusters'
import waves from '@/directive/waves' // Waves directive
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import jsonEditor from '@/components/JsonEditor'
import { validateEmpty } from '@/utils/validate'
import ElDragSelect from '@/components/DragSelect' // base on element-ui

const defaultForm = {
  tenant: '',
  otherOptions: ''
}

export default {
  name: 'Namespaces',
  components: {
    Pagination,
    jsonEditor,
    ElDragSelect
  },
  directives: { waves },
  data() {
    return {
      postForm: Object.assign({}, defaultForm),
      loading: false,
      clusters: [],
      clusterListOptions: [],
      tenantsListOptions: [],
      policiesListOptions: [],
      actionsListOptions: [],
      authModeListOptions: [],
      moreListOptions: [],
      tableKey: 0,
      tenant: '',
      otherOptions: '',
      list: null,
      localList: [],
      searchList: [],
      total: 0,
      jsonValue: {},
      listLoading: true,
      policiesListLoading: false,
      currentNamespace: '',
      listQuery: {
        namespace: '',
        page: 1,
        limit: 20
      },
      policiesQuery: {
        page: 1,
        limit: 20
      },
      temp: {
        namespace: '',
        limit: '',
        actions: [],
        clusters: [],
        subscriptionAuthMode: '',
        policy: '',
        ackQuorum: 0,
        ensemble: 0,
        writeQuorum: 0,
        deleteMaxRate: 0.0,
        messageTTL: 0,
        group: '',
        deduplication: false,
        retentionSize: '',
        retentionTime: '',
        unloadBundle: '',
        splitBundle: '',
        splitUnload: false,
        byteDispatchRate: -1,
        dispatchRatePeriod: 1,
        msgDispatchRate: 1,
        clearBundle: '',
        clearForce: false,
        clearSub: '',
        unsubBundle: '',
        encryption: false,
        maxProducersPerTopic: 0,
        maxConsumersPerTopic: 0,
        maxConsumersPerSub: 0,
        threshold: 0,
        thresholdSize: -1,
        deletionLag: -1,
        compatibility: 0
      },
      dialogFormVisible: false,
      dialogStatus: '',
      rules: {
        namespace: [{ required: true, message: 'namespace is required', trigger: 'blur' }],
        grant: [{ required: true, message: 'grant is required', trigger: 'blur' }],
        clusters: [{ required: true, message: 'clusters is required', trigger: 'blur' }],
        limit: [{ required: true, message: 'limit is required', trigger: 'blur' }],
        policies: [{ required: true, message: 'policies is required', trigger: 'blur' }],
        role: [{ required: true, message: 'role is required', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.tenant = this.$route.params && this.$route.params.tenant
    this.getNamespaces()
    this.getRemoteTenantsList()
  },
  mounted() {
    this.moreListOptions = this.loadAllOptions()
    this.actionsListOptions = [{ value: 'produce', label: 'produce' }, { value: 'consume', label: 'consume' }]
    this.clusterListOptions = []
    fetchClusters(this.listQuery).then(response => {
      for (var i = 0; i < response.data.length; i++) {
        this.clusterListOptions.push({ 'value': response.data[i], 'label': response.data[i] })
      }
    })
    this.policiesListOptions = [
      { value: 'producer_request_hold', label: 'producer_request_hold' },
      { value: 'producer_exception', label: 'producer_exception' },
      { value: 'consumer_backlog_eviction', label: 'consumer_backlog_eviction' }
    ]
    this.compatibilityListOptions = [
      { value: 0, label: 'AutoUpdateDisabled' },
      { value: 1, label: 'Backward' },
      { value: 2, label: 'Forward' },
      { value: 3, label: 'Full' }
    ]
    this.authModeListOptions = [
      { value: 0, label: 'None' },
      { value: 1, label: 'Prefix' }
    ]
  },
  methods: {
    getNamespaces() {
      if (this.localList.length > 0) {
        setTimeout(() => {
          this.localPaging()
        }, 0.5 * 1000)
      } else {
        this.listLoading = true
        if (this.tenant == null || this.tenant.length <= 0) {
          this.tenant = 'public'
        }
        fetchNamespaces(this.tenant, this.listQuery).then(response => {
          for (var i = 0; i < response.data.length; i++) {
            this.localList.push({ 'namespace': response.data[i] })
          }
          this.total = this.localList.length
          this.list = this.localList.slice((this.listQuery.page - 1) * this.listQuery.limit, this.listQuery.limit * this.listQuery.page)
          // Just to simulate the time of the request
          setTimeout(() => {
            this.listLoading = false
          }, 1.5 * 1000)
        })
      }
    },
    localPaging() {
      this.listLoading = true
      if (!validateEmpty(this.listQuery.namespace)) {
        this.searchList = []
        for (var i = 0; i < this.localList.length; i++) {
          if (this.localList[i]['namespace'].indexOf(this.listQuery.namespace) !== -1) {
            this.searchList.push(this.localList[i])
          }
        }
        this.total = this.searchList.length
        this.list = this.searchList.slice((this.listQuery.page - 1) * this.listQuery.limit, this.listQuery.limit * this.listQuery.page)
      } else {
        this.total = this.localList.length
        this.list = this.localList.slice((this.listQuery.page - 1) * this.listQuery.limit, this.listQuery.limit * this.listQuery.page)
      }
      this.listLoading = false
    },
    getNamespacePolicies(namespace) {
      // this.policiesListLoading = true
      fetchNamespacePolicies(namespace).then(response => {
        this.jsonValue = response.data
        // Just to simulate the time of the request
        setTimeout(() => {
          this.policiesListLoading = false
        }, 1.5 * 1000)
      })
    },
    handleFilter() {
      this.getNamespaces()
    },
    resetTemp() {
      this.temp = {
        namespace: ''
      }
    },
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createNamespace() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          putNamespace(this.tenant, this.temp.namespace, this.temp).then(() => {
            this.localList = []
            this.getNamespaces()
            this.dialogFormVisible = false
            this.$notify({
              title: 'success',
              message: 'create success',
              type: 'success',
              duration: 2000
            })
          })
        }
      })
    },
    handleDelete(row) {
      deleteNamespace(row.namespace).then((response) => {
        this.$notify({
          title: 'success',
          message: 'delete success',
          type: 'success',
          duration: 2000
        })
        this.localList = []
        this.getNamespaces()
      })
    },
    getRemoteTenantsList() {
      fetchTenants().then(response => {
        if (!response.data) return
        this.tenantsListOptions = response.data
        console.log(this.tenantsListOptions)
      })
    },
    getNamespacesList(tenant) {
      this.tenant = tenant
      this.localList = []
      this.getNamespaces()
    },
    moreListOptionsChange(item) {
      if (this.currentNamespace.length <= 0) {
        this.$notify({
          title: 'error',
          message: 'Please select any one namespace in table',
          type: 'error',
          duration: 3000
        })
        this.postForm.otherOptions = ''
        return
      }
      this.dialogStatus = item.value
      this.dialogFormVisible = true
    },
    querySearch(queryString, cb) {
      var moreListOptions = this.moreListOptions
      var results = moreListOptions.filter(this.createFilterOptions(queryString))
      cb(results)
    },
    createFilterOptions(queryString) {
      return (moreListOptions) => {
        return (moreListOptions.value.toLowerCase().indexOf(queryString.toLowerCase()) === 0)
      }
    },
    loadAllOptions() {
      return [
        { 'value': 'grant-permission' },
        { 'value': 'revoke-permission' },
        { 'value': 'set-clusters' },
        { 'value': 'set-backlog-quota' },
        { 'value': 'remove-backlog-quota' },
        { 'value': 'set-persistence' },
        { 'value': 'set-message-ttl' },
        { 'value': 'set-anti-affinity-group' },
        { 'value': 'delete-anti-affinity-group' },
        { 'value': 'set-deduplication' },
        { 'value': 'set-retention' },
        { 'value': 'unload' },
        { 'value': 'split-bundle' },
        { 'value': 'set-dispatch-rate' },
        { 'value': 'clear-backlog' },
        { 'value': 'unsubscribe' },
        { 'value': 'set-encryption-required' },
        { 'value': 'set-subscription-auth-mode' },
        { 'value': 'set-max-producers-per-topic' },
        { 'value': 'set-max-consumers-per-topic' },
        { 'value': 'set-max-consumers-per-subscription' },
        { 'value': 'set-compaction-threshold' },
        { 'value': 'set-offload-threshold' },
        { 'value': 'set-offload-deletion-lag' },
        { 'value': 'clear-offload-deletion-lag' },
        { 'value': 'set-schema-autoupdate-strategy' }
      ]
    },
    getCurrentRow(item) {
      this.currentNamespace = item.namespace
    },
    handleOptions() {
      switch (this.dialogStatus) {
        case 'create':
          this.createNamespace()
          break
        case 'grant-permission':
          this.confirmGrantPermission()
          break
        case 'revoke-permission':
          this.confirmRevokePermissions()
          break
        case 'set-clusters':
          this.confirmSetClusters()
          break
        case 'set-backlog-quota':
          this.confirmSetBacklogQuota()
          break
        case 'remove-backlog-quota':
          this.confirmRemoveBacklogQuota()
          break
        case 'set-persistence':
          this.confirmSetPersistence()
          break
        case 'set-message-ttl':
          this.confirmSetMessageTtl()
          break
        case 'set-anti-affinity-group':
          this.confirmSetAntiAffinityGroup()
          break
        case 'delete-anti-affinity-group':
          this.confirmDeleteAntiAffinityGroup()
          break
        case 'set-deduplication':
          this.confirmSetDeduplication()
          break
        case 'set-retention':
          this.confirmSetRetention()
          break
        case 'unload':
          this.confirmUnload()
          break
        case 'split-bundle':
          this.confirmSplitBundle()
          break
        case 'set-dispatch-rate':
          this.confirmSetDispatchRate()
          break
        case 'clear-backlog':
          this.confirmClearBacklog()
          break
        case 'unsubscribe':
          this.confirmUnsubscribe()
          break
        case 'set-encryption-required':
          this.confirmSetEncryptionRequired()
          break
        case 'set-subscription-auth-mode':
          this.confirmSetSubscriptionAuthMode()
          break
        case 'set-max-producers-per-topic':
          this.confirmSetMaxProducersPerTopic()
          break
        case 'set-max-consumers-per-topic':
          this.confirmSetMaxConsumersPerTopic()
          break
        case 'set-max-consumers-per-subscription':
          this.confirmSetMaxConsumersPerSubscription()
          break
        case 'set-compaction-threshold':
          this.confirmSetCompactionThreshold()
          break
        case 'set-offload-threshold':
          this.confirmSetOffloadThreshold()
          break
        case 'set-offload-deletion-lag':
          this.confirmSetOffloadDeletionLag()
          break
        case 'clear-offload-deletion-lag':
          this.confirmClearOffloadDeletionLag()
          break
        case 'set-schema-autoupdate-strategy':
          this.confirmSetSchemaAutoupdateStrategy()
          break
      }
    },
    confirmGrantPermission() {
      grantPermissions(this.currentNamespace, this.temp.role, this.temp.actions).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Add success',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmRevokePermissions() {
      revokePermissions(this.currentNamespace, this.temp.role).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Delete success',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmSetClusters() {
      setClusters(this.currentNamespace, this.temp.clusters).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Add clusters success for namespace',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmSetBacklogQuota() {
      setBacklogQuota(this.currentNamespace, this.temp).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Add Backlog Quota success for namespace',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmRemoveBacklogQuota() {
      removeBacklogQuota(this.currentNamespace).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Delete Backlog Quota success for namespace',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmSetPersistence() {
      const data = {
        'bookkeeper_ack_quorum': this.temp.ackQuorum,
        'bookkeeper_ensemble': this.temp.ensemble,
        'bookkeeper_write_quorum': this.temp.writeQuorum,
        'ml_mark_delete_max_rate': this.temp.deleteMaxRate
      }
      setPersistence(this.currentNamespace, data).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Set persistence success for namespace',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmSetMessageTtl() {
      setMessageTtl(this.currentNamespace, parseInt(this.temp.messageTTL)).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Set messageTTL success for namespace',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmSetAntiAffinityGroup() {
      setAntiAffinityGroup(this.currentNamespace, this.temp.group).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Set AntiAffinityGroup success for namespace',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmDeleteAntiAffinityGroup() {
      deleteAntiAffinityGroup(this.currentNamespace).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Delete AntiAffinityGroup success for namespace',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmSetDeduplication() {
      setDeduplication(this.currentNamespace, this.temp.deduplication).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Set deduplication success for namespace',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmSetRetention() {
      const data = { 'size': this.temp.retentionSize, 'time': this.temp.retentionTime }
      setRetention(this.currentNamespace, data).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Set Retention success for namespace',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmUnload() {
      unloadBundle(this.currentNamespace, this.temp.unloadBundle).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Set Retention success for namespace',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmSplitBundle() {
      const data = { 'unload': this.temp.splitUnload }
      // problem split http 412
      // to do solve
      splitBundle(this.currentNamespace, this.temp.splitBundle, data).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'splitBundle success for namespace',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmSetDispatchRate() {
      const data = {
        'dispatchThrottlingRateInByte': this.temp.byteDispatchRate,
        'ratePeriodInSecond': this.temp.dispatchRatePeriod,
        'dispatchThrottlingRateInMsg': this.temp.msgDispatchRate
      }
      setDispatchRate(this.currentNamespace, data).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'set DispatchRate success for namespace',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmClearBacklog() {
      // to do test
      clearBundleBacklogForSubscription(this.currentNamespace, this.temp.clearBundle, this.temp.clearSub).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'clearBundleBacklogForSubscription success for namespace',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmUnsubscribe() {
      unsubscribe(this.currentNamespace, this.temp.unsubBundle, this.temp.unsubscribe).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'unsubscribe success for namespace',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmSetEncryptionRequired() {
      setEncryptionRequired(this.currentNamespace, this.temp.encryption).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'success',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmSetSubscriptionAuthMode() {
      setSubscriptionAuthMode(this.currentNamespace, this.temp.subscriptionAuthMode).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'success',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmSetMaxProducersPerTopic() {
      setMaxProducersPerTopic(this.currentNamespace, this.temp.maxProducersPerTopic).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Set max producers per topic success',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmSetMaxConsumersPerTopic() {
      setMaxConsumersPerTopic(this.currentNamespace, this.temp.maxConsumersPerTopic).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Set max consumers per topic success',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmSetMaxConsumersPerSubscription() {
      setMaxConsumersPerSubscription(this.currentNamespace, this.temp.maxConsumersPerSub).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Set max subscription per topic success',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmSetCompactionThreshold() {
      setCompactionThreshold(this.currentNamespace, this.temp.threshold).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Set threshold success',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmSetOffloadThreshold() {
      setOffloadThreshold(this.currentNamespace, this.temp.thresholdSize).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Set threshold success',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmSetOffloadDeletionLag() {
      setOffloadDeletionLag(this.currentNamespace, this.temp.deletionLag).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Set DeletionLag success',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmClearOffloadDeletionLag() {
      clearOffloadDeletionLag(this.currentNamespace).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Clear DeletionLag success',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmSetSchemaAutoupdateStrategy() {
      // todo put method not allowed
      setSchemaAutoupdateStrategy(this.currentNamespace, this.temp.compatibility).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Set SchemaAutoupdateStrategy success',
          type: 'success',
          duration: 3000
        })
      })
    }
    // handleClearOptions() {
    //   this.moreListOptions = this.loadAllOptions()
    // }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
@import "src/styles/mixin.scss";
.createPost-container {
  position: relative;
  .createPost-main-container {
    .postInfo-container {
      position: relative;
      @include clearfix;
      margin-bottom: 10px;
      .postInfo-container-item {
        float: left;
      }
    }
  }
}
</style>
