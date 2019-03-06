import request from '@/utils/request'

const BASE_URL_V2 = '/admin/v2'

export function fetchSubscriptions(tenant, namespace, topic, query) {
  return request({
    url: BASE_URL_V2 + `/persistent/${tenant}/${namespace}/${topic}/subscriptions`,
    method: 'get',
    params: { query }
  })
}

export function putSubscription(tenant, namespace, topic, subscription) {
  return request({
    url: BASE_URL_V2 + `/persistent/${tenant}/${namespace}/${topic}/subscription/${subscription}`,
    method: 'put'
  })
}

export function deleteSubscription(tenant, namespace, topic, subscription) {
  return request({
    url: BASE_URL_V2 + `/persistent/${tenant}/${namespace}/${topic}/subscription/${subscription}`,
    method: 'delete'
  })
}
