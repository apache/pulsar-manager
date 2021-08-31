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

const LOOP_V2 = '/lookup/v2/topic'

export function fetchTopics(tenant, namespace, query) {
  return request({
    url: BASE_URL_V2 + `/persistent/${tenant}/${namespace}`,
    method: 'get',
    params: { query }
  })
}

export function fetchTopicsByPulsarManager(tenant, namespace, query) {
  return request({
    url: SPRING_BASE_URL_V2 + `/topics/${tenant}/${namespace}`,
    method: 'get',
    params: { query }
  })
}

export function fetchTopicsStatsByPulsarManager(tenant, namespace, query) {
  return request({
    url: SPRING_BASE_URL_V2 + `/topics/${tenant}/${namespace}/stats`,
    method: 'get',
    params: { query }
  })
}

export function fetchPersistentPartitonsTopics(tenant, namespace) {
  return request({
    url: BASE_URL_V2 + `/persistent/${tenant}/${namespace}/partitioned`,
    method: 'get'
  })
}

export function fetchNonPersistentPartitonsTopics(tenant, namespace) {
  return request({
    url: BASE_URL_V2 + `/non-persistent/${tenant}/${namespace}/partitioned`,
    method: 'get'
  })
}

export function fetchTopicStats(persistent, tenantNamespaceTopic) {
  return request({
    url: BASE_URL_V2 + `/${persistent}/${tenantNamespaceTopic}/stats`,
    method: 'get'
  })
}

export function fetchTopicStatsInternal(persistent, tenantNamespaceTopic) {
  return request({
    url: BASE_URL_V2 + `/${persistent}/${tenantNamespaceTopic}/internalStats`,
    method: 'get'
  })
}

export function fetchPartitionTopicStats(persistent, tenantNamespaceTopic, perPartition) {
  return request({
    url: BASE_URL_V2 + `/${persistent}/${tenantNamespaceTopic}/partitioned-stats?perPartition=${perPartition}`,
    method: 'get'
  })
}

export function putTopic(persistent, tenant, namespace, topic, data) {
  var url = `/${persistent}/${tenant}/${namespace}/${topic}`
  if (data > 0) {
    url += '/partitions'
  }
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + url,
    method: 'put',
    data
  })
}

export function putTopicByPartition(persistent, tenant, namespace, topic, data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/${persistent}/${tenant}/${namespace}/${topic}/partitions`,
    method: 'put',
    data
  })
}

export function putNonPersistentTopic(tenant, namespace, topic) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/non-persistent/${tenant}/${namespace}/${topic}`,
    method: 'put'
  })
}

export function putNonPersistentPartitionedTopic(tenant, namespace, topic, data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/non-persistent/${tenant}/${namespace}/${topic}`,
    method: 'put',
    data
  })
}

export function getPartitionMetadata(tenantNamespaceTopic) {
  return request({
    url: BASE_URL_V2 + `/persistent/${tenantNamespaceTopic}/partitions`,
    method: 'get'
  })
}

export function updateTopic(tenantNamespaceTopic, data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/persistent/${tenantNamespaceTopic}/partitions`,
    method: 'post',
    data
  })
}

export function deleteTopic(persistent, tenantNamespaceTopic) {
  return deleteTopicOnCluster('', persistent, tenantNamespaceTopic)
}

export function deleteTopicOnCluster(cluster, persistent, tenantNamespaceTopic) {
  return request({
    headers: {
      'Content-Type': 'application/json',
      'x-pulsar-cluster': cluster
    },
    url: BASE_URL_V2 + `/${persistent}/${tenantNamespaceTopic}`,
    method: 'delete'
  })
}

export function deletePartitionTopicOnCl(persistent, tenantNamespaceTopic) {
  return deletePartitionTopicOnCluster('', persistent, tenantNamespaceTopic)
}

export function deletePartitionTopicOnCluster(cluster, persistent, tenantNamespaceTopic) {
  return request({
    headers: {
      'Content-Type': 'application/json',
      'x-pulsar-cluster': cluster
    },
    url: BASE_URL_V2 + `/${persistent}/${tenantNamespaceTopic}/partitions`,
    method: 'delete'
  })
}

export function getPermissionsOnCluster(cluster, persistent, tenantNamespaceTopic) {
  return request({
    headers: {
      'Content-Type': 'application/json',
      'x-pulsar-cluster': cluster
    },
    url: BASE_URL_V2 + `/${persistent}/${tenantNamespaceTopic}/permissions`,
    method: 'get'
  })
}

export function grantPermissionsOnCluster(cluster, persistent, tenantNamespaceTopic, role, data) {
  return request({
    headers: {
      'Content-Type': 'application/json',
      'x-pulsar-cluster': cluster
    },
    url: BASE_URL_V2 + `/${persistent}/${tenantNamespaceTopic}/permissions/${role}`,
    method: 'post',
    data
  })
}

