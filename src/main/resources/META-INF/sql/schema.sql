--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

CREATE TABLE IF NOT EXISTS tenants (
  tenant varchar(255) NOT NULL PRIMARY KEY,
  tenantId INTEGER NOT NULL,
  adminRoles TEXT,
  allowedClusters TEXT
);

CREATE TABLE IF NOT EXISTS namespaces (
  namespaceId INTEGER NOT NULL,
  tenant varchar(255) NOT NULL,
  namespace varchar(255) NOT NULL,
  authPolicies TEXT,
  backlogQuota TEXT,
  replicationClusters TEXT,
  numBundles INTEGER,
  boundaries TEXT,
  topicDispatchRate TEXT,
  subscriptionDispatchRate TEXT,
  replicatorDispatchRate TEXT,
  clusterSubscribeRate TEXT,
  bookkeeperEnsemble INTEGER,
  bookkeeperWriteQuorum INTEGER,
  bookkeeperAckQuorum INTEGER,
  managedLedgerMaxMarkDeleteRate double,
  deduplicationEnabled false,
  latencyStatsSampleRate TEXT,
  messageTtlInSeconds INTEGER,
  retentionTimeInMinutes INTEGER,
--   mysql use bigint
  retentionSizeInMB INTEGER,
  deleted false,
  antiAffinityGroup VARCHAR(255),
  encryptionRequired false,
  subscriptionAuthMode VARCHAR(12),
  maxProducersPerTopic INTEGER,
  maxConsumersPerTopic INTEGER,
  maxConsumersPerSubscription INTEGER,
  compactionThreshold INTEGER,
  offloadThreshold INTEGER,
  offloadDeletionLagMs INTEGER,
  schemaValidationEnforced false,
  schemaAutoApdateCompatibilityStrategy VARCHAR(36),
  CONSTRAINT FK_tenant FOREIGN KEY (tenant) References tenants(tenant),
  CONSTRAINT PK_namespace PRIMARY KEY (tenant, namespace)
  UNIQUE (namespaceId)
);
CREATE INDEX IF NOT EXISTS namespaces_namespace_index ON namespaces(namespace);

CREATE TABLE IF NOT EXISTS brokers (
  broker varchar(1024) NOT NULL PRIMARY KEY,
  brokerId INTEGER,
  webServiceUrl varchar(1024),
  webServiceUrlTls varchar(1024),
  pulsarServiceUrl varchar(1024),
  pulsarServiceUrlTls varchar(1024),
  persistentTopicsEnabled true,
  nonPersistentTopicsEnabled true,
  cpuUsage double,
  cpuLimit double,
  memoryUsage double,
  memoryLimit double,
  directMemoryUsage double,
  directMemoryLimit double,
  bandwidthInUsage double,
  bandwidthInLimit double,
  bandwidthOutUsage double,
  bandwidthOutLimit double,
  msgThroughputIn double,
  msgThroughputOut double,
  msgRateIn double,
  msgRateOut double,
--   mysql use bigint
  lastUpdate integer,
  lastStats TEXT,
  bundleStats TEXT,
  numTopics integer,
  numBundles integer,
  numConsumers integer,
  numProducers integer,
  bundles TEXT,
  lastBundleGains TEXT,
  lastBundleLosses TEXT,
  brokerVersionString varchar(36),
  loadReportType varchar(36),
  maxResourceUsage double,
  UNIQUE (brokerId)
);

CREATE TABLE IF NOT EXISTS bundles (
  broker varchar(1024) NOT NULL,
  tenant varchar(255) NOT NULL,
  namespace varchar(255) NOT NULL,
  bundle varchar(1024) NOT NULL,
  msgRateIn double,
  msgThroughputIn double,
  msgRateOut double,
  msgThroughputOut double,
  consumerCount integer,
  producerCount integer,
  --   mysql use bigint
  topics integer,
  --   mysql use bigint
  cacheSize integer,
  throughputDifferenceThreshold double,
  msgRateDifferenceThreshold double,
  topicConnectionDifferenceThreshold double,
  cacheSizeDifferenceThreshold double,
  CONSTRAINT FK_broker FOREIGN KEY (broker) References brokers(broker),
  CONSTRAINT FK_tenant FOREIGN KEY (tenant) References tenants(tenant),
  CONSTRAINT FK_namespace FOREIGN KEY (namespace) References namespaces(namespace),
  CONSTRAINT PK_bundle PRIMARY KEY (broker, tenant, namespace, bundle)
);

CREATE INDEX IF NOT EXISTS bundles_bundle ON bundles(bundle);