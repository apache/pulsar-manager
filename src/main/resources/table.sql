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

CREATE TABLE tenants (
  tenantId INTEGER PRIMARY KEY AUTOINCREMENT,
  tenant varchar(255) NOT NULL,
  adminRoles TEXT,
  allowedClusters TEXT
);


INSERT INTO tenants (tenant) VALUES ('Paul1');
INSERT INTO tenants (tenant, adminRoles, allowedClusters) VALUES ('Paul2', 'role1,role2', 'standalone');
INSERT INTO tenants (tenant, adminRoles, allowedClusters) VALUES ('Paul3', 'role1,role2', 'standalone');
INSERT INTO tenants (tenant, adminRoles, allowedClusters) VALUES ('Paul4', 'role1,role2', 'standalone');
INSERT INTO tenants (tenant, adminRoles, allowedClusters) VALUES ('Paul5', 'role1,role2', 'standalone');
INSERT INTO tenants (tenant, adminRoles, allowedClusters) VALUES ('Paul6', 'role1,role2', 'standalone');
INSERT INTO tenants (tenant, adminRoles, allowedClusters) VALUES ('Paul7', 'role1,role2', 'standalone');
INSERT INTO tenants (tenant, adminRoles, allowedClusters) VALUES ('Paul8', 'role1,role2', 'standalone');
INSERT INTO tenants (tenant, adminRoles, allowedClusters) VALUES ('Paul9', 'role1,role2', 'standalone');
INSERT INTO tenants (tenant, adminRoles, allowedClusters) VALUES ('Paul10', 'role1,role2', 'standalone');
INSERT INTO tenants (tenant, adminRoles, allowedClusters) VALUES ('Paul11', 'role1,role2', 'standalone');