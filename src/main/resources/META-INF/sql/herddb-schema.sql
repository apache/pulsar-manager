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
  name varchar(256) NOT NULL PRIMARY KEY,
  broker varchar(1024) NOT NULL,
  bookie varchar(1024) NOT NULL
);

CREATE TABLE IF NOT EXISTS topics_stats (
  topic_stats_id LONG PRIMARY KEY AUTO_INCREMENT,
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
  msg_rate_in double	,
  msg_throughput_in double	,
  msg_rate_out double	,
  msg_throughput_out double	,
  average_msg_size double	,
  storage_size double	,
  time_stamp BIGINT
);

CREATE TABLE IF NOT EXISTS publishers_stats (
  publisher_stats_id LONG PRIMARY KEY AUTO_INCREMENT,
  producer_id BIGINT,
  topic_stats_id BIGINT NOT NULL,
  producer_name varchar(255) NOT NULL,
  msg_rate_in double	,
  msg_throughput_in double	,
  average_msg_size double	,
  address varchar(255),
  connected_since varchar(128),
  client_version varchar(36),
  metadata text,
  time_stamp BIGINT
);

CREATE TABLE IF NOT EXISTS replications_stats (
  replication_stats_id LONG PRIMARY KEY AUTO_INCREMENT,
  topic_stats_id BIGINT NOT NULL,
  cluster varchar(255) NOT NULL,
  connected BOOLEAN,
  msg_rate_in double	,
  msg_rate_out double 	,
  msg_rate_expired double 	,
  msg_throughput_in double 	,
  msg_throughput_out double 	,
  msg_rate_redeliver double 	,
  replication_backlog BIGINT,
  replication_delay_in_seconds BIGINT,
  inbound_connection varchar(255),
  inbound_connected_since varchar(255),
  outbound_connection varchar(255),
  outbound_connected_since varchar(255),
  time_stamp BIGINT
);

CREATE TABLE IF NOT EXISTS subscriptions_stats (
  subscription_stats_id LONG PRIMARY KEY AUTO_INCREMENT,
  topic_stats_id BIGINT NOT NULL,
  subscription varchar(255) NULL,
  msg_backlog BIGINT,
  msg_rate_expired double 	,
  msg_rate_out double 	,
  msg_throughput_out double 	,
  msg_rate_redeliver double 	,
  number_of_entries_since_first_not_acked_message BIGINT,
  total_non_contiguous_deleted_messages_range BIGINT,
  subscription_type varchar(16),
  blocked_subscription_on_unacked_msgs BOOLEAN,
  time_stamp BIGINT
);

CREATE TABLE IF NOT EXISTS consumers_stats (
  consumer_stats_id LONG PRIMARY KEY AUTO_INCREMENT,
  consumer varchar(255) NOT NULL,
  topic_stats_id BIGINT NOT NUll,
  replication_stats_id BIGINT,
  subscription_stats_id BIGINT,
  address varchar(255),
  available_permits BIGINT,
  connected_since varchar(255),
  msg_rate_out double 	,
  msg_throughput_out double 	,
  msg_rate_redeliver double 	,
  client_version varchar(36),
  time_stamp BIGINT,
  metadata text
);

CREATE TABLE IF NOT EXISTS tokens (
  token_id LONG PRIMARY KEY AUTO_INCREMENT,
  role varchar(256) NOT NULL,
  description varchar(128),
  token varchar(1024)
);

CREATE TABLE IF NOT EXISTS users (
  user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  access_token varchar(256),
  name varchar(256) NOT NULL,
  description varchar(128),
  email varchar(256),
  phone_number varchar(48),
  location varchar(256),
  company varchar(256),
  password varchar(256),
  expire LONG
);

CREATE TABLE IF NOT EXISTS tenants (
  tenant_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  tenant varchar(255) NOT NULL,
  admin_roles varchar(255),
  allowed_clusters varchar(255),
  environment_name varchar(255)
);

CREATE TABLE IF NOT EXISTS namespaces (
  namespace_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  tenant varchar(255) NOT NULL,
  namespace varchar(255) NOT NULL
);

