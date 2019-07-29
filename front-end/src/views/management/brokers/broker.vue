<template>
  <div class="app-container">
    <div class="createPost-container">
      <el-form :inline="true" :model="postForm" class="form-container">
        <el-form-item class="postInfo-container-item" label="Cluster">
          <el-select v-model="postForm.cluster" placeholder="select cluster" @change="getClusterList(postForm.cluster)">
            <el-option v-for="(item,index) in clustersListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
        <el-form-item class="postInfo-container-item" label="Broker">
          <el-select v-model="postForm.broker" placeholder="select broker" @change="getBrokersList(postForm.broker)">
            <el-option v-for="(item,index) in brokersListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
        <el-button type="primary" @click="handleHeartBeat">Heartbeat</el-button>
        <el-button type="primary" @click="handleRuntimeConfig">Runtime Config</el-button>
      </el-form>
      <el-table
        :data="brokerStats"
        border
        style="width: 100%">
        <el-table-column prop="inMsg" label="In - msg/s"/>
        <el-table-column prop="outMsg" label="Out - msg/s"/>
        <el-table-column prop="inBytes" label="In - bytes/s"/>
        <el-table-column prop="outBytes" label="Out - bytes/s"/>
      </el-table>
      <h4>Owned Namespaces</h4>
      <el-table
        :key="bundleTableKey"
        :data="bundleList"
        border
        fit
        highlight-current-row
        style="width: 100%;">
        <el-table-column label="Tenant" align="center" min-width="100px">
          <template slot-scope="scope">
            <span>{{ scope.row.tenant }}</span>
          </template>
        </el-table-column>
        <el-table-column label="Namespace" align="center" min-width="100px">
          <template slot-scope="scope">
            <span>{{ scope.row.namespace }}</span>
          </template>
        </el-table-column>
        <el-table-column label="Bundle" align="center" min-width="100px">
          <template slot-scope="scope">
            <span>{{ scope.row.bundle }}</span>
          </template>
        </el-table-column>
        <el-table-column label="Operations" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button size="medium" type="danger" @click="handleUnloadBundle(scope.row)">Unload</el-button>
          </template>
        </el-table-column>
      </el-table>
      <h4>Namespace Isolation Policies</h4>
      <el-table
        :data="isolationPolicyList"
        border
        fit
        highlight-current-row
        style="width: 100%;">
        <el-table-column label="Isolation Policy" align="center" min-width="100px">
          <template slot-scope="scope">
            <span>{{ scope.row.isolationPolicy }}</span>
          </template>
        </el-table-column>
        <el-table-column label="Number of Primary Brokers" align="center" min-width="100px">
          <template slot-scope="scope">
            <span>{{ scope.row.primaryBrokers }}</span>
          </template>
        </el-table-column>
        <el-table-column label="Number of Secondary Brokers" align="center" min-width="100px">
          <template slot-scope="scope">
            <span>{{ scope.row.secondaryBrokers }}</span>
          </template>
        </el-table-column>
      </el-table>
      <el-dialog :visible.sync="dialogFormVisible">
        <jsonEditor :value="jsonValue"/>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import { fetchClusters } from '@/api/clusters'
