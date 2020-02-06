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
    <div class="createPost-container">
      <el-form :inline="true" :model="postForm" class="form-container">
        <el-form-item :label="$t('tenant.label')">
          <el-select v-model="postForm.tenant" placeholder="select tenant" style="width: 150px;" @change="getNamespacesList(postForm.tenant)">
            <el-option v-for="(item,index) in tenantsListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('namespace.label')">
          <el-select v-model="postForm.namespace" placeholder="select namespace" style="width: 150px;" @change="getTopicsList()">
            <el-option v-for="(item,index) in namespacesListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('topic.label')">
          <el-select v-model="postForm.topic" placeholder="select topic" style="width: 150px;" @change="generatePartitions()">
            <el-option v-for="(item,index) in topicsListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('topic.partition')">
          <el-select v-model="postForm.partition" :disabled="partitionDisabled" placeholder="select partition" style="width: 150px;" @change="getSubscriptionsList()">
            <el-option v-for="(item,index) in partitionsListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('topic.subscription.label')">
          <el-select v-model="postForm.subscription" placeholder="select subscription" style="width: 150px;" @change="getSubscriptionsInfo()">
            <el-option v-for="(item,index) in subscriptionsListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
      </el-form>
      <el-form v-if="replicatedClusters.length > 0" :inline="true" :model="clusterForm" class="form-container">
        <el-form-item :label="$t('table.cluster')">
          <el-radio-group v-model="clusterForm.cluster" @change="onClusterChanged()">
            <el-radio-button
              v-for="cluster in replicatedClusters"
              :key="cluster"
              :label="cluster"/>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </div>
    <el-tabs v-model="topActiveName" @tab-click="handleClick">
      <el-tab-pane :label="$t('topic.consumer.consumers')" name="consumers">
        <el-row :gutter="24">
          <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}">
            <el-table
              v-loading="consumersListLoading"
              :key="consumerTableKey"
              :data="consumersList"
              border
              fit
              highlight-current-row
              style="width: 100%;">
              <el-table-column :label="$t('topic.consumer.name')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.consumerName }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('common.outMsg')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.outMsg }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('common.outBytes')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.outBytes }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.consumer.avgMsgSize')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.avgMsgSize }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.consumer.address')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.address }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.consumer.since')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.since }}</span>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane :label="$t('topic.backlogOpeartion')" name="backlogOperation">
        <el-tabs v-model="leftActiveName" :tab-position="tabPosition" @tab-click="handleLeftTabClick">
          <el-tab-pane :label="$t('topic.subscription.skip')" name="skip">
            <el-form :inline="true" :model="form">
              <el-button type="primary" @click="handleSkipMessages">{{ $t('topic.subscription.skip') }}</el-button>
              <el-form-item>
                <el-input v-model="form.skipNumMessages" placeholder="messages"/>
              </el-form-item>
              <span>{{ $t('topic.subscription.skipMessage') }}</span>
            </el-form>
          </el-tab-pane>
          <el-tab-pane :label="$t('topic.subscription.expire')" name="expire">
            <el-form :inline="true" :model="form">
              <el-button type="primary" @click="handleExpireMessages">{{ $t('topic.subscription.expire') }}</el-button>
              <el-form-item>
                <el-input v-model="form.expireNumMessages" placeholder="messages"/>
              </el-form-item>
              <span>{{ $t('topic.subscription.expireMessage') }}</span>
            </el-form>
          </el-tab-pane>
          <el-tab-pane :label="$t('topic.subscription.clear')" name="clear">
            <el-form :inline="true" :model="form">
              <el-button type="primary" @click="handleClearBacklog">{{ $t('topic.subscription.clear') }}</el-button>
              <span>{{ $t('topic.subscription.clearMessage') }}</span>
            </el-form>
          </el-tab-pane>
          <el-tab-pane :label="$t('topic.subscription.reset')" name="reset">
            <el-form :inline="true" :model="form">
              <el-button type="primary" @click="handleResetCursorByTime">{{ $t('topic.subscription.resetByTimeMessage') }}</el-button>
              <span>The cursor to</span>
              <el-form-item>
                <el-input v-model="form.minutes" placeholder="minutes"/>
              </el-form-item>
              <span>{{ $t('topic.subscription.mins') }}</span>
              <br>
              <el-button type="primary" @click="handleResetCursorByMessageId">{{ $t('topic.subscription.resetById') }}</el-button>
              <span>{{ $t('topic.subscription.messageId') }}</span>
              <el-form-item>
                <el-select v-model="form.ledgerValue" :placeholder="$t('topic.segment.ledgerId')" style="width:150px">
                  <el-option
                    v-for="item in ledgerOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"/>
                </el-select>
                <el-input v-model="form.messagesId" :placeholder="$t('topic.subscription.messageId')" style="width: 150px"/>
              </el-form-item>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="INSPECT" name="peek">
            <el-form :inline="true" :model="form">
              <el-button type="primary" @click="handlePeekMessages">{{ $t('topic.subscription.peek') }}</el-button>
              <el-form-item>
                <el-input v-model="form.peekNumMessages" placeholder="messages"/>
              </el-form-item>
              <span>{{ $t('topic.subscription.peekMessages') }}</span>
            </el-form>
            <el-row :gutter="24" style="margin-top:15px">
              <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}" style="padding-right:20px;margin-bottom:30px;">
                <el-table
                  v-loading="inspectListLoading"
                  :key="inspectTableKey"
                  :data="inspectsList"
                  border
                  fit
                  highlight-current-row
                  style="width: 100%;">
                  <el-table-column :label="$t('topic.segment.ledgerId')" min-width="10px" align="center">
                    <template slot-scope="scope">
                      <span>{{ scope.row.ledgerId }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column :label="$t('topic.subscription.entryId')" min-width="10px" align="center">
                    <template slot-scope="scope">
                      <span>{{ scope.row.entryId }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column :label="$t('topic.subscription.message')" min-width="30px" align="center">
                    <template slot-scope="scope">
                      <span>{{ scope.row.data }}</span>
                    </template>
                  </el-table-column>
                </el-table>
              </el-col>
            </el-row>
          </el-tab-pane>
        </el-tabs>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { fetchTenants } from '@/api/tenants'
import { fetchNamespaces, getClusters } from '@/api/namespaces'
import {
  fetchTopicsByPulsarManager,
  fetchTopicStats,
  skipOnCluster,
  expireMessageOnCluster,
  clearBacklogOnCluster,
  fetchTopicStatsInternal,
  resetCursorByTimestampOnCluster,
  resetCursorByPositionOnCluster
} from '@/api/topics'
import { fetchSubscriptions, peekMessagesOnCluster } from '@/api/subscriptions'
import { formatBytes } from '@/utils/index'
import { numberFormatter } from '@/filters/index'

const defaultForm = {
  persistent: '',
  tenant: '',
  namespace: '',
  topic: '',
  partition: '',
  subscription: ''
}
const defaultClusterForm = {
  cluster: ''
}
export default {
  name: 'SubscriptionInfo',
  data() {
    return {
      postForm: Object.assign({}, defaultForm),
      clusterForm: Object.assign({}, defaultClusterForm),
      subscriptionsListOptions: [],
      replicatedClusters: [],
      topActiveName: 'consumers',
      leftActiveName: '',
      currentTopTabName: 'consumers',
      currentLeftTabName: 'skip',
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
        minutes: '0',
        messagesId: '',
        ledgerValue: ''
      },
      firstInitNamespace: false,
      firstInitTopic: false,
      firstInitSubscription: false,
      ledgerOptions: [],
      partitionsListOptions: [],
      topicPartitions: {},
      partitionDisabled: false
    }
  },
  created() {
    this.postForm.persistent = this.$route.params && this.$route.params.persistent
    this.postForm.tenant = this.$route.params && this.$route.params.tenant
    this.postForm.namespace = this.$route.params && this.$route.params.namespace
    this.postForm.topic = this.$route.params && this.$route.params.topic
    if (this.postForm.topic.indexOf('-partition-') > 0) {
      var splitTopic = this.postForm.topic.split('-partition-')
      this.postForm.partition = splitTopic[1]
      this.postForm.topic = splitTopic[0]
    } else {
      this.postForm.partition = '-1'
      this.partitionDisabled = true
    }
    this.tenantNamespaceTopic = this.postForm.tenant + '/' + this.postForm.namespace + '/' + this.postForm.topic
    this.postForm.subscription = this.$route.params && this.$route.params.subscription
    this.firstInitNamespace = true
    this.firstInitTopic = true
    this.firstInitSubscription = true
    if (this.$route.query && this.$route.query.topTab) {
      this.topActiveName = this.$route.query.topTab
      this.currentTopTabName = this.$route.query.topTab
      if (this.$route.query.leftTab) {
        this.leftActiveName = this.$route.query.leftTab
      }
    }
    this.getRemoteTenantsList()
    this.getNamespacesList(this.postForm.tenant)
    this.getReplicatedClusters()
    this.getTopicsList()
    this.initTopicStats()
    this.handleStatsInternal()
  },
  methods: {
    generatePartitions() {
      var partitions = parseInt(this.topicPartitions[this.postForm.topic])
      this.partitionsListOptions = []
      if (partitions > 0) {
        this.partitionDisabled = false
        if (this.postForm.partition === '-1') {
          this.postForm.partition = ''
        }
        for (var i = 0; i < partitions; i++) {
          this.partitionsListOptions.push(i)
        }
        if (this.firstInitSubscription) {
          this.getSubscriptionsList()
        } else {
          this.subscriptionsListOptions = []
          this.postForm.subscription = ''
        }
      } else {
        this.partitionDisabled = true
        this.postForm.partition = '-1'
        this.partitionsListOptions.push('-1')
        this.getSubscriptionsList()
      }
    },
    getRemoteTenantsList() {
      fetchTenants().then(response => {
        if (!response.data) return
        for (var i = 0; i < response.data.total; i++) {
          this.tenantsListOptions.push(response.data.data[i].tenant)
        }
      })
    },
    getReplicatedClusters() {
      if (this.postForm.tenant && this.postForm.namespace) {
        getClusters(this.postForm.tenant, this.postForm.namespace).then(response => {
          if (!response.data) {
            return
          }
          this.replicatedClusters = response.data
          if (response.data.length > 0) {
            this.clusterForm.cluster = this.routeCluster || this.replicatedClusters[0]
          }
        })
      }
    },
    getCurrentCluster() {
      return this.clusterForm.cluster || ''
    },
    getNamespacesList(tenant) {
      let namespace = []
      this.namespacesListOptions = []
      this.topicsListOptions = []
      this.partitionsListOptions = []
      this.subscriptionsListOptions = []
      if (this.firstInitNamespace) {
        this.firstInitNamespace = false
      } else {
        this.postForm.namespace = ''
        this.postForm.topic = ''
        this.postForm.partition = ''
        this.postForm.subscription = ''
      }
      fetchNamespaces(tenant, this.query).then(response => {
        if (!response.data) return
        for (var i = 0; i < response.data.data.length; i++) {
          namespace = response.data.data[i].namespace
          this.namespacesListOptions.push(namespace)
        }
      })
    },
    getTopicsList() {
      this.getReplicatedClusters()
      this.topicsListOptions = []
      this.partitionsListOptions = []
      this.subscriptionsListOptions = []
      if (this.firstInitTopic) {
        this.firstInitTopic = false
      } else {
        this.postForm.topic = ''
        this.postForm.partition = ''
        this.postForm.subscription = ''
      }
      fetchTopicsByPulsarManager(this.postForm.tenant, this.postForm.namespace).then(response => {
        if (!response.data) return
        for (var i in response.data.topics) {
          this.topicsListOptions.push(response.data.topics[i]['topic'])
          this.topicPartitions[response.data.topics[i]['topic']] = response.data.topics[i]['partitions']
          if (response.data.topics[i]['topic'] === this.postForm.topic) {
            this.generatePartitions()
          }
        }
      })
    },
    getSubscriptionsList() {
      fetchSubscriptions(this.postForm.persistent, this.getFullTopic()).then(response => {
        if (!response.data) return
        if (this.firstInitSubscription) {
          this.firstInitSubscription = false
        } else {
          this.subscriptionsListOptions = []
          this.postForm.subscription = ''
        }
        for (var i in response.data) {
          this.subscriptionsListOptions.push(response.data[i])
        }
      })
    },
    getSubscriptionsInfo() {
      this.$router.push({ path: '/management/subscriptions/' + this.postForm.persistent +
        '/' + this.getFullTopic() + '/' + this.postForm.subscription + '/subscription?topTab=' +
        this.currentTopTabName + '&leftTab=' + this.currentLeftTabName })
    },
    handleStatsInternal() {
      fetchTopicStatsInternal(this.postForm.persistent, this.getFullTopic()).then(response => {
        if (!response.data) return
        for (var i in response.data.ledgers) {
          this.ledgerOptions.push({
            value: response.data.ledgers[i]['ledgerId'],
            label: response.data.ledgers[i]['ledgerId']
          })
        }
      })
    },
    getFullTopic() {
      var fullTopic = this.postForm.tenant + '/' + this.postForm.namespace + '/' + this.postForm.topic
      if (parseInt(this.postForm.partition) >= 0) {
        fullTopic += '-partition-' + this.postForm.partition
      }
      return fullTopic
    },
    initTopicStats() {
      fetchTopicStats(this.postForm.persistent, this.getFullTopic()).then(response => {
        if (!response.data) return
        if (response.data.subscriptions.hasOwnProperty(this.postForm.subscription)) {
          var subscription = response.data.subscriptions[this.postForm.subscription]
          if (subscription.hasOwnProperty('consumers')) {
            var consumers = subscription['consumers']
            for (var s in consumers) {
              this.consumersList.push({
                'consumerName': consumers[s].consumerName,
                'outMsg': numberFormatter(consumers[s].msgRateOut, 2),
                'outBytes': formatBytes(consumers[s].msgThroughputOut),
                'avgMsgSize': formatBytes(response.data.averageMsgSize),
                'address': consumers[s].address,
                'since': consumers[s].connectedSince
              })
            }
          }
        }
      })
    },
    handleClick(tab, event) {
      this.currentTopTabName = tab.name
      if (this.currentTopTabName === 'backlogOperation') {
        this.$router.push({ query: { 'topTab': tab.name, 'leftTab': this.currentLeftTabName }})
      } else {
        this.$router.push({ query: { 'topTab': tab.name }})
      }
    },
    handleLeftTabClick(tab, event) {
      this.currentLeftTabName = tab.name
    },
    getConsumers() {
    },
    handlePeekMessages() {
      if (this.form.peekNumMessages <= 0) {
        this.$notify({
          title: 'error',
          message: this.$i18n.t('topic.subscription.messageGreaterThanZero'),
          type: 'error',
          duration: 3000
        })
        return
      }
      peekMessagesOnCluster(
        this.getCurrentCluster(),
        this.postForm.persistent,
        this.tenantNamespaceTopic,
        this.postForm.subscription,
        this.form.peekNumMessages).then(response => {
        if (!response.data) return
        if (response.data.hasOwnProperty('error')) {
          this.$notify({
            title: 'error',
            message: response.data.error,
            type: 'error',
            duration: 3000
          })
          return
        }
        if (!response.data.data) return
        if (response.data.data.hasOwnProperty('error')) {
          this.$notify({
            title: 'error',
            message: this.$i18n.t('topic.subscription.peekMessageError'),
            type: 'error',
            duration: 3000
          })
          return
        }
        this.inspectsList = []
        for (var i = 0; i < response.data.data.length; i++) {
          this.inspectsList.push({
            'messageId': i,
            'entryId': response.data.data[i].entryId,
            'ledgerId': response.data.data[i].ledgerId,
            'data': window.atob(response.data.data[i].data)
          })
        }
      })
    },
    handleSkipMessages() {
      if (this.form.skipNumMessages <= 0) {
        this.$notify({
          title: 'error',
          message: this.$i18n.t('topic.subscription.messageGreaterThanZero'),
          type: 'error',
          duration: 3000
        })
        return
      }
      skipOnCluster(this.getCurrentCluster(), this.postForm.persistent, this.getFullTopic(), this.postForm.subscription, this.form.skipNumMessages).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('topic.subscription.messageSkipSuccess'),
          type: 'success',
          duration: 3000
        })
      })
    },
    handleExpireMessages() {
      if (this.form.expireMessages <= 0) {
        this.$notify({
          title: 'error',
          message: this.$i18n.t('topic.subscription.messageGreaterThanZero'),
          type: 'error',
          duration: 3000
        })
        return
      }
      expireMessageOnCluster(this.getCurrentCluster(), this.postForm.persistent, this.getFullTopic(), this.postForm.subscription, this.form.expireNumMessages).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('topic.subscription.expireMessageSuccess'),
          type: 'success',
          duration: 3000
        })
      })
    },
    handleClearBacklog() {
      clearBacklogOnCluster(this.getCurrentCluster(), this.postForm.persistent, this.getFullTopic(), this.postForm.subscription).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('topic.subscription.clearMessageSuccess'),
          type: 'success',
          duration: 3000
        })
      })
    },
    handleResetCursorByTime() {
      if (parseInt(this.form.minutes) <= 0) {
        this.$notify({
          title: 'error',
          message: this.$i18n.t('topic.subscription.minutesNotLessThanZero'),
          type: 'error',
          duration: 3000
        })
        return
      }
      var dateTime = new Date().getTime()
      var timestamp = Math.floor(dateTime) - parseInt(this.form.minutes) * 60 * 1000
      resetCursorByTimestampOnCluster(
        this.getCurrentCluster(),
        this.postForm.persistent,
        this.getFullTopic(),
        this.postForm.subscription, timestamp).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('topic.subscription.resetCursorSuccess'),
          type: 'success',
          duration: 3000
        })
      })
    },
    handleResetCursorByMessageId() {
      if (this.form.messagesId.length <= 0 && this.form.ledgerValue != null) {
        this.$notify({
          title: 'error',
          message: this.$i18n.t('topic.subscription.messageIdNotLessThanZero'),
          type: 'error',
          duration: 3000
        })
        return
      }
      var data = {
        'ledgerId': this.form.ledgerValue,
        'entryId': parseInt(this.form.messagesId)
      }
      resetCursorByPositionOnCluster(this.getCurrentCluster(), this.postForm.persistent, this.getFullTopic(), this.postForm.subscription, data).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('topic.subscription.resetCursorSuccess'),
          type: 'success',
          duration: 3000
        })
      })
    },
    handleFilterConsumer() {

    }
  }
}
</script>
