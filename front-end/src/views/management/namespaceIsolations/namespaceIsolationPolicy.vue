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
    <h2>{{ $t('ip.heading') }}</h2>
    <div class="createPost-container">
      <el-form :inline="true" :model="postForm" label-position="top" class="form-container">
        <el-form-item :label="$t('common.clusterLabel')" class="postInfo-container-item">
          <el-select
            v-model="postForm.cluster"
            :placeholder="$t('cluster.selectCluster')"
            @change="getClusterList(postForm.cluster)">
            <el-option v-for="(item,index) in clustersListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
        <el-form-item v-if="created===false" :label="$t('ip.label')" class="postInfo-container-item">
          <el-select
            v-model="postForm.policy"
            :placeholder="$t('ip.selectIp')"
            @change="(postForm.cluster)">
            <el-option v-for="(item,index) in policiesListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
        <el-form-item v-if="created===true" :label="$t('ip.label')" class="postInfo-container-item">
          <el-input
            v-model="policyName"
            :placeholder="$t('ip.newIpName')"
            clearable
            style="width:300px"/>
        </el-form-item>
      </el-form>
    </div>
    <el-row>
      <h3>{{ $t('common.namespacesLabel') }}</h3>
      <hr class="split-line">
      <span>{{ $t('ip.selectNsLabel') }}
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
      <el-button v-else class="button-new-tag" icon="el-icon-plus" size="small" @click="showInputNamespace">
        {{ $t('common.regex') }}
      </el-button>
      <h3>{{ $t('ip.pbHeading') }}</h3>
      <hr class="split-line">
      <span>{{ $t('ip.selectPbLabel') }}
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
      <el-button v-else class="button-new-tag" icon="el-icon-plus" size="small" @click="showInputPrimary">
        {{ $t('common.regex') }}
      </el-button>
      <h3>{{ $t('ip.sbHeading') }}</h3>
      <hr class="split-line">
      <span>{{ $t('ip.selectSbLabel') }}
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
      <el-button v-else class="button-new-tag" icon="el-icon-plus" size="small" @click="showInputSecondary">
        {{ $t('common.regex') }}
      </el-button>
      <h3>{{ $t('ip.afpHeading') }}</h3>
      <hr class="split-line">
      <span>{{ $t('ip.ptHeading') }}
        <el-tooltip :content="policyTypeContent" class="item" effect="dark" placement="top">
          <i class="el-icon-info"/>
        </el-tooltip>
      </span>
      <br>
      <el-select
        v-model="autoFailoverPolicy"
        :placeholder="$t('ip.selectAfpPh')"
        style="margin-top:20px;width:300px">
        <el-option
          v-for="item in autoFailoverPolicyOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"/>
      </el-select>
      <el-form :inline="true" :model="form" :rules="rules">
        <el-form-item prop="ensembleSize">
          <span>{{ $t('ip.brokerUsageThresholdLabel') }} (%)</span>
          <el-tooltip :content="brokerUsageThresholdContent" class="item" effect="dark" placement="top">
            <i class="el-icon-info"/>
          </el-tooltip>
          <md-input
            v-model="form.brokerUsageThreshold"
            :placeholder="$t('ip.brokerUsageThresholdPh')"
            class="md-input-style"
            name="brokerUsageThreshold"/>
        </el-form-item>
        <el-form-item prop="writeQuorumSize">
          <span>{{ $t('ip.minimalAvailableBrokerLabel') }}</span>
          <el-tooltip :content="minimalAvailableBrokerContent" class="item" effect="dark" placement="top">
            <i class="el-icon-info"/>
          </el-tooltip>
          <md-input
            v-model="form.minimalAvailableBroker"
            :placeholder="$t('ip.minimalAvailableBrokerPh')"
            class="md-input-style"
            name="minimalAvailableBroker" />
        </el-form-item>
      </el-form>
      <div v-if="created===true">
        <el-button type="primary" class="button" @click="handleIsolationPolicy">Create Policy</el-button>
      </div>
      <div v-if="created===false">
        <el-button type="primary" class="button" @click="handleIsolationPolicy">Update Policy</el-button>
      </div>
    </el-row>

    <div v-if="created===false">
      <h4 style="color:#E57470">{{ $t('common.dangerZone') }}</h4>
      <hr class="danger-line">
      <el-button type="danger" class="button" @click="handleDelete">Delete Policy</el-button>
    </div>
    <el-dialog :visible.sync="dialogFormVisible" :title="textMap[dialogStatus]" width="30%">
      <el-form label-position="top">
        <div v-if="dialogStatus==='delete'">
          <el-form-item>
            <h4>{{ $t('ip.deletePolicyMessage') }}</h4>
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
      namespaceContent: this.$i18n.t('ip.namespaceDesc'),
      brokerContent: this.$i18n.t('ip.primaryBrokerDesc'),
      secondaryBrokersContent: this.$i18n.t('ip.secondaryBrokerDesc'),
      policyTypeContent: this.$i18n.t('ip.policyTypeDesc'),
      brokerUsageThresholdContent: this.$i18n.t('ip.brokerUsageThresholdDesc'),
      minimalAvailableBrokerContent: this.$i18n.t('ip.minimalAvailableBrokerDesc'),
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
      policyName: '',
      textMap: {
        delete: this.$i18n.t('ip.deletePolicyDialogCaption')
      },
      dialogStatus: '',
      dialogFormVisible: false
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
            message: this.$i18n.t('ip.policyNameCannotBeEmpty'),
            type: 'error',
            duration: 3000
          })
          return
        }
        if (this.namespaceDynamicTags.length <= 0) {
          this.$notify({
            title: 'error',
            message: this.$i18n.t('ip.regexCannotBeEmpty'),
            type: 'error',
            duration: 3000
          })
          return
        }
        if (this.primaryDynamicTags.length <= 0) {
          this.$notify({
            title: 'error',
            message: this.$i18n.t('ip.primaryBrokerRegexCannotBeEmpty'),
            type: 'error',
            duration: 3000
          })
          return
        }
        if (this.secondaryDynamicTags.length <= 0) {
          this.$notify({
            title: 'error',
            message: this.$i18n.t('ip.secondaryBrokerRegexCannotBeEmpty'),
            type: 'error',
            duration: 3000
          })
          return
        }
        if (this.form.minimalAvailableBroker <= 0) {
          this.$notify({
            title: 'error',
            message: this.$i18n.t('ip.numLimitShouldGreaterThan0'),
            type: 'error',
            duration: 3000
          })
          return
        }
        if (this.form.brokerUsageThreshold <= 0) {
          this.$notify({
            title: 'error',
            message: this.$i18n.t('ip.usageThresholdShouldGreaterThan0'),
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
          message: this.$i18n.t('ip.updatePolicySuccessNotification'),
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
          message: this.$i18n.t('ip.primaryBrokerRegexAlreadyExists'),
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
    },
    handleCloseNamespace(tag) {
      this.namespaceDynamicTags.splice(this.namespaceDynamicTags.indexOf(tag), 1)
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
          message: this.$i18n.t('ip.nsRegexAlreadyExists'),
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
    },
    handleCloseSecondary(tag) {
      this.secondaryDynamicTags.splice(this.secondaryDynamicTags.indexOf(tag), 1)
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
          message: this.$i18n.t('ip.secondaryBrokerRegexAlreadyExists'),
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
    },
    handleDelete() {
      this.dialogFormVisible = true
      this.dialogStatus = 'delete'
    },
    deletePolicy() {
      deleteIsolationPolicies(this.postForm.cluster, this.postForm.policy).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('ip.deletePolicySuccessNotification'),
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
