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
                  <el-col :span="8">
                    <el-form-item class="postInfo-container-item">
                      <el-select v-model="postForm.namespace" placeholder="select namespace" @change="getTopicsList(postForm.tenant, postForm.namespace)">
                        <el-option v-for="(item,index) in namespacesListOptions" :key="item+index" :label="item" :value="item"/>
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item class="postInfo-container-item">
                      <el-select v-model="postForm.topic" placeholder="select topic" @change="getSubscriptionsList(postForm.tenant, postForm.namespace, postForm.topic)">
                        <el-option v-for="(item,index) in topicsListOptions" :key="item+index" :label="item" :value="item"/>
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
      <el-input :placeholder="$t('table.subscription')" v-model="listQuery.subscription" style="width: 200px;" class="filter-item" @keyup.enter.native="handleFilter"/>
      <el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">{{ $t('table.search') }}</el-button>
      <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleCreate">{{ $t('table.subscription') }}</el-button>
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
    <el-row :gutter="8">
      <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}" style="padding-right:8px;margin-bottom:30px;">
        <el-table
          v-loading="listLoading"
          :key="tableKey"
          :data="list"
          border
          fit
          highlight-current-row
          style="width: 100%;"
          @row-click="getCurrentRow">
          <el-table-column :label="$t('table.tenant')" min-width="100px" align="center">
            <template slot-scope="scope">
              <span>{{ scope.row.tenant }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('table.namespace')" min-width="100px" align="center">
            <template slot-scope="scope">
              <span>{{ scope.row.namespace }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('table.topic')" min-width="100px" align="center">
            <template slot-scope="scope">
              <span>{{ scope.row.topic }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('table.subscription')" min-width="100px" align="center">
            <template slot-scope="scope">
              <span>{{ scope.row.subscription }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('table.actions')" align="center" width="360" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button v-if="scope.row.status!='deleted'" class="el-button el-button--primary el-button--medium" type="danger" @click="handleDelete(scope.row)">{{ $t('table.unsubscription') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getSubscriptions" />
      </el-col>
    </el-row>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form ref="temp" :rules="rules" :model="temp" label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
        <div v-if="dialogStatus==='create'">
          <el-form-item label="messageId" prop="messageId">
            <el-input v-model="temp.messageId"/>
          </el-form-item>
          <el-form-item :label="$t('table.subscription')" prop="subscription">
            <el-input v-model="temp.subscription"/>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='skip'">
          <el-form-item label="count" prop="count">
            <el-input v-model="temp.count"/>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='expire-messages'">
          <el-form-item label="expireTime" prop="expireTime">
            <el-input v-model="temp.expireTime"/>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='expire-messages-all-subscriptions'">
          <el-form-item label="expireTime" prop="expireTime">
            <el-input v-model="temp.expireTime"/>
          </el-form-item>
        </div>
        <div v-else-if="dialogStatus==='reset-cursor'">
          <el-form-item label="messageId" prop="messageId">
            <el-input v-model="temp.messageId"/>
          </el-form-item>
          <el-form-item label="time" prop="time">
            <el-input v-model="temp.time"/>
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
import { fetchTenants } from '@/api/tenants'
import { fetchNamespaces } from '@/api/namespaces'
import { fetchSubscriptions, putSubscription, deleteSubscription } from '@/api/subscriptions'
import {
  fetchTopics,
  fetchPersistentPartitonsTopics,
  fetchNonPersistentPartitonsTopics,
  skip,
  expireMessage,
  expireMessagesAllSubscriptions,
  resetPersistentCursor
} from '@/api/topics'
// import { parsePulsarSchema } from '@/utils'
import waves from '@/directive/waves' // Waves directive
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import jsonEditor from '@/components/JsonEditor'
import { validateEmpty } from '@/utils/validate'
const defaultForm = {
  tenant: '',
  namespace: '',
  otherOptions: ''
}

export default {
  name: 'Topics',
  components: {
    Pagination,
    jsonEditor
  },
  directives: { waves },
  filters: {
    statusFilter(status) {
      const statusMap = {
        published: 'success',
        draft: 'info',
        deleted: 'danger'
      }
      return statusMap[status]
    }
  },
  props: {
    isEdit: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      postForm: Object.assign({}, defaultForm),
      loading: false,
      tenantsListOptions: [],
      namespacesListOptions: [],
      topicsListOptions: [],
      moreListOptions: [],
      tableKey: 0,
      list: null,
      localList: [],
      searchList: [],
      total: 0,
      listLoading: false,
      jsonValue: {},
      tenant: '',
      namespace: '',
      topic: '',
      currentSub: '',
      currentTenant: '',
      currentNamespace: '',
      currentTopic: '',
      listQuery: {
        subscription: '',
        page: 1,
        limit: 10
      },
      temp: {
        messageId: '',
        subscription: '',
        count: 0,
        expireTime: 0,
        time: ''
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: 'Edit',
        create: 'Create'
      },
      rules: {
        messageId: [{ required: true, message: 'messageId is required', trigger: 'blur' }],
        subscription: [{ required: true, message: 'subscription is required', trigger: 'blur' }],
        count: [{ required: true, message: 'count is required', trigger: 'blur' }],
        expireTime: [{ required: true, message: 'expireTime is required', trigger: 'blur' }],
        time: [{ required: true, message: 'time is required', trigger: 'blur' }]
      }
      // tempRoute: {}
    }
  },
  computed: {
  },
  created() {
    if (this.isEdit) {
      // const id = this.$route.params && this.$route.params.id
      // this.fetchData(id)
    } else {
      this.postForm = Object.assign({}, defaultForm)
    }
    this.getRemoteTenantsList()
  },
  mounted() {
    this.moreListOptions = this.loadAllOptions()
  },
  methods: {
    getSubscriptions() {
      if (this.localList.length > 0) {
        setTimeout(() => {
          this.localPaging()
        }, 0)
      } else {
        this.listLoading = true
        if (this.tenant.length <= 0 || this.namespace.length <= 0 || this.topic.length <= 0) {
          this.tenant = 'public'
          this.namespace = 'default'
          this.topic = 'test'
        }
        fetchSubscriptions(this.tenant, this.namespace, this.topic, this.listQuery).then(response => {
          for (var i = 0; i < response.data.length; i++) {
            this.localList.push({
              'tenant': this.tenant,
              'namespace': this.namespace,
              'topic': this.topic,
              'subscription': response.data[i]
            })
          }
          this.total = this.localList.length
          this.list = this.localList.slice((this.listQuery.page - 1) * this.listQuery.limit, this.listQuery.limit * this.listQuery.page)
          // this.localPaging()
          // Just to simulate the time of the request
          setTimeout(() => {
          }, 1.5 * 1000)
        })
        this.listLoading = false
      }
    },
    localPaging() {
      this.listLoading = true
      if (!validateEmpty(this.listQuery.subscription)) {
        this.searchList = []
        for (var i = 0; i < this.localList.length; i++) {
          if (this.localList[i]['subscription'].indexOf(this.listQuery.subscription) !== -1) {
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
    handleFilter() {
      this.getSubscriptions()
    },
    resetTemp() {
      this.temp = {
        subscription: ''
      }
    },
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
    },
    createData() {
      if (this.tenant.length <= 0 || this.namespace <= 0 || this.topic.length <= 0) {
        this.$notify({
          title: 'error',
          message: 'please select tenant and namespace and topic',
          type: 'success',
          duration: 2000
        })
        return
      }
      putSubscription(
        this.tenant,
        this.namespace,
        this.topic,
        this.temp.subscription
      ).then(() => {
        this.localList = []
        this.getSubscriptions()
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'create success',
          type: 'success',
          duration: 2000
        })
      })
    },
    handleDelete(row) {
      this.temp = Object.assign({}, row) // copy obj
      deleteSubscription(
        this.temp.tenant,
        this.temp.namespace,
        this.temp.topic,
        this.temp.subscription
      ).then(response => {
        this.$notify({
          title: 'success',
          message: 'delete success',
          type: 'success',
          duration: 2000
        })
        this.localList = []
        this.getSubscriptions()
      })
    },
    getRemoteTenantsList() {
      fetchTenants().then(response => {
        if (!response.data) return
        this.tenantsListOptions = response.data
      })
    },
    getNamespacesList(tenant) {
      this.tenant = tenant
      fetchNamespaces(tenant, this.query).then(response => {
        let namespace = []
        for (var i = 0; i < response.data.length; i++) {
          namespace = response.data[i].split('/')
          this.namespacesListOptions.push(namespace.splice(1, namespace.length).join('/'))
        }
      })
    },
    getTopicsList(tenant, namespace) {
      this.tenant = tenant
      this.namespace = namespace
      this.topicsListOptions = []
      fetchPersistentPartitonsTopics(this.tenant, this.namespace).then(response => {
        for (var i = 0; i < response.data.length; i++) {
          this.topicsListOptions.push(response.data[i].split('/' + this.namespace + '/')[1])
        }
      })
      fetchNonPersistentPartitonsTopics(this.tenant, this.namespace).then(response => {
        for (var i = 0; i < response.data.length; i++) {
          this.topicsListOptions.push(response.data[i].split('/' + this.namespace + '/')[1])
        }
      })
      fetchTopics(this.tenant, this.namespace, this.listQuery).then(response => {
        for (var i = 0; i < response.data.length; i++) {
          this.topicsListOptions.push(response.data[i].split('/' + this.namespace + '/')[1])
        }
      })
    },
    getSubscriptionsList(tenant, namespace, topic) {
      this.tenant = tenant
      this.namespace = namespace
      this.topic = topic
      this.localList = []
      this.getSubscriptions()
    },
    moreListOptionsChange(item) {
      if (item.value === 'expire-messages-all-subscriptions') {
        this.dialogStatus = item.value
        this.dialogFormVisible = true
        return
      }
      if (this.currentSub.length <= 0) {
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
      this.$nextTick(() => {
        this.$refs['temp'].clearValidate()
      })
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
        { 'value': 'skip' },
        { 'value': 'expire-messages' },
        { 'value': 'expire-messages-all-subscriptions' },
        { 'value': 'reset-cursor' }
      ]
    },
    getCurrentRow(item) {
      this.currentTenant = item.tenant
      this.currentNamespace = item.namespace
      this.currentTopic = item.topic
      this.currentSub = item.subscription
    },
    handleOptions() {
      this.$refs['temp'].validate((valid) => {
        if (valid) {
          switch (this.dialogStatus) {
            case 'create':
              this.createData()
              break
            case 'skip':
              this.confirmSkip()
              break
            case 'expire-messages':
              this.confirmExpireMessage()
              break
            case 'expire-messages-all-subscriptions':
              this.confirmExpireMessagesAllSubscriptions()
              break
            case 'reset-cursor':
              this.confirmResetCursor()
              break
          }
        }
      })
    },
    confirmSkip() {
      const tenantNamespaceTopic = this.currentTenant + '/' + this.currentNamespace + '/' + this.currentTopic
      skip(tenantNamespaceTopic, this.currentSub, this.temp.count).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Skip success for this topic',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmExpireMessage() {
      const tenantNamespaceTopic = this.currentTenant + '/' + this.currentNamespace + '/' + this.currentTopic
      expireMessage(tenantNamespaceTopic, this.currentSub, this.temp.expireTime).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Expire message for this topic',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmExpireMessagesAllSubscriptions() {
      const tenantNamespaceTopic = this.tenant + '/' + this.namespace + '/' + this.topic
      expireMessagesAllSubscriptions(tenantNamespaceTopic, this.temp.expireTime).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'Expire all message for this topic',
          type: 'success',
          duration: 3000
        })
      })
    },
    confirmResetCursor() {
      const tenantNamespaceTopic = this.currentTenant + '/' + this.currentNamespace + '/' + this.currentTopic
      resetPersistentCursor(tenantNamespaceTopic, this.currentSub, this.temp.time, this.temp.messageId).then(response => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'success',
          message: 'resetCursor for this topic',
          type: 'success',
          duration: 3000
        })
      })
    }
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
