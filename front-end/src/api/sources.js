import request from '@/utils/request'

const BASE_URL_V3 = '/admin/v3'

export function fetchSources(tenant, namespace) {
  return request({
    url: BASE_URL_V3 + `/source/${tenant}/${namespace}`,
    method: 'get'
  })
}

export function fetchSourcesStats(tenant, namespace, sourceName) {
  return request({
    url: BASE_URL_V3 + `/source/${tenant}/${namespace}/${sourceName}`,
    method: 'get'
  })
}

export function createSource(tenant, namespace, sourceName, data) {
  return request({
    headers: { 'Content-Type': 'multipart/form-data' },
    // headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V3 + `/source/${tenant}/${namespace}/${sourceName}`,
    method: 'post',
    data
  })
}

export function updateSource(tenant, namespace, sourceName, data) {
  return request({
    headers: { 'Content-Type': 'multipart/form-data' },
    // headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V3 + `/source/${tenant}/${namespace}/${sourceName}`,
    method: 'put',
    data
  })
}

export function startSourceInstance(tenant, namespace, sourceName, instanceId) {
  return request({
    url: BASE_URL_V3 + `/source/${tenant}/${namespace}/${sourceName}/${instanceId}/start`,
    method: 'post'
  })
}

export function stopSourceInstance(tenant, namespace, sourceName, instanceId) {
  return request({
    url: BASE_URL_V3 + `/source/${tenant}/${namespace}/${sourceName}/${instanceId}/stop`,
    method: 'post'
  })
}

export function restartSourceInstance(tenant, namespace, sourceName, instanceId) {
  return request({
    url: BASE_URL_V3 + `/source/${tenant}/${namespace}/${sourceName}/${instanceId}/restart`,
    method: 'post'
  })
}

export function deleteSource(tenant, namespace, sourceName) {
  return request({
    url: BASE_URL_V3 + `/source/${tenant}/${namespace}/${sourceName}`,
    method: 'delete'
  })
}

export function getSourceStatus(tenant, namespace, sourceName) {
  return request({
    url: BASE_URL_V3 + `/source/${tenant}/${namespace}/${sourceName}/status`,
    method: 'get'
  })
}

export function getSourceStatusInstance(tenant, namespace, sourceName, instanceId) {
  return request({
    url: BASE_URL_V3 + `/source/${tenant}/${namespace}/${sourceName}/${instanceId}/status`,
    method: 'get'
  })
}

export function getBuiltinSources() {
  return request({
    url: BASE_URL_V3 + `/source/builtinsources`,
    method: 'get'
  })
}
