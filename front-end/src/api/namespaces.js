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
import request from '@/utils/request'

const SPRING_BASE_URL_V2 = '/pulsar-manager/admin/v2'

const BASE_URL_V2 = '/admin/v2'

export function fetchNamespaces(tenant, query) {
  return request({
    url: SPRING_BASE_URL_V2 + `/namespaces/${tenant}`,
    method: 'get',
    params: { query }
  })
}

export function fetchNamespaceStats(tenant, namespace) {
  return request({
    url: SPRING_BASE_URL_V2 + `/namespaces/${tenant}/${namespace}/stats`,
    method: 'get'
  })
}

export function fetchNamespacePolicies(tenantNamespace) {
  return request({
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}`,
    method: 'get'
  })
}

export function putNamespace(tenant, namespace, data) {
  return request({
    url: BASE_URL_V2 + `/namespaces/${tenant}/${namespace}`,
    method: 'put',
    data
  })
}

export function updateNamespace(tenant, namespace, data) {
  return request({
    url: BASE_URL_V2 + `/namespaces/${tenant}/${namespace}`,
    method: 'post',
    data
  })
}

export function deleteNamespace(namespace) {
  return request({
    url: BASE_URL_V2 + `/namespaces/${namespace}`,
    method: 'delete'
  })
}

export function getPermissions(tenantNamespace) {
  return request({
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/permissions`,
    method: 'get'
  })
}

export function grantPermissions(tenantNamespace, role, data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/permissions/${role}`,
    method: 'post',
    data
  })
}

export function revokePermissions(tenantNamespace, role) {
  return request({
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/permissions/${role}`,
    method: 'delete'
  })
}

export function setClusters(tenantNamespace, data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/replication`,
    method: 'post',
    data
  })
}

export function getClusters(tenant, namespace) {
  return request({
    url: BASE_URL_V2 + `/namespaces/${tenant}/${namespace}/replication`,
    method: 'get'
  })
}

export function setBacklogQuota(tenantNamespace, data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/backlogQuota`,
    method: 'post',
    data
  })
}

export function removeBacklogQuota(tenantNamespace) {
  return request({
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/backlogQuota`,
    method: 'delete'
  })
}

export function getPersistence(tenantNamespace) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/persistence`,
    method: 'get'
  })
}

export function setPersistence(tenantNamespace, data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/persistence`,
    method: 'post',
    data
  })
}

export function setMessageTtl(tenantNamespace, data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/messageTTL`,
    method: 'post',
    data
  })
}

export function setAntiAffinityGroup(tenantNamespace, data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/antiAffinity`,
    method: 'post',
    data
  })
}

export function deleteAntiAffinityGroup(tenantNamespace) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/antiAffinity`,
    method: 'delete'
  })
}

export function setDeduplication(tenantNamespace, data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/deduplication`,
    method: 'post',
    data
  })
}

export function getRetention(tenantNamespace) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/retention`,
    method: 'get'
  })
}

export function setRetention(tenantNamespace, data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/retention`,
    method: 'post',
    data
  })
}

export function unload(tenantNamespace, data) {
  return unloadOnCluster('', tenantNamespace, data)
}

export function unloadOnCluster(cluster, tenantNamespace, data) {
  return request({
    headers: {
      'Content-Type': 'application/json',
      'x-pulsar-cluster': cluster
    },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/unload`,
    method: 'put',
    data
  })
}

export function unloadBundle(tenantNamespace, bundle) {
  return unloadBundleImpl('', '', tenantNamespace, bundle)
}

export function unloadBundleOnBroker(broker, tenantNamespace, bundle) {
  return unloadBundleImpl('', broker, tenantNamespace, bundle)
}

export function unloadBundleOnCluster(cluster, tenantNamespace, bundle) {
  return unloadBundleImpl(cluster, '', tenantNamespace, bundle)
}

