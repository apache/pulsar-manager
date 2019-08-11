<template>
  <div class="app-container">
    <div class="createPost-container">
      <el-form :inline="true" :model="postForm" class="form-container">
        <el-form-item :label="$t('tenant.label')">
          <el-select v-model="postForm.tenant" placeholder="select tenant" @change="getNamespacesList(postForm.tenant)">
            <el-option v-for="(item,index) in tenantsListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('namespace.label')">
          <el-select v-model="postForm.namespace" placeholder="select namespace" @change="getTopicsList()">
            <el-option v-for="(item,index) in namespacesListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('topic.label')">
          <el-select v-model="postForm.topic" placeholder="select topic" @change="generatePartitions()">
            <el-option v-for="(item,index) in topicsListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('topic.partition')">
          <el-select v-model="postForm.partition" :disabled="partitionDisabled" placeholder="select partition" @change="getTopicInfo()">
            <el-option v-for="(item,index) in partitionsListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane :label="$t('tabs.overview')" name="overview">
        <el-row :gutter="12">
          <el-col :span="12">
            <el-card style="height: 305px">
              <h4>{{ $t('topic.info') }}</h4>
              <el-table
                :data="infoData"
                :show-header="false"
                border
                style="width: 100%">
                <el-table-column :label="$t('topic.column')" prop="infoColumn"/>
                <el-table-column :label="$t('topic.data')" prop="data"/>
              </el-table>
              <el-button
                class="filter-item"
                type="danger"
                style="margin-top:15px;"
                icon="el-icon-download"
                @click="handleUnload">
                {{ $t('topic.unload') }}
              </el-button>
            </el-card>
          </el-col>
          <el-col v-if="nonPersistent===false" :span="4">
            <el-card>
              <h4>{{ $t('topic.status') }}</h4>
              <el-button
                v-if="terminateStatus !== 'Terminated'"
                type="primary"
                circle
                class="circle">
                <span class="circle-font">{{ terminateStatus }}</span>
              </el-button>
              <el-button
                v-if="terminateStatus === 'Terminated'"
                type="info"
                circle
                class="circle"
                disabled>
                <span class="circle-font">{{ terminateStatus }}</span>
              </el-button>
              <el-button
                v-if="terminateStatus !== 'Terminated'"
                type="primary"
                style="display:block;margin-top:15px;margin-left:auto;margin-right:auto;"
                icon="el-icon-close"
                @click="handleTerminate">
                {{ $t('topic.terminate') }}
              </el-button>
              <el-button
                v-if="terminateStatus === 'Terminated'"
                type="info"
                style="display:block;margin-top:15px;margin-left:auto;margin-right:auto;"
                icon="el-icon-close"
                disabled
                @click="handleTerminate">
                {{ $t('topic.terminate') }}
              </el-button>
            </el-card>
          </el-col>
          <el-col v-if="nonPersistent===false" :span="4">
            <el-card>
              <h4>{{ $t('topic.compactionName') }}</h4>
              <el-button
                v-if="compaction === 'NOT_RUN' || compaction === 'SUCCESS'"
                type="primary"
                circle
                class="circle">
                <span class="circle-font">{{ compaction }}</span>
              </el-button>
              <el-button
                v-if="compaction === 'RUNNING'"
                type="success"
                circle
                class="circle">
                <span class="circle-font">{{ compaction }}</span>
              </el-button>
              <el-button
                v-if="compaction === 'ERROR'"
                type="danger"
                circle
                class="circle">
                <span class="circle-font">{{ compaction }}</span>
              </el-button>
              <el-button
                v-if="compaction !== 'RUNNING'"
                type="primary"
                style="display:block;margin-top:15px;margin-left:auto;margin-right:auto;"
                icon="el-icon-minus"
                @click="handleCompaction">
                {{ $t('topic.compaction') }}
              </el-button>
              <el-button
                v-if="compaction === 'RUNNING'"
                type="success"
                style="display:block;margin-top:15px;margin-left:auto;margin-right:auto;"
                icon="el-icon-minus"
                disabled
                @click="handleCompaction">
                {{ $t('topic.compaction') }}
              </el-button>
            </el-card>
          </el-col>
          <el-col v-if="nonPersistent===false" :span="4">
            <el-card>
              <h4>{{ $t('topic.offloadName') }}</h4>
              <el-button
                v-if="offload === 'NOT_RUN' || offload === 'SUCCESS'"
                type="primary"
                circle
                class="circle">
                <span class="circle-font">{{ offload }}</span>
              </el-button>
              <el-button
                v-if="offload === 'RUNNING'"
                type="success"
                circle
                class="circle">
                <span class="circle-font">{{ offload }}</span>
              </el-button>
              <el-button
                v-if="offload === 'ERROR'"
                type="danger"
                circle
                class="circle">
                <span class="circle-font">{{ offload }}</span>
              </el-button>
              <el-button
                v-if="offload !== 'RUNNING'"
                type="primary"
                style="display:block;margin-top:15px;margin-left:auto;margin-right:auto;"
                icon="el-icon-refresh"
                @click="handleOffload">
                {{ $t('topic.offload') }}
              </el-button>
              <el-button
                v-if="offload === 'RUNNING'"
                type="success"
                style="display:block;margin-top:15px;margin-left:auto;margin-right:auto;"
                icon="el-icon-refresh"
                disabled
                @click="handleOffload">
                {{ $t('topic.offload') }}
              </el-button>
            </el-card>
          </el-col>
        </el-row>
        <el-table
          :data="topicStats"
          border
          style="width: 100%">
          <el-table-column :label="$t('common.inMsg')" prop="inMsg"/>
          <el-table-column :label="$t('common.outMsg')" prop="outMsg"/>
          <el-table-column :label="$t('common.inBytes')" prop="inBytes"/>
          <el-table-column :label="$t('common.outBytes')" prop="outBytes"/>
        </el-table>
        <h4>{{ $t('topic.producer.producers') }}</h4>
        <el-row :gutter="24">
          <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}">
            <el-table
              v-loading="producersListLoading"
              :key="producerTableKey"
              :data="producersList"
              border
              fit
              highlight-current-row
              style="width: 100%;">
              <el-table-column :label="$t('topic.producer.producerId')" min-width="50px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.producerId }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.producer.producerName')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.producerName }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('common.inMsg')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.inMsg }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('common.inBytes')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.inBytes }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.producer.avgMsgSize')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.avgMsgSize }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.producer.address')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.address }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.producer.since')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.since }}</span>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>
        <h4>{{ $t('topic.subscription.subscriptions') }}</h4>
        <el-row :gutter="24">
          <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}">
            <el-table
              v-loading="subscriptionsListLoading"
              :key="subscriptionTableKey"
              :data="subscriptionsList"
              border
              fit
              highlight-current-row
              style="width: 100%;">
              <el-table-column :label="$t('topic.subscription.name')" min-width="50px" align="center">
                <template slot-scope="scope">
                  <router-link :to="scope.row.subscriptionLink" class="link-type">
                    <span>{{ scope.row.subscription }}</span>
                  </router-link>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.subscription.type')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.type }}</span>
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
              <el-table-column :label="$t('topic.subscription.msgExpired')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.msgExpired }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.subscription.backlog')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.backlog }}</span>
                  <el-dropdown>
                    <span class="el-dropdown-link"><i class="el-icon-more"/></span>
                    <el-dropdown-menu slot="dropdown">
                      <router-link :to="scope.row.subscriptionLink + '?topTab=backlogOperation&leftTab=skip'" class="link-type">
                        <el-dropdown-item command="skip">{{ $t('topic.subscription.skip') }}</el-dropdown-item>
                      </router-link>
                      <router-link :to="scope.row.subscriptionLink + '?topTab=backlogOperation&leftTab=expire'" class="link-type">
                        <el-dropdown-item command="expire">{{ $t('topic.subscription.expire') }}</el-dropdown-item>
                      </router-link>
                      <router-link :to="scope.row.subscriptionLink + '?topTab=backlogOperation&leftTab=clear'" class="link-type">
                        <el-dropdown-item command="clear">{{ $t('topic.subscription.clear') }}</el-dropdown-item>
                      </router-link>
                      <router-link :to="scope.row.subscriptionLink + '?topTab=backlogOperation&leftTab=reset'" class="link-type">
                        <el-dropdown-item command="reset">{{ $t('topic.subscription.reset') }}</el-dropdown-item>
                      </router-link>
                    </el-dropdown-menu>
                  </el-dropdown>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane v-if="nonPersistent===false" :label="$t('tabs.storage')" name="storage">
        <el-row :gutter="12">
          <el-col :span="8">
            <el-card class="box-card" shadow="always">
              <div slot="header" class="clearfix">
                <span>{{ $t('topic.subscription.storageSize') }}</span>
              </div>
              <el-button type="primary" class="circle">
                <span style="font-size: 200%;">{{ storageSize }}</span>
              </el-button>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card class="box-card" shadow="always">
              <div slot="header" class="clearfix">
                <span>{{ $t('topic.subscription.entries') }}</span>
              </div>
              <el-button type="primary" class="circle">
                <span style="font-size: 200%;">{{ entries }}</span>
              </el-button>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card class="box-card" shadow="always">
              <div slot="header" class="clearfix">
                <span>{{ $t('topic.subscription.segments') }}</span>
              </div>
              <el-button type="primary" class="circle">
                <span style="font-size: 200%;">{{ segments }}</span>
              </el-button>
            </el-card>
          </el-col>
        </el-row>
        <h4>{{ $t('topic.segment.label') }}</h4>
        <el-row :gutter="24">
          <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}">
            <el-table
              v-loading="segmentsListLoading"
              :key="segmentTableKey"
              :data="segmentsList"
              border
              fit
              highlight-current-row
              style="width: 100%;">
              <el-table-column :label="$t('topic.segment.ledgerId')" min-width="50px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.ledgerId }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.segment.entries')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.entries }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.segment.size')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.size }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.segment.status')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.status }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.segment.offload')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.offload }}</span>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>
        <h4>{{ $t('topic.cursor.cursors') }}</h4>
        <el-row :gutter="24">
          <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}">
            <el-table
              v-loading="cursorListLoading"
              :key="cursorTableKey"
              :data="cursorsList"
              border
              fit
              highlight-current-row
              style="width: 100%;">
              <el-table-column :label="$t('topic.cursor.label')" min-width="50px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.name }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.cursor.markDeletePosition')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.markDeletePosition }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.cursor.readPosition')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.readPosition }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.cursor.waitingReadOp')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.waitingReadOp }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.cursor.pendingReadOp')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.pendingReadOps }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.cursor.numberOfEntriesSinceFirstNotAckedMessage')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.numberOfEntriesSinceFirstNotAckedMessage }}</span>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane :label="$t('tabs.policies')" name="policies">
        <h4>{{ $t('topic.policy.authentication') }}
          <el-tooltip :content="authorizationContent" class="item" effect="dark" placement="top">
            <i class="el-icon-info"/>
          </el-tooltip>
        </h4>
        <hr class="split-line">
        <el-form>
          <el-tag
            v-for="tag in dynamicTags"
            :label="tag"
            :key="tag"
            :disable-transitions="false"
            style="margin-top:20px"
            class="role-el-tag">
            <div>
              <span> {{ tag }} </span>
            </div>
            <el-select
              v-model="roleMap[tag]"
              multiple
              placeholder="Please Select Options"
              style="width:300px;"
              @change="handleChangeOptions()">
              <el-option
                v-for="item in roleMapOptions[tag]"
                :key="item.value"
                :label="item.label"
                :value="item.value"
                style="width:300px"/>
            </el-select>
            <el-button @click.prevent="handleClose(tag)">{{ $t('topic.delete') }}</el-button>
          </el-tag>
          <el-form-item style="margin-top:30px">
            <el-input
              v-if="inputVisible"
              ref="saveTagInput"
              v-model="inputValue"
              style="margin-right:10px;width:200px;vertical-align:top"
              size="small"
              class="input-new-tag"
              @keyup.enter.native="handleInputConfirm"
              @blur="handleInputConfirm"
            />
            <el-button @click="showInput()">{{ $t('topic.addRole') }}</el-button>
            <!-- <el-button @click="revokeAllRole()">Revoke All</el-button> -->
          </el-form-item>
        </el-form>
        <h4 style="color:#E57470">{{ $t('common.dangerZone') }}</h4>
        <hr class="danger-line">
        <el-button type="danger" class="button" @click="handleDeleteTopic">{{ $t('topic.deleteTopic') }}</el-button>
      </el-tab-pane>
    </el-tabs>
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="30%">
      <el-form label-position="top">
        <el-form-item v-if="dialogStatus==='delete'">
          <h4>{{ $t(topic.deleteTopicMessage) }}</h4>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="deleteTopic">{{ $t('table.confirm') }}</el-button>
          <el-button @click="dialogFormVisible=false">{{ $t('table.cancel') }}</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import { fetchTenants } from '@/api/tenants'
