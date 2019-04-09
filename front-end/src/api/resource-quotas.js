import request from '@/utils/request'

const BASE_URL_V2 = '/admin/v2'

export function getResourceQuotas() {
  return request({
    url: BASE_URL_V2 + `/resource-quotas`,
    method: 'get'
  })
}

export function setResourceQuotas(data) {
  return request({
    url: BASE_URL_V2 + `/resource-quotas`,
    method: 'post',
    data
  })
}

export function getResourceQuotasByNamespace(tenantNamespace, bundle) {
  return request({
    url: BASE_URL_V2 + `/resource-quotas/${tenantNamespace}/${bundle}`,
    method: 'get'
  })
}

export function setResourceQuotasByNamespace(tenantNamespace, bundle, data) {
  return request({
    url: BASE_URL_V2 + `/resource-quotas/${tenantNamespace}/${bundle}`,
    method: 'post',
    data
  })
}

export function removeResourceQuotasByNamespace(tenantNamespace, bundle) {
  return request({
    url: BASE_URL_V2 + `/resource-quotas/${tenantNamespace}/${bundle}`,
    method: 'delete'
  })
}