export function revokePermissionsOnCluster(cluster, persistent, tenantNamespaceTopic, role) {
  return request({
    headers: {
      'Content-Type': 'application/json',
      'x-pulsar-cluster': cluster
    },
    url: BASE_URL_V2 + `/${persistent}/${tenantNamespaceTopic}/permissions/${role}`,
    method: 'delete'
  })
}

export function getPermissions(tenantNamespaceTopic) {
  return request({
    url: BASE_URL_V2 + `/persistent/${tenantNamespaceTopic}/permissions`,
    method: 'get'
  })
}

export function grantPermissions(tenantNamespaceTopic, role, data) {
  return request({
    url: BASE_URL_V2 + `/persistent/${tenantNamespaceTopic}/permissions/${role}`,
    method: 'post',
    data
  })
}

export function revokePermissions(tenantNamespaceTopic, role) {
  return request({
    url: BASE_URL_V2 + `/persistent/${tenantNamespaceTopic}/permissions/${role}`,
    method: 'delete'
  })
}

export function unload(persistent, tenantNamespaceTopic) {
  return unloadOnCluster('', persistent, tenantNamespaceTopic)
}

export function unloadOnCluster(cluster, persistent, tenantNamespaceTopic) {
  return request({
    headers: {
      'Content-Type': 'application/json',
      'x-pulsar-cluster': cluster
    },
    url: BASE_URL_V2 + `/${persistent}/${tenantNamespaceTopic}/unload`,
    method: 'put'
  })
}

export function skip(persistent, tenantNamespaceTopic, subName, numMessages) {
  return request({
    url: BASE_URL_V2 + `/${persistent}/${tenantNamespaceTopic}/subscription/${subName}/skip/${numMessages}`,
    method: 'post'
  })
}

export function skipOnCluster(cluster, persistent, tenantNamespaceTopic, subName, numMessages) {
  return request({
    headers: {
      'Content-Type': 'application/json',
      'x-pulsar-cluster': cluster
    },
    url: BASE_URL_V2 + `/${persistent}/${tenantNamespaceTopic}/subscription/${subName}/skip/${numMessages}`,
    method: 'post'
  })
}

export function clearBacklog(persistent, tenantNamespaceTopic, subName) {
  return request({
    url: BASE_URL_V2 + `/${persistent}/${tenantNamespaceTopic}/subscription/${subName}/skip_all`,
    method: 'post'
  })
}

export function clearBacklogOnCluster(cluster, persistent, tenantNamespaceTopic, subName) {
  return request({
    headers: {
      'Content-Type': 'application/json',
      'x-pulsar-cluster': cluster
    },
    url: BASE_URL_V2 + `/${persistent}/${tenantNamespaceTopic}/subscription/${subName}/skip_all`,
    method: 'post'
  })
}

export function expireMessage(persistent, tenantNamespaceTopic, subName, expireTimeInSeconds) {
  return request({
    url: BASE_URL_V2 + `/${persistent}/${tenantNamespaceTopic}/subscription/${subName}/expireMessages/${expireTimeInSeconds}`,
    method: 'post'
  })
}

export function expireMessageOnCluster(cluster, persistent, tenantNamespaceTopic, subName, expireTimeInSeconds) {
  return request({
    headers: {
      'Content-Type': 'application/json',
      'x-pulsar-cluster': cluster
    },
    url: BASE_URL_V2 + `/${persistent}/${tenantNamespaceTopic}/subscription/${subName}/expireMessages/${expireTimeInSeconds}`,
    method: 'post'
  })
}

export function expireMessagesAllSubscriptionsOnCluster(cluster, persistent, tenantNamespaceTopic, expireTimeInSeconds) {
  return request({
    headers: {
      'Content-Type': 'application/json',
      'x-pulsar-cluster': cluster
    },
    url: BASE_URL_V2 + `/${persistent}/${tenantNamespaceTopic}/all_subscription/expireMessages/${expireTimeInSeconds}`,
    method: 'post'
  })
}

export function peekMessages(persistent, tenantNamespaceTopic, subName, messagePosition) {
  return request({
    url: BASE_URL_V2 + `/${persistent}/${tenantNamespaceTopic}/subscription/${subName}/position/${messagePosition}`,
    method: 'get'
  })
}

export function resetCursorByTimestamp(persistent, tenantNamespaceTopic, subName, timestamp) {
  return request({
    url: BASE_URL_V2 + `/${persistent}/${tenantNamespaceTopic}/subscription/${subName}/resetcursor/${timestamp}`,
    method: 'post'
  })
}

export function resetCursorByTimestampOnCluster(cluster, persistent, tenantNamespaceTopic, subName, timestamp) {
  return request({
    headers: {
      'Content-Type': 'application/json',
      'x-pulsar-cluster': cluster
    },
    url: BASE_URL_V2 + `/${persistent}/${tenantNamespaceTopic}/subscription/${subName}/resetcursor/${timestamp}`,
    method: 'post'
  })
}

