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
          <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}">
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
      <el-tab-pane :label="$t('tabs.isolationpolicies')" name="isolationPolicies">
        <el-input
          v-model="isolationPolicyKey"
          :placeholder="$t('ip.searchIps')"
          style="width: 200px;"
          @keyup.enter.native="handlePolicyFilter"/>
        <el-button type="primary" icon="el-icon-search" @click="handlePolicyFilter"/>
        <el-button type="primary" icon="el-icon-plus" @click="handleCreatePolicy">{{ $t('ip.newIp') }}</el-button>
        <el-row :gutter="24">
          <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}">
            <el-table
              :key="isolationTableKey"
              :data="isolationPoliciesList"
              border
              fit
              highlight-current-row
              style="width: 100%;">
              <el-table-column :label="$t('ip.nameLabel')" min-width="50px" align="center">
                <template slot-scope="scope">
                  <router-link :to="'/management/clusters/' + scope.row.cluster + '/' + scope.row.isolationPolicy + '/namespaceIsolationPolicy'" class="link-type">
                    <span>{{ scope.row.isolationPolicy }}</span>
                  </router-link>
                </template>
              </el-table-column>
              <el-table-column :label="$t('ip.numPBLabel')" align="center" min-width="100px">
                <template slot-scope="scope">
                  <span>{{ scope.row.numberOfPrimaryBrokers }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('ip.numSBLabel')" align="center" min-width="100px">
                <template slot-scope="scope">
                  <span>{{ scope.row.numberOfSecondaryBrokers }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('table.actions')" align="center" width="240" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                  <router-link :to="'/management/clusters/' + scope.row.cluster + '/' + scope.row.isolationPolicy + '/namespaceIsolationPolicy'">
                    <el-button type="primary" size="mini">{{ $t('table.edit') }}</el-button>
                  </router-link>
                  <el-button class="el-button el-button--primary el-button--medium" type="danger" size="mini" @click="handleDeletePolicy(scope.row)">Delete</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane :label="$t('tabs.failuredomains')" name="failureDomains">
        <el-input
          v-model="failureDomainsKey"
          :placeholder="$t('fd.searchFds')"
          style="width: 200px;"
          @keyup.enter.native="handleFailureDomainFilter"/>
        <el-button type="primary" icon="el-icon-search" @click="handleFailureDomainFilter"/>
        <el-button type="primary" icon="el-icon-plus" @click="newFailureDomain">{{ $t('fd.newFd') }}</el-button>
        <el-table
          :data="failureDomainList"
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
          <el-table-column :label="$t('table.actions')" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <router-link :to="'/management/clusters/' + scope.row.cluster + '/' + scope.row.domain + '/failureDomainName'">
                <el-button type="primary" size="mini">{{ $t('table.edit') }}</el-button>
              </router-link>
              <el-button class="el-button el-button--primary el-button--medium" type="danger" size="mini" @click="handleDeleteFailureDomain(scope.row)">Delete</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane :label="$t('tabs.config')" name="config">
        <el-row>
          <el-col :span="6">
            <el-form :inline="false" :model="form" :rules="rules" label-position="top">
              <el-form-item :label="$t('cluster.webServiceUrlPrefix')" prop="httpServiceUrl">
                <el-input v-model="form.httpServiceUrl" placeholder="http://"/>
              </el-form-item>
              <el-form-item :label="$t('cluster.webServiceUrlTlsPrefix')" prop="httpsServiceUrlTls">
                <el-input v-model="form.httpsServiceUrl" placeholder="https://"/>
              </el-form-item>
              <el-form-item :label="$t('cluster.brokerServiceUrlPrefix')" prop="brokerServiceUrl">
                <el-input v-model="form.brokerServiceUrl" placeholder="pulsar://"/>
              </el-form-item>
              <el-form-item :label="$t('cluster.brokerServiceUrlTlsPrefix')" prop="brokersServiceUrl">
                <el-input v-model="form.brokersServiceUrl" placeholder="pulsar+ssl://"/>
              </el-form-item>
              <el-button type="primary" class="button" @click="handleServiceUrl">{{ $t('cluster.updateCluster') }}</el-button>
            </el-form>
          </el-col>
        </el-row>
        <h4 style="color:#E57470">{{ $t('common.dangerZone') }}</h4>
        <hr class="danger-line">
        <el-button type="danger" class="button" @click="handleDelete">{{ $t('cluster.deleteCluster') }}</el-button>
      </el-tab-pane>
    </el-tabs>
    <el-dialog :visible.sync="dialogFormVisible" :title="textMap[dialogStatus]" width="30%">
      <el-form ref="temp" :rules="rules" :model="temp" label-position="top">
        <div v-if="dialogStatus==='createFailureDomain'">
          <el-form-item :label="$t('fd.name')" prop="domainName">
            <el-input v-model="temp.domainName"/>
          </el-form-item>
          <el-form-item :label="$t('fd.brokerList')" prop="brokerList">
            <el-select
              v-model="temp.brokerValue"
              style="width:254px;"
              multiple
              placeholder="Please select brokers">
              <el-option v-for="item in brokerOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleCreateFailureDomain()">{{ $t('table.confirm') }}</el-button>
            <el-button @click="dialogFormVisible = false">{{ $t('table.cancel') }}</el-button>
          </el-form-item>
        </div>
        <div v-if="dialogStatus==='deleteCluster'">
          <el-form-item>
            <h4>Are you sure you want to delete this cluster?</h4>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="deleteCluster()">{{ $t('table.confirm') }}</el-button>
            <el-button @click="dialogFormVisible = false">{{ $t('table.cancel') }}</el-button>
          </el-form-item>
        </div>
        <div v-if="dialogStatus==='deleteDomain'">
          <el-form-item>
            <h4>Are you sure you want to delete this domain?</h4>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="deleteDomain()">{{ $t('table.confirm') }}</el-button>
            <el-button @click="dialogFormVisible = false">{{ $t('table.cancel') }}</el-button>
          </el-form-item>
        </div>
        <div v-if="dialogStatus==='deletePolicy'">
          <el-form-item>
            <h4>Are you sure you want to delete this policy?</h4>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="deletePolicy()">{{ $t('table.confirm') }}</el-button>
            <el-button @click="dialogFormVisible = false">{{ $t('table.cancel') }}</el-button>
          </el-form-item>
        </div>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import {
  fetchClusters,
  updateCluster,
  listClusterDomainName,
  deleteCluster,
  createClusterDomainName,
  deleteClusterDomainName
} from '@/api/clusters'
import { fetchBrokerStatsMetrics } from '@/api/brokerStats'
import { fetchBrokers, fetchBrokersByDirectBroker } from '@/api/brokers'
import { fetchIsolationPolicies, deleteIsolationPolicies } from '@/api/isolationPolicies'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import MdInput from '@/components/MDinput'
import { validateEmpty } from '@/utils/validate'

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
        policyName: '',
        brokerValue: []
      },
      rules: {
        domainName: [{ required: true, message: 'Please input Domain Name', trigger: 'change' }]
      },
      failureDomainList: [],
      brokersList: [],
      brokerTableKey: 0,
      isolationTableKey: 0,
      isolationPoliciesList: [],
      isolationPolicyKey: '',
      failureDomainsKey: '',
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        createFailureDomain: this.$i18n.t('fd.createFdTitle'),
        deleteCluster: 'Delete Cluster',
        deleteDomain: 'Delete Domain',
        deletePolicy: 'Delete Policy'
      },
      tempIsolationPolicyList: [],
      tempFailureDomainList: []
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
  },
  methods: {
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
        for (var i = 0; i < response.data.data.length; i++) {
          var brokerInfo = {}
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
          })
          this.brokersList.push(brokerInfo)
        }
      })
    },
    getNamespaceIsolationPolicy() {
      fetchIsolationPolicies(this.postForm.cluster).then(response => {
        if (!response.data) return
        this.isolationPoliciesList = []
        this.tempIsolationPolicyList = []
        for (var key in response.data) {
          var policy = {
            'cluster': this.postForm.cluster,
            'isolationPolicy': key,
            'numberOfPrimaryBrokers': response.data[key].primary.length,
            'numberOfSecondaryBrokers': response.data[key].secondary.length
          }
          this.isolationPoliciesList.push(policy)
          this.tempIsolationPolicyList.push(policy)
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
        this.failureDomainList = []
        this.tempFailureDomainList = []
        for (var key in response.data) {
          var domain = {
            'cluster': this.postForm.cluster,
            'domain': key,
            'brokers': response.data[key].brokers.length
          }
          this.failureDomainList.push(domain)
          this.tempFailureDomainList.push(domain)
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
      this.dialogStatus = 'createFailureDomain'
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
            this.dialogFormVisible = false
            this.getFailureDomainsList()
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
      this.dialogFormVisible = true
      this.dialogStatus = 'deleteCluster'
    },
    deleteCluster() {
      deleteCluster(this.postForm.cluster).then(response => {
        this.$notify({
          title: 'success',
          message: 'delete success',
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
        this.$router.push({ path: '/management/clusters' })
      })
    },
    handleFailureDomainFilter() {
      if (!validateEmpty(this.failureDomainsKey)) {
        var searchFailureDomainList = []
        for (var i = 0; i < this.tempFailureDomainList.length; i++) {
          if (this.tempFailureDomainList[i]['domain'].indexOf(this.failureDomainsKey) !== -1) {
            searchFailureDomainList.push(this.tempFailureDomainList[i])
          }
        }
        this.failureDomainList = searchFailureDomainList
      } else {
        this.failureDomainList = this.tempFailureDomainList
      }
    },
    handleDeleteFailureDomain(row) {
      this.dialogFormVisible = true
      this.dialogStatus = 'deleteDomain'
      this.temp.domainName = row.domain
    },
    deleteDomain() {
      deleteClusterDomainName(this.postForm.cluster, this.temp.domainName).then(response => {
        this.$notify({
          title: 'success',
          message: 'Delete falure domain success',
          type: 'success',
          duration: 3000
        })
        this.dialogFormVisible = false
        this.getFailureDomainsList()
      })
    },
    handleDeletePolicy(row) {
      this.dialogFormVisible = true
      this.dialogStatus = 'deletePolicy'
      this.temp.policyName = row.isolationPolicy
    },
    deletePolicy() {
      deleteIsolationPolicies(this.postForm.cluster, this.temp.policyName).then(response => {
        this.$notify({
          title: 'success',
          message: 'Delete policy success',
          type: 'success',
          duration: 3000
        })
        this.dialogFormVisible = false
        this.getNamespaceIsolationPolicy()
      })
    },
    handlePolicyFilter() {
      if (!validateEmpty(this.isolationPolicyKey)) {
        var searchIsolationPolicyList = []
        for (var i = 0; i < this.tempIsolationPolicyList.length; i++) {
          if (this.tempIsolationPolicyList[i]['isolationPolicy'].indexOf(this.isolationPolicyKey) !== -1) {
            searchIsolationPolicyList.push(this.tempIsolationPolicyList[i])
          }
        }
        this.isolationPoliciesList = searchIsolationPolicyList
      } else {
        this.isolationPoliciesList = this.tempIsolationPolicyList
      }
    }
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
