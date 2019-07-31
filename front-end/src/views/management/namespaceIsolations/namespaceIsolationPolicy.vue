<template>
  <div class="app-container">
    <div class="createPost-container">
      <el-form :inline="true" :model="postForm" class="form-container">
        <el-form-item class="postInfo-container-item" label="Cluster">
          <el-select v-model="postForm.cluster" placeholder="select cluster" @change="getClusterList(postForm.cluster)">
            <el-option v-for="(item,index) in clustersListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
        <el-form-item v-if="created===false" class="postInfo-container-item" label="Policy">
          <el-select v-model="postForm.policy" placeholder="select policy" @change="(postForm.cluster)">
            <el-option v-for="(item,index) in policiesListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <h2>Namespace Isolation Policy</h2>
    <hr class="split-line">
    <div v-if="created===true">
      <h3>Policy Name</h3>
      <hr class="split-line">
      <el-input
        v-model="policyName"
        placeholder="Please Input policy name"
        clearable
        style="width:300px"/>
    </div>
    <h3>Namespaces</h3>
    <hr class="split-line">
    <span>Selected Namespace (Regex)
      <el-tooltip :content="namespaceContent" class="item" effect="dark" placement="top">
        <i class="el-icon-info"/>
      </el-tooltip>
    </span>
    <br>
    <el-tag
      v-for="(ntag, index) in namespaceDynamicTags"
      :key="'namespace-' + index"
      :disable-transitions="false"
      closable
      @close="handleCloseNamespace(ntag)">
      {{ ntag }}
    </el-tag>
    <el-input
      v-if="inputVisibleNamespace"
      ref="saveTagInputNamespace"
      v-model="inputValueNamespace"
      class="input-new-tag"
      size="small"
      @keyup.enter.native="handleInputConfirmNamespace"/>
    <el-button v-else class="button-new-tag" size="small" @click="showInputNamespace">+ Regex</el-button>
    <h3>Primary Brokers</h3>
    <hr class="split-line">
    <span>Selected Brokers (Regex)
      <el-tooltip :content="brokerContent" class="item" effect="dark" placement="top">
        <i class="el-icon-info"/>
      </el-tooltip>
    </span>
    <br>
    <el-tag
      v-for="(ptag, index) in primaryDynamicTags"
      :key="'primary-' + index"
      :disable-transitions="false"
      closable
      @close="handleClosePrimary(ptag)">
      {{ ptag }}
    </el-tag>
    <el-input
      v-if="inputVisiblePrimary"
      ref="saveTagInputPrimary"
      v-model="inputValuePrimary"
      class="input-new-tag"
      size="small"
      @keyup.enter.native="handleInputConfirmPrimary"/>
    <el-button v-else class="button-new-tag" size="small" @click="showInputPrimary">+ Regex</el-button>
    <h3>Secondary Brokers</h3>
    <hr class="split-line">
    <span>Selected Brokers (Regex)
      <el-tooltip :content="secondaryBrokersContent" class="item" effect="dark" placement="top">
        <i class="el-icon-info"/>
      </el-tooltip>
    </span>
    <br>
    <el-tag
      v-for="(stag, index) in secondaryDynamicTags"
      :key="'secondary-' + index"
      :disable-transitions="false"
      closable
      @close="handleCloseSecondary(stag)">
      {{ stag }}
    </el-tag>
    <el-input
      v-if="inputVisibleSecondary"
      ref="saveTagInputSecondary"
      v-model="inputValueSecondary"
      class="input-new-tag"
      size="small"
      @keyup.enter.native="handleInputConfirmSecondary"/>
    <el-button v-else class="button-new-tag" size="small" @click="showInputSecondary">+ Regex</el-button>
    <h3>Auto Failover Policy</h3>
    <hr class="split-line">
    <span>Policy Type
      <el-tooltip :content="policyTypeContent" class="item" effect="dark" placement="top">
        <i class="el-icon-info"/>
      </el-tooltip>
    </span>
    <br>
    <el-select
      v-model="autoFailoverPolicy"
      placeholder="Please select Auto Failover Policy"
      style="margin-top:20px;width:300px">
      <el-option
        v-for="item in autoFailoverPolicyOptions"
        :key="item.value"
        :label="item.label"
        :value="item.value"/>
    </el-select>
    <el-form :inline="true" :model="form" :rules="rules">
      <el-form-item prop="ensembelSize">
        <span>Broker Usage Threshold (%)</span>
        <el-tooltip :content="brokerUsageThresholdContent" class="item" effect="dark" placement="top">
          <i class="el-icon-info"/>
        </el-tooltip>
        <md-input
          v-model="form.brokerUsageThreshold"
          class="md-input-style"
          name="brokerUsageThreshold"
          placeholder="Please input brokerUsageThreshold"
          @keyup.enter.native="handleIsolationPolicy"/>
      </el-form-item>
      <el-form-item prop="writeQuorumSize">
        <span>Minimal Available Brokers</span>
        <el-tooltip :content="minimalAvailableBrokerContent" class="item" effect="dark" placement="top">
          <i class="el-icon-info"/>
        </el-tooltip>
        <md-input
          v-model="form.minimalAvailableBroker"
          class="md-input-style"
          name="minimalAvailableBroker"
          placeholder="Please input minimalAvailableBroker"
          @keyup.enter.native="handleIsolationPolicy"/>
      </el-form-item>
    </el-form>
    <div v-if="created===true">
      <h4>Create Zone</h4>
      <hr class="split-line">
      <el-button type="primary" class="button" @click="handleIsolationPolicy">Create Policy</el-button>
    </div>
    <div v-if="created===false">
      <h4>Danager Zone</h4>
      <hr class="danger-line">
      <el-button type="danger" class="button" @click="handleDelete">Delete Policy</el-button>
    </div>
  </div>
