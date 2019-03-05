import request from '@/utils/request'

export function fetchTopics(tenant, namespace, query) {
  return request({
    url: `/persistent/${tenant}/${namespace}`,
    method: 'get',
    params: { query }
  })
}

export function fetchTopicStats(tenant, namespace, topic) {
  return request({
    url: `/persistent/${tenant}/${namespace}/${topic}/stats`,
    method: 'get'
  })
}

export function putTopic(tenant, namespace, topic, params) {
  return request({
    url: `/persistent/${tenant}/${namespace}/${topic}/partitions`,
    method: 'put',
    params: { params }
  })
}

export function getPartitionMetadata(tenant, namespace, topic) {
  return request({
    url: `/persistent/${tenant}/${namespace}/${topic}/partitions`,
    method: 'get'
  })
}

export function updateTopic(tenant, namespace, topic, partition) {
  return request({
    url: `/persistent/${tenant}/${namespace}/${topic}/${partition}/partitions`,
    method: 'post'
  })
}
