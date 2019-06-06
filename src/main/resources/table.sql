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
  tenantId BIGINT PRIMARY KEY,
  tenant varchar(255) NOT NULL,
  adminRoles TEXT,
  allowedClusters TEXT
);


INSERT INTO tenants (tenantId, tenant) VALUES (1, 'Paul1');
INSERT INTO tenants (tenantId, tenant, adminRoles, allowedClusters) VALUES (2, 'Paul2', 'role1,role2', 'standalone');
INSERT INTO tenants (tenantId, tenant, adminRoles, allowedClusters) VALUES (3, 'Paul3', 'role1,role2', 'standalone');
INSERT INTO tenants (tenantId, tenant, adminRoles, allowedClusters) VALUES (4, 'Paul4', 'role1,role2', 'standalone');
INSERT INTO tenants (tenantId, tenant, adminRoles, allowedClusters) VALUES (5, 'Paul5', 'role1,role2', 'standalone');
INSERT INTO tenants (tenantId, tenant, adminRoles, allowedClusters) VALUES (6, 'Paul6', 'role1,role2', 'standalone');
INSERT INTO tenants (tenantId, tenant, adminRoles, allowedClusters) VALUES (7, 'Paul7', 'role1,role2', 'standalone');
INSERT INTO tenants (tenantId, tenant, adminRoles, allowedClusters) VALUES (8, 'Paul8', 'role1,role2', 'standalone');
INSERT INTO tenants (tenantId, tenant, adminRoles, allowedClusters) VALUES (9, 'Paul9', 'role1,role2', 'standalone');
INSERT INTO tenants (tenantId, tenant, adminRoles, allowedClusters) VALUES (10, 'Paul10', 'role1,role2', 'standalone');
INSERT INTO tenants (tenantId, tenant, adminRoles, allowedClusters) VALUES (11, 'Paul11', 'role1,role2', 'standalone');

