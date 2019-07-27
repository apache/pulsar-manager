<template>
  <div class="app-container">
    <div class="createPost-container">
      <el-form :inline="true" :model="postForm" class="form-container">
        <el-form-item class="postInfo-container-item" label="Cluster">
          <el-select v-model="postForm.cluster" placeholder="select cluster" @change="getFailureDomainsList()">
            <el-option v-for="(item,index) in clustersListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
        <el-form-item class="postInfo-container-item" label="Failure Domain">
          <el-select v-model="postForm.failureDomainName" placeholder="select domain" @change="getFailureDomainInfo()">
            <el-option v-for="(item,index) in failureDomainListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <h3>FailureDomain</h3>
    <h4>Brokers
      <el-tooltip :content="brokersContent" class="item" effect="dark" placement="top">
        <i class="el-icon-info"/>
      </el-tooltip>
    </h4>
    <el-select
      v-model="brokerValue"
      style="width:500px;margin-top:20px"
      multiple
      placeholder="Please Select Brokers"
      @change="handleSelectBrokers()">
      <el-option v-for="item in brokerOptions" :key="item.value" :label="item.label" :value="item.value" />
    </el-select>
    <h4>Danager Zone</h4>
    <hr class="danger-line">
    <el-button type="danger" class="button" @click="handleDelete">Delete Failure Domain</el-button>
  </div>
</template>

<script>
// import MdInput from '@/components/MDinput'
import {
  fetchClusters,
  getClusterDomainName,
  updateClusterDomainName,
  listClusterDomainName,
  deleteClusterDomainName
} from '@/api/clusters'
import { fetchBrokersByDirectBroker } from '@/api/brokers'

const defaultForm = {
  cluster: '',
  failureDomainName: ''
}
export default {
  name: 'FailureDomainInfo',
  // components: {
  //   MdInput
  // },
  data() {
    return {
      postForm: Object.assign({}, defaultForm),
      clustersListOptions: [],
      brokersContent: 'This is BrokersContent',
      brokerValue: [],
      brokerOptions: [],
      failureDomainListOptions: [],
      firstInit: false
    }
  },
  created() {
    this.postForm.cluster = this.$route.params && this.$route.params.cluster
    this.postForm.failureDomainName = this.$route.params && this.$route.params.failureDomainName
    this.firstInit = true
    this.getClusterList()
    this.initBrokerValue()
    this.initSelectBrokers()
    this.getFailureDomainsList()
  },
  methods: {
    initBrokerValue() {
      getClusterDomainName(this.postForm.cluster, this.postForm.failureDomainName).then(response => {
        if (!response.data) return
        this.brokerValue = response.data.brokers
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
    getFailureDomainsList() {
      listClusterDomainName(this.postForm.cluster).then(response => {
        if (!response.data) return
        if (this.firstInit) {
          this.firstInit = false
        } else {
          this.postForm.failureDomainName = ''
        }
        this.failureDomainListOptions = []
        for (var key in response.data) {
          this.failureDomainListOptions.push(key)
        }
      })
    },
    initSelectBrokers() {
      fetchBrokersByDirectBroker(this.postForm.cluster).then(response => {
        for (var i = 0; i < response.data.length; i++) {
          this.brokerOptions.push({
            value: response.data[i],
            label: response.data[i]
          })
        }
      })
    },
    handleSelectBrokers() {
      const data = {
        'brokers': this.brokerValue
      }
      updateClusterDomainName(this.postForm.cluster, this.postForm.failureDomainName, data).then(response => {
        this.$notify({
          title: 'success',
          message: 'Update brokers success',
          type: 'success',
          duration: 3000
        })
      })
    },
    getFailureDomainInfo() {
      this.$router.push({ path: '/management/clusters/' + this.postForm.cluster + '/' + this.postForm.failureDomainName + '/failureDomainName' })
    },
    handleDelete() {
      deleteClusterDomainName(this.postForm.cluster, this.postForm.failureDomainName).then(response => {
        this.$notify({
          title: 'success',
          message: 'Delete Domain success',
          type: 'success',
          duration: 3000
        })
        this.$router.push({ path: '/management/clusters/' + this.postForm.cluster + '/cluster?tab=failureDomains' })
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
