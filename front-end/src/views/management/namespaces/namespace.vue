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
        <el-form-item :label="$t('tenant.label')" class="postInfo-container-item">
          <el-select v-model="postForm.tenant" :placeholder="$t('tenant.name')" @change="getNamespacesList(postForm.tenant)">
            <el-option v-for="(item,index) in tenantsListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('namespace.label')" class="postInfo-container-item">
          <el-select v-model="postForm.namespace" :placeholder="$t('namespace.name')" @change="getNamespaceInfo(postForm.tenant, postForm.namespace)">
            <el-option v-for="(item,index) in namespacesListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane :label="$t('tabs.overview')" name="overview">
        <el-table
          :data="namespaceStats"
          border
          style="width: 100%">
          <el-table-column :label="$t('common.inMsg')" prop="inMsg"/>
          <el-table-column :label="$t('common.outMsg')" prop="outMsg"/>
          <el-table-column :label="$t('common.inBytes')" prop="inBytes"/>
          <el-table-column :label="$t('common.outBytes')" prop="outBytes"/>
        </el-table>
        <h4>
          Bundles
          <el-tooltip class="item" effect="dark" placement="top">
            <div slot="content">
              {{ $t('namespace.bundle.bundleInfoContent') }}
              <br>
              <br>
              <el-link
                type="primary"
                href="https://pulsar.apache.org/docs/en/next/administration-load-balance/#pulsar-load-manager-architecture"
                target="_blank">
                {{ $t('namespace.bundle.bundleInfoLink') }}
              </el-link>
            </div>
            <i class="el-icon-info"/>
          </el-tooltip>
        </h4>
        <hr class="split-line">
        <el-tabs v-model="activeBundleCluster" type="border-card" @tab-click="handleBundleTabClick">
          <el-tab-pane v-for="(item,index) in replicationClustersValue" :key="item+index" :label="item" :name="item">
            <div class="filter-container">
              <el-button class="filter-item" style="margin-left: 10px;" type="danger" icon="el-icon-download" @click="handleUnloadAll">Unload All</el-button>
              <el-button class="filter-item" style="margin-left: 10px;" type="danger" icon="el-icon-close" @click="hanldeClearAllBacklog">Clear All Backlog</el-button>
            </div>
            <el-table
              :key="tableKey"
              :data="localList"
              border
              fit
              highlight-current-row
              style="width: 100%;">
              <el-table-column :label="$t('namespace.bundle.label')" align="center" min-width="100px">
                <template slot-scope="scope">
                  <span>{{ scope.row.bundle }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('namespace.bundle.operation')" align="center" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                  <el-button size="medium" style="margin-left: 10px;" type="danger" icon="el-icon-share" @click="handleSplitBundle(scope.row)">{{ $t('namespace.bundle.split') }}</el-button>
                  <el-button size="medium" style="margin-left: 10px;" type="danger" icon="el-icon-download" @click="handleUnloadBundle(scope.row)">{{ $t('namespace.bundle.unload') }}</el-button>
                  <el-button size="medium" style="margin-left: 10px;" type="danger" icon="el-icon-close" @click="handleClearBundleBacklog(scope.row)">{{ $t('namespace.clearBacklog') }}</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>

        <!-- <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit"/> -->
      </el-tab-pane>
      <el-tab-pane :label="$t('tabs.topic')" name="topics">
        <el-input v-model="searchTopic" :placeholder="$t('namespace.searchTopics')" style="width: 200px;" @keyup.enter.native="handleFilterTopic"/>
        <el-button type="primary" icon="el-icon-search" @click="handleFilterTopic"/>
        <el-button type="primary" icon="el-icon-plus" @click="handleCreateTopic">{{ $t('namespace.newTopic') }}</el-button>
        <el-row :gutter="24">
          <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}" style="margin-top:15px">
            <el-table
              v-loading="topicsListLoading"
              :key="topicsTableKey"
              :data="topicsList"
              :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
              fit
              highlight-current-row
              row-key="id">
              <el-table-column :label="$t('topic.label')" min-width="50px" align="left">
                <template slot-scope="scope">
                  <router-link :to="scope.row.topicLink" class="link-type">
                    <span>{{ scope.row.topic }}</span>
                  </router-link>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.partitionNumber')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.partitions }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.topicDomain')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.persistent }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.producer.producerNumber')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.producers }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('topic.subscription.subscriptionNumber')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.subscriptions }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('common.inMsg')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <i class="el-icon-download" style="margin-right: 2px"/><span>{{ scope.row.inMsg }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('common.outMsg')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <i class="el-icon-upload2" style="margin-right: 2px"/><span>{{ scope.row.outMsg }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('common.inBytes')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <i class="el-icon-download" style="margin-right: 2px"/><span>{{ scope.row.inBytes }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('common.outBytes')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <i class="el-icon-upload2" style="margin-right: 2px"/><span>{{ scope.row.outBytes }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('common.storageSize')" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.storageSize }}</span>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane :label="$t('tabs.policies')" name="policies">
        <h4>{{ $t('namespace.policy.cluster') }}</h4>
        <hr class="split-line">
        <div class="component-item">
          <div class="section-title">
            <span>{{ $t('namespace.policy.replicatedCluster') }}</span>
            <el-tooltip :content="$t('namespace.policy.replicatedClustersContent')" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
          </div>
          <el-select
            v-model="replicationClustersValue"
            :placeholder="$t('cluster.selectClusterMessage')"
            style="width:500px;margin-top:20px"
            multiple
            @change="handleReplicationsClusters()">
            <el-option v-for="item in replicationClustersOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </div>
        <h4>{{ $t('namespace.policy.authorization') }}
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
              :placeholder="$t('namespace.policy.selectRole')"
              multiple
              style="width:300px;"
              @change="handleChangeOptions(tag)">
              <el-option
                v-for="item in roleMapOptions[tag]"
                :key="item.value"
                :label="item.label"
                :value="item.value"
                style="width:300px"/>
            </el-select>
            <el-button @click.prevent="handleClose(tag)">{{ $t('namespace.policy.deleteRole') }}</el-button>
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
            <el-button @click="showInput()">{{ $t('namespace.policy.addRole') }}</el-button>
            <!-- <el-button @click="revokeAllRole()">Revoke All</el-button> -->
          </el-form-item>
        </el-form>
        <h4>{{ $t('namespace.policy.subscriptionAuthentication') }}</h4>
        <hr class="split-line">
        <div class="section-title">
          <span>{{ $t('namespace.policy.subscriptionAuthenticationMode') }}</span>
          <el-tooltip class="item" effect="dark" placement="top">
            <div slot="content">
              {{ $t('namespace.policy.subscriptionAuthenticationModeContent') }}
              <br>
              <br>
              {{ $t('namespace.policy.subscriptionAuthenticationModeOptions') }}
              <ul>
                <li>{{ $t('namespace.policy.subscriptionAuthenticationModeNone') }}</li>
                <li>
                  {{ $t('namespace.policy.subscriptionAuthenticaitonModePrefix') }}
                  <br>
                  {{ $t('namespace.policy.subscriptionAuthenticaitonModePrefixExample') }}
                </li>
              </ul>
            </div>
            <i class="el-icon-info"/>
          </el-tooltip>
        </div>
        <el-select
          v-model="subscriptionAuthenticationMode"
          placeholder="Please select Authentication"
          style="margin-top:20px;width:300px"
          @change="handleSubscriptionAuthMode()">
          <el-option
            v-for="item in subscriptionAuthenticationModeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"/>
        </el-select>
        <h4>{{ $t('namespace.policy.storage') }}</h4>
        <hr class="split-line">
        <div class="section-title">
          <span>{{ $t('namespace.policy.replicationFactor') }}</span>
          <el-tooltip class="item" effect="dark" placement="top">
            <div slot="content">
              {{ $t('namespace.policy.replicationFactorContent') }}
              <ul>
                <li>{{ $t('namespace.policy.ensembleSize') }}{{ $t('namespace.policy.ensembleSizeDescription') }}</li>
                <li>{{ $t('namespace.policy.writeQuorumSize') }}{{ $t('namespace.policy.writeQuorumSizeDescription') }}</li>
                <li>{{ $t('namespace.policy.readQuorumSize') }}{{ $t('namespace.policy.ackQuorumSizeDescription') }}</li>
              </ul>
            </div>
            <i class="el-icon-info"/>
          </el-tooltip>
        </div>
        <el-form :inline="true" :model="form" :rules="rules">
          <el-form-item prop="ensembleSize">
            <span>{{ $t('namespace.policy.ensembleSize') }}</span>
            <md-input
              v-model="form.ensembleSize"
              :placeholder="$t('namespace.policy.inputEnsemble')"
              class="md-input-style"
              name="ensembleSize"
              @keyup.enter.native="handlePersistence"/>
          </el-form-item>
          <el-form-item prop="writeQuorumSize">
            <span>{{ $t('namespace.policy.writeQuorumSize') }}</span>
            <md-input
              v-model="form.writeQuorumSize"
              :placeholder="$t('namespace.policy.inputWriteQuorumSize')"
              class="md-input-style"
              name="writeQuorumSize"
              @keyup.enter.native="handlePersistence"/>
          </el-form-item>
          <el-form-item prop="readQuorumSize">
            <span>{{ $t('namespace.policy.readQuorumSize') }}</span>
            <md-input
              v-model="form.readQuorumSize"
              :placeholder="$t('namespace.policy.inputReadQuorumSize')"
              class="md-input-style"
              name="readQuorumSize"
              @keyup.enter.native="handlePersistence"/>
          </el-form-item>
        </el-form>
        <el-form :inline="true" :model="form" :rules="rules">
          <el-form-item prop="markDeleteMaxRate">
            <span>{{ $t('namespace.policy.markDeleteRate') }}</span>
            <el-tooltip :content="markDeleteRateContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <md-input
              v-model="form.markDeleteMaxRate"
              :placeholder="$t('namespace.policy.inputDeleteMaxRate')"
              class="md-input-style"
              name="markDeleteMaxRate"
              @keyup.enter.native="handlePersistence"/>
          </el-form-item>
        </el-form>
        <el-form :inline="true" :model="form" :rules="rules">
          <el-form-item prop="encryptionRequire">
            <span>{{ $t('namespace.policy.encryptionRequire') }}</span>
            <el-tooltip :content="encryptionRequireContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <br>
            <el-radio-group
              v-model="form.encryptionRequireRadio"
              size="medium"
              style="margin-top:8px;width:300px"
              @change="handleEncryption()">
              <el-radio :label="$t('common.enabled')"/>
              <el-radio :label="$t('common.disabled')"/>
            </el-radio-group>
          </el-form-item>
          <el-form-item prop="deduplication">
            <span>{{ $t('namespace.policy.deduplication') }}</span>
            <el-tooltip :content="deduplicationContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <br>
            <el-radio-group
              v-model="form.deduplicationRadio"
              size="medium"
              style="margin-top:8px;width:300px"
              @change="handleDeduplication()">
              <el-radio :label="$t('common.enabled')"/>
              <el-radio :label="$t('common.disabled')"/>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <h4>{{ $t('namespace.policy.backlog') }}</h4>
        <hr class="split-line">
        <el-form :inline="true" :model="form" :rules="rules">
          <el-form-item prop="backlogQuotasLimit">
            <span>{{ $t('namespace.policy.backlogQuotasLimit') }}</span>
            <el-tooltip :content="backlogQuotasLimitContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <md-input
              v-model="form.backlogQuotasLimit"
              :placeholder="$t('namespace.policy.inputBacklogQuotasLimit')"
              class="md-input-style"
              name="backlogQuotasLimit"
              @keyup.enter.native="handleBacklogQuota"/>
          </el-form-item>
          <el-form-item style="width:300px">
            <span>{{ $t('namespace.policy.backlogRententionPolicy') }}</span>
            <el-tooltip class="item" effect="dark" placement="top">
              <div slot="content">
                {{ $t('namespace.policy.backlogRententionPolicyContent') }}
                <br>
                <br>
                {{ $t('namespace.policy.backlogRententionPolicyOptions') }}
                <ul>
                  <li>{{ $t('namespace.policy.producerRequestHoldDesc') }}</li>
                  <li>{{ $t('namespace.policy.producerExceptionDesc') }}</li>
                  <li>{{ $t('namespace.policy.consumerEvictionDesc') }}</li>
                </ul>
              </div>
              <i class="el-icon-info"/>
            </el-tooltip>
            <br>
            <el-select
              v-model="form.backlogRententionPolicy"
              :placeholder="$t('namespace.policy.inputBacklogRententionPolicyContent')"
              style="margin-top:8px;width:400px"
              @change="handleBacklogQuota()">
              <el-option
                v-for="item in backlogRententionPolicyOption"
                :key="item.value"
                :label="item.label"
                :value="item.value"/>
            </el-select>
          </el-form-item>
        </el-form>
        <h4>{{ $t('namespace.policy.schema') }}</h4>
        <hr class="split-line">
        <el-form :inline="true" :model="form" :rules="rules">
          <el-form-item prop="autoUpdateStrategy">
            <span>{{ $t('namespace.policy.autoUpdateStrategy') }}</span>
            <el-tooltip class="item" effect="dark" placement="top">
              <div slot="content">
                {{ $t('namespace.policy.autoUpdateStrategyContent') }}
                <br>
                <br>
                <el-link
                  type="primary"
                  href="http://pulsar.apache.org/docs/en/next/schema-evolution-compatibility/#schema-compatibility-check-strategy"
                  target="_blank">
                  {{ $t('namespace.policy.schemaCompatibilityCheckStrategyLink') }}
                </el-link>
              </div>
              <i class="el-icon-info"/>
            </el-tooltip>
            <br>
            <el-select
              v-model="form.autoUpdateStrategy"
              :placeholder="$t('namespace.policy.inputAutoUpdateStrategy')"
              style="margin-top:8px;width:300px"
              @change="handleSchemaAutoUpdateStrategy()">
              <el-option
                v-for="item in autoUpdateStrategyOption"
                :key="item.value"
                :label="item.label"
                :value="item.value"/>
            </el-select>
          </el-form-item>
          <el-form-item prop="schemaValidationEnforced">
            <span>{{ $t('namespace.policy.schemaValidationEnforced') }}</span>
            <el-tooltip :content="schemaValidationEnforcedContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <br>
            <el-radio-group
              v-model="form.schemaValidationEnforcedRadio"
              size="medium"
              style="margin-top:8px;width:300px"
              @change="handleSchemaValidationEnforced()">
              <el-radio :label="$t('common.enabled')"/>
              <el-radio :label="$t('common.disabled')"/>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <h4>{{ $t('namespace.policy.cleanupPolicy') }}</h4>
        <hr class="split-line">
        <el-form :inline="true" :model="form" :rules="rules" @submit.native.prevent>
          <el-form-item prop="messageTTL">
            <span>{{ $t('namespace.policy.messageTTL') }}</span>
            <el-tooltip :content="messageTTLContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <md-input
              v-model="form.messageTTL"
              :placeholder="$t('namespace.policy.inputMessageTTL')"
              class="md-input-style"
              name="messageTTL"
              @keyup.enter.native="handleMessageTTL"/>
          </el-form-item>
        </el-form>
        <el-form :inline="true" :model="form" :rules="rules">
          <el-form-item prop="retentionSize">
            <span>{{ $t('namespace.policy.retentionSize') }}</span>
            <el-tooltip :content="retentionSizeContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <md-input
              v-model="form.retentionSize"
              :placeholder="$t('namespace.policy.inputRententionSize')"
              class="md-input-style"
              name="retentionSize"
              @keyup.enter.native="handleRetention"/>
          </el-form-item>
          <el-form-item prop="retentionTime">
            <span>{{ $t('namespace.policy.retentionTime') }}</span>
            <el-tooltip :content="retentionTimeContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <md-input
              v-model="form.retentionTime"
              :placeholder="$t('namespace.policy.inputRetentionTime')"
              class="md-input-style"
              name="retentionTime"
              @keyup.enter.native="handleRetention"/>
          </el-form-item>
        </el-form>
        <el-form :inline="true" :model="form" :rules="rules">
          <el-form-item prop="compactionThreshold">
            <span>{{ $t('namespace.policy.compactionThreshold') }}</span>
            <el-tooltip :content="compactionThresholdContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <md-input
              v-model="form.compactionThreshold"
              :placeholder="$t('namespace.policy.inputCompactionThreshold')"
              class="md-input-style"
              name="compactionThreshold"
              @keyup.enter.native="handleCompactionThreshold"/>
          </el-form-item>
        </el-form>
        <el-form :inline="true" :model="form" :rules="rules">
          <el-form-item prop="offloadThreshold">
            <span>{{ $t('namespace.policy.offloadThreshold') }}</span>
            <el-tooltip :content="offloadThresholdContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <md-input
              v-model="form.offloadThreshold"
              :placeholder="$t('namespace.policy.inputOffloadThreshold')"
              class="md-input-style"
              name="offloadThreshold"
              @keyup.enter.native="handleOffloadThreshold"/>
          </el-form-item>
          <el-form-item prop="offloadDeletionLag">
            <span>{{ $t('namespace.policy.offloadDeletionLag') }}</span>
            <el-tooltip :content="offloadDeletionLagContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <md-input
              v-model="form.offloadDeletionLag"
              :placeholder="$t('namespace.policy.inputOffloadDeleteLag')"
              class="md-input-style"
              name="offloadDeletionLag"
              @keyup.enter.native="handleOffloadDeletionLag"/>
          </el-form-item>
        </el-form>
        <h4>{{ $t('namespace.policy.throttling') }}</h4>
        <hr class="split-line">
        <el-form :inline="true" :model="form" :rules="rules">
          <el-form-item prop="maxProducersPerTopic">
            <span>{{ $t('namespace.policy.maxProducersPerTopic') }}</span>
            <el-tooltip :content="maxProducersPerTopicContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <md-input
              v-model="form.maxProducersPerTopic"
              :placeholder="$t('namespace.policy.inputMaxProducersPerTopic')"
              class="md-input-style"
              name="maxProducersPerTopic"
              @keyup.enter.native="handleMaxProducersPerTopic"/>
          </el-form-item>
          <el-form-item prop="maxConsumersPerTopic">
            <span>{{ $t('namespace.policy.maxConsumersPerTopic') }}</span>
            <el-tooltip :content="maxConsumersPerTopicContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <md-input
              v-model="form.maxConsumersPerTopic"
              :placeholder="$t('namespace.policy.inputMaxConsumersForTopic')"
              class="md-input-style"
              name="maxConsumersPerTopic"
              @keyup.enter.native="handleMaxConsumersPerTopic"/>
          </el-form-item>
        </el-form>
        <el-form :inline="true" :model="form" :rules="rules" @submit.native.prevent>
          <el-form-item prop="maxConsumerPerSub">
            <span>{{ $t('namespace.policy.maxConsumerPerSubscription') }}</span>
            <el-tooltip :content="maxConsumerPerSubContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <md-input
              v-model="form.maxConsumerPerSub"
              :placeholder="$t('namespace.policy.inputMaxConsumersForSub')"
              class="md-input-style"
              name="maxConsumerPerSub"
              @keyup.enter.native="handleMaxConsuemrsPerSubscription"/>
          </el-form-item>
        </el-form>
        <h4>{{ $t('namespace.policy.dispatchRate') }}</h4>
        <hr class="split-line">
        <span>{{ $t('namespace.policy.dispatchRatePerTopic') }}</span>
        <el-tooltip :content="dispatchRatePerTopicContent" class="item" effect="dark" placement="top">
          <i class="el-icon-info"/>
        </el-tooltip>
        <el-form :inline="true" :model="form" :rules="rules">
          <el-form-item prop="dispatchRatePerTopicBytes">
            <span>{{ $t('namespace.policy.dispatchRatePerTopicBytes') }}</span>
            <md-input
              v-model="form.dispatchRatePerTopicBytes"
              :placeholder="$t('namespace.policy.inputDispatchRateBytes')"
              class="md-input-style"
              name="dispatchRatePerTopicBytes"
              @keyup.enter.native="handleDispatchRatePerTopic"/>
          </el-form-item>
          <el-form-item prop="dispatchRatePerTopicMessage">
            <span>{{ $t('namespace.policy.dispatchRatePerTopicMessage') }}</span>
            <md-input
              v-model="form.dispatchRatePerTopicMessage"
              :placeholder="$t('namespace.policy.inputDispatchRatePerTopicMessage')"
              class="md-input-style"
              name="dispatchRatePerTopicMessage"
              @keyup.enter.native="handleDispatchRatePerTopic"/>
          </el-form-item>
          <el-form-item prop="dispatchRatePerTopicPeriod">
            <span>{{ $t('namespace.policy.dispatchRatePerTopicPeriod') }}</span>
            <md-input
              v-model="form.dispatchRatePerTopicPeriod"
              :placeholder="$t('namespace.policy.inputDispatchPerTopicPerPeriod')"
              class="md-input-style"
              name="dispatchRatePerTopicPeriod"
              @keyup.enter.native="handleDispatchRatePerTopic"/>
          </el-form-item>
        </el-form>
        <span>{{ $t('namespace.policy.dispatchRateForSub') }}</span>
        <el-tooltip :content="dispatchRatePerSubContent" class="item" effect="dark" placement="top">
          <i class="el-icon-info"/>
        </el-tooltip>
        <el-form :inline="true" :model="form" :rules="rules" @submit.native.prevent>
          <el-form-item prop="dispatchRatePerSubBytes">
            <span>{{ $t('namespace.policy.dispatchRatePerSubBytes') }}</span>
            <md-input
              v-model="form.dispatchRatePerSubBytes"
              :placeholder="$t('namespace.policy.inputDispatchRatePerSubBytes')"
              class="md-input-style"
              name="dispatchRatePerSubBytes"
              @keyup.enter.native="handleDispatchRatePerSub"/>
          </el-form-item>
          <el-form-item prop="dispatchRatePerSubMessage">
            <span>{{ $t('namespace.policy.dispatchRatePerSubMessage') }}</span>
            <md-input
              v-model="form.dispatchRatePerSubMessage"
              :placeholder="$t('namespace.policy.inputDispatchRatePerSubMessage')"
              class="md-input-style"
              name="dispatchRatePerSubMessage"
              @keyup.enter.native="handleDispatchRatePerSub"/>
          </el-form-item>
          <el-form-item prop="dispatchRatePerSubPeriod">
            <span>{{ $t('namespace.policy.dispatchRatePerSubPeriod') }}</span>
            <md-input
              v-model="form.dispatchRatePerSubPeriod"
              :placeholder="$t('namespace.policy.inputDispatchRatePerSubPeriod')"
              class="md-input-style"
              name="dispatchRatePerSubPeriod"
              @keyup.enter.native="handleDispatchRatePerSub"/>
          </el-form-item>
        </el-form>
        <span>{{ $t('namespace.policy.subscribeRatePerConsumer') }}</span>
        <el-tooltip :content="subscribeRatePerConsumerContent" class="item" effect="dark" placement="top">
          <i class="el-icon-info"/>
        </el-tooltip>
        <el-form :inline="true" :model="form" :rules="rules" @submit.native.prevent>
          <el-form-item prop="subscribeRatePerConsumerSub">
            <span>{{ $t('namespace.policy.subscribeRatePerConsumerSub') }}</span>
            <md-input
              v-model="form.subscribeRatePerConsumerSub"
              :placeholder="$t('namespace.policy.inputSubscribeRate')"
              class="md-input-style"
              name="subscribeRatePerConsumerSub"
              @keyup.enter.native="handleSubscribeRate"/>
          </el-form-item>
          <el-form-item prop="subscribeRatePerConsumerPeriod">
            <span>{{ $t('namespace.policy.subscribeRatePerConsumerPeriod') }}</span>
            <md-input
              v-model="form.subscribeRatePerConsumerPeriod"
              :placeholder="$t('namespace.policy.inputSubscribeRate')"
              class="md-input-style"
              name="subscribeRatePerConsumerPeriod"
              @keyup.enter.native="handleSubscribeRate"/>
          </el-form-item>
        </el-form>
        <h4>{{ $t('namespace.policy.antiAffinity') }}</h4>
        <hr class="split-line">
        <el-form :inline="true" :model="form" :rules="rules" @submit.native.prevent>
          <el-form-item prop="antiAffinityGroup">
            <span>{{ $t('namespace.policy.antiAffinityGroup') }}</span>
            <el-tooltip :content="antiAffinityGroupContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <md-input
              v-model="form.antiAffinityGroup"
              :placeholder="$t('namespace.policy.inputAntiAffinityGroup')"
              class="md-input-style"
              name="antiAffinityGroup"
              @keyup.enter.native="handleAntiAffinityGroup"/>
          </el-form-item>
        </el-form>
        <h4 style="color:#E57470">{{ $t('common.dangerZone') }}</h4>
        <hr class="danger-line">
        <el-button type="danger" class="button" @click="handleDeleteNamespace">{{ $t('namespace.deleteNamespace') }}</el-button>
      </el-tab-pane>
    </el-tabs>
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="30%">
      <el-form ref="form" :rules="rules" :model="form" label-position="top">
        <div v-if="dialogStatus==='create'">
          <el-form-item :label="$t('topic.topicDomain')">
            <el-radio-group
              v-model="form.isPersistent"
              size="medium">
              <el-radio :label="$t('topic.persistent')"/>
              <el-radio :label="$t('topic.nonPersistent')"/>
            </el-radio-group>
          </el-form-item>
          <el-form-item :label="$t('topic.topicName')" prop="topic">
            <el-input v-model="form.topic"/>
          </el-form-item>
          <el-form-item :label="$t('topic.partitions')" prop="partition">
            <el-input v-model="form.partitions"/>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="createTopic()">{{ $t('table.confirm') }}</el-button>
            <el-button @click="dialogFormVisible = false">{{ $t('table.cancel') }}</el-button>
          </el-form-item>
        </div>
        <el-form-item v-if="dialogStatus==='delete'">
          <h4>{{ $t('namespace.deleteNamespaceMessage') }}</h4>
        </el-form-item>
        <el-form-item v-if="dialogStatus==='delete'">
          <el-button type="primary" @click="deleteNamespace()">{{ $t('table.confirm') }}</el-button>
          <el-button @click="dialogFormVisible = false">{{ $t('table.cancel') }}</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>
