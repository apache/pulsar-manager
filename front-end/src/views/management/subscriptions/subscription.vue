<template>
  <div class="app-container">
    <div class="createPost-container">
      <el-form :inline="true" :model="postForm" class="form-container">
        <el-form-item class="postInfo-container-item" label="Tenant">
          <el-select v-model="postForm.tenant" placeholder="select tenant" @change="getNamespacesList(postForm.tenant)">
            <el-option v-for="(item,index) in tenantsListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
        <el-form-item class="postInfo-container-item" label="Namespace">
          <el-select v-model="postForm.namespace" placeholder="select namespace" @change="getTopicsList()">
            <el-option v-for="(item,index) in namespacesListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
        <el-form-item class="postInfo-container-item" label="Topic">
          <el-select v-model="postForm.topic" placeholder="select topic" @change="getSubscriptionsList()">
            <el-option v-for="(item,index) in topicsListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
        <el-form-item class="postInfo-container-item" label="Subscription">
          <el-select v-model="postForm.subscription" placeholder="select subscription" @change="getSubscriptionsInfo()">
            <el-option v-for="(item,index) in subscriptionsListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane label="Consumers" name="consumers">
        <div class="filter-container">
          <el-input placeholder="Search Bundles" prefix-icon="el-icon-search" style="width: 200px;" class="filter-item" @keyup.enter.native="handleFilterBundle"/>
        </div>
        <el-row :gutter="24">
          <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}" style="padding-right:8px;margin-bottom:30px;">
            <el-table
              v-loading="consumersListLoading"
              :key="consumerTableKey"
              :data="consumersList"
              border
              fit
              highlight-current-row
              style="width: 100%;">
              <el-table-column label="Consumer Name" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.consumerName }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Out - msg/s" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.outMsg }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Out - bytes/s" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.outBytes }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Avg Msg Size" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.avgMsgSize }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Address" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.address }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Since" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.since }}</span>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane label="Backlog Operation" name="backlogOperation">
        <el-tabs :tab-position="tabPosition">
          <!-- <el-tab-pane label="INSPECT">
            <el-form :inline="true" :model="form">
              <el-button type="primary" @click="handlePeekMessages">Peek</el-button>
              <el-form-item>
                <el-input v-model="form.peekNumMessages" placeholder="messages"/>
              </el-form-item>
              <span>messages</span>
            </el-form>
            <el-row :gutter="24" style="margin-top:15px">
              <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}" style="padding-right:8px;margin-bottom:30px;">
                <el-table
                  v-loading="inspectListLoading"
                  :key="inspectTableKey"
                  :data="inspectsList"
                  border
                  fit
                  highlight-current-row
                  style="width: 100%;">
                  <el-table-column label="Message ID" min-width="10px" align="center">
                    <template slot-scope="scope">
                      <span>{{ scope.row.messageId }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="data" min-width="30px" align="center">
                    <template slot-scope="scope">
                      <span>{{ scope.row.data }}</span>
                    </template>
                  </el-table-column>
                </el-table>
              </el-col>
            </el-row>
          </el-tab-pane> -->
          <el-tab-pane label="SKIP">
            <el-form :inline="true" :model="form">
              <el-button type="primary" @click="handleSkipMessages">Skip</el-button>
              <el-form-item>
                <el-input v-model="form.skipNumMessages" placeholder="messages"/>
              </el-form-item>
              <span>messages</span>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="EXPIRE">
            <el-form :inline="true" :model="form">
              <el-button type="primary" @click="handleExpireMessages">Expire</el-button>
              <el-form-item>
                <el-input v-model="form.expireNumMessages" placeholder="messages"/>
              </el-form-item>
              <span>messages older than timestamp (in seconds)</span>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="CLEAR">
            <el-form :inline="true" :model="form">
              <el-button type="primary" @click="handleClearBacklog">Clear</el-button>
              <span>the backlog</span>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="RESET">
            <el-form :inline="true" :model="form">
              <el-button type="primary">Reset</el-button>
              <span>The cursor to</span>
              <el-form-item>
                <el-input v-model="form.minutes" placeholder="minutes"/>
              </el-form-item>
              <span>mins or to Message ID</span>
              <el-form-item>
                <el-input v-model="form.messagesIds" placeholder="messages ids"/>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { fetchTenants } from '@/api/tenants'
import { fetchNamespaces } from '@/api/namespaces'
import {
  fetchTopicsByPulsarManager,
  fetchTopicStats,
  // peekMessages,
  skip,
  expireMessage,
  clearBacklog
} from '@/api/topics'
import { fetchSubscriptions } from '@/api/subscriptions'
const defaultForm = {
  persistent: '',
  tenant: '',
  namespace: '',
  topic: '',
  subscription: ''
}
export default {
  name: 'SubscriptionInfo',
  data() {
    return {
      postForm: Object.assign({}, defaultForm),
      subscriptionsListOptions: [],
      activeName: 'consumers',
      tenantsListOptions: [],
      namespacesListOptions: [],
      topicsListOptions: [],
      consumersListLoading: false,
      consumerTableKey: 0,
      consumersList: [],
      consumersTotal: 0,
      consumersListQuery: {
        page: 0,
        limit: 1
      },
      inspectsList: [],
      inspectTableKey: 0,
      inspectListLoading: false,
      tabPosition: 'left',
      form: {
        peekNumMessages: 0,
        skipNumMessages: 0,
        expireNumMessages: 10,
        minutes: 1,
        messagesIds: ''
      },
      firstInitNamespace: false,
      firstInitTopic: false,
      firstInitSubscription: false
    }
  },
  created() {
    this.postForm.persistent = this.$route.params && this.$route.params.persistent
    this.postForm.tenant = this.$route.params && this.$route.params.tenant
    this.postForm.namespace = this.$route.params && this.$route.params.namespace
    this.postForm.topic = this.$route.params && this.$route.params.topic
    this.tenantNamespaceTopic = this.postForm.tenant + '/' + this.postForm.namespace + '/' + this.postForm.topic
    this.postForm.subscription = this.$route.params && this.$route.params.subscription
    this.firstInitNamespace = true
    this.firstInitTopic = true
    this.firstInitSubscription = true
    this.getRemoteTenantsList()
    this.getNamespacesList(this.postForm.tenant)
    this.getTopicsList()
    this.getSubscriptionsList()
    this.initTopicStats()
  },
  methods: {
    getRemoteTenantsList() {
      fetchTenants().then(response => {
        if (!response.data) return
        for (var i = 0; i < response.data.total; i++) {
          this.tenantsListOptions.push(response.data.data[i].tenant)
        }
      })
    },
    getNamespacesList(tenant) {
      fetchNamespaces(tenant, this.query).then(response => {
        if (!response.data) return
        let namespace = []
        this.namespacesListOptions = []
        if (this.firstInitNamespace) {
          this.firstInitNamespace = false
        } else {
          this.postForm.namespace = ''
        }
        for (var i = 0; i < response.data.data.length; i++) {
          namespace = response.data.data[i].namespace
          this.namespacesListOptions.push(namespace)
        }
      })
    },
    getTopicsList() {
      fetchTopicsByPulsarManager(this.postForm.tenant, this.postForm.namespace).then(response => {
        if (!response.data) return
        this.topicsListOptions = []
        if (this.firstInitTopic) {
          this.firstInitTopic = false
        } else {
          this.postForm.topic = ''
        }
        for (var i in response.data.topics) {
          this.topicsListOptions.push(response.data.topics[i]['topic'])
        }
      })
    },
    getSubscriptionsList() {
      fetchSubscriptions(this.postForm.persistent, this.postForm.tenant, this.postForm.namespace, this.postForm.topic).then(response => {
        if (!response.data) return
        this.subscriptionsListOptions = []
        if (this.firstInitSubscription) {
          this.firstInitSubscription = false
        } else {
          this.postForm.subscription = ''
        }
        for (var i in response.data) {
          this.subscriptionsListOptions.push(response.data[i])
        }
      })
    },
    getSubscriptionsInfo() {
      this.$router.push({ path: '/management/subscriptions/' + this.postForm.persistent +
        '/' + this.postForm.tenant + '/' + this.postForm.namespace + '/' +
        this.postForm.topic + '/' + this.postForm.subscription + '/subscription' })
    },
    initTopicStats() {
      fetchTopicStats(this.postForm.persistent, this.tenantNamespaceTopic).then(response => {
        if (!response.data) return
        if (response.data.subscriptions.hasOwnProperty(this.postForm.subscription)) {
          var subscription = response.data.subscriptions[this.postForm.subscription]
          if (subscription.hasOwnProperty('consumers')) {
            var consumers = subscription['consumers']
            for (var s in consumers) {
              this.consumersList.push({
                'consumerName': consumers[s].consumerName,
                'outMsg': consumers[s].msgRateOut,
                'outBytes': consumers[s].msgThroughputOut,
                'avgMsgSize': response.data.averageMsgSize,
                'address': consumers[s].address,
                'since': consumers[s].connectedSince
              })
            }
          }
        }
      })
    },
    handleClick(tab, event) {

    },
    getConsumers() {
    },
    // To do, parse message
    // handlePeekMessages() {
    //   if (this.form.peekNumMessages <= 0) {
    //     this.$notify({
    //       title: 'error',
    //       message: 'Messages should greater than 0',
    //       type: 'error',
    //       duration: 3000
    //     })
    //     return
    //   }
    //   peekMessages(this.postForm.persistent, this.tenantNamespaceTopic, this.postForm.subscription, this.form.peekNumMessages).then(response => {
    //     if (!response.data) return
    //     console.log(response)
    //     console.log(response.data)
    //   })
    // },
    handleSkipMessages() {
      if (this.form.skipNumMessages <= 0) {
        this.$notify({
          title: 'error',
          message: 'Messages should greater than 0',
          type: 'error',
          duration: 3000
        })
        return
      }
      skip(this.postForm.persistent, this.tenantNamespaceTopic, this.postForm.subscription, this.form.skipNumMessages).then(response => {
        this.$notify({
          title: 'success',
          message: 'Messages skip success',
          type: 'success',
          duration: 3000
        })
      })
    },
    handleExpireMessages() {
      if (this.form.expireMessages <= 0) {
        this.$notify({
          title: 'error',
          message: 'Messages should greater than 0',
          type: 'error',
          duration: 3000
        })
        return
      }
      expireMessage(this.postForm.persistent, this.tenantNamespaceTopic, this.postForm.subscription, this.form.expireNumMessages).then(response => {
        this.$notify({
          title: 'success',
          message: 'Messages expire success',
          type: 'success',
          duration: 3000
        })
      })
    },
    handleClearBacklog() {
      clearBacklog(this.postForm.persistent, this.tenantNamespaceTopic, this.postForm.subscription).then(response => {
        this.$notify({
          title: 'success',
          message: 'Clear messages success',
          type: 'success',
          duration: 3000
        })
      })
    }
  }
}
</script>
