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
class PulsarUtils {

  getNamespaceName(fully_qualified_namespace) {
    var namespace_parts = fully_qualified_namespace.split('/');
    return {
      'tenant': namespace_parts[0],
      'namespace': namespace_parts.slice(1,namespace_parts.length).join("/"),
      'fully_qualified_namespace': fully_qualified_namespace
    };
  }

  // Urls

  getTenantUrl(tenant) {
    return `/management/tenant/${tenant}`;
  }

  getNamespaceUrl(tenant, namespace) {
    return `/management/namespace/${tenant}/${namespace}`;
  }

}

const PULSAR = new PulsarUtils();

export default PULSAR;