import { fetchBrokerStatsMetrics, fetchBrokerStatsTopics } from '@/api/brokerStats'
import { fetchBrokersHealth } from '@/api/brokers'
import { fetchIsolationPolicies } from '@/api/isolationPolicies'
import { unloadBundle } from '@/api/namespaces'
import { fetchBrokersRuntimeConfiguration } from '@/api/brokers'
import jsonEditor from '@/components/JsonEditor'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import MdInput from '@/components/MDinput'
const defaultForm = {
  cluster: '',
  broker: ''
}
export default {
  name: 'BrokerInfo',
  components: {
    Pagination,
    MdInput,
    jsonEditor
  },
  data() {
    return {
      postForm: Object.assign({}, defaultForm),
      clustersListOptions: [],
      brokersListOptions: [],
      brokerStats: [],
      bundleTableKey: 0,
      bundleList: [],
      isolationPolicyList: [],
      isolationPolicyTableKey: 0,
      dialogFormVisible: false,
      jsonValue: {}
    }
  },
  created() {
    this.postForm.cluster = this.$route.params && this.$route.params.cluster
    this.postForm.broker = this.$route.params && this.$route.params.broker
    this.getBrokerInfo()
    this.getBrokerStats()
    this.getIsolationPolicy()
  },
  methods: {
    getBrokerInfo() {
      fetchBrokerStatsTopics(this.postForm.broker).then(response => {
        if (!response.data) return
        this.brokerStatsTopic = response.data
        for (var tenantNamespace in this.brokerStatsTopic) {
          var tn = tenantNamespace.split('/')
          for (var bundle in this.brokerStatsTopic[tenantNamespace]) {
            var ownedNamespace = {}
            ownedNamespace['tenant'] = tn[0]
            ownedNamespace['namespace'] = tn[1]
            ownedNamespace['bundle'] = bundle
            this.bundleList.push(ownedNamespace)
          }
        }
      })
    },
    getBrokerStats() {
      var throughputIn = 0
      var throughputOut = 0
      var bandwidthIn = 0
      var bandwidthOut = 0
      fetchBrokerStatsMetrics(this.postForm.broker).then(response => {
        for (var m = 0; m < response.data.length; m++) {
          if (response.data[m].dimensions.hasOwnProperty('namespace')) {
            if (response.data[m].dimensions.namespace.split('/').length === 2) {
              throughputIn += response.data[m].metrics.brk_in_tp_rate
              throughputOut += response.data[m].metrics.brk_out_tp_rate
              bandwidthIn += response.data[m].metrics.brk_in_rate
              bandwidthOut += response.data[m].metrics.brk_out_rate
            }
          }
        }
        this.brokerStats.push({
          'inBytes': throughputIn,
          'outBytes': throughputOut,
          'inMsg': bandwidthIn,
          'outMsg': bandwidthOut
        })
      })
    },
    getClusterList() {
      fetchClusters(this.listQuery).then(response => {})
    },
    getIsolationPolicy() {
      fetchIsolationPolicies(this.postForm.cluster).then(res => {
        var tempIsolationPolicy = []
        for (var policy in res.data) {
          for (var i in res.data[policy].primary) {
            var regexPrimary = new RegExp(res.data[policy].primary[i])
            if (regexPrimary.test(this.postFormbroker)) {
              if (tempIsolationPolicy.indexOf(policy) < 0) {
                tempIsolationPolicy.push(policy)
              }
            }
          }
          for (var j in res.data[policy].secondary) {
            var regexSecondary = new RegExp(res.data[policy].secondary[j])
            if (regexSecondary.test(this.postFormbroker)) {
              if (tempIsolationPolicy.indexOf(policy) < 0) {
                tempIsolationPolicy.push(policy)
              }
            }
          }
          if (tempIsolationPolicy.indexOf(policy) >= 0) {
            this.isolationPolicyList.push({
              'isolationPolicy': policy,
              'primaryBrokers': res.data[policy].primary.length,
              'secondaryBrokers': res.data[policy].secondary.length
            })
          }
        }
      })
    },
    getBrokersList(cluster) {
    },
    handleUnloadBundle(row) {
      unloadBundle(row.tenant + '/' + row.namespace, row.bundle).then(response => {
        this.$notify({
          title: 'success',
          message: 'Unload bundle success',
          type: 'success',
          duration: 3000
        })
      })
    },
    handleHeartBeat() {
      fetchBrokersHealth().then(response => {
        if (response.data === 'ok') {
          this.$notify({
            title: 'success',
            message: 'Health Check success',
            type: 'success',
            duration: 3000
          })
        } else {
          this.$notify({
            title: 'error',
            message: 'Health Check failed',
            type: 'error',
            duration: 3000
          })
        }
      })
    },
    handleRuntimeConfig() {
      fetchBrokersRuntimeConfiguration().then(response => {
        this.dialogFormVisible = true
        this.jsonValue = response.data
      })
    }
  }
}
</script>

<style>
</style>
