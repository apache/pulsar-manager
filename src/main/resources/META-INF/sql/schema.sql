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
  tenantId INTEGER PRIMARY KEY AUTOINCREMENT,
  tenant varchar(255) NOT NULL,
  adminRoles TEXT,
  allowedClusters TEXT
);

CREATE TABLE IF NOT EXISTS brokers (
  brokerId INTEGER PRIMARY KEY AUTOINCREMENT,
  broker varchar(1024) NOT NULL,
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
  UNIQUE (broker)
);
CREATE INDEX IF NOT EXISTS index_broker ON brokers(broker);

CREATE TABLE IF NOT EXISTS bundles (
  bundleId INTEGER PRIMARY KEY AUTOINCREMENT,
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
  UNIQUE (broker, tenant, namespace, bundle)
);
CREATE INDEX IF NOT EXISTS index_broker_tenant_namespace_bundle ON bundles(broker, tenant, namespace, bundle);
CREATE INDEX IF NOT EXISTS index_bundle ON bundles(bundle);
