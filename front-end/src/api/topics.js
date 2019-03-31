import request from '@/utils/request'

const BASE_URL_V2 = '/admin/v2'

export function fetchTopics(tenant, namespace, query) {
  return request({
    url: BASE_URL_V2 + `/persistent/${tenant}/${namespace}`,
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

export function fetchTopicStats(tenantNamespaceTopic) {
  return request({
    url: BASE_URL_V2 + `/persistent/${tenantNamespaceTopic}/stats`,
    method: 'get'
  })
}

export function fetchPartitionTopicStats(tenantNamespaceTopic) {
  return request({
    url: BASE_URL_V2 + `/persistent/${tenantNamespaceTopic}/partitioned-stats`,
    method: 'get'
  })
}

export function putTopic(tenant, namespace, topic, data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/persistent/${tenant}/${namespace}/${topic}/partitions`,
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

export function deletePartitionTopic(tenantNamespaceTopic) {
  return request({
    url: BASE_URL_V2 + `/persistent/${tenantNamespaceTopic}/partitions`,
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

export function unload(tenantNamespaceTopic) {
  return request({
    url: BASE_URL_V2 + `/persistent/${tenantNamespaceTopic}/unload`,
    method: 'put'
  })
}

export function skip(tenantNamespaceTopic, subName, numMessages) {
  return request({
    url: BASE_URL_V2 + `/persistent/${tenantNamespaceTopic}/subscription/${subName}/skip/${numMessages}`,
    method: 'post'
  })
}

// no find document
// export function clearBacklog(tenantNamespaceTopic) {
//   return request({
//     url: `persistent/${tenantNamespaceTopic}/permissions/${role}`,
//     method: 'delete'
//   })
// }

export function expireMessage(tenantNamespaceTopic, subName, expireTimeInSeconds) {
  return request({
    url: BASE_URL_V2 + `/persistent/${tenantNamespaceTopic}/subscription/${subName}/expireMessages/${expireTimeInSeconds}`,
    method: 'post'
  })
}

export function expireMessagesAllSubscriptions(tenantNamespaceTopic, expireTimeInSeconds) {
  return request({
    url: BASE_URL_V2 + `/persistent/${tenantNamespaceTopic}/all_subscription/expireMessages/${expireTimeInSeconds}`,
    method: 'post'
  })
}

export function peekMessages(tenantNamespaceTopic, subName, messagePosition) {
  return request({
    url: BASE_URL_V2 + `/persistent/${tenantNamespaceTopic}/subscription/${subName}/position/${messagePosition}`,
    method: 'get'
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

export function terminate(tenantNamespaceTopic) {
  return request({
    url: BASE_URL_V2 + `/persistent/${tenantNamespaceTopic}/terminate`,
    method: 'post'
  })
}

export function compact(tenantNamespaceTopic) {
  return request({
    url: BASE_URL_V2 + `/persistent/${tenantNamespaceTopic}/compaction`,
    method: 'put'
  })
}

export function compactionStatus(tenantNamespaceTopic, data) {
  return request({
    url: BASE_URL_V2 + `/persistent/${tenantNamespaceTopic}/compaction`,
    method: 'get',
    data
  })
}

export function offload(tenantNamespaceTopic, data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/persistent/${tenantNamespaceTopic}/offload`,
    method: 'put',
    data
  })
}

export function offloadStatus(tenantNamespaceTopic, data) {
  return request({
    url: BASE_URL_V2 + `/persistent/${tenantNamespaceTopic}/offload`,
    method: 'get',
    data
  })
}
