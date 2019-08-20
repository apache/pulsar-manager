<template>
  <div class="app-container">
    <div class="createPost-container">
      <el-form :inline="true" :model="postForm" label-position="top" class="form-container">
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
          <el-select v-model="postForm.topic" placeholder="select topic" @change="getPartitionTopicInfo()">
            <el-option v-for="(item,index) in topicsListOptions" :key="item+index" :label="item" :value="item"/>
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
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane :label="$t('tabs.overview')" name="overview">
        <el-table
          :data="partitionTopicStats"
          border
          style="width: 100%">
          <el-table-column :label="$t('common.inMsg')" prop="inMsg"/>
          <el-table-column :label="$t('common.outMsg')" prop="outMsg"/>
          <el-table-column :label="$t('common.inBytes')" prop="inBytes"/>
          <el-table-column :label="$t('common.outBytes')" prop="outBytes"/>
        </el-table>
        <h4>{{ $t('topic.subscription.subscriptions') }}</h4>
        <el-row :gutter="24">
          <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}">
            <el-table
              v-loading="subscriptionsListLoading"
              :key="subscriptionTableKey"
              :data="subscriptionsList"
              :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
              border
              fit
              highlight-current-row
              style="width: 100%;"
              row-key="id">
              <el-table-column :label="$t('topic.subscription.name')" min-width="50px" align="left">
                <template slot-scope="scope">
                  <router-link v-if="scope.row.enableSubscriptionLink===true" :to="scope.row.subscriptionLink" class="link-type">
                    <span>{{ scope.row.subscription }}</span>
                  </router-link>
                  <span v-if="scope.row.enableSubscriptionLink===false">{{ scope.row.subscription }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.subscription.type')" min-width="30px" align="left">
                <template slot-scope="scope">
                  <span>{{ scope.row.type }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('common.outMsg')" min-width="30px" align="left">
                <template slot-scope="scope">
                  <span>{{ scope.row.outMsg }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('common.outBytes')" min-width="30px" align="left">
                <template slot-scope="scope">
                  <span>{{ scope.row.outBytes }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.subscription.msgExpired')" min-width="30px" align="left">
                <template slot-scope="scope">
                  <span>{{ scope.row.msgExpired }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.subscription.backlog')" min-width="30px" align="left">
                <template slot-scope="scope">
                  <span>{{ scope.row.backlog }}</span>
                  <el-dropdown v-if="scope.row.enableSubscriptionLink===true">
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
                  <el-dropdown v-else @command="handleAllSub">
                    <span class="el-dropdown-link"><i class="el-icon-more"/></span>
                    <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item :command="{'action': 'expire', 'subscription': scope.row.subscription }">
                        {{ $t('topic.subscription.expire') }}
                      </el-dropdown-item>
                      <el-dropdown-item :command="{'action': 'reset', 'subscription': scope.row.subscription }">
                        {{ $t('topic.subscription.reset') }}
                      </el-dropdown-item>
                      <el-dropdown-item :command="{'action': 'clear', 'subscription': scope.row.subscription }">
                        {{ $t('topic.subscription.clear') }}
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </el-dropdown>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>
        <h4>{{ $t('topic.partitions') }}</h4>
        <hr class="split-line">
        <el-row :gutter="24">
          <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}">
            <el-table
              :key="partitionTableKey"
              :data="partitionsList"
              :default-sort = "{prop: 'partition', order: 'descending'}"
              border
              fit
              highlight-current-row
              style="width: 100%;">
              <el-table-column :label="$t('topic.partition')" sortable min-width="50px" align="left" prop="partiton">
                <template slot-scope="scope">
                  <router-link :to="scope.row.partitionTopicLink" class="link-type">
                    <span>{{ scope.row.partition }}</span>
                  </router-link>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.producer.producerNumber')" min-width="30px" align="left" prop="producers">
                <template slot-scope="scope">
                  <span>{{ scope.row.producers }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.subscription.subscriptionNumber')" min-width="30px" align="left" prop="subscriptions">
                <template slot-scope="scope">
                  <span>{{ scope.row.subscriptions }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('common.inMsg')" min-width="30px" align="left" prop="inMsg">
                <template slot-scope="scope">
                  <span>{{ scope.row.inMsg }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('common.outMsg')" min-width="30px" align="left" prop="outMsg">
                <template slot-scope="scope">
                  <span>{{ scope.row.outMsg }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('common.inBytes')" min-width="30px" align="left" prop="inBytes">
                <template slot-scope="scope">
                  <span>{{ scope.row.inBytes }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('common.outBytes')" min-width="30px" align="left" prop="outBytes">
                <template slot-scope="scope">
                  <span>{{ scope.row.outBytes }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('common.storageSize')" min-width="30px" align="left" prop="storageSize">
                <template slot-scope="scope">
                  <span>{{ scope.row.storageSize }}</span>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane label="POLICIES" name="policies">
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
              :placeholder="$t('topic.selectRoleMessage')"
              multiple
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
        <el-button type="danger" class="button" @click="handleDeletePartitionTopic">{{ $t('topic.deleteTopic') }}</el-button>
      </el-tab-pane>
    </el-tabs>
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="30%">
      <el-form ref="form" :model="form" :rules="rules" label-position="top">
        <el-form-item v-if="dialogStatus==='delete'">
          <h4>{{ $t('topic.deleteTopicMessage') }}</h4>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='expire'">
          <el-form-item prop="expireTime">
            <el-input v-model="form.expireTime" :placeholder="$t('topic.subscription.expireTimePlaceholder')"/>
          </el-form-item>
          <span>{{ $t('topic.subscription.expireMessage') }}</span>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='reset'">
          <el-form-item prop="resetByTime">
            <el-input v-model="form.resetByTime"/>
          </el-form-item>
          <span>{{ $t('topic.subscription.resetByTimeMessage') }}</span>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='clear'">
          <el-form-item>
            <span>{{ $t('topic.subscription.clearMessageConfirm') }}</span>
          </el-form-item>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleOptions()">{{ $t('table.confirm') }}</el-button>
          <el-button @click="dialogFormVisible=false">{{ $t('table.cancel') }}</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import { fetchTenants } from '@/api/tenants'
import { fetchNamespaces, getClusters } from '@/api/namespaces'
import {
  fetchPartitionTopicStats,
  deletePartitionTopicOnCluster,
  expireMessagesAllSubscriptionsOnCluster,
  resetCursorByTimestampOnCluster,
  clearBacklogOnCluster
} from '@/api/topics'
import { fetchTopicsByPulsarManager } from '@/api/topics'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import { formatBytes } from '@/utils/index'
import { numberFormatter } from '@/filters/index'

const defaultForm = {
  persistent: '',
  tenant: '',
  namespace: '',
  topic: ''
}
const defaultClusterForm = {
  cluster: ''
}
export default {
  name: 'ParititionTopicInfo',
  components: {
    Pagination
  },
  data() {
    return {
      postForm: Object.assign({}, defaultForm),
      clusterForm: Object.assign({}, defaultClusterForm),
      activeName: 'overview',
      replicatedClusters: [],
      tenantsListOptions: [],
      namespacesListOptions: [],
      topicsListOptions: [],
      partitionTopicStats: [],
      partitionsList: [],
      partitionTableKey: 0,
      partitionsListLoading: false,
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
      authorizationContent: this.$i18n.t('topic.policy.authorizationContent'),
      partitionedTopicName: '',
      firstInit: false,
      firstInitTopic: false,
      currentTabName: '',
      textMap: {
        delete: this.$i18n.t('topic.deleteTopic'),
        expire: this.$i18n.t('topic.subscription.msgExpired'),
        clear: this.$i18n.t('topic.subscription.clearMessage'),
        reset: this.$i18n.t('topic.subscription.resetByTime')
      },
      dialogFormVisible: false,
      dialogStatus: '',
      subscriptionsListLoading: false,
      subscriptionTableKey: 0,
      subscriptionsList: [],
      subscriptionsTotal: 0,
      subscriptionsListQuery: {
        page: 1,
        limit: 0
      },
      form: {
        expireTime: '',
        resetByTime: ''
      },
      rules: {
        expireTime: [{ required: true, message: this.$i18n.t('topic.subscription.expireTimeRequired'), trigger: 'blur' }],
        resetByTime: [{ required: true, message: this.$i18n.t('topic.subscription.resetByTimeRequired'), trigger: 'blur' }]
      },
      currentSubscription: ''
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
    this.partitionedTopicName = this.postForm.persistent + '://' + this.tenantNamespaceTopic
    this.firstInit = true
    this.firstInitTopic = true
    this.getRemoteTenantsList()
    this.getNamespacesList(this.postForm.tenant)
    this.getTopicsList()
    this.getReplicatedClusters()
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
    initTopicStats() {
      fetchPartitionTopicStats(this.postForm.persistent, this.tenantNamespaceTopic, true).then(response => {
        if (!response.data) return
        this.partitionTopicStats.push({
          inMsg: numberFormatter(response.data.msgRateIn, 2),
          outMsg: numberFormatter(response.data.msgRateOut, 2),
          inBytes: formatBytes(response.data.msgThroughputIn),
          outBytes: formatBytes(response.data.msgThroughputOut)
        })
        var prefix = this.postForm.persistent + '://' + this.tenantNamespaceTopic
        var tempPartitionsList = Object.keys(response.data.partitions)
        for (var i = 0; i < tempPartitionsList.length; i++) {
          var key = prefix + '-partition-' + i
          var partition = this.postForm.topic + '-partition-' + i
          this.partitionsList.push({
            'partition': partition,
            'producers': response.data.partitions[key].publishers.length,
            'subscriptions': Object.keys(response.data.partitions[key].subscriptions).length,
            'inMsg': numberFormatter(response.data.partitions[key].msgRateIn, 2),
            'outMsg': numberFormatter(response.data.partitions[key].msgRateOut, 2),
            'inBytes': formatBytes(response.data.partitions[key].msgThroughputIn),
            'outBytes': formatBytes(response.data.partitions[key].msgThroughputOut),
            'storageSize': formatBytes(response.data.partitions[key].storageSize, 0),
            'partitionTopicLink': '/management/topics/' + this.postForm.persistent + '/' + this.tenantNamespaceTopic + '-partition-' + i + '/topic'
          })
        }
        var index = 0
        for (var s in response.data.subscriptions) {
          index += 1
          var type = 'Exclusive'
          var children = []
          for (var j in response.data.partitions) {
            var subSplitPartition = j.split('://')
            var subPartition = subSplitPartition[1].split('/')[2]
            if (response.data.partitions[j].hasOwnProperty('subscriptions')) {
              for (var p in response.data.partitions[j].subscriptions) {
                if (p === s) {
                  children.push({
                    'id': 1000000 * (index + 1) + j,
                    'subscription': subPartition,
                    'outMsg': numberFormatter(response.data.partitions[j].subscriptions[p].msgRateOut, 2),
                    'outBytes': formatBytes(response.data.partitions[j].subscriptions[p].msgThroughputOut),
                    'msgExpired': numberFormatter(response.data.partitions[j].subscriptions[p].msgRateExpired, 2),
                    'backlog': response.data.partitions[j].subscriptions[p].msgBacklog,
                    'type': response.data.partitions[j].subscriptions[p].type,
                    'subscriptionLink': '/management/subscriptions/' + this.postForm.persistent + '/' + subSplitPartition[1] + '/' + s + '/subscription',
                    'enableSubscriptionLink': true
                  })
                  type = response.data.partitions[j].subscriptions[p].type
                }
              }
            }
          }

          this.subscriptionsList.push({
            'id': index,
            'subscription': s,
            'outMsg': numberFormatter(response.data.subscriptions[s].msgRateOut, 2),
            'outBytes': formatBytes(response.data.subscriptions[s].msgThroughputOut),
            'msgExpired': numberFormatter(response.data.subscriptions[s].msgRateExpired, 2),
            'backlog': response.data.subscriptions[s].msgBacklog,
            'type': type,
            'children': children,
            'subscriptionLink': '#',
            'enableSubscriptionLink': false
          })
        }
      })
    },
    getNamespacesList(tenant) {
      let namespace = []
      this.namespacesListOptions = []
      this.topicsListOptions = []
      if (this.firstInit) {
        this.firstInit = false
      } else {
        this.postForm.namespace = ''
        this.postForm.topic = ''
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
      fetchTopicsByPulsarManager(this.postForm.tenant, this.postForm.namespace).then(response => {
        if (!response.data) return
        this.topicsListOptions = []
        if (this.firstInitTopic) {
          this.firstInitTopic = false
        } else {
          this.postForm.topic = ''
        }
        for (var i in response.data.topics) {
          if (response.data.topics[i]['partitions'] !== '0') {
            this.topicsListOptions.push(response.data.topics[i]['topic'])
          }
        }
      })
    },
    getPartitionTopicInfo() {
      this.$router.push({ path: '/management/topics/' + this.postForm.persistent +
        '/' + this.postForm.tenant + '/' + this.postForm.namespace + '/' +
        this.postForm.topic + '/partitionedTopic?tab=' + this.currentTabName + '&cluster=' + this.getCurrentCluster()
      })
    },
    getCurrentCluster() {
      return this.clusterForm.cluster || ''
    },
    handleClick(tab, event) {
      this.currentTabName = tab.name
      this.$router.push({ query: {
        'tab': tab.name,
        'cluster': this.getCurrentCluster()
      }})
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
    handleDeletePartitionTopic() {
      this.postForm.expireTime = ''
      this.dialogFormVisible = true
      this.dialogStatus = 'delete'
    },
    deleteParititionTopic() {
      deletePartitionTopicOnCluster(this.getCurrentCluster(), this.postForm.persistent, this.tenantNamespaceTopic).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('topic.notification.deletePartitionedTopicSuccess'),
          type: 'success',
          duration: 3000
        })
        this.$router.push({ path: '/management/namespaces/' + this.postForm.tenant + '/' + this.postForm.namespace + '/namespace?tab=topics' })
      })
    },
    handleOptions() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          switch (this.dialogStatus) {
            case 'delete':
              this.deleteParititionTopic()
              break
            case 'expire':
              this.expireAllSubMessage()
              break
            case 'reset':
              this.resetAllSubMessage()
              break
            case 'clear':
              this.clearAllSubMessage()
              break
          }
        }
      })
    },
    expireAllSubMessage() {
      expireMessagesAllSubscriptionsOnCluster(
        this.getCurrentCluster(),
        this.postForm.persistent,
        this.tenantNamespaceTopic,
        this.form.expireTime).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('topic.notification.expireMessageSuccess'),
          type: 'success',
          duration: 3000
        })
        this.dialogFormVisible = false
      })
    },
    handleAllSub(command, subscription) {
      this.dialogFormVisible = true
      this.dialogStatus = command.action
      this.currentSubscription = command.subscription
    },
    resetAllSubMessage() {
      resetCursorByTimestampOnCluster(
        this.getCurrentCluster(),
        this.postForm.persistent,
        this.tenantNamespaceTopic,
        this.currentSubscription,
        this.form.expireTime).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('topic.notification.resetMessageSuccess'),
          type: 'success',
          duration: 3000
        })
        this.dialogFormVisible = false
      })
    },
    clearAllSubMessage() {
      clearBacklogOnCluster(
        this.getCurrentCluster(),
        this.postForm.persistent,
        this.tenantNamespaceTopic,
        this.currentSubscription).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('topic.notification.clearMessageSuccess'),
          type: 'success',
          duration: 3000
        })
        this.dialogFormVisible = false
        this.getPartitionTopicInfo()
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
.danger-line {
  background: red;
  border: none;
  height: 1px;
}
.el-icon-more {
  transform: rotate(90deg);
  -ms-transform: rotate(90deg); 	/* IE 9 */
  -moz-transform: rotate(90deg); 	/* Firefox */
  -webkit-transform: rotate(90deg); /* Safari 和 Chrome */
  -o-transform: rotate(90deg); 	/* Opera */
}
</style>
