USE mysql;
FLUSH PRIVILEGES;
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY "pulsar" WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
UPDATE user SET password=PASSWORD("pulsar") WHERE user='root';

CREATE DATABASE IF NOT EXISTS pulsar_manager;

USE pulsar_manager;

CREATE TABLE IF NOT EXISTS tenants (
  tenant varchar(255) NOT NULL PRIMARY KEY,
  tenantId BIGINT NOT NULL,
  adminRoles TEXT,
  allowedClusters TEXT,
  UNIQUE (tenantId)
) ENGINE=InnoDB CHARACTER SET utf8;


CREATE TABLE IF NOT EXISTS namespaces (
  namespaceId BIGINT NOT NULL,
  tenant varchar(255) NOT NULL,
  namespace varchar(255) NOT NULL,
  authPolicies TEXT,
  backlogQuota TEXT,
  replicationClusters TEXT,
  numBundles BIGINT,
  boundaries TEXT,
  topicDispatchRate TEXT,
  subscriptionDispatchRate TEXT,
  replicatorDispatchRate TEXT,
  clusterSubscribeRate TEXT,
  bookkeeperEnsemble BIGINT,
  bookkeeperWriteQuorum BIGINT,
  bookkeeperAckQuorum BIGINT,
  managedLedgerMaxMarkDeleteRate double,
  deduplicationEnabled BOOLEAN,
  latencyStatsSampleRate TEXT,
  messageTtlInSeconds BIGINT,
  retentionTimeInMinutes BIGINT,
  retentionSizeInMB BIGINT,
  deleted BOOLEAN,
  antiAffinityGroup VARCHAR(255),
  encryptionRequired BOOLEAN,
  subscriptionAuthMode VARCHAR(12),
  maxProducersPerTopic BIGINT,
  maxConsumersPerTopic BIGINT,
  maxConsumersPerSubscription BIGINT,
  compactionThreshold BIGINT,
  offloadThreshold BIGINT,
  offloadDeletionLagMs BIGINT,
  schemaValidationEnforced BOOLEAN,
  schemaAutoApdateCompatibilityStrategy VARCHAR(36),
  CONSTRAINT FK_tenant FOREIGN KEY (tenant) References tenants(tenant),
  CONSTRAINT PK_namespace PRIMARY KEY (tenant, namespace),
  UNIQUE (namespaceId)
) ENGINE=InnoDB CHARACTER SET utf8;
-- ALTER table namespaces ADD INDEX namespaces_namespace_index(namespace);

CREATE TABLE IF NOT EXISTS clusters (
  cluster varchar(255) NOT NULL PRIMARY KEY,
  clusterId BIGINT NOT NULL,
  serviceUrl varchar(1024),
  serviceUrlTls varchar(1024),
  brokerServiceUrl varchar(1024),
  brokerServiceUrlTls varchar(1024),
  peerClusterNames varchar(1024),
  UNIQUE (clusterId)
) ENGINE=InnoDB CHARACTER SET utf8;
-- ALTER table clusters ADD INDEX clusters_cluster_index(cluster);

CREATE TABLE IF NOT EXISTS brokers (
  broker varchar(1024) NOT NULL PRIMARY KEY,
  brokerId BIGINT NOT NULl,
  webServiceUrl varchar(1024),
  webServiceUrlTls varchar(1024),
  pulsarServiceUrl varchar(1024),
  pulsarServiceUrlTls varchar(1024),
  persistentTopicsEnabled BOOLEAN,
  nonPersistentTopicsEnabled BOOLEAN,
  brokerVersionString varchar(36),
  loadReportType varchar(36),
  maxResourceUsage double,
  UNIQUE (brokerId)
) ENGINE=InnoDB CHARACTER SET utf8;

-- CREATE TABLE IF NOT EXISTS bundles (
--   broker varchar(1024) NOT NULL,
--   tenant varchar(255) NOT NULL,
--   namespace varchar(255) NOT NULL,
--   bundle varchar(1024) NOT NULL,
--   CONSTRAINT FK_broker FOREIGN KEY (broker) References brokers(broker),
--   CONSTRAINT FK_tenant FOREIGN KEY (tenant) References tenants(tenant),
--   CONSTRAINT FK_namespace FOREIGN KEY (namespace) References namespaces(namespace),
--   CONSTRAINT PK_bundle PRIMARY KEY (broker, tenant, namespace, bundle)
-- ) ENGINE=InnoDB CHARACTER SET utf8;

