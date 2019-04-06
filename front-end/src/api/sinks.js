import request from '@/utils/request'

const BASE_URL_V3 = '/admin/v3'

export function fetchSinks(tenant, namespace) {
  return request({
    url: BASE_URL_V3 + `/sink/${tenant}/${namespace}`,
    method: 'get'
  })
}

export function fetchSinksStats(tenant, namespace, sinkName) {
  return request({
    url: BASE_URL_V3 + `/sink/${tenant}/${namespace}/${sinkName}`,
    method: 'get'
  })
}

export function createSink(tenant, namespace, sinkName, data) {
  return request({
    headers: { 'Content-Type': 'multipart/form-data' },
    // headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V3 + `/sink/${tenant}/${namespace}/${sinkName}`,
    method: 'post',
    data
  })
}

export function updateSink(tenant, namespace, sinkName, data) {
  return request({
    headers: { 'Content-Type': 'multipart/form-data' },
    // headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V3 + `/sink/${tenant}/${namespace}/${sinkName}`,
    method: 'put',
    data
  })
}

export function startSinkInstance(tenant, namespace, sinkName, instanceId) {
  return request({
    url: BASE_URL_V3 + `/sink/${tenant}/${namespace}/${sinkName}/${instanceId}/start`,
    method: 'post'
  })
}

export function stopSinkInstance(tenant, namespace, sinkName, instanceId) {
  return request({
    url: BASE_URL_V3 + `/sink/${tenant}/${namespace}/${sinkName}/${instanceId}/stop`,
    method: 'post'
  })
}

export function restartSinkInstance(tenant, namespace, sinkName, instanceId) {
  return request({
    url: BASE_URL_V3 + `/sink/${tenant}/${namespace}/${sinkName}/${instanceId}/restart`,
    method: 'post'
  })
}

export function deleteSink(tenant, namespace, sinkName) {
  return request({
    url: BASE_URL_V3 + `/sink/${tenant}/${namespace}/${sinkName}`,
    method: 'delete'
  })
}

export function getSinkStatus(tenant, namespace, sinkName) {
  return request({
    url: BASE_URL_V3 + `/sink/${tenant}/${namespace}/${sinkName}/status`,
    method: 'get'
  })
}

export function getSinkStatusInstance(tenant, namespace, sinkName, instanceId) {
  return request({
    url: BASE_URL_V3 + `/sink/${tenant}/${namespace}/${sinkName}/${instanceId}/status`,
    method: 'get'
  })
}

export function getBuiltinSinks() {
  return request({
    url: BASE_URL_V3 + `/sink/builtinsinks`,
    method: 'get'
  })
}
