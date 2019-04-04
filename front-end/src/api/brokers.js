import request from '@/utils/request'

const BASE_URL_V2 = '/admin/v2'

export function fetchBrokers(cluster) {
  return request({
    url: BASE_URL_V2 + `/brokers/${cluster}`,
    method: 'get'
  })
}

export function fetchBrokersConfiguration() {
  return request({
    url: BASE_URL_V2 + `/brokers/configuration`,
    method: 'get'
  })
}

export function fetchBrokersRuntimeConfiguration() {
  return request({
    url: BASE_URL_V2 + `/brokers/configuration/runtime`,
    method: 'get'
  })
}

export function fetchBrokersInternalConfiguration() {
  return request({
    url: BASE_URL_V2 + `/brokers/internal-configuration`,
    method: 'get'
  })
}

export function fetchBrokersDynamicConfiguration() {
  return request({
    url: BASE_URL_V2 + `/brokers/configuration/values`,
    method: 'get'
  })
}

export function updateBrokersDynamicConfiguration(configName, configValue) {
  return request({
    url: BASE_URL_V2 + `/brokers/configuration/${configName}/${configValue}`,
    method: 'post'
  })
}

export function fetchBrokersOwnedNamespaces(cluster, broker) {
  return request({
    url: BASE_URL_V2 + `/brokers/${cluster}/${broker}/ownedNamespaces`,
    method: 'get'
  })
}

export function fetchBrokersHealth() {
  return request({
    url: BASE_URL_V2 + `/brokers/health`,
    method: 'get'
  })
}
