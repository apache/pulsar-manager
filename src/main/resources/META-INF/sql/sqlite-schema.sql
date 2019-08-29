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

CREATE TABLE IF NOT EXISTS environments (
  name varchar(256) NOT NULL,
  broker varchar(1024) NOT NULL,
  CONSTRAINT PK_name PRIMARY KEY (name),
  UNIQUE (broker)
);

CREATE TABLE IF NOT EXISTS topicsStats (
  topicStatsId INTEGER PRIMARY KEY AUTOINCREMENT,
  environment varchar(255) NOT NULL,
  cluster varchar(255) NOT NULL,
  broker varchar(255) NOT NULL,
  tenant varchar(255) NOT NULL,
  namespace varchar(255) NOT NULL,
  bundle varchar(255) NOT NULL,
  persistent varchar(36) NOT NULL,
  topic varchar(255) NOT NULL,
  producerCount INTEGER,
  subscriptionCount INTEGER,
  msgRateIn double,
  msgThroughputIn double,
  msgRateOut double,
  msgThroughputOut double,
  averageMsgSize double,
  storageSize double,
  timestamp integer
);

CREATE TABLE IF NOT EXISTS publishersStats (
  publisherStatsId  INTEGER PRIMARY KEY AUTOINCREMENT,
  producerId INTEGER,
  topicStatsId INTEGER NOT NULL,
  producerName varchar(255) NOT NULL,
  msgRateIn double,
  msgThroughputIn double,
  averageMsgSize double,
  address varchar(255),
  connectedSince varchar(128),
  clientVersion varchar(36),
  metadata text,
  timestamp integer,
  CONSTRAINT FK_topic_stats_id FOREIGN KEY (topicStatsId) References topicsStats(topicStatsId)
);

CREATE TABLE IF NOT EXISTS replicationsStats (
  replicationStatsId INTEGER PRIMARY KEY AUTOINCREMENT,
  topicStatsId INTEGER NOT NULL,
  cluster varchar(255) NOT NULL,
  connected false,
  msgRateIn double,
  msgRateOut double,
  msgRateExpired double,
  msgThroughputIn double,
  msgThroughputOut double,
  msgRateRedeliver double,
  replicationBacklog INTEGER,
  replicationDelayInSeconds integer,
  inboundConnection varchar(255),
  inboundConnectedSince varchar(255),
  outboundConnection varchar(255),
  outboundConnectedSince varchar(255),
  timestamp integer,
  CONSTRAINT FK_topic_stats_id FOREIGN KEY (topicStatsId) References topicsStats(topicStatsId)
);

CREATE TABLE IF NOT EXISTS subscriptionsStats (
  subscriptionStatsId INTEGER PRIMARY KEY AUTOINCREMENT,
  topicStatsId INTEGER NOT NULL,
  subscription varchar(255) NULL,
  msgBacklog integer,
  msgRateExpired double,
  msgRateOut double,
  msgThroughputOut double,
  msgRateRedeliver double,
  numberOfEntriesSinceFirstNotAckedMessage integer,
  totalNonContiguousDeletedMessagesRange integer,
  subscriptionType varchar(16),
  blockedSubscriptionOnUnackedMsgs false,
  timestamp integer,
  UNIQUE (topicStatsId, subscription),
  CONSTRAINT FK_topic_stats_id FOREIGN KEY (topicStatsId) References topicsStats(topicStatsId)
);

CREATE TABLE IF NOT EXISTS consumersStats (
  consumerStatsId INTEGER PRIMARY KEY AUTOINCREMENT,
  consumer varchar(255) NOT NULL,
  topicStatsId INTEGER NOT NUll,
  replicationStatsId integer,
  subscriptionStatsId integer,
  address varchar(255),
  availablePermits integer,
  connectedSince varchar(255),
  msgRateOut double,
  msgThroughputOut double,
  msgRateRedeliver double,
  clientVersion varchar(36),
  timestamp integer,
  metadata text
);
