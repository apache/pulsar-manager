<template>
  <div class="app-container">
    <div class="createPost-container">
      <el-form :inline="true" :model="postForm" class="form-container">
        <el-form-item class="postInfo-container-item" label="Tenant">
          <el-select v-model="postForm.tenant" placeholder="select tenant" @change="getNamespacesList(postForm.tenant)">
            <el-option v-for="(item,index) in tenantsListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
        <el-form-item class="postInfo-container-item" label="Namespace">
          <el-select v-model="postForm.namespace" placeholder="select namespace" @change="getNamespaceInfo(postForm.tenant, postForm.namespace)">
            <el-option v-for="(item,index) in namespacesListOptions" :key="item+index" :label="item" :value="item"/>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane label="OVERVIEW" name="overview">
        <el-table
          :data="namespaceStats"
          border
          style="width: 100%">
          <el-table-column prop="inMsg" label="In - msg/s"/>
          <el-table-column prop="outMsg" label="Out - msg/s"/>
          <el-table-column prop="inBytes" label="In - bytes/s"/>
          <el-table-column prop="outBytes" label="Out - bytes/s"/>
        </el-table>
        <h4>
          Bundles
          <el-tooltip :content="bundleInfoContent" class="item" effect="dark" placement="top">
            <i class="el-icon-info"/>
          </el-tooltip>
        </h4>
        <hr class="split-line">
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
          <el-table-column label="Bundle" align="center" min-width="100px">
            <template slot-scope="scope">
              <span>{{ scope.row.bundle }}</span>
            </template>
          </el-table-column>
          <el-table-column label="Operations" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button size="medium" style="margin-left: 10px;" type="danger" icon="el-icon-share" @click="handleSplitBundle(scope.row)">Split</el-button>
              <el-button size="medium" style="margin-left: 10px;" type="danger" icon="el-icon-download" @click="handleUnloadBundle(scope.row)">Unload</el-button>
              <el-button size="medium" style="margin-left: 10px;" type="danger" icon="el-icon-close" @click="handleClearBundleBacklog(scope.row)">Clear Backlog</el-button>
            </template>
          </el-table-column>
        </el-table>
        <!-- <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit"/> -->
      </el-tab-pane>
      <el-tab-pane label="TOPICS" name="topics">
        <el-input v-model="searchTopic" :placeholder="$t('namespace.searchTopics')" style="width: 200px;" @keyup.enter.native="handleFilterTopic"/>
        <el-button type="primary" icon="el-icon-search" @click="handleFilterTopic"/>
        <el-button type="primary" icon="el-icon-plus" @click="handleCreateTopic">{{ $t('namespace.newTopic') }}</el-button>
        <el-row :gutter="24">
          <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}" style="margin-top:15px">
            <el-table
              v-loading="topicsListLoading"
              :key="topicsTableKey"
              :data="topicsList"
              border
              fit
              highlight-current-row
              style="width: 100%;">
              <el-table-column label="Topic" min-width="50px" align="center">
                <template slot-scope="scope">
                  <router-link :to="scope.row.topicLink" class="link-type">
                    <span>{{ scope.row.topic }}</span>
                  </router-link>
                </template>
              </el-table-column>
              <el-table-column label="Partitions" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.partitions }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Persistent" min-width="30px" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.persistent }}</span>
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
        <h4>Clusters</h4>
        <hr class="split-line">
        <div class="component-item">
          <div class="section-title">
            <span>Replicated Clusters</span>
            <el-tooltip :content="replicatedClustersContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
          </div>
          <el-select
            v-model="replicationClustersValue"
            style="width:500px;margin-top:20px"
            multiple
            placeholder="Please Select Cluster"
            @change="handleReplicationsClusters()">
            <el-option v-for="item in replicationClustersOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </div>
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
        <h4>Subscription Authentication</h4>
        <hr class="split-line">
        <div class="section-title">
          <span>Subscription Authentication Mode</span>
          <el-tooltip :content="subscriptionAuthenticationModeContent" class="item" effect="dark" placement="top">
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
        <h4>Storage</h4>
        <hr class="split-line">
        <div class="section-title">
          <span>Replication Factor</span>
          <el-tooltip :content="replicationFactorContent" class="item" effect="dark" placement="top">
            <i class="el-icon-info"/>
          </el-tooltip>
        </div>
        <el-form :inline="true" :model="form" :rules="rules">
          <el-form-item prop="ensembelSize">
            <span>Ensemble Size</span>
            <md-input
              v-model="form.ensembleSize"
              class="md-input-style"
              name="ensembelSize"
              placeholder="Please input Ensemble"
              @keyup.enter.native="handlePersistence"/>
          </el-form-item>
          <el-form-item prop="writeQuorumSize">
            <span>Write Quorum Size</span>
            <md-input
              v-model="form.writeQuorumSize"
              class="md-input-style"
              name="writeQuorumSize"
              placeholder="Please input Write Quorum Size"
              @keyup.enter.native="handlePersistence"/>
          </el-form-item>
          <el-form-item prop="readQuorumSize">
            <span>Read Quorum Size</span>
            <md-input
              v-model="form.readQuorumSize"
              class="md-input-style"
              name="readQuorumSize"
              placeholder="Please input Read Quorum Size"
              @keyup.enter.native="handlePersistence"/>
          </el-form-item>
        </el-form>
        <el-form :inline="true" :model="form" :rules="rules">
          <el-form-item prop="markDeleteMaxRate">
            <span>Mark Delete Rate</span>
            <el-tooltip :content="markDeleteRateContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <md-input
              v-model="form.markDeleteMaxRate"
              class="md-input-style"
              name="markDeleteMaxRate"
              placeholder="Please input Delete Max Rate"
              @keyup.enter.native="handlePersistence"/>
          </el-form-item>
        </el-form>
        <el-form :inline="true" :model="form" :rules="rules">
          <el-form-item prop="encryptionRequire">
            <span>Encryption Require</span>
            <el-tooltip :content="encryptionRequireContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <br>
            <el-radio-group
              v-model="form.encryptionRequireRadio"
              size="medium"
              style="margin-top:8px;width:300px"
              @change="handleEncryption()">
              <el-radio label="Enabled"/>
              <el-radio label="Disabled"/>
            </el-radio-group>
          </el-form-item>
          <el-form-item prop="deduplication">
            <span>Deduplication</span>
            <el-tooltip :content="deduplicationContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <br>
            <el-radio-group
              v-model="form.deduplicationRadio"
              size="medium"
              style="margin-top:8px;width:300px"
              @change="handleDeduplication()">
              <el-radio label="Enabled"/>
              <el-radio label="Disabled"/>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <h4>Backlog</h4>
        <hr class="split-line">
        <el-form :inline="true" :model="form" :rules="rules">
          <el-form-item prop="backlogQuotasLimit">
            <span>Backlog Quotas Limit</span>
            <el-tooltip :content="backlogQuotasLimitContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <md-input
              v-model="form.backlogQuotasLimit"
              class="md-input-style"
              name="backlogQuotasLimit"
              placeholder="Please input Backlog Quotas Limit"
              @keyup.enter.native="handleBacklogQuota"/>
          </el-form-item>
          <el-form-item style="width:300px">
            <span>Backlog Retention Policy</span>
            <el-tooltip :content="backlogRententionPolicyContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <br>
            <el-select
              v-model="form.backlogRententionPolicy"
              placeholder="Please select backlog rentention policy"
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
        <h4>Schema</h4>
        <hr class="split-line">
        <el-form :inline="true" :model="form" :rules="rules">
          <el-form-item prop="autoUpdateStrategy">
            <span>AutoUpdate Strategy</span>
            <el-tooltip :content="autoUpdateStrategyContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <br>
            <el-select
              v-model="form.autoUpdateStrategy"
              placeholder="Please select backlog autoUpdate strategy"
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
            <span>Schema Validation Enforced</span>
            <el-tooltip :content="schemaValidationEnforcedContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <br>
            <el-radio-group
              v-model="form.schemaValidationEnforcedRadio"
              size="medium"
              style="margin-top:8px;width:300px"
              @change="handleSchemaValidationEnforced()">
              <el-radio label="Enabled"/>
              <el-radio label="Disabled"/>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <h4>Cleanup Policy</h4>
        <hr class="split-line">
        <el-form :inline="true" :model="form" :rules="rules">
          <el-form-item prop="messageTTL">
            <span>Message TTL(Unit Second)</span>
            <el-tooltip :content="messageTTLContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <md-input
              v-model="form.messageTTL"
              class="md-input-style"
              name="messageTTL"
              placeholder="Please input Backlog Quotas Limit"
              @keyup.enter.native="handleMessageTTL"/>
          </el-form-item>
        </el-form>
        <el-form :inline="true" :model="form" :rules="rules">
          <el-form-item prop="retentionSize">
            <span>Retention Size</span>
            <el-tooltip :content="retentionSizeContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <md-input
              v-model="form.retentionSize"
              class="md-input-style"
              name="retentionSize"
              placeholder="Please input retention size"
              @keyup.enter.native="handleRetention"/>
          </el-form-item>
          <el-form-item prop="retentionTime">
            <span>Retention Time(Unit Minutes)</span>
            <el-tooltip :content="retentionTimeContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <md-input
              v-model="form.retentionTime"
              class="md-input-style"
              name="retentionTime"
              placeholder="Please input Retention Time"
              @keyup.enter.native="handleRetention"/>
          </el-form-item>
        </el-form>
        <el-form :inline="true" :model="form" :rules="rules">
          <el-form-item prop="compactionThreshold">
            <span>Compaction Threshold</span>
            <el-tooltip :content="compactionThresholdContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <md-input
              v-model="form.compactionThreshold"
              class="md-input-style"
              name="compactionThreshold"
              placeholder="Please input retention size"
              @keyup.enter.native="handleCompactionThreshold"/>
          </el-form-item>
        </el-form>
        <el-form :inline="true" :model="form" :rules="rules">
          <el-form-item prop="offloadThreshold">
            <span>Offload Threshold</span>
            <el-tooltip :content="offloadThresholdContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <md-input
              v-model="form.offloadThreshold"
              class="md-input-style"
              name="offloadThreshold"
              placeholder="Please input Offload Threshold"
              @keyup.enter.native="handleOffloadThreshold"/>
          </el-form-item>
          <el-form-item prop="offloadDeletionLag">
            <span>Offload Deletion Lag</span>
            <el-tooltip :content="offloadDeletionLagContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <md-input
              v-model="form.offloadDeletionLag"
              class="md-input-style"
              name="offloadDeletionLag"
              placeholder="Please input Retention Time"
              @keyup.enter.native="handleOffloadDeletionLag"/>
          </el-form-item>
        </el-form>
        <h4>Throttling</h4>
        <hr class="split-line">
        <el-form :inline="true" :model="form" :rules="rules">
          <el-form-item prop="maxProducersPerTopic">
            <span>Max Producers Per Topic</span>
            <el-tooltip :content="maxProducersPerTopicContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <md-input
              v-model="form.maxProducersPerTopic"
              class="md-input-style"
              name="maxProducersPerTopic"
              placeholder="Please input max Producers"
              @keyup.enter.native="handleMaxProducersPerTopic"/>
          </el-form-item>
          <el-form-item prop="maxConsumersPerTopic">
            <span>Max Consumers Per Topic</span>
            <el-tooltip :content="maxConsumersPerTopicContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <md-input
              v-model="form.maxConsumersPerTopic"
              class="md-input-style"
              name="maxConsumersPerTopic"
              placeholder="Please input max Consumers"
              @keyup.enter.native="handleMaxConsumersPerTopic"/>
          </el-form-item>
        </el-form>
        <el-form :inline="true" :model="form" :rules="rules" @submit.native.prevent>
          <el-form-item prop="maxConsumerPerSub">
            <span>Max Consumers Per Subscription</span>
            <el-tooltip :content="maxConsumerPerSubContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <md-input
              v-model="form.maxConsumerPerSub"
              class="md-input-style"
              name="maxConsumerPerSub"
              placeholder="Please input max Consumers"
              @keyup.enter.native="handleMaxConsuemrsPerSubscription"/>
          </el-form-item>
        </el-form>
        <h4>Dispatch Rate</h4>
        <hr class="split-line">
        <span>Dispatch Rate Per Topic</span>
        <el-tooltip :content="dispatchRatePerTopicContent" class="item" effect="dark" placement="top">
          <i class="el-icon-info"/>
        </el-tooltip>
        <el-form :inline="true" :model="form" :rules="rules">
          <el-form-item prop="dispatchRatePerTopicBytes">
            <span>bytes/second</span>
            <md-input
              v-model="form.dispatchRatePerTopicBytes"
              class="md-input-style"
              name="dispatchRatePerTopicBytes"
              placeholder="Please input dispatch rate"
              @keyup.enter.native="handleDispatchRatePerTopic"/>
          </el-form-item>
          <el-form-item prop="dispatchRatePerTopicMessage">
            <span>message/second</span>
            <md-input
              v-model="form.dispatchRatePerTopicMessage"
              class="md-input-style"
              name="dispatchRatePerTopicMessage"
              placeholder="Please input dispatch rate"
              @keyup.enter.native="handleDispatchRatePerTopic"/>
          </el-form-item>
          <el-form-item prop="dispatchRatePerTopicPeriod">
            <span>period(Unit second)</span>
            <md-input
              v-model="form.dispatchRatePerTopicPeriod"
              class="md-input-style"
              name="dispatchRatePerTopicPeriod"
              placeholder="Please input dispatch rate"
              @keyup.enter.native="handleDispatchRatePerTopic"/>
          </el-form-item>
        </el-form>
        <span>Dispatch Rate Per Subscription</span>
        <el-tooltip :content="dispatchRatePerSubContent" class="item" effect="dark" placement="top">
          <i class="el-icon-info"/>
        </el-tooltip>
        <el-form :inline="true" :model="form" :rules="rules" @submit.native.prevent>
          <el-form-item prop="dispatchRatePerSubBytes">
            <span>bytes/second</span>
            <md-input
              v-model="form.dispatchRatePerSubBytes"
              class="md-input-style"
              name="dispatchRatePerSubBytes"
              placeholder="Please input dispatch rate"
              @keyup.enter.native="handleDispatchRatePerSub"/>
          </el-form-item>
          <el-form-item prop="dispatchRatePerSubMessage">
            <span>message/second</span>
            <md-input
              v-model="form.dispatchRatePerSubMessage"
              class="md-input-style"
              name="dispatchRatePerSubMessage"
              placeholder="Please input dispatch rate"
              @keyup.enter.native="handleDispatchRatePerSub"/>
          </el-form-item>
          <el-form-item prop="dispatchRatePerSubPeriod">
            <span>period(Unit Second)</span>
            <md-input
              v-model="form.dispatchRatePerSubPeriod"
              class="md-input-style"
              name="dispatchRatePerSubPeriod"
              placeholder="Please input dispatch rate"
              @keyup.enter.native="handleDispatchRatePerSub"/>
          </el-form-item>
        </el-form>
        <span>Subscribe Rate Per Consumer</span>
        <el-tooltip :content="subscribeRatePerConsumerContent" class="item" effect="dark" placement="top">
          <i class="el-icon-info"/>
        </el-tooltip>
        <el-form :inline="true" :model="form" :rules="rules" @submit.native.prevent>
          <el-form-item prop="subscribeRatePerConsumerSub">
            <span>message/second</span>
            <md-input
              v-model="form.subscribeRatePerConsumerSub"
              class="md-input-style"
              name="subscribeRatePerConsumerSub"
              placeholder="Please input subscribe rate"
              @keyup.enter.native="handleSubscribeRate"/>
          </el-form-item>
          <el-form-item prop="subscribeRatePerConsumerPeriod">
            <span>period(Unit Second)</span>
            <md-input
              v-model="form.subscribeRatePerConsumerPeriod"
              class="md-input-style"
              name="subscribeRatePerConsumerPeriod"
              placeholder="Please input subscribe rate"
              @keyup.enter.native="handleSubscribeRate"/>
          </el-form-item>
        </el-form>
        <h4>Anti Affinity</h4>
        <hr class="split-line">
        <el-form :inline="true" :model="form" :rules="rules" @submit.native.prevent>
          <el-form-item prop="antiAffinityGroup">
            <span>Anti Affinity Group</span>
            <el-tooltip :content="antiAffinityGroupContent" class="item" effect="dark" placement="top">
              <i class="el-icon-info"/>
            </el-tooltip>
            <md-input
              v-model="form.antiAffinityGroup"
              class="md-input-style"
              name="antiAffinityGroup"
              placeholder="Please input Anti Affinity Group"
              @keyup.enter.native="handleAntiAffinityGroup"/>
          </el-form-item>
        </el-form>
        <h4 style="color:#E57470">Danger Zone</h4>
        <hr class="danger-line">
        <el-button type="danger" class="button" @click="handleDeleteNamespace">Delete Namespace</el-button>
      </el-tab-pane>
    </el-tabs>
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="30%">
      <el-form ref="form" :rules="rules" :model="form" label-position="top">
        <div v-if="dialogStatus==='create'">
          <el-form-item label="Topic Domain">
            <el-radio-group
              v-model="form.isPersistent"
              size="medium">
              <el-radio label="Persistent"/>
              <el-radio label="Non-persistent"/>
            </el-radio-group>
          </el-form-item>
          <el-form-item :label="$t('table.topic')" prop="topic">
            <el-input v-model="form.topic"/>
          </el-form-item>
          <el-form-item :label="$t('table.partition')" prop="partition">
            <el-input v-model="form.partitions"/>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="createTopic()">{{ $t('table.confirm') }}</el-button>
            <el-button @click="dialogFormVisible = false">{{ $t('table.cancel') }}</el-button>
          </el-form-item>
        </div>
        <el-form-item v-if="dialogStatus==='delete'">
          <h4>Are you sure you want to delete this namespace?</h4>
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
import { fetchTenants } from '@/api/tenants'
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
  splitBundle,
  unloadBundle,
  unload,
  clearBacklog,
  deleteNamespace,
  clearBundleBacklog
} from '@/api/namespaces'
import { fetchBrokerStatsTopics } from '@/api/brokerStats'
import { putTopic, fetchTopicsByPulsarManager } from '@/api/topics'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import MdInput from '@/components/MDinput'
import { validateEmpty } from '@/utils/validate'

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
      namespaceStats: [{
        inMsg: 0,
        outMsg: 0,
        inBytes: 0,
        outBytes: 0
      }],
      replicatedClustersContent: 'This is Allowed Clusters',
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
        label: 'consume'
      }, {
        value: 'produce',
        label: 'produce'
      }, {
        value: 'functions',
        label: 'functions'
      }],
      authorizationContent: 'This is AuthorizationContent',
      subscriptionAuthenticationMode: '',
      subscriptionAuthenticationModeContent: 'This is subscriptionAuthenticationMode',
      subscriptionAuthenticationModeOptions: [{
        value: 'None',
        label: 'None'
      }, {
        value: 'Prefix',
        label: 'Prefix'
      }],
      replicationFactorContent: 'This is replicationFactorContent',
      form: {
        ensembelSize: '',
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
        encryptionRequireRadio: 'Disabled',
        deduplicationRadio: 'Disabled',
        schemaValidationEnforcedRadio: 'Disabled',
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
        // ensembelSize: [{ required: true, message: 'EnsembelSize is greater more than 0', trigger: 'blur' }]
      },
      markDeleteRateContent: 'This is markDeleteRateContent',
      backlogQuotasLimitContent: 'This is backlogQuotasLimitContent',
      backlogRententionPolicyContent: 'This is backlogRententionPolicyContent',
      backlogRententionPolicyRadio: 'consumer_backlog_eviction',
      backlogRententionPolicyOption: [{
        value: 'consumer_backlog_eviction',
        label: 'consumer_backlog_eviction'
      }, {
        value: 'producer_exception',
        label: 'producer_exception'
      }, {
        value: 'producer_request_hold',
        lable: 'producer_request_hold'
      }],
      encryptionRequireContent: 'This is encryptionRequireContent',
      encryptionRequireOption: [{
        value: 'Enabled',
        label: 'Enabled'
      }, {
        value: 'Disabled',
        label: 'Disabled'
      }],
      deduplicationContent: 'This is deduplicationContent',
      deduplication: '',
      deduplicationOption: [{
        value: 'Enabled',
        label: 'Enabled'
      }, {
        value: 'Disabled',
        label: 'Disabled'
      }],
      autoUpdateStrategyContent: 'This is schemaValidationEnforced',
      autoUpdateStrategyOption: [{
        value: 'Full',
        label: 'Full'
      }, {
        value: 'Forward',
        label: 'Forward'
      }, {
        value: 'Backward',
        label: 'Backward'
      }, {
        value: 'AlwaysCompatible',
        label: 'Always Compatible'
      }, {
        value: 'AutoUpdateDisabled',
        label: 'AutoUpdateDisabled'
      }, {
        value: 'BackwardTransitive',
        label: 'BackwardTransitive'
      }, {
        value: 'ForwardTransitive',
        label: 'ForwardTransitive'
      }, {
        value: 'FullTransitive',
        label: 'FullTransitive'
      }],
      schemaValidationEnforcedContent: 'This is schemaValidationEnforcedContent',
      schemaValidationEnforced: '',
      messageTTLContent: 'This is messageTTLContent',
      retentionSizeContent: 'This is retentionSizeContent',
      retentionTimeContent: 'This is retentionTimeContent',
      compactionThresholdContent: 'This is compactionThresholdContent',
      offloadThresholdContent: 'This is offloadThresholdContent',
      offloadDeletionLagContent: 'This is offloadDeletionLagContent',
      maxProducersPerTopicContent: 'This is maxProducersPerTopicContent',
      maxConsumersPerTopicContent: 'This is maxConsumersPerTopicContent',
      maxConsumerPerSubContent: 'This is maxConsumerPerSubContent',
      dispatchRatePerTopicContent: 'This is dispatchRatePerTopicContent',
      dispatchRatePerSubContent: 'This is dispatchRatePerSubContent',
      subscribeRatePerConsumerContent: 'This is subscribeRatePerConsumerContent',
      antiAffinityGroupContent: 'This is antiAffinityGroupContent',
      tableKey: 0,
      topicsListLoading: true,
      topicsTableKey: 0,
      brokerStats: null,
      topics: {},
      localList: [],
      tempTopicsList: [],
      topicsList: [],
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
        create: 'Create Topic'
      },
      currentTabName: '',
      bundleInfoContent: 'This is bundleInfoContent'
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
    this.getPolicies(this.tenantNamespace)
    this.initPermissions(this.tenantNamespace)
    this.initPersistence(this.tenantNamespace)
    this.initRetention(this.tenantNamespace)
    this.getStats()
  },
  methods: {
    getStats() {
      this.getTopics()
    },
    getTopics() {
      fetchTopicsByPulsarManager(this.postForm.tenant, this.postForm.namespace).then(response => {
        if (!response.data) return
        for (var i in response.data.topics) {
          this.topics[response.data.topics[i]['topic']] = i
          var topicLink = ''
          if (response.data.topics[i]['partitions'] === '0') {
            topicLink = '/management/topics/' + response.data.topics[i]['persistent'] + '/' + this.tenantNamespace + '/' + response.data.topics[i]['topic'] + '/topic'
          } else {
            topicLink = '/management/topics/' + response.data.topics[i]['persistent'] + '/' + this.tenantNamespace + '/' + response.data.topics[i]['topic'] + '/partitionedTopic'
          }
          var topicInfo = {
            'topic': response.data.topics[i]['topic'],
            'partitions': response.data.topics[i]['partitions'],
            'persistent': response.data.topics[i]['persistent'],
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
          this.topicsList.push(topicInfo)
          this.tempTopicsList.push(topicInfo)
        }
        fetchBrokerStatsTopics('').then(res => {
          if (!res.data) return
          this.brokerStats = res.data
          if (this.brokerStats.hasOwnProperty(this.tenantNamespace)) {
            for (var bundle in this.brokerStats[this.tenantNamespace]) {
              for (var p in this.brokerStats[this.tenantNamespace][bundle]) {
                for (var topic in this.brokerStats[this.tenantNamespace][bundle][p]) {
                  this.namespaceStats[0].inMsg += this.brokerStats[this.tenantNamespace][bundle][p][topic].msgRateIn
                  this.namespaceStats[0].outMsg += this.brokerStats[this.tenantNamespace][bundle][p][topic].msgRateOut
                  this.namespaceStats[0].inBytes += this.brokerStats[this.tenantNamespace][bundle][p][topic].msgThroughputIn
                  this.namespaceStats[0].outBytes += this.brokerStats[this.tenantNamespace][bundle][p][topic].msgThroughputOut
                  var topicName = topic.split('://')[1].split('/')[2]
                  var isPartition = false
                  if (topicName.indexOf('-partition-') > 0) {
                    topicName = topicName.split('-partition-')[0]
                    isPartition = true
                  }
                  if (this.topics.hasOwnProperty(topicName)) {
                    if (isPartition) {
                      this.topicsList[this.topics[topicName]]['producers'] += this.brokerStats[this.tenantNamespace][bundle][p][topic].producerCount
                      for (var psub in this.brokerStats[this.tenantNamespace][bundle][p][topic].subscriptions) {
                        this.topicsList[this.topics[topicName]]['subscriptions'] += Object.keys(this.brokerStats[this.tenantNamespace][bundle][p][topic].subscriptions[psub].consumers).length
                      }
                      this.topicsList[this.topics[topicName]]['inMsg'] += this.brokerStats[this.tenantNamespace][bundle][p][topic].msgRateIn
                      this.topicsList[this.topics[topicName]]['outMsg'] += this.brokerStats[this.tenantNamespace][bundle][p][topic].msgRateOut
                      this.topicsList[this.topics[topicName]]['inBytes'] += this.brokerStats[this.tenantNamespace][bundle][p][topic].msgThroughputIn
                      this.topicsList[this.topics[topicName]]['outBytes'] += this.brokerStats[this.tenantNamespace][bundle][p][topic].msgThroughputOut
                      if (p === 'non-persistent') {
                        this.topicsList[this.topics[topicName]]['storageSize'] = 0
                      } else {
                        this.topicsList[this.topics[topicName]]['storageSize'] += this.brokerStats[this.tenantNamespace][bundle][p][topic].storageSize
                      }
                    } else {
                      this.topicsList[this.topics[topicName]]['producers'] = this.brokerStats[this.tenantNamespace][bundle][p][topic].producerCount
                      for (var sub in this.brokerStats[this.tenantNamespace][bundle][p][topic].subscriptions) {
                        this.topicsList[this.topics[topicName]]['subscriptions'] = Object.keys(this.brokerStats[this.tenantNamespace][bundle][p][topic].subscriptions[sub].consumers).length
                      }
                      this.topicsList[this.topics[topicName]]['inMsg'] = this.brokerStats[this.tenantNamespace][bundle][p][topic].msgRateIn
                      this.topicsList[this.topics[topicName]]['outMsg'] = this.brokerStats[this.tenantNamespace][bundle][p][topic].msgRateOut
                      this.topicsList[this.topics[topicName]]['inBytes'] = this.brokerStats[this.tenantNamespace][bundle][p][topic].msgThroughputIn
                      this.topicsList[this.topics[topicName]]['outBytes'] = this.brokerStats[this.tenantNamespace][bundle][p][topic].msgThroughputOut
                      if (p === 'non-persistent') {
                        this.topicsList[this.topics[topicName]]['storageSize'] = 0
                      } else {
                        this.topicsList[this.topics[topicName]]['storageSize'] += this.brokerStats[this.tenantNamespace][bundle][p][topic].storageSize
                      }
                    }
                  }
                }
              }
            }
          }
        })
        this.topicsListLoading = false
      })
    },
    handleFilterTopic() {
      if (!validateEmpty(this.searchTopic)) {
        this.searchList = []
        for (var i = 0; i < this.tempTopicsList.length; i++) {
          if (this.tempTopicsList[i]['topic'].indexOf(this.searchTopic) !== -1) {
            this.searchList.push(this.tempTopicsList[i])
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
          this.roleMap[key] = response.data.key
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
    initPoliciesOptions(policies) {
      for (var i = 0; i < policies.replication_clusters.length; i++) {
        this.replicationClustersOptions.push({
          value: policies.replication_clusters[i],
          label: policies.replication_clusters[i]
        })
      }
      this.replicationClustersValue = policies.replication_clusters
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
          message: 'Add success',
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
            message: 'This role is exist',
            type: 'error'
          })
          this.inputVisible = false
          this.inputValue = ''
          return
        }
        grantPermissions(this.currentNamespace, inputValue, this.roleMap[inputValue]).then(response => {
          this.$notify({
            title: 'success',
            message: 'Add success',
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
    handleChangeOptions() {
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
          message: 'Set persistence success for namespace',
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
          message: 'Add Backlog Quota success for namespace',
          type: 'success',
          duration: 3000
        })
      })
    },
    handleSplitBundle(row) {
      splitBundle(this.tenantNamespace, row.bundle, false).then(response => {
        this.$notify({
          title: 'success',
          message: 'splitBundle success for namespace',
          type: 'success',
          duration: 3000
        })
        this.localList = []
        this.getPolicies(this.tenantNamespace)
      })
    },
    handleUnloadAll() {
      unload(this.tenantNamespace).then(response => {
        this.$notify({
          title: 'success',
          message: 'Unload success for namespace',
          type: 'success',
          duration: 3000
        })
        this.localList = []
        this.getPolicies(this.tenantNamespace)
      })
    },
    handleUnloadBundle(row) {
      unloadBundle(this.tenantNamespace, row.bundle).then(response => {
        this.$notify({
          title: 'success',
          message: 'Unload success for namespace',
          type: 'success',
          duration: 3000
        })
      })
      this.localList = []
      this.getPolicies(this.tenantNamespace)
    },
    hanldeClearAllBacklog() {
      clearBacklog(this.tenantNamespace).then(response => {
        this.$notify({
          title: 'success',
          message: 'clearBacklog success for namespace',
          type: 'success',
          duration: 3000
        })
      })
    },
    handleClearBundleBacklog(row) {
      clearBundleBacklog(this.tenantNamespace, row.bundle).then(response => {
        this.$notify({
          title: 'success',
          message: 'clearBacklog success for bundle',
          type: 'success',
          duration: 3000
        })
      })
    },
    handleReplicationsClusters() {
      setClusters(this.tenantNamespace, this.replicationClustersValue).then(response => {
        this.$notify({
          title: 'success',
          message: 'Add clusters success for namespace',
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
          message: 'success',
          type: 'success',
          duration: 3000
        })
      })
    },
    handleEncryption() {
      var encryptionRequire = false
      if (this.form.encryptionRequireRadio === 'Enabled') {
        encryptionRequire = true
      }
      setEncryptionRequired(this.tenantNamespace, encryptionRequire).then(response => {
        this.$notify({
          title: 'success',
          message: 'success',
          type: 'success',
          duration: 3000
        })
      })
    },
    handleDeduplication() {
      var deduplication = false
      if (this.form.deduplicationRadio === 'Enabled') {
        deduplication = true
      }
      setDeduplication(this.tenantNamespace, deduplication).then(response => {
        this.$notify({
          title: 'success',
          message: 'Set deduplication success for namespace',
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
          message: 'Set SchemaAutoupdateStrategy success',
          type: 'success',
          duration: 3000
        })
      })
    },
    handleSchemaValidationEnforced() {
      var schemaValidation = false
      if (this.form.schemaValidationEnforcedRadio === 'Enabled') {
        schemaValidation = true
      }
      setSchemaValidationEnforced(this.tenantNamespace, schemaValidation).then(response => {
        this.$notify({
          title: 'success',
          message: 'Set SchemaValidationEnforced success',
          type: 'success',
          duration: 3000
        })
      })
    },
    handleMessageTTL() {
      setMessageTtl(this.tenantNamespace, parseInt(this.form.messageTTL)).then(response => {
        this.$notify({
          title: 'success',
          message: 'Set messageTTL success for namespace',
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
          message: 'Set Retention success for namespace',
          type: 'success',
          duration: 3000
        })
      })
    },
    handleCompactionThreshold() {
      setCompactionThreshold(this.tenantNamespace, this.form.compactionThreshold).then(response => {
        this.$notify({
          title: 'success',
          message: 'Set threshold success',
          type: 'success',
          duration: 3000
        })
      })
    },
    handleOffloadThreshold() {
      setOffloadThreshold(this.tenantNamespace, this.form.offloadThreshold).then(response => {
        this.$notify({
          title: 'success',
          message: 'Set threshold success',
          type: 'success',
          duration: 3000
        })
      })
    },
    handleOffloadDeletionLag() {
      setOffloadDeletionLag(this.tenantNamespace, this.form.offloadDeletionLag).then(response => {
        this.$notify({
          title: 'success',
          message: 'Set DeletionLag success',
          type: 'success',
          duration: 3000
        })
      })
    },
    handleMaxProducersPerTopic() {
      setMaxProducersPerTopic(this.tenantNamespace, this.form.maxProducersPerTopic).then(response => {
        this.$notify({
          title: 'success',
          message: 'Set max producers per topic success',
          type: 'success',
          duration: 3000
        })
      })
    },
    handleMaxConsumersPerTopic() {
      setMaxConsumersPerTopic(this.tenantNamespace, this.form.maxConsumersPerTopic).then(response => {
        this.$notify({
          title: 'success',
          message: 'Set max consumers per topic success',
          type: 'success',
          duration: 3000
        })
      })
    },
    handleMaxConsuemrsPerSubscription() {
      setMaxConsumersPerSubscription(this.tenantNamespace, this.form.maxConsumerPerSub).then(response => {
        this.$notify({
          title: 'success',
          message: 'Set max subscription per topic success',
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
          message: 'set DispatchRate success for namespace',
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
          message: 'set subscription dispatchRate success for namespace',
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
          message: 'set subscription dispatchRate success for namespace',
          type: 'success',
          duration: 3000
        })
      })
    },
    handleAntiAffinityGroup() {
      setAntiAffinityGroup(this.tenantNamespace, this.form.antiAffinityGroup).then(response => {
        this.$notify({
          title: 'success',
          message: 'Set AntiAffinityGroup success for namespace',
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
          message: 'delete success',
          type: 'success',
          duration: 2000
        })
        this.$router.push({ path: '/management/namespaces/' + this.postForm.tenant })
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
          message: 'create topic success',
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
        this.topicsList = []
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