export function resetCursorByPosition(persistent, tenantNamespaceTopic, subName, data) {
  return request({
    url: BASE_URL_V2 + `/${persistent}/${tenantNamespaceTopic}/subscription/${subName}/resetcursor`,
    method: 'post',
    data
  })
}

export function resetCursorByPositionOnCluster(cluster, persistent, tenantNamespaceTopic, subName, data) {
  return request({
    headers: {
      'Content-Type': 'application/json',
      'x-pulsar-cluster': cluster
    },
    url: BASE_URL_V2 + `/${persistent}/${tenantNamespaceTopic}/subscription/${subName}/resetcursor`,
    method: 'post',
    data
  })
}

export function resetPersistentCursor(tenantNamespaceTopic, subName, timestamp, data) {
  return request({
    url: BASE_URL_V2 + `/persistent/${tenantNamespaceTopic}/subscription/${subName}/resetcursor/${timestamp}`,
    method: 'post',
    data
  })
}

export function resetNonPersistentCursor(tenantNamespaceTopic, subName, timestamp, data) {
  return request({
    url: BASE_URL_V2 + `/non-persistent/${tenantNamespaceTopic}/subscription/${subName}/resetcursor/${timestamp}`,
    method: 'post',
    data
  })
}

export function terminate(persistent, tenantNamespaceTopic) {
  return terminateOnCluster('', persistent, tenantNamespaceTopic)
}

export function terminateOnCluster(cluster, persistent, tenantNamespaceTopic) {
  return request({
    headers: {
      'Content-Type': 'application/json',
      'x-pulsar-cluster': cluster
    },
    url: BASE_URL_V2 + `/${persistent}/${tenantNamespaceTopic}/terminate`,
    method: 'post'
  })
}

export function compact(persistent, tenantNamespaceTopic) {
  return compactOnCluster('', persistent, tenantNamespaceTopic)
}

export function compactOnCluster(cluster, persistent, tenantNamespaceTopic) {
  return request({
    headers: {
      'Content-Type': 'application/json',
      'x-pulsar-cluster': cluster
    },
    url: BASE_URL_V2 + `/${persistent}/${tenantNamespaceTopic}/compaction`,
    method: 'put'
  })
}

export function compactionStatus(persistent, tenantNamespaceTopic, data) {
  return compactionStatusOnCluster('', persistent, tenantNamespaceTopic, data)
}

export function compactionStatusOnCluster(cluster, persistent, tenantNamespaceTopic, data) {
  return request({
    headers: {
      'Content-Type': 'application/json',
      'x-pulsar-cluster': cluster
    },
    url: BASE_URL_V2 + `/${persistent}/${tenantNamespaceTopic}/compaction`,
    method: 'get',
    data
  })
}

export function offload(persistent, tenantNamespaceTopic, data) {
  return offloadOnCluster('', persistent, tenantNamespaceTopic, data)
}
export function offloadOnCluster(cluster, persistent, tenantNamespaceTopic, data) {
  return request({
    headers: {
      'Content-Type': 'application/json',
      'x-pulsar-cluster': cluster
    },
    url: BASE_URL_V2 + `/${persistent}/${tenantNamespaceTopic}/offload`,
    method: 'put',
    data
  })
}

export function offloadStatus(persistent, tenantNamespaceTopic, data) {
  return offloadStatusOnCluster('', persistent, tenantNamespaceTopic, data)
}

export function offloadStatusOnCluster(cluster, persistent, tenantNamespaceTopic, data) {
  return request({
    headers: {
      'Content-Type': 'application/json',
      'x-pulsar-cluster': cluster
    },
    url: BASE_URL_V2 + `/${persistent}/${tenantNamespaceTopic}/offload`,
    method: 'get',
    data
  })
}

export function getBundleRange(persistent, tenantNamespaceTopic) {
  return getBundleRangeOnCluster('', persistent, tenantNamespaceTopic)
}

export function getBundleRangeOnCluster(cluster, persistent, tenantNamespaceTopic) {
  return request({
    headers: {
      'Content-Type': 'application/json',
      'x-pulsar-cluster': cluster
    },
    url: LOOP_V2 + `/${persistent}/${tenantNamespaceTopic}/bundle`,
    method: 'get'
  })
}

export function getTopicBroker(persistent, tenantNamespaceTopic) {
  return getTopicBrokerOnCluster('', persistent, tenantNamespaceTopic)
}

export function getTopicBrokerOnCluster(cluster, persistent, tenantNamespaceTopic) {
  return request({
    headers: {
      'Content-Type': 'application/json',
      'x-pulsar-cluster': cluster
    },
    url: LOOP_V2 + `/${persistent}/${tenantNamespaceTopic}`,
    method: 'get'
  })
}

export function fetchTopicSchemaFromBroker(tenantNamespaceTopic) {
  return request({
    url: BASE_URL_V2 + `/schemas/${tenantNamespaceTopic}/schema`,
    method: 'get'
  })
}
