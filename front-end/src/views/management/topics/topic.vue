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
          <el-select v-model="postForm.topic" placeholder="select topic" @change="getTopicInfo()">
            <el-option v-for="(item,index) in topicsListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane label="OVERVIEW" name="overview">
        <el-row :gutter="12">
          <el-col :span="12">
            <el-card style="height: 305px">
              <h4>INFO</h4>
              <el-table
                :data="infoData"
                :show-header="false"
                border
                style="width: 100%">
                <el-table-column prop="infoColumn" label="column"/>
                <el-table-column prop="data" label="data"/>
              </el-table>
              <el-button class="filter-item" type="primary" style="margin-top:15px;" @click="handleUnload">Unload</el-button>
            </el-card>
          </el-col>
          <el-col v-if="nonPersistent===false" :span="4">
            <el-card>
              <h4>STATUS</h4>
              <el-button type="primary" circle class="circle"><span class="circle-font">{{ terminateStatus }}</span></el-button>
              <el-button type="primary" style="display:block;margin-top:15px;margin-left:auto;margin-right:auto;" @click="handleTerminate">terminate</el-button>
            </el-card>
          </el-col>
          <el-col v-if="nonPersistent===false" :span="4">
            <el-card>
              <h4>COMPACTION</h4>
              <el-button type="primary" circle class="circle"><span class="circle-font">{{ compaction }}</span></el-button>
              <el-button type="primary" style="display:block;margin-top:15px;margin-left:auto;margin-right:auto;" @click="handleCompaction">compaction</el-button>
            </el-card>
          </el-col>
          <el-col v-if="nonPersistent===false" :span="4">
            <el-card>
              <h4>OFFLOAD</h4>
              <el-button type="primary" circle class="circle"><span class="circle-font">{{ offload }}</span></el-button>
              <el-button type="primary" style="display:block;margin-top:15px;margin-left:auto;margin-right:auto;" @click="handleOffload">offload</el-button>
            </el-card>
          </el-col>
        </el-row>
        <el-table
          :data="topicStats"
          border
          style="width: 100%">
          <el-table-column prop="inMsg" label="In - msg/s"/>
          <el-table-column prop="outMsg" label="Out - msg/s"/>
          <el-table-column prop="inBytes" label="In - bytes/s"/>
          <el-table-column prop="outBytes" label="Out - bytes/s"/>
        </el-table>
        <h4>Producers</h4>
        <el-row :gutter="24">
          <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}" style="padding-right:8px;margin-bottom:30px;">
            <el-table
              v-loading="producersListLoading"
              :key="producerTableKey"
              :data="producersList"
              border
              fit
              highlight-current-row
              style="width: 100%;">
              <el-table-column label="Producer Id" min-width="50px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.producerId }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Producer Name" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.producerName }}</span>
                </template>
              </el-table-column>
              <el-table-column label="In - msg/s" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.inMsg }}</span>
                </template>
              </el-table-column>
              <el-table-column label="In - bytes/s" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.inBytes }}</span>
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
            <pagination v-show="producersTotal>0" :total="producersTotal" :page.sync="producersListQuery.page" :limit.sync="producersListQuery.limit" @pagination="getProducers" />
          </el-col>
        </el-row>
        <h4>Subscriptions</h4>
        <el-row :gutter="24">
          <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}" style="padding-right:8px;margin-bottom:30px;">
            <el-table
              v-loading="subscriptionsListLoading"
              :key="subscriptionTableKey"
              :data="subscriptionsList"
              border
              fit
              highlight-current-row
              style="width: 100%;">
              <el-table-column label="Subscription" min-width="50px" align="center">
                <template slot-scope="scope">
                  <router-link :to="scope.row.subscriptionLink" class="link-type">
                    <span>{{ scope.row.subscription }}</span>
                  </router-link>
                </template>
              </el-table-column>
              <el-table-column label="type" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.type }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Out - msg/s" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.outMsg }}</span>
                </template>
              </el-table-column>
              <el-table-column label="outBytes" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.outBytes }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Msg Expired" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.msgExpired }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Backlog" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.backlog }}</span>
                  <el-dropdown>
                    <span class="el-dropdown-link"><i class="el-icon-more"/></span>
                    <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item>INSPECT</el-dropdown-item>
                      <el-dropdown-item>SKIP</el-dropdown-item>
                      <el-dropdown-item>EXPIRE</el-dropdown-item>
                      <el-dropdown-item>CLEAR</el-dropdown-item>
                      <el-dropdown-item>RESET</el-dropdown-item>
                    </el-dropdown-menu>
                  </el-dropdown>
                </template>
              </el-table-column>
            </el-table>
            <pagination v-show="subscriptionsTotal>0" :total="subscriptionsTotal" :page.sync="subscriptionsListQuery.page" :limit.sync="subscriptionsListQuery.limit" @pagination="getSubscriptions" />
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane v-if="nonPersistent===false" label="STORAGE" name="storage">
        <el-row :gutter="12">
          <el-col :span="8">
            <el-card>
              <el-button type="primary" class="circle"><span>Storage Size <br>{{ storageSize }}</span></el-button>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card>
              <el-button type="primary" class="circle"><span>Entries <br>{{ entries }}</span></el-button>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card>
              <el-button type="primary" class="circle"><span>Segments<br>{{ segments }}</span></el-button>
            </el-card>
          </el-col>
        </el-row>
        <h4>Segments</h4>
        <el-row :gutter="24">
          <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}" style="padding-right:8px;margin-bottom:30px;">
            <el-table
              v-loading="segmentsListLoading"
              :key="segmentTableKey"
              :data="segmentsList"
              border
              fit
              highlight-current-row
              style="width: 100%;">
              <el-table-column label="Ledger Id" min-width="50px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.ledgerId }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Entries" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.entries }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Size" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.size }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Status" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.status }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Offload" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.offload }}</span>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}" style="padding-right:8px;margin-bottom:30px;">
            <el-table
              v-loading="cursorListLoading"
              :key="cursorTableKey"
              :data="cursorsList"
              border
              fit
              highlight-current-row
              style="width: 100%;">
              <el-table-column label="Name" min-width="50px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.name }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Mark Delete Position" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.markDeletePosition }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Read Position" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.readPosition }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Waiting Read Op" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.waitingReadOp }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Pending Read Ops" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.pendingReadOps }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Entries Since First Not AckedMessage" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.numberOfEntriesSinceFirstNotAckedMessage }}</span>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane label="POLICIES" name="policies">
        <h4>Topic Name</h4>
        <hr class="split-line">
        <span>{{ topicName }}</span>
        <h4>Authorization
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
            <el-button @click.prevent="handleClose(tag)">删除</el-button>
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
            <el-button @click="showInput()">Add Role</el-button>
            <!-- <el-button @click="revokeAllRole()">Revoke All</el-button> -->
          </el-form-item>
        </el-form>
        <h4>Danager Zone</h4>
        <hr class="danger-line">
        <el-button type="danger" class="button" @click="handleDeleteTopic">Delete Topic</el-button>
      </el-tab-pane>
    </el-tabs>
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
  topic: ''
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
        label: 'consume'
      }, {
        value: 'produce',
        label: 'produce'
      }, {
        value: 'functions',
        label: 'functions'
      }],
      authorizationContent: 'This is authorizationContent',
      topicName: '',
      firstInit: false,
      firstInitTopic: false,
      cursorTableKey: 0,
      cursorsList: [],
      cursorListLoading: false,
      currentTabName: '',
      nonPersistent: false
    }
  },
  created() {
    this.postForm.persistent = this.$route.params && this.$route.params.persistent
    this.postForm.tenant = this.$route.params && this.$route.params.tenant
    this.postForm.namespace = this.$route.params && this.$route.params.namespace
    this.postForm.topic = this.$route.params && this.$route.params.topic
    this.tenantNamespaceTopic = this.postForm.tenant + '/' + this.postForm.namespace + '/' + this.postForm.topic
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
    getRemoteTenantsList() {
      fetchTenants().then(response => {
        if (!response.data) return
        for (var i = 0; i < response.data.total; i++) {
          this.tenantsListOptions.push(response.data.data[i].tenant)
        }
      })
    },
    getOffloadStatus() {
      offloadStatus(this.postForm.persistent, this.tenantNamespaceTopic).then(response => {
        if (!response.data) return
        this.offload = response.data.status
      })
    },
    getCompactionStatus() {
      compactionStatus(this.postForm.persistent, this.tenantNamespaceTopic).then(response => {
        if (!response.data) return
        this.compaction = response.data.status
      })
    },
    initTopicStats() {
      fetchTopicStats(this.postForm.persistent, this.tenantNamespaceTopic).then(response => {
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
            'subscriptionLink': '/management/subscriptions/' + this.postForm.persistent + '/' + this.tenantNamespaceTopic + '/' + s + '/subscription'
          })
        }
        this.storageSize = response.data.storageSize
      })
    },
    initBundleRange() {
      getBundleRange(this.postForm.persistent, this.tenantNamespaceTopic).then(response => {
        if (!response.data) return
        this.infoData.push({
          infoColumn: 'bundle',
          data: response.data
        })
      })
    },
    initTopicBroker() {
      getTopicBroker(this.postForm.persistent, this.tenantNamespaceTopic).then(response => {
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
      fetchTopicStatsInternal(this.postForm.persistent, this.tenantNamespaceTopic).then(response => {
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
          if (response.data.topics[i]['partitions'] === '0') {
            this.topicsListOptions.push(response.data.topics[i]['topic'])
          }
        }
      })
    },
    getTopicInfo() {
      this.$router.push({ path: '/management/topics/' + this.postForm.persistent +
        '/' + this.postForm.tenant + '/' + this.postForm.namespace + '/' + this.postForm.topic + '/topic?tab=' + this.currentTabName })
    },
    handleClick(tab, event) {
      this.currentTabName = tab.name
      this.$router.push({ query: { 'tab': tab.name }})
    },
    handleUnload() {
      unload(this.postForm.persistent, this.tenantNamespaceTopic).then(response => {
        this.$notify({
          title: 'success',
          message: 'Unload topic success',
          type: 'success',
          duration: 3000
        })
      })
    },
    handleTerminate() {
      terminate(this.postForm.persistent, this.tenantNamespaceTopic).then(response => {
        this.$notify({
          title: 'success',
          message: 'Terminate topic success',
          type: 'success',
          duration: 3000
        })
        this.initTerminateAndSegments()
      })
    },
    getProducers() {
    },
    getSubscriptions() {
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
            message: 'This role is exist',
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
    handleCompaction() {
      compact(this.postForm.persistent, this.tenantNamespaceTopic).then(response => {
        this.$notify({
          title: 'success',
          message: 'Start topic compaction requested',
          type: 'success',
          duration: 3000
        })
        this.getCompactionStatus()
      })
    },
    handleOffload() {
      offload(this.postForm.persistent, this.tenantNamespaceTopic).then(response => {
        this.$notify({
          title: 'success',
          message: 'Start topic offload requested',
          type: 'success',
          duration: 3000
        })
      })
      this.getOffloadStatus()
    },
    handleDeleteTopic() {
      deleteTopic(this.postForm.persistent, this.tenantNamespaceTopic).then(response => {
        this.$notify({
          title: 'success',
          message: 'Delete topic success',
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
