CREATE TABLE clusters (
  id INTEGER PRIMARY KEY autoincrement,
  cluser_name varchar(255) NOT NULL
);
CREATE TABLE tenants (
  id INTEGER PRIMARY KEY autoincrement,
  tenant_name varchar(255) NOT NULL
);

CREATE TABLE namespaces (
  namespaceId INTEGER PRIMARY KEY autoincrement,
  tenant varchar(255) NOT NULL,
  namespace varchar(255) NOT NULL,
  authPolicies TEXT,
  replicationClusters TEXT,
  numBundles int,
  boundaries TEXT,
  topicDispatchRate TEXT,
  subscriptionDispatchRate TEXT,
  replicatorDispatchRate TEXT,
  clusterSubscribeRate TEXT,
  bookkeeperEnsemble int,
  bookkeeperWriteQuorum int,
  bookkeeperAckQuorum int,
  managedLedgerMaxMarkDeleteRate double,
  deduplicationEnabled false,
  latencyStatsSampleRate TEXT,
  messageTtlInSeconds int,
  retentionTimeInMinutes int,
--   mysql use bigint
  retentionSizeInMB int,
  deleted false,
  antiAffinityGroup VARCHAR(255),
  encryptionRequired false,
  subscriptionAuthMode VARCHAR(12),
  maxProducersPerTopic int,
  maxConsumersPerTopic int,
  maxConsumersPerSubscription int,
  compactionThreshold INTEGER,
  offloadThreshold int,
  offloadDeletionLagMs INTEGER,
  schemaValidationEnforced false,
  schemaAutoApdateCompatibilityStrategy VARCHAR(36)
)
