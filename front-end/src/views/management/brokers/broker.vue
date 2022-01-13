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
        <el-form-item class="postInfo-container-item" label="Cluster">
          <el-select v-model="postForm.cluster" placeholder="select cluster" @change="getBrokersList()">
            <el-option v-for="(item,index) in clustersListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
        <el-form-item class="postInfo-container-item" label="Broker">
          <el-select v-model="postForm.broker" filterable placeholder="select broker" @change="changeBrokerInfo()">
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
        <el-table-column :label="$t('common.inMsg')" prop="inMsg"/>
        <el-table-column :label="$t('common.outMsg')" prop="outMsg"/>
        <el-table-column :label="$t('common.inBytes')" prop="inBytes"/>
        <el-table-column :label="$t('common.outBytes')" prop="outBytes"/>
      </el-table>
      <h4>Owned Namespaces</h4>
      <el-table
        :key="bundleTableKey"
        :data="bundleList"
        border
        fit
        highlight-current-row
        style="width: 100%;">
        <el-table-column :label="$t('tenant.label')" align="center" min-width="100px">
          <template slot-scope="scope">
            <router-link :to="'/management/tenants/tenantInfo/' + scope.row.tenant + '?tab=namespaces'" class="link-type">
              <span>{{ scope.row.tenant }}</span>
            </router-link>
          </template>
        </el-table-column>
        <el-table-column :label="$t('namespace.label')" align="center" min-width="100px">
          <template slot-scope="scope">
            <router-link :to="'/management/namespaces/' + scope.row.tenant + '/' + scope.row.namespace + '/namespace?tab=overview'" class="link-type">
              <span>{{ scope.row.namespace }}</span>
            </router-link>
          </template>
        </el-table-column>
        <el-table-column :label="$t('namespace.bundle.label')" align="center" min-width="100px">
          <template slot-scope="scope">
            <span>{{ scope.row.bundle }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('namespace.bundle.operation')" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button size="medium" type="danger" icon="el-icon-download" @click="handleUnloadBundle(scope.row)">Unload</el-button>
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
        <el-table-column :label="$t('ip.nameLabel')" align="center" min-width="100px">
          <template slot-scope="scope">
            <router-link :to="'/management/clusters/' + scope.row.cluster + '/' + scope.row.isolationPolicy + '/namespaceIsolationPolicy'" class="link-type">
              <span>{{ scope.row.isolationPolicy }}</span>
            </router-link>
          </template>
        </el-table-column>
        <el-table-column :label="$t('ip.numPBLabel')" align="center" min-width="100px">
          <template slot-scope="scope">
            <span>{{ scope.row.primaryBrokers }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('ip.numSBLabel')" align="center" min-width="100px">
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
import { fetchBrokersHealth, fetchBrokers } from '@/api/brokers'
import { fetchIsolationPolicies } from '@/api/isolationPolicies'
import { unloadBundleOnBroker } from '@/api/namespaces'
import { fetchBrokersRuntimeConfiguration } from '@/api/brokers'
import jsonEditor from '@/components/JsonEditor'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import MdInput from '@/components/MDinput'
import { isValidResponse } from '@/utils/http'
import { formatBytes } from '@/utils/index'
import { numberFormatter } from '@/filters/index'

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
      jsonValue: {},
      firstInit: false
    }
  },
  created() {
    this.postForm.cluster = this.$route.params && this.$route.params.cluster
    this.postForm.broker = this.$route.params && this.$route.params.broker
    this.firstInit = true
    this.getBrokerInfo()
    this.getBrokerStats()
    this.getIsolationPolicy()
    this.getClusterList()
    this.getBrokersList()
  },
  methods: {
    getBrokerInfo() {
      fetchBrokerStatsTopics(this.postForm.broker).then(response => {
        if (!response.data) return
        this.brokerStatsTopic = response.data
        if ((typeof this.brokerStatsTopic) === 'string') {
          // failed to fetch broker stats
          this.brokerStatsTopic = {}
          this.$notify({
            title: 'error',
            message: 'Failed to fetch broker stats from broker ' + this.postForm.broker,
            type: 'error',
            duration: 3000
          })
        }
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
    changeBrokerInfo() {
      this.$router.push({ path: '/management/brokers/' + this.postForm.cluster + '/' + this.postForm.broker + '/broker' })
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
          'inBytes': formatBytes(throughputIn),
          'outBytes': formatBytes(throughputOut),
          'inMsg': numberFormatter(bandwidthIn, 2),
          'outMsg': numberFormatter(bandwidthOut, 2)
        })
      })
    },
    getClusterList() {
      fetchClusters(this.listQuery).then(response => {
        if (!response.data) return
        for (var i = 0; i < response.data.data.length; i++) {
          this.clustersListOptions.push(response.data.data[i].cluster)
        }
      })
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
              'cluster': this.postForm.cluster,
              'isolationPolicy': policy,
              'primaryBrokers': res.data[policy].primary.length,
              'secondaryBrokers': res.data[policy].secondary.length
            })
          }
        }
      })
    },
    getBrokersList() {
      fetchBrokers(this.postForm.cluster).then(response => {
        if (!response.data) return
        if (this.firstInit) {
          this.firstInit = false
        } else {
          this.postForm.broker = ''
        }
        this.brokersListOptions = []
        for (var i = 0; i < response.data.data.length; i++) {
          this.brokersListOptions.push(response.data.data[i].broker)
        }
      })
    },
    handleUnloadBundle(row) {
      unloadBundleOnBroker(this.postForm.broker, row.tenant + '/' + row.namespace, row.bundle).then(response => {
        if (isValidResponse(response)) {
          this.$notify({
            title: 'success',
            message: 'Successfully unload namespace bundle from the broker',
            type: 'success',
            duration: 3000
          })
        } else {
          this.$notify({
            title: 'error',
            message: 'Failed to unload namespace bundle from the broker : ' + response.data,
            type: 'error',
            duration: 3000
          })
        }
      })
    },
    handleHeartBeat() {
      fetchBrokersHealth(this.postForm.broker).then(response => {
        if (isValidResponse(response)) {
          this.$notify({
            title: 'success',
            message: 'Health Check success',
            type: 'success',
            duration: 3000
          })
        } else {
          this.$notify({
            title: 'error',
            message: 'Health Check failed: \n' + response.data,
            type: 'error',
            duration: 3000
          })
        }
      })
    },
    handleRuntimeConfig() {
      fetchBrokersRuntimeConfiguration(this.postForm.broker).then(response => {
        this.dialogFormVisible = true
        this.jsonValue = response.data
      })
    }
  }
}
</script>
