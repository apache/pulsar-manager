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

CREATE DATABASE IF NOT EXISTS pulsar_manager;

USE pulsar_manager;

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
