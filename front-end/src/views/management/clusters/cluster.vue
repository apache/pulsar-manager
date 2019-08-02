<template>
  <div class="app-container">
    <div class="createPost-container">
      <el-form :inline="true" :model="postForm" class="form-container">
        <el-form-item class="postInfo-container-item" label="Cluster">
          <el-select v-model="postForm.cluster" placeholder="select cluster" @change="getClusterInfo()">
            <el-option v-for="(item,index) in clustersListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane label="BROKERS" name="brokers">
        <el-row :gutter="24">
          <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}" style="padding-right:8px;margin-bottom:30px;">
            <el-table
              :key="brokerTableKey"
              :data="brokersList"
              border
              fit
              highlight-current-row
              style="width: 100%;">
              <el-table-column label="Brokers" min-width="50px" align="center">
                <template slot-scope="scope">
                  <router-link :to="'/management/brokers/' + scope.row.cluster + '/' + scope.row.broker + '/broker'" class="link-type">
                    <span>{{ scope.row.broker }}</span>
                  </router-link>
                </template>
              </el-table-column>
              <el-table-column label="Failure Domains" align="center" min-width="100px">
                <template slot-scope="scope">
                  <span>{{ scope.row.failureDomain }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Owned Namespaces" align="center" min-width="100px">
                <template slot-scope="scope">
                  <span>{{ scope.row.ownedNamespaces }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Throughput" align="center" min-width="100px">
                <template slot-scope="scope">
                  <span>In:{{ scope.row.throughputIn }}<br>Out:{{ scope.row.throughputOut }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Bandwidth" align="center" min-width="100px">
                <template slot-scope="scope">
                  <span>In: {{ scope.row.bandwidthIn }}<br>Out: {{ scope.row.bandwidthOut }}</span>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane label="BOOKIES" name="bookies">
        <el-row :gutter="24">
          <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}" style="padding-right:8px;margin-bottom:30px;">
            <el-table
              :key="bookieTableKey"
              :data="bookiesList"
              border
              fit
              highlight-current-row
              style="width: 100%;">
              <el-table-column label="Bookies" min-width="50px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.bookie }}</span>
                </template>
              </el-table-column>
              <el-table-column label="State" align="center" min-width="100px">
                <template slot-scope="scope">
                  <span>{{ scope.row.status }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Storage" align="center" min-width="100px">
                <template slot-scope="scope">
                  <span>{{ scope.row.storage }}%</span>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane label="ISOLATION-POLICIES" name="isolationPolicies">
        <el-input v-model="isolationPolicyKey" placeholder="policy" style="width: 200px;" @keyup.enter.native="handlePolicyFilter"/>
        <el-button type="primary" icon="el-icon-search" @click="handleFilter">Search</el-button>
        <el-button type="primary" icon="el-icon-edit" @click="handleCreatePolicy">New Policy</el-button>
        <el-row :gutter="24">
          <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}" style="padding-right:8px;margin-top:15px;">
            <el-table
              :key="isolationTableKey"
              :data="isolationPoliciesList"
              border
              fit
              highlight-current-row
              style="width: 100%;">
              <el-table-column label="Isolation Policy" min-width="50px" align="center">
                <template slot-scope="scope">
                  <router-link :to="'/management/clusters/' + scope.row.cluster + '/' + scope.row.isolationPolicy + '/namespaceIsolationPolicy'" class="link-type">
                    <span>{{ scope.row.isolationPolicy }}</span>
                  </router-link>
                </template>
              </el-table-column>
              <el-table-column label="Number of Primary Brokers" align="center" min-width="100px">
                <template slot-scope="scope">
                  <span>{{ scope.row.numberOfPrimaryBrokers }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Number of Secondary Brokers" align="center" min-width="100px">
                <template slot-scope="scope">
                  <span>{{ scope.row.numberOfSecondaryBrokers }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Delete Policy" align="center" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                  <el-button class="el-button el-button--primary el-button--medium" type="danger" @click="handleDeletePolicy(scope.row)">Delete
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane label="FAILURE-DOMAINS" name="failureDomains">
        <el-input v-model="failureDomainsKey" placeholder="Failure Domains" style="width: 200px;" @keyup.enter.native="handleFailureDomainFilter"/>
        <el-button type="primary" icon="el-icon-search" @click="handleFailureDomainFilter">Search</el-button>
        <el-button type="primary" icon="el-icon-edit" @click="newFailureDomain">New FailureDomain</el-button>
        <el-table
          :data="faileDomainList"
          border
          style="width: 100%;margin-top:15px">
          <el-table-column prop="domain" label="Domain">
            <template slot-scope="scope">
              <router-link :to="'/management/clusters/' + scope.row.cluster + '/' + scope.row.domain + '/failureDomainName'" class="link-type">
                <span>{{ scope.row.domain }}</span>
              </router-link>
            </template>
          </el-table-column>
          <el-table-column prop="brokers" label="Brokers">
            <template slot-scope="scope">
              <span>{{ scope.row.brokers }}</span>
            </template>
          </el-table-column>
          <!-- <el-table-column label="Delete FailureDomain" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button class="el-button el-button--primary el-button--medium" type="danger" @click="handleDeleteFailureDomain(scope.row)">Delete</el-button>
            </template>
          </el-table-column> -->
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="CONFIG" name="config">
        <el-form :inline="true" :model="form" :rules="rules">
          <el-form-item prop="httpServiceUrl">
            <span>Http Service Url</span>
            <md-input
              v-model="form.httpServiceUrl"
              class="md-input-style"
              name="serviceUrl"
              placeholder="Please input httpServiceUrl"
              @keyup.enter.native="handleServiceUrl">
              http://
            </md-input>
          </el-form-item>
          <el-form-item prop="httpsServiceUrl">
            <span>Https Service Url</span>
            <md-input
              v-model="form.httpsServiceUrl"
              class="md-input-style"
              name="httpsServiceUrl"
              placeholder="Please input httpsServiceUrl"
              @keyup.enter.native="handleServiceUrl">
              https://
            </md-input>
          </el-form-item>
          <el-form-item prop="brokerServiceUrl">
            <span>Broker Service Url</span>
            <md-input
              v-model="form.brokerServiceUrl"
              class="md-input-style"
              name="brokerServiceUrl"
              placeholder="Please input brokerServiceUrl"
              @keyup.enter.native="handleServiceUrl">
              pulsar://
            </md-input>
          </el-form-item>
          <el-form-item prop="brokersServiceUrl">
            <span>Http Secure Service Url</span>
            <md-input
              v-model="form.brokersServiceUrl"
              class="md-input-style"
              name="brokersServiceUrl"
              placeholder="Please input brokersServiceUrl"
              @keyup.enter.native="handleServiceUrl">
              pulsar+ssl://
            </md-input>
          </el-form-item>
        </el-form>
        <h4>Danager Zone</h4>
        <hr class="danger-line">
        <el-button type="danger" class="button" @click="handleDelete">Delete Cluster</el-button>
      </el-tab-pane>
    </el-tabs>
    <el-dialog :visible.sync="dialogFormVisible" title="Create Failure Domain Name">
      <el-form ref="temp" :rules="rules" :model="temp" label-position="left" label-width="150px" style="width: 400px; margin-left:50px;">
        <div v-if="dialogStatus==='create'">
          <div v-if="dialogStatus==='create'">
            <el-form-item label="domainName" prop="domainName">
              <el-input v-model="temp.domainName"/>
            </el-form-item>
            <el-form-item label="brokerList" prop="brokerList">
              <el-select
                v-model="temp.brokerValue"
                style="width:254px;"
                multiple
                placeholder="Please Select Brokers">
                <el-option v-for="item in brokerOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </div>
        </div>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">{{ $t('table.cancel') }}</el-button>
        <el-button type="primary" @click="handleCreateFailureDomain()">{{ $t('table.confirm') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  fetchClusters,
  updateCluster,
  listClusterDomainName,
  deleteCluster,
  createClusterDomainName
} from '@/api/clusters'
import { fetchBrokerStatsMetrics } from '@/api/brokerStats'
import { fetchBrokers, fetchBrokersByDirectBroker } from '@/api/brokers'
import { fetchIsolationPolicies } from '@/api/isolationPolicies'
import { getBookiesList } from '@/api/bookies'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import MdInput from '@/components/MDinput'
const defaultForm = {
  cluster: ''
}
export default {
  name: 'ClusterInfo',
  components: {
    Pagination,
    MdInput
  },
  data() {
    return {
      postForm: Object.assign({}, defaultForm),
      localList: [],
      listQuery: {
        cluster: '',
        page: 1,
        limit: 10
      },
      activeName: 'brokers',
      clustersListOptions: [],
      form: {
        serviceUrl: '',
        httpsServiceUrl: '',
        brokerServiceUrl: '',
        brokersServiceUrl: ''
      },
      brokerOptions: [],
      temp: {
        domainName: '',
        brokerValue: []
      },
      rules: {
        domainName: [{ required: true, message: 'Please input Domain Name', trigger: 'change' }]
      },
      faileDomainList: [],
      brokersList: [],
      brokerTableKey: 0,
      isolationTableKey: 0,
      isolationPoliciesList: [],
      isolationPolicyKey: '',
      failureDomainsKey: '',
      dialogFormVisible: false,
      dialogStatus: '',
      bookieTableKey: 0,
      bookiesList: []
    }
  },
  created() {
    this.postForm.cluster = this.$route.params && this.$route.params.cluster
    this.getClusterList()
    this.getBrokerList()
    if (this.$route.query && this.$route.query.tab) {
      this.activeName = this.$route.query.tab
      if (this.activeName === 'isolationPolicies') {
        this.getNamespaceIsolationPolicy()
      }
    }
    this.getFailureDomainsList()
    this.getBookiesList()
  },
  methods: {
    getBookiesList() {
      getBookiesList(this.postForm.cluster).then(response => {
        if (response.data.enableBookieHttp) {
          for (var i = 0; i < response.data.data.length; i++) {
            var bookieInfo = {}
            if (response.data.data[i].status === 'rw') {
              bookieInfo['status'] = 'Writable'
            } else if (response.data.data[i].status === 'ro') {
              bookieInfo['status'] = 'Readonly'
            }
            var storage = (parseInt(response.data.data[i].storage[1]) - parseInt(response.data.data[i].storage[0])) / parseInt(response.data.data[i].storage[1])
            bookieInfo['storage'] = storage.toFixed(2) * 100
            bookieInfo['bookie'] = response.data.data[i].bookie
            bookieInfo['cluster'] = this.postForm.cluster
            this.bookiesList.push(bookieInfo)
          }
        }
      })
    },
    getClusterList() {
      fetchClusters(this.listQuery).then(response => {
        for (var i = 0; i < response.data.data.length; i++) {
          this.clustersListOptions.push(response.data.data[i].cluster)
          if (response.data.data[i].cluster === this.postForm.cluster) {
            this.form.httpServiceUrl = response.data.data[i].serviceUrl
            this.form.httpsServiceUrl = response.data.data[i].serviceUrlTls
            this.form.brokerServiceUrl = response.data.data[i].brokerServiceUrl
            this.form.brokersServiceUrl = response.data.data[i].brokerServiceUrlTls
          }
        }
      })
    },
    getBrokerList() {
      fetchBrokers(this.postForm.cluster).then(response => {
        if (!response.data) return
        var brokerInfo = {}
        for (var i = 0; i < response.data.data.length; i++) {
          var throughputIn = 0
          var throughputOut = 0
          var bandwidthIn = 0
          var bandwidthOut = 0
          var numberNamespaces = 0
          brokerInfo['cluster'] = this.postForm.cluster
          brokerInfo['broker'] = response.data.data[i].broker
          brokerInfo['failureDomain'] = response.data.data[i].failureDomain.join(',')
          fetchBrokerStatsMetrics(response.data.data[i].broker).then(res => {
            for (var m = 0; m < res.data.length; m++) {
              if (res.data[m].dimensions.hasOwnProperty('namespace')) {
                if (res.data[m].dimensions.namespace.split('/').length === 2) {
                  throughputIn += res.data[m].metrics.brk_in_tp_rate
                  throughputOut += res.data[m].metrics.brk_out_tp_rate
                  bandwidthIn += res.data[m].metrics.brk_in_rate
                  bandwidthOut += res.data[m].metrics.brk_out_rate
                  numberNamespaces += 1
                }
              }
            }
            brokerInfo['ownedNamespaces'] = numberNamespaces
            brokerInfo['throughputIn'] = throughputIn
            brokerInfo['throughputOut'] = throughputOut
            brokerInfo['bandwidthOut'] = bandwidthOut
            brokerInfo['bandwidthIn'] = bandwidthIn
            this.brokersList.push(brokerInfo)
          })
        }
      })
    },
    getNamespaceIsolationPolicy() {
      fetchIsolationPolicies(this.postForm.cluster).then(response => {
        if (!response.data) return
        for (var key in response.data) {
          this.isolationPoliciesList.push({
            'cluster': this.postForm.cluster,
            'isolationPolicy': key,
            'numberOfPrimaryBrokers': response.data[key].primary.length,
            'numberOfSecondaryBrokers': response.data[key].secondary.length
          })
        }
      })
    },
    getClusterInfo() {
      this.$router.push({ path: '/management/clusters/' + this.postForm.cluster + '/cluster?tab=config' })
    },
    handleClick(tab, event) {
      if (tab.name === 'isolationPolicies') {
        this.getNamespaceIsolationPolicy()
      }
      this.$router.push({ query: { 'tab': tab.name }})
    },
    handleServiceUrl() {
      var data = {
        'serviceUrl': this.form.httpServiceUrl,
        'serviceUrlTls': this.form.httpsServiceUrl,
        'brokerServiceUrl': this.form.brokerServiceUrl,
        'brokerServiceUrlTls': this.form.brokersServiceUrl
      }
      updateCluster(this.postForm.cluster, data).then(() => {
        this.$notify({
          title: 'success',
          message: 'Update cluster success',
          type: 'success',
          duration: 2000
        })
      })
    },
    getFailureDomainsList() {
      listClusterDomainName(this.postForm.cluster).then(response => {
        if (!response.data) return
        for (var key in response.data) {
          this.faileDomainList.push({
            'cluster': this.postForm.cluster,
            'domain': key,
            'brokers': response.data[key].brokers.length
          })
        }
      })
    },
    getSelectBrokers() {
      fetchBrokersByDirectBroker(this.postForm.cluster).then(response => {
        for (var i = 0; i < response.data.length; i++) {
          this.brokerOptions.push({
            value: response.data[i],
            label: response.data[i]
          })
        }
      })
    },
    newFailureDomain() {
      this.temp.domainName = ''
      this.temp.brokerValue = []
      this.brokerOptions = []
      this.getSelectBrokers()
      this.dialogFormVisible = true
      this.dialogStatus = 'create'
      this.$nextTick(() => {
        this.$refs['temp'].clearValidate()
      })
    },
    handleCreateFailureDomain() {
      this.$refs['temp'].validate((valid) => {
        if (valid) {
          const data = {
            'brokers': this.temp.brokerValue
          }
          createClusterDomainName(this.postForm.cluster, this.temp.domainName, data).then(response => {
            this.$notify({
              title: 'success',
              message: 'Set Domain Name success',
              type: 'success',
              duration: 3000
            })
            this.getFailureDomainsList()
            this.dialogFormVisible = false
          })
        }
      })
    },
    handleCreatePolicy() {
      this.$router.push({ path: '/management/clusters/' + this.postForm.cluster + '/create/namespaceIsolationPolicy?created=true' })
    },
    handleFilter() {
    },
    handleDelete() {
      deleteCluster(this.postForm.cluster).then(response => {
        this.$notify({
          title: 'success',
          message: 'delete success',
          type: 'success',
          duration: 2000
        })
        this.$router.push({ path: '/management/clusters' })
      })
    },
    handleFailureDomainFilter() {
    }
    // handleDeletePolicy(row) {
    //   deleteIsolationPolicies(this.postForm.cluster, row.isolationPolicy).then(response => {
    //     this.$notify({
    //       title: 'success',
    //       message: 'Delete policy success',
    //       type: 'success',
    //       duration: 3000
    //     })
    //     this.$router.push({ path: '/management/clusters/' + this.postForm.cluster + '/cluster?tab=isolationPolicies' })
    //   })
    // }
  }
}
</script>

<style>
.md-input-style {
  width: 300px;
  margin-top: 15px;
}
.danger-line {
  background: red;
  border: none;
  height: 1px;
}
</style>
