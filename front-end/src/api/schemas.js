import request from '@/utils/request'

const BASE_URL_V2 = '/admin/v2'

export function schemasGet(tenantNamespaceTopic, version) {
  return request({
    url: BASE_URL_V2 + `/schemas/${tenantNamespaceTopic}/schema/${version}`,
    method: 'get'
  })
}

export function schemasDelete(tenantNamespaceTopic) {
  return request({
    url: BASE_URL_V2 + `/schemas/${tenantNamespaceTopic}/schema`,
    method: 'delete'
  })
}

export function schemasUpload(tenantNamespaceTopic, data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V2 + `/schemas/${tenantNamespaceTopic}/schema`,
    method: 'post',
    data
  })
}
