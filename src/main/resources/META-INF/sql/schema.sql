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

CREATE TABLE IF NOT EXISTS clusters (
  clusterId INTEGER PRIMARY KEY AUTOINCREMENT,
  cluster varchar(255),
  serviceUrl varchar(1024),
  serviceUrlTls varchar(1024),
  brokerServiceUrl varchar(1024),
  brokerServiceUrlTls varchar(1024),
  UNIQUE (cluster)
);
CREATE INDEX IF NOT EXISTS index_cluster ON clusters(cluster);