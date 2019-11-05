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
        <el-form-item :label="$t('cluster.label')" class="postInfo-container-item">
          <el-select v-model="postForm.cluster" placeholder="select cluster" @change="getFailureDomainsList()">
            <el-option v-for="(item,index) in clustersListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('fd.label')" class="postInfo-container-item">
          <el-select v-model="postForm.failureDomainName" placeholder="select domain" @change="getFailureDomainInfo()">
            <el-option v-for="(item,index) in failureDomainListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <h3>{{ $t('fd.label') }}</h3>
    <h4>{{ $t('fd.brokerList') }}
      <el-tooltip :content="brokersContent" class="item" effect="dark" placement="top">
        <i class="el-icon-info"/>
      </el-tooltip>
    </h4>
    <el-form>
      <el-form-item>
        <el-select
          v-model="brokerValue"
          :placeholder="$t('fd.selectBrokers')"
          style="width:500px;margin-top:20px"
          multiple>
          <el-option v-for="item in brokerOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-button type="primary" class="button" @click="handleSelectBrokers()">{{ $t('fd.updateFd') }}</el-button>
    </el-form>
    <h4 style="color:#E57470">{{ $t('common.dangerZone') }}</h4>
    <hr class="danger-line">
    <el-button type="danger" class="button" @click="handleDelete">{{ $t('fd.deleteFd') }}</el-button>
    <el-dialog :visible.sync="dialogFormVisible" :title="textMap[dialogStatus]" width="30%">
      <el-form label-position="top">
        <div v-if="dialogStatus==='delete'">
          <el-form-item>
            <h4>{{ deleteFdMessage }}</h4>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="deleteDomain()">{{ $t('table.confirm') }}</el-button>
            <el-button @click="dialogFormVisible = false">{{ $t('table.cancel') }}</el-button>
          </el-form-item>
        </div>
      </el-form>
    </el-dialog>
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
      brokersContent: this.$i18n.t('broker.brokerContent'),
      brokerValue: [],
      brokerOptions: [],
      failureDomainListOptions: [],
      firstInit: false,
      textMap: {
        delete: this.$i18n.t('fd.deleteFd')
      },
      dialogStatus: '',
      dialogFormVisible: false,
      deleteFdMessage: this.$i18n.t('fd.deleteFdMessage')
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
          message: this.$i18n.t('fd.updateFdSuccessNotification'),
          type: 'success',
          duration: 3000
        })
      })
    },
    getFailureDomainInfo() {
      this.$router.push({ path: '/management/clusters/' + this.postForm.cluster + '/' + this.postForm.failureDomainName + '/failureDomainName' })
    },
    handleDelete() {
      this.dialogFormVisible = true
      this.dialogStatus = 'delete'
    },
    deleteDomain() {
      deleteClusterDomainName(this.postForm.cluster, this.postForm.failureDomainName).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('fd.deleteFdSuccessNotification'),
          type: 'success',
          duration: 3000
        })
        this.dialogFormVisible = false
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
