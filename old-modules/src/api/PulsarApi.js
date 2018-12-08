/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
const PULSAR_ADMIN_API = `/admin/v2`;
const axios = require('axios');

class PulsarApi {

  constructor() {
    this.apiPath = PULSAR_ADMIN_API;
    axios.defaults.headers.get['Content-Type'] = 'application/json';
    axios.defaults.headers.post['Content-Type'] = 'application/json';
    axios.defaults.headers.put['Content-Type'] = 'application/json';
  }

  // tenants

  getTenants() {
    return axios.get(`${this.apiPath}/tenants`);
  }

  createTenant(tenant, tenantInfo) {
    return axios.put(`${this.apiPath}/tenants/${tenant}`, tenantInfo);
  }

  deleteTenant(tenant) {
    return axios.delete(`${this.apiPath}/tenants/${tenant}`);
  }

  // Namespaces

  getNamespaces(tenant) {
    return axios.get(`${this.apiPath}/namespaces/${tenant}`);
  }

  deleteNamespace(fully_qualified_namespace) {
    return axios.delete(`${this.apiPath}/namespaces/${fully_qualified_namespace}`);
  }

  createNamespace(tenant, namespace, policies) {
    console.log("Create name space : " + tenant + "/" + namespace);
    return axios.put(`${this.apiPath}/namespaces/${tenant}/${namespace}`, policies);
  }

  // Topics

  getTopics(tenant, namespace) {
    return axios.get(`${this.apiPath}/persistent/${tenant}/${namespace}`);
  }

  getPartitionedTopics(tenant, namespace) {
    return axios.get(`${this.apiPath}/persistent/${tenant}/${namespace}/partitioned`);
  }

}

const API = new PulsarApi();

export default API;