import { fetchNamespaces } from '@/api/namespaces'
import {
  fetchTopicsByPulsarManager,
  getBundleRange,
  getTopicBroker,
  unload,
  fetchTopicStats,
  fetchTopicStatsInternal,
  terminate,
  compact,
  compactionStatus,
  offload,
  offloadStatus,
  deleteTopic
} from '@/api/topics'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
const defaultForm = {
  persistent: '',
  tenant: '',
  namespace: '',
  topic: '',
  partition: ''
}
export default {
  name: 'TopicInfo',
  components: {
    Pagination
  },
  data() {
    return {
      postForm: Object.assign({}, defaultForm),
      tenantsListOptions: [],
      namespacesListOptions: [],
      topicsListOptions: [],
      activeName: 'overview',
      topicStats: [],
      infoData: [],
      terminateStatus: '',
      compaction: 'NOT RUN',
      offload: 'NOT RUN',
      producersListLoading: false,
      producerTableKey: 0,
      producersList: [],
      producersTotal: 0,
      producersListQuery: {
        page: 1,
        limit: 0
      },
      subscriptionsListLoading: false,
      subscriptionTableKey: 0,
      subscriptionsList: [],
      subscriptionsTotal: 0,
      subscriptionsListQuery: {
        page: 1,
        limit: 0
      },
      segmentsListLoading: false,
      segmentTableKey: 0,
      segmentsList: [],
      storageSize: 0,
      entries: 0,
      segments: 0,
      dynamicTags: [],
      inputVisible: false,
      inputValue: '',
      roleValue: [],
      roleMap: {},
      roleMapOptions: {},
      roleOptions: [{
        value: 'consume',
        label: this.$i18n.t('role_actions.consume')
      }, {
        value: 'produce',
        label: this.$i18n.t('role_actions.produce')
      }, {
        value: 'functions',
        label: this.$i18n.t('role_actions.functions')
      }],
      authorizationContent: this.$i18n.t('topic.policy.authorizationContent'),
      topicName: '',
      firstInit: false,
      firstInitTopic: false,
      cursorTableKey: 0,
      cursorsList: [],
      cursorListLoading: false,
      currentTabName: '',
      nonPersistent: false,
      textMap: {
        delete: this.$i18n.t('topic.deleteTopic')
      },
      dialogFormVisible: false,
      dialogStatus: '',
      topicPartitions: {},
      partitionDisabled: false,
      partitionsListOptions: []
    }
  },
  created() {
    this.postForm.persistent = this.$route.params && this.$route.params.persistent
    this.postForm.tenant = this.$route.params && this.$route.params.tenant
    this.postForm.namespace = this.$route.params && this.$route.params.namespace
    this.postForm.topic = this.$route.params && this.$route.params.topic
    this.tenantNamespaceTopic = this.postForm.tenant + '/' + this.postForm.namespace + '/' + this.postForm.topic
    if (this.postForm.topic.indexOf('-partition-') > 0) {
      var splitTopic = this.postForm.topic.split('-partition-')
      this.postForm.partition = splitTopic[1]
      this.postForm.topic = splitTopic[0]
    } else {
      this.postForm.partition = '-1'
      this.partitionDisabled = true
    }
    if (this.$route.query && this.$route.query.tab) {
      this.activeName = this.$route.query.tab
    }
    if (this.postForm.persistent === 'persistent') {
      this.nonPersistent = false
    } else if (this.postForm.persistent === 'non-persistent') {
      this.nonPersistent = true
    }
    this.topicName = this.postForm.persistent + '://' + this.tenantNamespaceTopic
    this.firstInit = true
    this.firstInitTopic = true
    this.getRemoteTenantsList()
    this.getNamespacesList(this.postForm.tenant)
    this.getTopicsList()
    this.initBundleRange()
    this.initTopicBroker()
    this.initTopicStats()
    if (!this.nonPersistent) {
      this.initTerminateAndSegments()
      this.getCompactionStatus()
      this.getOffloadStatus()
    }
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
      } else {
        this.partitionDisabled = true
        if (this.postForm.partition !== '-1') {
          this.getTopicInfo()
        }
        this.postForm.partition = '-1'
        this.partitionsListOptions.push('-1')
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
    getOffloadStatus() {
      offloadStatus(this.postForm.persistent, this.getFullTopic()).then(response => {
        if (!response.data) return
        this.offload = response.data.status
      })
    },
    getCompactionStatus() {
      compactionStatus(this.postForm.persistent, this.getFullTopic()).then(response => {
        if (!response.data) return
        this.compaction = response.data.status
      })
    },
    initTopicStats() {
      fetchTopicStats(this.postForm.persistent, this.getFullTopic()).then(response => {
        if (!response.data) return
        this.topicStats.push({
          inMsg: response.data.msgRateIn,
          outMsg: response.data.msgRateOut,
          inBytes: response.data.msgThroughputIn,
          outBytes: response.data.msgThroughputOut
        })
        for (var i in response.data.publishers) {
          this.producersList.push({
            'producerId': response.data.publishers[i].producerId,
            'producerName': response.data.publishers[i].producerName,
            'inMsg': response.data.publishers[i].msgRateIn,
            'inBytes': response.data.publishers[i].msgThroughputIn,
            'avgMsgSize': response.data.publishers[i].averageMsgSize,
            'address': response.data.publishers[i].address,
            'since': response.data.publishers[i].connectedSince
          })
        }
        for (var s in response.data.subscriptions) {
          var type = ''
          if (response.data.subscriptions[s].hasOwnProperty('type')) {
            type = response.data.subscriptions[s].type
          }
          this.subscriptionsList.push({
            'subscription': s,
            'outMsg': response.data.subscriptions[s].msgRateOut,
            'outBytes': response.data.subscriptions[s].msgThroughputOut,
            'msgExpired': response.data.subscriptions[s].msgRateExpired,
            'backlog': response.data.subscriptions[s].msgBacklog,
            'type': type,
            // subscriptions/:persistent/:tenant/:namespace/:topic/:subscription/subscription
            'subscriptionLink': '/management/subscriptions/' + this.postForm.persistent + '/' + this.getFullTopic() + '/' + s + '/subscription'
          })
        }
        this.storageSize = response.data.storageSize
      })
    },
    initBundleRange() {
      getBundleRange(this.postForm.persistent, this.getFullTopic()).then(response => {
        if (!response.data) return
        this.infoData.push({
          infoColumn: 'bundle',
          data: response.data
        })
      })
    },
    initTopicBroker() {
      getTopicBroker(this.postForm.persistent, this.getFullTopic()).then(response => {
        if (!response.data) return
        this.infoData.push({
          infoColumn: 'broker',
          data: response.data.brokerUrl
        })
        this.infoData.push({
          infoColumn: 'httpUrl',
          data: response.data.httpUrl
        })
      })
    },
    initTerminateAndSegments() {
      fetchTopicStatsInternal(this.postForm.persistent, this.getFullTopic()).then(response => {
        if (!response.data) return
        this.terminateStatus = response.data.state
        this.segments = response.data.ledgers.length
        var lastIndex = this.segments - 1
        for (var i in response.data.ledgers) {
          if (lastIndex === parseInt(i)) {
            this.segmentsList.push({
              'ledgerId': response.data.ledgers[i]['ledgerId'],
              'entries': response.data.currentLedgerEntries,
              'size': response.data.currentLedgerSize,
              'offload': response.data.ledgers[i]['offloaded'],
              'status': 'opening'
            })
          } else {
            this.segmentsList.push({
              'ledgerId': response.data.ledgers[i]['ledgerId'],
              'entries': response.data.ledgers[i]['entries'],
              'size': response.data.ledgers[i]['size'],
              'offload': response.data.ledgers[i]['offloaded'],
              'status': 'closing'
            })
          }
        }
        this.entries = response.data.numberOfEntries
        for (var c in response.data.cursors) {
          this.cursorsList.push({
            'name': c,
            'markDeletePosition': response.data.cursors[c].markDeletePosition,
            'readPosition': response.data.cursors[c].readPosition,
            'waitingReadOp': response.data.cursors[c].waitingReadOp,
            'pendingReadOps': response.data.cursors[c].pendingReadOps,
            'numberOfEntriesSinceFirstNotAckedMessage': response.data.cursors[c].numberOfEntriesSinceFirstNotAckedMessage
          })
        }
      })
    },
    getNamespacesList(tenant) {
      fetchNamespaces(tenant, this.query).then(response => {
        if (!response.data) return
        let namespace = []
        this.namespacesListOptions = []
        if (this.firstInit) {
          this.firstInit = false
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
          this.topicPartitions[response.data.topics[i]['topic']] = response.data.topics[i]['partitions']
          if (response.data.topics[i]['topic'] === this.postForm.topic) {
            this.generatePartitions()
          }
        }
      })
    },
    getTopicInfo() {
      this.$router.push({ path: '/management/topics/' + this.postForm.persistent +
        '/' + this.getFullTopic() + '/topic?tab=' + this.currentTabName })
    },
    handleClick(tab, event) {
      this.currentTabName = tab.name
      this.$router.push({ query: { 'tab': tab.name }})
    },
    handleUnload() {
      unload(this.postForm.persistent, this.getFullTopic()).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('topic.notification.unloadTopicSuccess'),
          type: 'success',
          duration: 3000
        })
      })
    },
    handleTerminate() {
      terminate(this.postForm.persistent, this.getFullTopic()).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('topic.notification.terminateTopicSuccess'),
          type: 'success',
          duration: 3000
        })
        this.initTerminateAndSegments()
      })
    },
    handleClose(tag) {
      this.dynamicTags.splice(this.dynamicTags.indexOf(tag), 1)
    },
    showInput() {
      this.inputVisible = true
      this.$nextTick(_ => {
        this.$refs.saveTagInput.$refs.input.focus()
      })
    },
    handleInputConfirm() {
      const inputValue = this.inputValue
      if (inputValue) {
        if (this.roleMap.hasOwnProperty(inputValue)) {
          this.$message({
            message: this.$i18n.t('role.roleAlreadyExists'),
            type: 'error'
          })
          this.inputVisible = false
          this.inputValue = ''
          return
        }
        // grantPermissions(this.currentNamespace, inputValue, this.roleMap[inputValue]).then(response => {
        //   this.$notify({
        //     title: 'success',
        //     message: 'Add success',
        //     type: 'success',
        //     duration: 3000
        //   })
        //   this.dynamicTags.push(inputValue)
        //   this.roleMap[inputValue] = []
        //   this.roleMapOptions[inputValue] = this.roleOptions
        // })
      }
      this.inputVisible = false
      this.inputValue = ''
    },
    handleChangeOptions() {
      this.$forceUpdate()
    },
    getFullTopic() {
      var fullTopic = this.postForm.tenant + '/' + this.postForm.namespace + '/' + this.postForm.topic
      if (parseInt(this.postForm.partition) >= 0) {
        fullTopic += '-partition-' + this.postForm.partition
      }
      return fullTopic
    },
    handleCompaction() {
      compact(this.postForm.persistent, this.getFullTopic()).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('topic.notification.startCompactionSuccess'),
          type: 'success',
          duration: 3000
        })
        this.getCompactionStatus()
      })
    },
    handleOffload() {
      offload(this.postForm.persistent, this.getFullTopic()).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('topic.notification.startOffloadSuccess'),
          type: 'success',
          duration: 3000
        })
      })
      this.getOffloadStatus()
    },
    handleDeleteTopic() {
      this.dialogStatus = 'delete'
      this.dialogFormVisible = true
    },
    deleteTopic() {
      deleteTopic(this.postForm.persistent, this.getFullTopic()).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('topic.notification.deleteTopicSuccess'),
          type: 'success',
          duration: 3000
        })
        this.$router.push({ path: '/management/namespaces/' + this.postForm.tenant + '/' + this.postForm.namespace + '/namespace?tab=topics' })
      })
    }
  }
}
</script>

<style>
.split-line {
  background: #e6e9f3;
  border: none;
  height: 1px;
}
.circle {
  height: 150px !important;
  width: 150px !important;
  border-radius: 100% !important;
  display:block;
  margin:0 auto;
}
.el-icon-more {
  transform: rotate(90deg);
  -ms-transform: rotate(90deg); 	/* IE 9 */
  -moz-transform: rotate(90deg); 	/* Firefox */
  -webkit-transform: rotate(90deg); /* Safari 和 Chrome */
  -o-transform: rotate(90deg); 	/* Opera */
}
.square {
  width: 150px;
  height: 150px;
  display:block;
  margin:0 auto;
}
.danger-line {
  background: red;
  border: none;
  height: 1px;
}
</style>