<script>
import { fetchTenants, fetchTenantsInfo } from '@/api/tenants'
import {
  fetchNamespaces,
  fetchNamespacePolicies,
  getPermissions,
  getPersistence,
  getRetention,
  setClusters,
  grantPermissions,
  revokePermissions,
  setSubscriptionAuthMode,
  setPersistence,
  setBacklogQuota,
  setEncryptionRequired,
  setDeduplication,
  setSchemaAutoupdateStrategy,
  setSchemaValidationEnforced,
  setMessageTtl,
  setRetention,
  setCompactionThreshold,
  setOffloadThreshold,
  setOffloadDeletionLag,
  setMaxProducersPerTopic,
  setMaxConsumersPerTopic,
  setMaxConsumersPerSubscription,
  setDispatchRate,
  setSubscriptionDispatchRate,
  setSubscribeRate,
  setAntiAffinityGroup,
  splitBundleOnCluster,
  unloadBundleOnCluster,
  unloadOnCluster,
  clearBacklogOnCluster,
  deleteNamespace,
  clearBundleBacklogOnCluster,
  fetchNamespaceStats
} from '@/api/namespaces'
import { putTopic, fetchTopicsStatsByPulsarManager } from '@/api/topics'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import MdInput from '@/components/MDinput'
import { validateEmpty } from '@/utils/validate'
import { formatBytes } from '@/utils/index'
import { numberFormatter } from '@/filters/index'

