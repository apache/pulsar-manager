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

ALTER USER pulsar WITH PASSWORD 'pulsar';
CREATE DATABASE pulsar_manager OWNER pulsar;
GRANT ALL PRIVILEGES ON DATABASE pulsar_manager to pulsar;

\c pulsar_manager;

CREATE TABLE IF NOT EXISTS environments (
  name varchar(256) NOT NULL,
  broker varchar(1024) NOT NULL,
  bookie varchar(1024) NOT NULL,
  CONSTRAINT PK_name PRIMARY KEY (name),
  UNIQUE (broker)
);

CREATE TABLE IF NOT EXISTS topics_stats (
  topic_stats_id BIGSERIAL PRIMARY KEY,
  environment varchar(255) NOT NULL,
  cluster varchar(255) NOT NULL,
  broker varchar(255) NOT NULL,
  tenant varchar(255) NOT NULL,
  namespace varchar(255) NOT NULL,
  bundle varchar(255) NOT NULL,
  persistent varchar(36) NOT NULL,
  topic varchar(255) NOT NULL,
  producer_count BIGINT,
  subscription_count BIGINT,
  msg_rate_in double precision	,
  msg_throughput_in double precision	,
  msg_rate_out double precision	,
  msg_throughput_out double precision	,
  average_msg_size double precision	,
  storage_size double precision	,
  time_stamp BIGINT
);

CREATE TABLE IF NOT EXISTS publishers_stats (
  publisher_stats_id BIGSERIAL PRIMARY KEY,
  producer_id BIGINT,
  topic_stats_id BIGINT NOT NULL,
  producer_name varchar(255) NOT NULL,
  msg_rate_in double precision	,
  msg_throughput_in double precision	,
  average_msg_size double precision	,
  address varchar(255),
  connected_since varchar(128),
  client_version varchar(36),
  metadata text,
  time_stamp BIGINT,
  CONSTRAINT fk_publishers_stats_topic_stats_id FOREIGN KEY (topic_stats_id) References topics_stats(topic_stats_id)
);

CREATE TABLE IF NOT EXISTS replications_stats (
  replication_stats_id BIGSERIAL PRIMARY KEY,
  topic_stats_id BIGINT NOT NULL,
  cluster varchar(255) NOT NULL,
  connected BOOLEAN,
  msg_rate_in double precision	,
  msg_rate_out double precision	,
  msg_rate_expired double precision	,
  msg_throughput_in double precision	,
  msg_throughput_out double precision	,
  msg_rate_redeliver double precision	,
  replication_backlog BIGINT,
  replication_delay_in_seconds BIGINT,
  inbound_connection varchar(255),
  inbound_connected_since varchar(255),
  outbound_connection varchar(255),
  outbound_connected_since varchar(255),
  time_stamp BIGINT,
  CONSTRAINT FK_replications_stats_topic_stats_id FOREIGN KEY (topic_stats_id) References topics_stats(topic_stats_id)
);

CREATE TABLE IF NOT EXISTS subscriptions_stats (
  subscription_stats_id BIGSERIAL PRIMARY KEY,
  topic_stats_id BIGINT NOT NULL,
  subscription varchar(255) NULL,
  msg_backlog BIGINT,
  msg_rate_expired double precision	,
  msg_rate_out double precision	,
  msg_throughput_out double precision	,
  msg_rate_redeliver double precision	,
  number_of_entries_since_first_not_acked_message BIGINT,
  total_non_contiguous_deleted_messages_range BIGINT,
  subscription_type varchar(16),
  blocked_subscription_on_unacked_msgs BOOLEAN,
  time_stamp BIGINT,
  UNIQUE (topic_stats_id, subscription),
  CONSTRAINT FK_subscriptions_stats_topic_stats_id FOREIGN KEY (topic_stats_id) References topics_stats(topic_stats_id)
);

CREATE TABLE IF NOT EXISTS consumers_stats (
  consumer_stats_id BIGSERIAL PRIMARY KEY,
  consumer varchar(255) NOT NULL,
  topic_stats_id BIGINT NOT NUll,
  replication_stats_id BIGINT,
  subscription_stats_id BIGINT,
  address varchar(255),
  available_permits BIGINT,
  connected_since varchar(255),
  msg_rate_out double precision	,
  msg_throughput_out double precision	,
  msg_rate_redeliver double precision	,
  client_version varchar(36),
  time_stamp BIGINT,
  metadata text
);

CREATE TABLE IF NOT EXISTS tokens (
  token_id BIGSERIAL PRIMARY KEY,
  role varchar(256) NOT NULL,
  description varchar(128),
  token varchar(1024) NOT NUll,
  UNIQUE (role)
);

CREATE TABLE IF NOT EXISTS users (
  user_id BIGSERIAL PRIMARY KEY,
  access_token varchar(256),
  name varchar(256) NOT NULL,
  description varchar(128),
  email varchar(256),
  phone_number varchar(48),
  location varchar(256),
  company varchar(256),
  expire BIGINT,
  password varchar(256),
  UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS tenants (
  tenant_id BIGSERIAL PRIMARY KEY,
  tenant varchar(255) NOT NULL,
  admin_roles varchar(255),
  allowed_clusters varchar(255),
  environment_name varchar(255),
  UNIQUE(tenant)
);

CREATE TABLE IF NOT EXISTS namespaces (
  namespace_id BIGSERIAL PRIMARY KEY,
  tenant varchar(255) NOT NULL,
  namespace varchar(255) NOT NULL,
  UNIQUE(tenant, namespace)
);