CREATE TABLE IF NOT EXISTS environments (
  name varchar(256) NOT NULL,
  broker varchar(1024) NOT NULL,
  CONSTRAINT PK_name PRIMARY KEY (name),
  UNIQUE (broker)
) ENGINE=InnoDB CHARACTER SET utf8;

CREATE TABLE IF NOT EXISTS topicsStats (
  topicStatsId BIGINT PRIMARY KEY AUTO_INCREMENT,
  environment varchar(255) NOT NULL,
  cluster varchar(255) NOT NULL,
  broker varchar(255) NOT NULL,
  tenant varchar(255) NOT NULL,
  namespace varchar(255) NOT NULL,
  bundle varchar(255) NOT NULL,
  persistent varchar(36) NOT NULL,
  topic varchar(255) NOT NULL,
  producerCount BIGINT,
  subscriptionCount BIGINT,
  msgRateIn double,
  msgThroughputIn double,
  msgRateOut double,
  msgThroughputOut double,
  averageMsgSize double,
  storageSize double,
  timestamp BIGINT
) ENGINE=InnoDB CHARACTER SET utf8;

CREATE TABLE IF NOT EXISTS publishersStats (
  publisherStatsId  BIGINT PRIMARY KEY AUTO_INCREMENT,
  producerId BIGINT,
  topicStatsId BIGINT NOT NULL,
  producerName varchar(255) NOT NULL,
  msgRateIn double,
  msgThroughputIn double,
  averageMsgSize double,
  address varchar(255),
  connectedSince varchar(128),
  clientVersion varchar(36),
  metadata text,
  timestamp BIGINT,
  CONSTRAINT FK_publishers_stats_topic_stats_id FOREIGN KEY (topicStatsId) References topicsStats(topicStatsId)
) ENGINE=InnoDB CHARACTER SET utf8;

CREATE TABLE IF NOT EXISTS replicationsStats (
  replicationStatsId BIGINT PRIMARY KEY AUTO_INCREMENT,
  topicStatsId BIGINT NOT NULL,
  cluster varchar(255) NOT NULL,
  connected BOOLEAN,
  msgRateIn double,
  msgRateOut double,
  msgRateExpired double,
  msgThroughputIn double,
  msgThroughputOut double,
  msgRateRedeliver double,
  replicationBacklog BIGINT,
  replicationDelayInSeconds BIGINT,
  inboundConnection varchar(255),
  inboundConnectedSince varchar(255),
  outboundConnection varchar(255),
  outboundConnectedSince varchar(255),
  timestamp BIGINT,
  CONSTRAINT FK_replications_stats_topic_stats_id FOREIGN KEY (topicStatsId) References topicsStats(topicStatsId)
) ENGINE=InnoDB CHARACTER SET utf8;

CREATE TABLE IF NOT EXISTS subscriptionsStats (
  subscriptionStatsId BIGINT PRIMARY KEY AUTO_INCREMENT,
  topicStatsId BIGINT NOT NULL,
  subscription varchar(255) NULL,
  msgBacklog BIGINT,
  msgRateExpired double,
  msgRateOut double,
  msgThroughputOut double,
  msgRateRedeliver double,
  numberOfEntriesSinceFirstNotAckedMessage BIGINT,
  totalNonContiguousDeletedMessagesRange BIGINT,
  subscriptionType varchar(16),
  blockedSubscriptionOnUnackedMsgs BOOLEAN,
  timestamp BIGINT,
  UNIQUE (topicStatsId, subscription),
  CONSTRAINT FK_subscriptions_stats_topic_stats_id FOREIGN KEY (topicStatsId) References topicsStats(topicStatsId)
) ENGINE=InnoDB CHARACTER SET utf8;

CREATE TABLE IF NOT EXISTS consumersStats (
  consumerStatsId BIGINT PRIMARY KEY AUTO_INCREMENT,
  consumer varchar(255) NOT NULL,
  topicStatsId BIGINT NOT NUll,
  replicationStatsId BIGINT,
  subscriptionStatsId BIGINT,
  address varchar(255),
  availablePermits BIGINT,
  connectedSince varchar(255),
  msgRateOut double,
  msgThroughputOut double,
  msgRateRedeliver double,
  clientVersion varchar(36),
  timestamp BIGINT,
  metadata text
) ENGINE=InnoDB CHARACTER SET utf8;