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
