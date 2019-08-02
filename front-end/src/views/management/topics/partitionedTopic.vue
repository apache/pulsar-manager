<template>
  <div class="app-container">
    <div class="createPost-container">
      <el-form :inline="true" :model="postForm" label-position="top" class="form-container">
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
          <el-select v-model="postForm.topic" placeholder="select topic" @change="getPartitionTopicInfo()">
            <el-option v-for="(item,index) in topicsListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane label="OVERVIEW" name="overview">
        <el-table
          :data="partitionTopicStats"
          border
          style="width: 100%">
          <el-table-column prop="inMsg" label="In - msg/s"/>
          <el-table-column prop="outMsg" label="Out - msg/s"/>
          <el-table-column prop="inBytes" label="In - bytes/s"/>
          <el-table-column prop="outBytes" label="Out - bytes/s"/>
        </el-table>
        <h4>Partitions</h4>
        <hr class="split-line">
        <el-row :gutter="24">
          <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}" style="padding-right:8px;margin-bottom:30px;">
            <el-table
              :key="partitionTableKey"
              :data="partitionsList"
              border
              fit
              highlight-current-row
              style="width: 100%;">
              <el-table-column label="Partition" min-width="50px" align="center">
                <template slot-scope="scope">
                  <router-link :to="scope.row.partitionTopicLink" class="link-type">
                    <span>{{ scope.row.partition }}</span>
                  </router-link>
                </template>
              </el-table-column>
              <el-table-column label="Producers" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.producers }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Subscriptions" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.subscriptions }}</span>
                </template>
              </el-table-column>
              <el-table-column label="In - msg/s" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.inMsg }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Out - msg/s" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.outMsg }}</span>
                </template>
              </el-table-column>
              <el-table-column label="In - bytes/s" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.inBytes }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Out - bytes/s" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.outBytes }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Storage Size" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.storageSize }}</span>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane label="POLICIES" name="policies">
        <h4>Partitioned Topic Name</h4>
        <hr class="split-line">
        <span>{{ partitionedTopicName }}</span>
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
        <el-button type="danger" class="button" @click="handleDeletePartitionTopic">Delete Topic</el-button>
      </el-tab-pane>
    </el-tabs>
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
      authorizationContent: 'This is authorizationContent',
      partitionedTopicName: '',
      firstInit: false,
      firstInitTopic: false,
      currentTabName: ''
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
          inMsg: response.data.msgRateIn,
          outMsg: response.data.msgRateOut,
          inBytes: response.data.msgThroughputIn,
          outBytes: response.data.msgThroughputOut
        })
        for (var i in response.data.partitions) {
          var splitPartition = i.split('://')
          var partition = splitPartition[1].split('/')[2]
          this.partitionsList.push({
            'partition': partition,
            'producers': response.data.partitions[i].publishers.length,
            'subscriptions': Object.keys(response.data.partitions[i].subscriptions).length,
            'inMsg': response.data.partitions[i].msgRateIn,
            'outMsg': response.data.partitions[i].msgRateOut,
            'inBytes': response.data.partitions[i].msgThroughputIn,
            'outBytes': response.data.partitions[i].msgThroughputOut,
            'storageSize': response.data.partitions[i].storageSize,
            'partitionTopicLink': '/management/topics/' + this.postForm.persistent + '/' + splitPartition[1] + '/topic'
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
    handleDeletePartitionTopic() {
      deletePartitionTopic(this.postForm.persistent, this.tenantNamespaceTopic).then(response => {
        this.$notify({
          title: 'success',
          message: 'Delete partitioned topic success',
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
