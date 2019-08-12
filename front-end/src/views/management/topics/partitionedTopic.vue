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
              border
              fit
              highlight-current-row
              style="width: 100%;">
              <el-table-column :label="$t('topic.partition')" min-width="50px" align="left">
                <template slot-scope="scope">
                  <router-link :to="scope.row.partitionTopicLink" class="link-type">
                    <span>{{ scope.row.partition }}</span>
                  </router-link>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.producer.producerNumber')" min-width="30px" align="left">
                <template slot-scope="scope">
                  <span>{{ scope.row.producers }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.subscription.subscriptionNumber')" min-width="30px" align="left">
                <template slot-scope="scope">
                  <span>{{ scope.row.subscriptions }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('common.inMsg')" min-width="30px" align="left">
                <template slot-scope="scope">
                  <span>{{ scope.row.inMsg }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('common.outMsg')" min-width="30px" align="left">
                <template slot-scope="scope">
                  <span>{{ scope.row.outMsg }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('common.inBytes')" min-width="30px" align="left">
                <template slot-scope="scope">
                  <span>{{ scope.row.inBytes }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('common.outBytes')" min-width="30px" align="left">
                <template slot-scope="scope">
                  <span>{{ scope.row.outBytes }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('common.storageSize')" min-width="30px" align="left">
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
        <el-button type="danger" class="button" @click="handleDeletePartitionTopic">Delete Topic</el-button>
      </el-tab-pane>
    </el-tabs>
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="30%">
      <el-form label-position="top">
        <el-form-item v-if="dialogStatus==='delete'">
          <h4>{{ $t('topic.deleteTopicMessage') }}</h4>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="deleteParititionTopic">{{ $t('table.confirm') }}</el-button>
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
  fetchPartitionTopicStats,
  deletePartitionTopic
} from '@/api/topics'
import { fetchTopicsByPulsarManager } from '@/api/topics'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
const defaultForm = {
  persistent: '',
  tenant: '',
  namespace: '',
  topic: ''
}
export default {
  name: 'ParititionTopicInfo',
  components: {
    Pagination
  },
  data() {
    return {
      postForm: Object.assign({}, defaultForm),
      activeName: 'overview',
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
        delete: this.$i18n.t('topic.deleteTopic')
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
      }
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
    initTopicStats() {
      fetchPartitionTopicStats(this.postForm.persistent, this.tenantNamespaceTopic, true).then(response => {
        if (!response.data) return
        this.partitionTopicStats.push({
          inMsg: response.data.msgRateIn.toFixed(2),
          outMsg: response.data.msgRateOut.toFixed(2),
          inBytes: response.data.msgThroughputIn.toFixed(2),
          outBytes: response.data.msgThroughputOut.toFixed(2)
        })
        for (var i in response.data.partitions) {
          var splitPartition = i.split('://')
          var partition = splitPartition[1].split('/')[2]
          this.partitionsList.push({
            'partition': partition,
            'producers': response.data.partitions[i].publishers.length,
            'subscriptions': Object.keys(response.data.partitions[i].subscriptions).length,
            'inMsg': response.data.partitions[i].msgRateIn.toFixed(2),
            'outMsg': response.data.partitions[i].msgRateOut.toFixed(2),
            'inBytes': response.data.partitions[i].msgThroughputIn.toFixed(2),
            'outBytes': response.data.partitions[i].msgThroughputOut.toFixed(2),
            'storageSize': response.data.partitions[i].storageSize.toFixed(2),
            'partitionTopicLink': '/management/topics/' + this.postForm.persistent + '/' + splitPartition[1] + '/topic'
          })
        }
        var index = 0
        for (var s in response.data.subscriptions) {
          index += 1
          var type = 'Exclusive'
          var children = []
          if (response.data.subscriptions[s].hasOwnProperty('type')) {
            type = response.data.subscriptions[s].type
          }
          for (var j in response.data.partitions) {
            var subSplitPartition = j.split('://')
            var subPartition = subSplitPartition[1].split('/')[2]
            if (response.data.partitions[j].hasOwnProperty('subscriptions')) {
              for (var p in response.data.partitions[j].subscriptions) {
                if (p === s) {
                  children.push({
                    'id': 1000000 * (index + 1) + j,
                    'subscription': subPartition,
                    'outMsg': response.data.partitions[j].subscriptions[p].msgRateOut.toFixed(2),
                    'outBytes': response.data.partitions[j].subscriptions[p].msgThroughputOut.toFixed(2),
                    'msgExpired': response.data.partitions[j].subscriptions[p].msgRateExpired.toFixed(2),
                    'backlog': response.data.partitions[j].subscriptions[p].msgBacklog,
                    'type': type,
                    'subscriptionLink': '/management/subscriptions/' + this.postForm.persistent + '/' + subSplitPartition[1] + '/' + s + '/subscription',
                    'enableSubscriptionLink': true
                  })
                }
              }
            }
          }

          this.subscriptionsList.push({
            'id': index,
            'subscription': s,
            'outMsg': response.data.subscriptions[s].msgRateOut.toFixed(2),
            'outBytes': response.data.subscriptions[s].msgThroughputOut.toFixed(2),
            'msgExpired': response.data.subscriptions[s].msgRateExpired.toFixed(2),
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
          if (response.data.topics[i]['partitions'] !== '0') {
            this.topicsListOptions.push(response.data.topics[i]['topic'])
          }
        }
      })
    },
    getPartitionTopicInfo() {
      this.$router.push({ path: '/management/topics/' + this.postForm.persistent +
        '/' + this.postForm.tenant + '/' + this.postForm.namespace + '/' + this.postForm.topic + '/partitionedTopic?tab=' + this.currentTabName })
    },
    handleClick(tab, event) {
      this.currentTabName = tab.name
      this.$router.push({ query: { 'tab': tab.name }})
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
      this.dialogFormVisible = true
      this.dialogStatus = 'delete'
    },
    deleteParititionTopic() {
      deletePartitionTopic(this.postForm.persistent, this.tenantNamespaceTopic).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('topic.notification.deletePartitionedTopicSuccess'),
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
.danger-line {
  background: red;
  border: none;
  height: 1px;
}
</style>