</template>

<script>
import { fetchClusters } from '@/api/clusters'
import { fetchIsolationPolicies, updateIsolationPolicies, deleteIsolationPolicies } from '@/api/isolationPolicies'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import MdInput from '@/components/MDinput'
const defaultForm = {
  cluster: '',
  policy: ''
}
export default {
  name: 'NamespaceIsolationPolicy',
  components: {
    Pagination,
    MdInput
  },
  data() {
    return {
      postForm: Object.assign({}, defaultForm),
      clustersListOptions: [],
      namespaceContent: 'This is namespaces Content',
      brokerContent: 'This is broker content',
      secondaryBrokersContent: 'This is secondary brokers content',
      policyTypeContent: 'This is policy type content',
      brokerUsageThresholdContent: 'This is brokerUsageThresholdContent',
      minimalAvailableBrokerContent: 'This is minimalAvailableBrokerContent',
      form: {
        namespaces: '',
        broker: '',
        brokerUsageThreshold: '',
        minimalAvailableBroker: ''
      },
      autoFailoverPolicy: 'min_available',
      autoFailoverPolicyOptions: [],
      rules: {
      },
      policiesListOptions: [],
      primaryDynamicTags: [],
      inputVisiblePrimary: false,
      inputValuePrimary: '',
      namespaceDynamicTags: [],
      inputValueNamespace: '',
      inputVisibleNamespace: false,
      secondaryDynamicTags: [],
      inputVisibleSecondary: false,
      inputValueSecondary: '',
      created: false,
      policyName: ''
    }
  },
  created() {
    this.postForm.cluster = this.$route.params && this.$route.params.cluster
    this.getClusterList()
    if (this.$route.query && this.$route.query.created) {
      this.created = true
    } else {
      this.postForm.policy = this.$route.params && this.$route.params.namespaceIsolation
      this.getPoliciesList()
    }
  },
  methods: {
    getClusterList() {
      fetchClusters().then(response => {
        if (!response.data) return
        for (var i = 0; i < response.data.data.length; i++) {
          this.clustersListOptions.push(response.data.data[i].cluster)
        }
      })
    },
    getPoliciesList() {
      fetchIsolationPolicies(this.postForm.cluster).then(response => {
        if (!response.data) return
        for (var key in response.data) {
          this.policiesListOptions.push(key)
          if (key === this.postForm.policy) {
            this.namespaceDynamicTags = response.data[key].namespaces
            this.primaryDynamicTags = response.data[key].primary
            this.secondaryDynamicTags = response.data[key].secondary
            this.autoFailoverPolicyOptions.push({
              value: response.data[key].auto_failover_policy.policy_type,
              label: response.data[key].auto_failover_policy.policy_type
            })
            this.form.minimalAvailableBroker = response.data[key].auto_failover_policy.parameters.min_limit
            this.form.brokerUsageThreshold = response.data[key].auto_failover_policy.parameters.usage_threshold
          }
        }
      })
    },
    handleIsolationPolicy() {
      var policyName = this.postForm.policy
      if (this.created) {
        if (this.policyName.length <= 0) {
          this.$notify({
            title: 'error',
            message: 'Policy Name cannot be empty',
            type: 'error',
            duration: 3000
          })
          return
        }
        if (this.namespaceDynamicTags.length <= 0) {
          this.$notify({
            title: 'error',
            message: 'Namespace Regex cannot be empty',
            type: 'error',
            duration: 3000
          })
          return
        }
        if (this.primaryDynamicTags.length <= 0) {
          this.$notify({
            title: 'error',
            message: 'Primary Broker Regex cannot be empty',
            type: 'error',
            duration: 3000
          })
          return
        }
        if (this.secondaryDynamicTags.length <= 0) {
          this.$notify({
            title: 'error',
            message: 'Secondary Broker Regex cannot be empty',
            type: 'error',
            duration: 3000
          })
          return
        }
        if (this.form.minimalAvailableBroker <= 0) {
          this.$notify({
            title: 'error',
            message: 'min_limit should greater than 0',
            type: 'error',
            duration: 3000
          })
          return
        }
        if (this.form.brokerUsageThreshold <= 0) {
          this.$notify({
            title: 'error',
            message: 'usage_threshold should greater than 0',
            type: 'error',
            duration: 3000
          })
          return
        }
        policyName = this.policyName
      }
      const data = {
        'namespaces': this.namespaceDynamicTags,
        'primary': this.primaryDynamicTags,
        'secondary': this.secondaryDynamicTags,
        'auto_failover_policy': {
          'policy_type': 0,
          'parameters': {
            'min_limit': this.form.minimalAvailableBroker,
            'usage_threshold': this.form.brokerUsageThreshold
          }
        }
      }
      updateIsolationPolicies(this.postForm.cluster, policyName, data).then(response => {
        this.$notify({
          title: 'success',
          message: 'Set policy success',
          type: 'success',
          duration: 3000
        })
        if (this.created) {
          this.$router.push({ path: '/management/clusters/' + this.postForm.cluster + '/cluster?tab=isolationPolicies' })
        }
      })
    },
    handleClosePrimary(tag) {
      this.primaryDynamicTags.splice(this.primaryDynamicTags.indexOf(tag), 1)
      this.handleIsolationPolicy()
    },
    showInputPrimary() {
      this.inputVisiblePrimary = true
      this.$nextTick(_ => {
        this.$refs.saveTagInputPrimary.$refs.input.focus()
      })
    },
    handleInputConfirmPrimary() {
      const inputValue = this.inputValuePrimary
      if (this.primaryDynamicTags.indexOf(inputValue) >= 0) {
        this.$notify({
          title: 'error',
          message: 'This regex exist',
          type: 'error',
          duration: 3000
        })
        return
      }
      if (inputValue) {
        this.primaryDynamicTags.push(inputValue)
      }
      this.inputVisiblePrimary = false
      this.inputValuePrimary = ''
      if (this.created) {
        return
      }
      this.handleIsolationPolicy()
    },
    handleCloseNamespace(tag) {
      this.namespaceDynamicTags.splice(this.namespaceDynamicTags.indexOf(tag), 1)
      this.handleIsolationPolicy()
    },
    showInputNamespace() {
      this.inputVisibleNamespace = true
      this.$nextTick(_ => {
        this.$refs.saveTagInputNamespace.$refs.input.focus()
      })
    },
    handleInputConfirmNamespace() {
      const inputValue = this.inputValueNamespace
      if (this.namespaceDynamicTags.indexOf(inputValue) >= 0) {
        this.$notify({
          title: 'error',
          message: 'This regex exist',
          type: 'error',
          duration: 3000
        })
        return
      }
      if (inputValue) {
        this.namespaceDynamicTags.push(inputValue)
      }
      this.inputVisibleNamespace = false
      this.inputValueNamespace = ''
      if (this.created) {
        return
      }
      this.handleIsolationPolicy()
    },
    handleCloseSecondary(tag) {
      this.secondaryDynamicTags.splice(this.secondaryDynamicTags.indexOf(tag), 1)
      this.handleIsolationPolicy()
    },
    showInputSecondary() {
      this.inputVisibleSecondary = true
      this.$nextTick(_ => {
        this.$refs.saveTagInputSecondary.$refs.input.focus()
      })
    },
    handleInputConfirmSecondary() {
      const inputValue = this.inputValueSecondary
      if (this.secondaryDynamicTags.indexOf(inputValue) >= 0) {
        this.$notify({
          title: 'error',
          message: 'This regex exist',
          type: 'error',
          duration: 3000
        })
        return
      }
      if (inputValue) {
        this.secondaryDynamicTags.push(inputValue)
      }
      this.inputVisibleSecondary = false
      this.inputValueSecondary = ''
      if (this.created) {
        return
      }
      this.handleIsolationPolicy()
    },
    handleDelete() {
      deleteIsolationPolicies(this.postForm.cluster, this.postForm.policy).then(response => {
        this.$notify({
          title: 'success',
          message: 'Delete policy success',
          type: 'success',
          duration: 3000
        })
        this.$router.push({ path: '/management/clusters/' + this.postForm.cluster + '/cluster?tab=isolationPolicies' })
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
.md-input-style {
  width: 300px;
  margin-top: 20px;
}
.danger-line {
  background: red;
  border: none;
  height: 1px;
}
.el-tag + .el-tag {
  margin-left: 10px;
}
.button-new-tag {
  margin-top: 20px;
  margin-left: 10px;
  height: 32px;
  line-height: 30px;
  padding-top: 0;
  padding-bottom: 0;
}
.input-new-tag {
  width: 90px;
  margin-top: 20px;
  margin-left: 10px;
  vertical-align: bottom;
}
</style>