export function unloadBundleImpl(cluster, broker, tenantNamespace, bundle) {
  return request({
    headers: {
      'Content-Type': 'application/json',
      'x-pulsar-cluster': cluster,
      'x-pulsar-broker': broker
    },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/${bundle}/unload`,
    method: 'put'
  })
}

export function splitBundle(tenantNamespace, bundle, unload) {
  return splitBundleOnCluster('', tenantNamespace, bundle, unload)
}

export function splitBundleOnCluster(cluster, tenantNamespace, bundle, unload) {
  return request({
    headers: {
      'Content-Type': 'application/json',
      'x-pulsar-cluster': cluster
    },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/${bundle}/split?unload=${unload}`,
    method: 'put'
  })
}

export function setDispatchRate(tenantNamespace, data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/dispatchRate`,
    method: 'post',
    data
  })
}

export function setSubscribeRate(tenantNamespace, data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/subscribeRate`,
    method: 'post',
    data
  })
}

export function setSubscriptionDispatchRate(tenantNamespace, data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/subscriptionDispatchRate`,
    method: 'post',
    data
  })
}

export function clearBacklog(tenantNamespace) {
  return clearBacklogOnCluster('', tenantNamespace)
}

export function clearBacklogOnCluster(cluster, tenantNamespace) {
  return request({
    headers: {
      'Content-Type': 'application/json',
      'x-pulsar-cluster': cluster
    },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/clearBacklog`,
    method: 'post'
  })
}

export function clearBundleBacklog(tenantNamespace, bundle) {
  return clearBundleBacklogOnCluster('', tenantNamespace, bundle)
}

export function clearBundleBacklogOnCluster(cluster, tenantNamespace, bundle) {
  return request({
    headers: {
      'Content-Type': 'application/json',
      'x-pulsar-cluster': cluster
    },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/${bundle}/clearBacklog`,
    method: 'post'
  })
}

export function clearBundleBacklogForSubscription(tenantNamespace, bundle, subscription) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/${bundle}/clearBacklog/${subscription}`,
    method: 'post'
  })
}

export function unsubscribe(tenantNamespace, subscription) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/unsubscribe/${subscription}`,
    method: 'post'
  })
}

export function unsubscribeByBundle(tenantNamespace, bundle, subscription) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/${bundle}/unsubscribe/${subscription}`,
    method: 'post'
  })
}

export function setEncryptionRequired(tenantNamespace, data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/encryptionRequired`,
    method: 'post',
    data
  })
}

export function setSubscriptionAuthMode(tenantNamespace, data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/subscriptionAuthMode`,
    method: 'post',
    data
  })
}

export function setMaxProducersPerTopic(tenantNamespace, data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/maxProducersPerTopic`,
    method: 'post',
    data
  })
}

export function setMaxConsumersPerTopic(tenantNamespace, data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/maxConsumersPerTopic`,
    method: 'post',
    data
  })
}

export function setMaxConsumersPerSubscription(tenantNamespace, data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/maxConsumersPerSubscription`,
    method: 'post',
    data
  })
}

export function setCompactionThreshold(tenantNamespace, data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/compactionThreshold`,
    method: 'put',
    data
  })
}

export function setOffloadThreshold(tenantNamespace, data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/offloadThreshold`,
    method: 'put',
    data
  })
}

export function setOffloadDeletionLag(tenantNamespace, data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/offloadDeletionLagMs`,
    method: 'put',
    data
  })
}

export function clearOffloadDeletionLag(tenantNamespace) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/offloadDeletionLagMs`,
    method: 'delete'
  })
}

export function setSchemaAutoupdateStrategy(tenantNamespace, data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/schemaAutoUpdateCompatibilityStrategy`,
    method: 'put',
    data
  })
}

export function setSchemaValidationEnforced(tenantNamespace, data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/namespaces/${tenantNamespace}/schemaValidationEnforced`,
    method: 'post',
    data
  })
}