const defaultForm = {
  tenant: '',
  namespace: ''
}
export default {
  name: 'NamespaceInfo',
  components: {
    MdInput,
    Pagination
  },
  data() {
    return {
      postForm: Object.assign({}, defaultForm),
      tenantNamespace: '',
      tenantsListOptions: [],
      namespacesListOptions: [],
      activeName: 'overview',
      activeBundleCluster: '',
      namespaceStats: [{
        inMsg: 0,
        outMsg: 0,
        inBytes: 0,
        outBytes: 0
      }],
      replicatedClustersContent: this.$i18n.t('namespace.policy.replicatedClustersContent'),
      replicationClustersValue: [],
      replicationClustersOptions: [],
      dynamicValidateForm: {
        domains: [{
          value: ''
        }],
        email: ''
      },
      dynamicTags: [],
      inputVisible: false,
      inputValue: '',
      roleValue: [],
      roleMap: {},
      roleMapOptions: {},
      roleOptions: [{
        value: 'consume',
        label: this.$i18n.t('role_actions.consume')
      }, {
        value: 'produce',
        label: this.$i18n.t('role_actions.produce')
      }, {
        value: 'functions',
        label: this.$i18n.t('role_actions.functions')
      }],
      authorizationContent: this.$i18n.t('namespace.policy.authorizationContent'),
      subscriptionAuthenticationMode: '',
      subscriptionAuthenticationModeContent: this.$i18n.t('namespace.policy.subscriptionAuthenticationModeContent'),
      subscriptionAuthenticationModeOptions: [{
        value: 'None',
        label: this.$i18n.t('subscription_auth_mode.none')
      }, {
        value: 'Prefix',
        label: this.$i18n.t('subscription_auth_mode.prefix')
      }],
      replicationFactorContent: this.$i18n.t('namespace.policy.replicationFactorContent'),
      form: {
        ensembleSize: '',
        writeQuorumSize: '',
        readQuorumSize: '',
        markDeleteMaxRate: '',
        backlogQuotasLimit: '',
        backlogRententionPolicy: '',
        encryptionRequire: '',
        deduplication: '',
        autoUpdateStrategy: '',
        messageTTL: '',
        retentionTime: '',
        encryptionRequireRadio: this.$i18n.t('common.disabled'),
        deduplicationRadio: this.$i18n.t('common.disabled'),
        schemaValidationEnforcedRadio: this.$i18n.t('common.disabled'),
        dispatchRatePerTopicBytes: '',
        dispatchRatePerTopicMessage: '',
        dispatchRatePerTopicPeriod: '',
        dispatchRatePerSubBytes: '',
        antiAffinityGroup: '',
        dispatchRatePerSubMessage: '',
        dispatchRatePerSubPeriod: '',
        subscribeRatePerConsumerSub: '',
        subscribeRatePerConsumerPeriod: '',
        maxConsumerPerSub: '',
        topic: '',
        isPersistent: 'Persistent',
        partitions: 0
      },
      rules: {
        topic: [{ required: true, message: 'topic is required', trigger: 'blur' }]
      },
      markDeleteRateContent: this.$i18n.t('namespace.policy.markDeleteRateContent'),
      backlogQuotasLimitContent: this.$i18n.t('namespace.policy.backlogQuotasLimitContent'),
      backlogRententionPolicyContent: this.$i18n.t('namespace.policy.backlogRententionPolicyContent'),
      backlogRententionPolicyRadio: 'consumer_backlog_eviction',
      backlogRententionPolicyOption: [{
        value: 'consumer_backlog_eviction',
        label: this.$i18n.t('backlog_policy.consumer_backlog_eviction')
      }, {
        value: 'producer_exception',
        label: this.$i18n.t('backlog_policy.producer_exception')
      }, {
        value: 'producer_request_hold',
        label: this.$i18n.t('backlog_policy.producer_request_hold')
      }],
      encryptionRequireContent: this.$i18n.t('namespace.policy.encryptionRequireContent'),
      encryptionRequireOption: [{
        value: 'Enabled',
        label: this.$i18n.t('common.enabled')
      }, {
        value: 'Disabled',
        label: this.$i18n.t('common.disabled')
      }],
      deduplicationContent: this.$i18n.t('namespace.policy.deduplicationContent'),
      deduplication: '',
      deduplicationOption: [{
        value: 'Enabled',
        label: this.$i18n.t('common.enabled')
      }, {
        value: 'Disabled',
        label: this.$i18n.t('common.disabled')
      }],
      autoUpdateStrategyContent: this.$i18n.t('namespace.policy.autoUpdateStrategyContent'),
      autoUpdateStrategyOption: [{
        value: 'Full',
        label: this.$i18n.t('schema_bc.full')
      }, {
        value: 'FullTransitive',
        label: this.$i18n.t('schema_bc.full_transitive')
      }, {
        value: 'Forward',
        label: this.$i18n.t('schema_bc.forward')
      }, {
        value: 'ForwardTransitive',
        label: this.$i18n.t('schema_bc.forward_transitive')
      }, {
        value: 'Backward',
        label: this.$i18n.t('schema_bc.backward')
      }, {
        value: 'BackwardTransitive',
        label: this.$i18n.t('schema_bc.backward_transitive')
      }, {
        value: 'AlwaysCompatible',
        label: this.$i18n.t('schema_bc.always')
      }, {
        value: 'AutoUpdateDisabled',
        label: this.$i18n.t('schema_bc.auto_update_disabled')
      }],
      schemaValidationEnforcedContent: this.$i18n.t('namespace.policy.schemaValidationEnforcedContent'),
      schemaValidationEnforced: '',
      messageTTLContent: this.$i18n.t('namespace.policy.messageTTLContent'),
      retentionSizeContent: this.$i18n.t('namespace.policy.retentionSizeContent'),
      retentionTimeContent: this.$i18n.t('namespace.policy.retentionTimeContent'),
      compactionThresholdContent: this.$i18n.t('namespace.policy.compactionThresholdContent'),
      offloadThresholdContent: this.$i18n.t('namespace.policy.offloadThresholdContent'),
      offloadDeletionLagContent: this.$i18n.t('namespace.policy.offloadDeletionLagContent'),
      maxProducersPerTopicContent: this.$i18n.t('namespace.policy.maxProducersPerTopicContent'),
      maxConsumersPerTopicContent: this.$i18n.t('namespace.policy.maxConsumersPerTopicContent'),
      maxConsumerPerSubContent: this.$i18n.t('namespace.policy.maxConsumerPerSubContent'),
      dispatchRatePerTopicContent: this.$i18n.t('namespace.policy.dispatchRatePerTopicContent'),
      dispatchRatePerSubContent: this.$i18n.t('namespace.policy.dispatchRatePerSubContent'),
      subscribeRatePerConsumerContent: this.$i18n.t('namespace.policy.subscribeRatePerConsumerContent'),
      antiAffinityGroupContent: this.$i18n.t('namespace.policy.antiAffinityGroupContent'),
      tableKey: 0,
      topicsListLoading: false,
      topicsTableKey: 0,
      brokerStats: null,
      topics: {},
      localList: [],
      tempTopicsList: [],
      topicsList: [],
      allTopicList: [],
      topicsTotal: 0,
      // total: 0,
      topicsListQuery: {
        page: 1,
        limit: 10
      },
      firstInit: false,
      searchTopic: '',
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        create: this.$i18n.t('topic.newTopic'),
        delete: this.$i18n.t('namespace.deleteNamespace')
      },
      currentTabName: '',
      bundleInfoContent: this.$i18n.t('namespace.bundle.bundleInfoContent')
    }
  },
  created() {
    this.postForm.tenant = this.$route.params && this.$route.params.tenant
    this.postForm.namespace = this.$route.params && this.$route.params.namespace
    this.tenantNamespace = this.postForm.tenant + '/' + this.postForm.namespace
    this.firstInit = true
    if (this.$route.query && this.$route.query.tab) {
      this.activeName = this.$route.query.tab
      this.currentTabName = this.$route.query.tab
    }
    this.getRemoteTenantsList()
    this.getNamespacesList(this.postForm.tenant)
    this.getTenantsInfo(this.postForm.tenant)
    this.getPolicies(this.tenantNamespace)
    this.initPermissions(this.tenantNamespace)
    this.initPersistence(this.tenantNamespace)
    this.initRetention(this.tenantNamespace)
    this.getStats()
    this.getTopicsStats()
    this.activeBundleCluster = this.replicationClustersValue.length > 0 ? this.replicationClustersValue[0] : ''
  },
  methods: {
    getNamespaceStats() {
      fetchNamespaceStats(this.postForm.tenant, this.postForm.namespace).then(response => {
        if (!response.data) return
        this.namespaceStats = []
        if (response.data.hasOwnProperty('inMsg')) {
          this.namespaceStats.push({
            inMsg: numberFormatter(response.data.inMsg, 2),
            outMsg: numberFormatter(response.data.outMsg, 2),
            inBytes: formatBytes(response.data.msgThroughputIn),
            outBytes: formatBytes(response.data.msgThroughputOut)
          })
        }
      })
    },
    getStats() {
      this.getNamespaceStats()
      this.getTopics()
    },
    getTopicsStats() {
      fetchTopicsStatsByPulsarManager(this.postForm.tenant, this.postForm.namespace).then(response => {
        if (!response.data) return
      })
    },
    getTopics() {
      fetchTopicsStatsByPulsarManager(this.postForm.tenant, this.postForm.namespace).then(response => {
        if (!response.data) return
        for (var i in response.data.topics) {
          var topicLink = ''
          if (response.data.topics[i]['partitions'] === 0) {
            topicLink = '/management/topics/' + response.data.topics[i]['persistent'] + '/' + this.tenantNamespace + '/' + response.data.topics[i]['topic'] + '/topic'
          } else {
            topicLink = '/management/topics/' + response.data.topics[i]['persistent'] + '/' + this.tenantNamespace + '/' + response.data.topics[i]['topic'] + '/partitionedTopic'
          }
          var children = []
          var clusters = response.data.topics[i]['clusters']
          for (var j in clusters) {
            var clusterTopicInfo = {
              'id': 1000000 * (i + 1) + j,
              'topic': clusters[j]['topic'],
              'partitions': clusters[j]['partitions'],
              'persistent': clusters[j]['persistent'],
              'producers': clusters[j]['producerCount'],
              'subscriptions': clusters[j]['subscriptionCount'],
              'inMsg': numberFormatter(clusters[j]['msgRateIn'], 2),
              'outMsg': numberFormatter(clusters[j]['msgRateOut'], 2),
              'inBytes': formatBytes(clusters[j]['msgThroughputIn']),
              'outBytes': formatBytes(clusters[j]['msgThroughputOut']),
              'storageSize': formatBytes(clusters[j]['storageSize'], 0),
              'tenantNamespace': this.tenantNamespace,
              'topicLink': topicLink + '?cluster=' + clusters[j]['topic'] + '&tab='
            }
            children.push(clusterTopicInfo)
          }

          if (clusters.length <= 0) {
            var tempCluster = {
              'id': 1000000 * (i + 1),
              'topic': '-',
              'partitions': this.form.partitions,
              'persistent': this.form.isPersistent,
              'producers': 0,
              'subscriptions': 0,
              'inMsg': 0,
              'outMsg': 0,
              'inBytes': 0,
              'outBytes': 0,
              'storageSize': 0,
              'tenantNamespace': this.tenantNamespace,
              'topicLink': topicLink
            }
            if (this.replicationClustersValue.length <= 0) {
              children.push(tempCluster)
            }
            for (var c in this.replicationClustersValue) {
              tempCluster.topic = this.replicationClustersValue[c]
              children.push(tempCluster)
            }
          }

          var topicInfo = {
            'id': i,
            'topic': response.data.topics[i]['topic'],
            'partitions': response.data.topics[i]['partitions'],
            'persistent': response.data.topics[i]['persistent'],
            'producers': response.data.topics[i]['producers'],
            'subscriptions': response.data.topics[i]['subscriptions'],
            'inMsg': numberFormatter(response.data.topics[i]['inMsg'], 2),
            'outMsg': numberFormatter(response.data.topics[i]['outMsg'], 2),
            'inBytes': formatBytes(response.data.topics[i]['inBytes']),
            'outBytes': formatBytes(response.data.topics[i]['outBytes']),
            'storageSize': formatBytes(response.data.topics[i]['storageSize'], 0),
            'children': children,
            'tenantNamespace': this.tenantNamespace,
            'topicLink': topicLink + '?tab='
          }
          this.topicsListLoading = false
          this.topicsList.push(topicInfo)
          this.allTopicList.push(topicInfo)
        }
      })
    },
    handleFilterTopic() {
      if (this.tempTopicsList.length <= 0) {
        for (var t = 0; t < this.allTopicList.length; t++) {
          this.tempTopicsList.push(this.allTopicList[t])
        }
      }
      if (!validateEmpty(this.searchTopic)) {
        this.searchList = []
        for (var i = 0; i < this.allTopicList.length; i++) {
          if (this.allTopicList[i]['topic'].indexOf(this.searchTopic) !== -1) {
            this.searchList.push(this.allTopicList[i])
          }
        }
        this.topicsList = this.searchList
      } else {
        this.topicsList = this.tempTopicsList
      }
    },
    getPolicies(tenantNamespace) {
      fetchNamespacePolicies(tenantNamespace).then(response => {
        if (!response.data) return
        this.initPoliciesOptions(response.data)
      })
    },
    initPersistence(tenantNamespace) {
      getPersistence(tenantNamespace).then(response => {
        if (!response.data) return
        this.form.ensembleSize = String(response.data.bookkeeperEnsemble)
        this.form.writeQuorumSize = String(response.data.bookkeeperWriteQuorum)
        this.form.readQuorumSize = String(response.data.bookkeeperAckQuorum)
        this.form.markDeleteMaxRate = String(response.data.managedLedgerMaxMarkDeleteRate)
      })
    },
    initPermissions(tenantNamespace) {
      getPermissions(tenantNamespace).then(response => {
        if (!response.data) return
        for (var key in response.data) {
          this.dynamicTags.push(key)
          this.roleMap[key] = response.data[key]
          this.roleMapOptions[key] = this.roleOptions
        }
      })
    },
    initRetention(tenantNamespace) {
      getRetention(tenantNamespace).then(response => {
        if (!response.data) return
        this.form.retentionSize = String(response.data.retentionSizeInMB)
        this.form.retentionTime = String(response.data.retentionTimeInMinutes)
      })
    },
    getTenantsInfo(tenant) {
      fetchTenantsInfo(tenant).then(response => {
        this.replicationClustersOptions = []
        for (let i = 0; i < response.data.allowedClusters.length; i++) {
          this.replicationClustersOptions.push({
            value: response.data.allowedClusters[i],
            label: response.data.allowedClusters[i]
          })
        }
      })
    },
    initPoliciesOptions(policies) {
      this.replicationClustersValue = policies.replication_clusters
      this.activeBundleCluster = this.activeBundleCluster || this.replicationClustersValue.length > 0 ? this.replicationClustersValue[0] : ''
      this.subscriptionAuthenticationMode = policies.subscription_auth_mode
      this.form.backlogQuotasLimit = String(policies.backlog_quota_map.destination_storage.limit)
      this.form.backlogRententionPolicy = String(policies.backlog_quota_map.destination_storage.policy)
      if (policies.encryption_required) {
        this.form.encryptionRequireRadio = 'Enabled'
      }
      if (policies.hasOwnProperty('deduplicationRadio')) {
        this.form.deduplicationRadio = policies.deduplicationRadio
      }
      this.form.autoUpdateStrategy = policies.schema_auto_update_compatibility_strategy
      if (policies.schema_validation_enforced) {
        this.form.schemaValidationEnforcedRadio = 'Enabled'
      }
      this.form.messageTTL = String(policies.message_ttl_in_seconds)
      this.form.compactionThreshold = String(policies.compaction_threshold)
      this.form.offloadThreshold = String(policies.offload_threshold)
      if (policies.hasOwnProperty('offload_deletion_lag_ms')) {
        this.form.offloadDeletionLag = String(policies.offload_deletion_lag_ms)
      }
      this.form.maxProducersPerTopic = String(policies.max_producers_per_topic)
      this.form.maxConsumersPerTopic = String(policies.max_consumers_per_topic)
      this.form.maxConsumerPerSub = String(policies.max_consumers_per_subscription)
      for (var t in policies.topicDispatchRate) {
        this.form.dispatchRatePerTopicMessage = String(policies.topicDispatchRate[t].dispatchThrottlingRateInMsg)
        this.form.dispatchRatePerTopicBytes = String(policies.topicDispatchRate[t].dispatchThrottlingRateInByte)
        this.form.dispatchRatePerTopicPeriod = String(policies.topicDispatchRate[t].ratePeriodInSecond)
      }
      for (var s in policies.subscriptionDispatchRate) {
        this.form.dispatchRatePerSubBytes = String(policies.subscriptionDispatchRate[s].dispatchThrottlingRateInByte)
        this.form.dispatchRatePerSubMessage = String(policies.subscriptionDispatchRate[s].dispatchThrottlingRateInMsg)
        this.form.dispatchRatePerSubPeriod = String(policies.subscriptionDispatchRate[s].ratePeriodInSecond)
      }
      for (var c in policies.clusterSubscribeRate) {
        this.form.subscribeRatePerConsumerSub = String(policies.clusterSubscribeRate[c].subscribeThrottlingRatePerConsumer)
        this.form.subscribeRatePerConsumerPeriod = String(policies.clusterSubscribeRate[c].ratePeriodInSecond)
      }
      if (policies.hasOwnProperty('antiAffinityGroup')) {
        this.form.antiAffinityGroup = policies.antiAffinityGroup
      }
      // this.listQuery.limit = policies.bundles.numBundles
      // this.listQuery.page = 1
      // this.total = policies.bundles.numBundles
      for (var n = 0; n < policies.bundles.numBundles; n++) {
        this.localList.push({ 'bundle': policies.bundles.boundaries[n] + '_' + policies.bundles.boundaries[n + 1] })
      }
    },
    handleClick(tab, event) {
      this.currentTabName = tab.name
      this.$router.push({ query: { 'tab': tab.name }})
    },
    handleBundleTabClick(tab, event) {
      this.activeBundleCluster = tab.name
    },
    getRemoteTenantsList() {
      fetchTenants().then(response => {
        if (!response.data) return
        for (var i = 0; i < response.data.total; i++) {
          this.tenantsListOptions.push(response.data.data[i].tenant)
        }
      })
    },
    getNamespacesList(tenant) {
      fetchNamespaces(tenant, this.query).then(response => {
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
    getNamespaceInfo(tenant, namespace) {
      this.$router.push({ path: '/management/namespaces/' + tenant + '/' + namespace + '/namespace?tab=' + this.currentTabName })
    },
    handleFilterBundle() {
    },
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          alert('submit!')
        } else {
          return false
        }
      })
    },
    resetForm(formName) {
      this.$refs[formName].resetFields()
    },
    handleClose(tag) {
      this.dynamicTags.splice(this.dynamicTags.indexOf(tag), 1)
      revokePermissions(this.tenantNamespace, tag).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('namespace.notification.removeRoleSuccess'),
          type: 'success',
          duration: 3000
        })
        delete this.roleMap[tag]
        delete this.roleMapOptions[tag]
      })
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
            message: this.$i18n.t('namespace.policy.roleAlreadyExists'),
            type: 'error'
          })
          this.inputVisible = false
          this.inputValue = ''
          return
        }
        grantPermissions(this.tenantNamespace, inputValue, this.roleMap[inputValue]).then(response => {
          this.$notify({
            title: 'success',
            message: this.$i18n.t('namespace.notification.addRoleSuccess'),
            type: 'success',
            duration: 3000
          })
          this.dynamicTags.push(inputValue)
          this.roleMap[inputValue] = []
          this.roleMapOptions[inputValue] = this.roleOptions
        })
      }
      this.inputVisible = false
      this.inputValue = ''
    },
    handleChangeOptions(role) {
      grantPermissions(this.tenantNamespace, role, this.roleMap[role]).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('namespace.notification.addRoleActionsSuccess'),
          type: 'success',
          duration: 3000
        })
      })
      this.$forceUpdate()
    },
    revokeAllRole() {
    },
    handlePersistence() {
      const data = {
        'bookkeeperAckQuorum': parseInt(this.form.readQuorumSize),
        'bookkeeperEnsemble': parseInt(this.form.ensembleSize),
        'bookkeeperWriteQuorum': parseInt(this.form.writeQuorumSize),
        'managedLedgerMaxMarkDeleteRate': parseInt(this.form.markDeleteMaxRate)
      }
      setPersistence(this.tenantNamespace, data).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('namespace.notification.updatePersistenceSuccess'),
          type: 'success',
          duration: 3000
        })
      })
    },
    handleBacklogQuota() {
      const data = {
        'limit': this.form.backlogQuotasLimit,
        'policy': this.form.backlogRententionPolicy
      }
      setBacklogQuota(this.tenantNamespace, data).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('namespace.notification.updateBacklogQuotaSuccess'),
          type: 'success',
          duration: 3000
        })
      })
    },
    handleSplitBundle(row) {
      splitBundleOnCluster(this.activeBundleCluster, this.tenantNamespace, row.bundle, false).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('namespace.notification.splitBundleSuccess'),
          type: 'success',
          duration: 3000
        })
        this.localList = []
        this.getPolicies(this.tenantNamespace)
      })
    },
    handleUnloadAll() {
      unloadOnCluster(this.activeBundleCluster, this.tenantNamespace).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('namespace.notification.unloadAllBundlesSuccess'),
          type: 'success',
          duration: 3000
        })
      })
    },
    handleUnloadBundle(row) {
      unloadBundleOnCluster(this.activeBundleCluster, this.tenantNamespace, row.bundle).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('namespace.notification.unloadBundleSuccess'),
          type: 'success',
          duration: 3000
        })
      })
    },
    hanldeClearAllBacklog() {
      clearBacklogOnCluster(this.activeBundleCluster, this.tenantNamespace).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('namespace.notification.clearBacklogSuccess'),
          type: 'success',
          duration: 3000
        })
      })
    },
    handleClearBundleBacklog(row) {
      clearBundleBacklogOnCluster(this.activeBundleCluster, this.tenantNamespace, row.bundle).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('namespace.notification.clearBundleBacklogSuccess'),
          type: 'success',
          duration: 3000
        })
      })
    },
    handleReplicationsClusters() {
      setClusters(this.tenantNamespace, this.replicationClustersValue).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('namespace.notification.updateReplicatedClustersSuccess'),
          type: 'success',
          duration: 3000
        })
      })
    },
    handleSubscriptionAuthMode() {
      var subAuthMode = 0
      if (this.subscriptionAuthenticationMode === 'Prefix') {
        subAuthMode = 1
      }
      setSubscriptionAuthMode(this.tenantNamespace, subAuthMode).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('namespace.notification.updateSubscriptionAuthModeSuccess'),
          type: 'success',
          duration: 3000
        })
      })
    },
    handleEncryption() {
      let encryptionRequire = false
      let successMessage = this.$i18n.t('namespace.notification.disableEncryptionSuccess')
      if (this.form.encryptionRequireRadio === 'Enabled') {
        encryptionRequire = true
        successMessage = this.$i18n.t('namespace.notification.enableEncryptionSuccess')
      }
      setEncryptionRequired(this.tenantNamespace, encryptionRequire).then(response => {
        this.$notify({
          title: 'success',
          message: successMessage,
          type: 'success',
          duration: 3000
        })
      })
    },
    handleDeduplication() {
      let deduplication = false
      let successMessage = this.$i18n.t('namespace.notification.disableDeduplicationSuccess')
      if (this.form.deduplicationRadio === 'Enabled') {
        deduplication = true
        successMessage = this.$i18n.t('namespace.notification.enableDeduplicationSuccess')
      }
      setDeduplication(this.tenantNamespace, deduplication).then(response => {
        this.$notify({
          title: 'success',
          message: successMessage,
          type: 'success',
          duration: 3000
        })
      })
    },
    handleSchemaAutoUpdateStrategy() {
      var strategy = 3
      if (this.form.autoUpdateStrategy === 'AutoUpdateDisabled') {
        strategy = 0
      } else if (this.form.autoUpdateStrategy === 'Backward') {
        strategy = 1
      } else if (this.form.autoUpdateStrategy === 'Forward') {
        strategy = 2
      } else if (this.form.autoUpdateStrategy === 'Full') {
        strategy = 3
      } else if (this.form.autoUpdateStrategy === 'AlwaysCompatible') {
        strategy = 4
      } else if (this.form.autoUpdateStrategy === 'BackwardTransitive') {
        strategy = 5
      } else if (this.form.autoUpdateStrategy === 'ForwardTransitive') {
        strategy = 6
      } else if (this.form.autoUpdateStrategy === 'FullTransitive') {
        strategy = 7
      }
      setSchemaAutoupdateStrategy(this.tenantNamespace, strategy).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('namespace.notification.updateSchemaAutoUpdateStrategySuccess'),
          type: 'success',
          duration: 3000
        })
      })
    },
    handleSchemaValidationEnforced() {
      var schemaValidation = false
      var successMessage = this.$i18n.t('namespace.notification.disableSchemaValidationEnforcedSuccess')
      if (this.form.schemaValidationEnforcedRadio === 'Enabled') {
        schemaValidation = true
        successMessage = this.$i18n.t('namespace.notification.enableSchemaValidationEnforcedSuccess')
      }
      setSchemaValidationEnforced(this.tenantNamespace, schemaValidation).then(response => {
        this.$notify({
          title: 'success',
          message: successMessage,
          type: 'success',
          duration: 3000
        })
      })
    },
    handleMessageTTL() {
      setMessageTtl(this.tenantNamespace, parseInt(this.form.messageTTL)).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('namespace.notification.updateMessageTTLSuccess'),
          type: 'success',
          duration: 3000
        })
      })
    },
    handleRetention() {
      const data = { 'retentionSizeInMB': parseInt(this.form.retentionSize), 'retentionTimeInMinutes': parseInt(this.form.retentionTime) }
      setRetention(this.tenantNamespace, data).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('namespace.notification.updateRetentionSuccess'),
          type: 'success',
          duration: 3000
        })
      })
    },
    handleCompactionThreshold() {
      setCompactionThreshold(this.tenantNamespace, this.form.compactionThreshold).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('namespace.notification.updateCompactionThresholdSuccess'),
          type: 'success',
          duration: 3000
        })
      })
    },
    handleOffloadThreshold() {
      setOffloadThreshold(this.tenantNamespace, this.form.offloadThreshold).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('namespace.notification.updateOffloadThresholdSuccess'),
          type: 'success',
          duration: 3000
        })
      })
    },
    handleOffloadDeletionLag() {
      setOffloadDeletionLag(this.tenantNamespace, this.form.offloadDeletionLag).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('namespace.notification.updateOffloadDeletionLagSuccess'),
          type: 'success',
          duration: 3000
        })
      })
    },
    handleMaxProducersPerTopic() {
      setMaxProducersPerTopic(this.tenantNamespace, this.form.maxProducersPerTopic).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('namespace.notification.setMaxProducersSuccess'),
          type: 'success',
          duration: 3000
        })
      })
    },
    handleMaxConsumersPerTopic() {
      setMaxConsumersPerTopic(this.tenantNamespace, this.form.maxConsumersPerTopic).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('namespace.notification.setMaxConsumersSuccess'),
          type: 'success',
          duration: 3000
        })
      })
    },
    handleMaxConsuemrsPerSubscription() {
      setMaxConsumersPerSubscription(this.tenantNamespace, this.form.maxConsumerPerSub).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('namespace.notification.setMaxConsumersPerSubscriptionSuccess'),
          type: 'success',
          duration: 3000
        })
      })
    },
    handleDispatchRatePerTopic() {
      const data = {
        'dispatchThrottlingRateInByte': this.form.dispatchRatePerTopicBytes,
        'ratePeriodInSecond': this.form.dispatchRatePerTopicPeriod,
        'dispatchThrottlingRateInMsg': this.form.dispatchRatePerTopicMessage
      }
      setDispatchRate(this.tenantNamespace, data).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('namespace.notification.setDispatchRateSuccess'),
          type: 'success',
          duration: 3000
        })
      })
    },
    handleDispatchRatePerSub() {
      const data = {
        'dispatchThrottlingRateInByte': this.form.dispatchRatePerSubBytes,
        'ratePeriodInSecond': this.form.dispatchRatePerSubPeriod,
        'dispatchThrottlingRateInMsg': this.form.dispatchRatePerSubMessage
      }
      setSubscriptionDispatchRate(this.tenantNamespace, data).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('namespace.notification.setDispatchRatePerSubscriptionSuccess'),
          type: 'success',
          duration: 3000
        })
      })
    },
    handleSubscribeRate() {
      const data = {
        'subscribeThrottlingRatePerConsumer': this.form.subscribeRatePerConsumerSub,
        'ratePeriodInSecond': this.form.subscribeRatePerConsumerPeriod
      }
      setSubscribeRate(this.tenantNamespace, data).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('namespace.notification.setSubscribeRateSuccess'),
          type: 'success',
          duration: 3000
        })
      })
    },
    handleAntiAffinityGroup() {
      setAntiAffinityGroup(this.tenantNamespace, this.form.antiAffinityGroup).then(response => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('namespace.notification.setAntiAffinityGroupSuccess'),
          type: 'success',
          duration: 3000
        })
      })
    },
    handleDeleteNamespace() {
      this.dialogStatus = 'delete'
      this.dialogFormVisible = true
    },
    deleteNamespace() {
      deleteNamespace(this.tenantNamespace).then((response) => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('namespace.deleteNsSuccessNotification'),
          type: 'success',
          duration: 2000
        })
        this.$router.push({ path: '/management/tenants/tenantInfo/' + this.postForm.tenant + '?tab=namespaces' })
      })
    },
    handleCreateTopic() {
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.form.topic = ''
      this.form.isPersistent = 'Persistent'
      this.form.partitions = 0
    },
    generalCreateTopic(persistent) {
      putTopic(
        persistent,
        this.postForm.tenant,
        this.postForm.namespace,
        this.form.topic, parseInt(this.form.partitions)).then(() => {
        this.$notify({
          title: 'success',
          message: this.$i18n.t('topic.notification.createTopicSuccess'),
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
        this.topicsList = []
        this.allTopicList = []
        this.tempTopicsList = []
        this.getTopics()
      })
    },
    createTopic() {
      // if (this.form.topic === null || this.form.topic.length <= 0) {
      //   this.$notify({
      //     title: 'error',
      //     message: 'Topic name is incorrect',
      //     type: 'error',
      //     duration: 3000
      //   })
      //   return
      // }
      this.$refs['form'].validate((valid) => {
        if (valid) {
          if (this.form.isPersistent === 'Persistent') {
            this.generalCreateTopic('persistent')
          } else if (this.form.isPersistent === 'Non-persistent') {
            this.generalCreateTopic('non-persistent')
          }
        }
      })
    }
  }
}
</script>

<style>
.role-el-tag {
  background-color: #fff !important;
  border: none !important;
  font-size: 16px !important;
  color: black !important;
}
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
.md-input-style {
  width: 300px;
  margin-top: 8px !important;
}
</style>
