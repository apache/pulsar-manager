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

const BASE_URL_V2 = '/admin/v2'

const SPRING_BASE_URL_V2 = '/pulsar-manager/admin/v2'

export function fetchBrokers(cluster) {
  return request({
    headers: {
      'x-pulsar-cluster': cluster
    },
    url: SPRING_BASE_URL_V2 + `/brokers/${cluster}`,
    method: 'get'
  })
}

export function fetchBrokersByDirectBroker(cluster) {
  return request({
    headers: {
      'x-pulsar-cluster': cluster
    },
    url: BASE_URL_V2 + `/brokers/${cluster}`,
    method: 'get'
  })
}

export function fetchBrokersConfiguration(broker) {
  return request({
    headers: {
      'x-pulsar-broker': broker
    },
    url: BASE_URL_V2 + `/brokers/configuration`,
    method: 'get'
  })
}

export function fetchBrokersRuntimeConfiguration(broker) {
  return request({
    headers: {
      'x-pulsar-broker': broker
    },
    url: BASE_URL_V2 + `/brokers/configuration/runtime`,
    method: 'get'
  })
}

export function fetchBrokersInternalConfiguration(cluster) {
  return request({
    headers: {
      'x-pulsar-cluster': cluster
    },
    url: BASE_URL_V2 + `/brokers/internal-configuration`,
    method: 'get'
  })
}

export function fetchBrokersDynamicConfiguration(cluster) {
  return request({
    headers: {
      'x-pulsar-cluster': cluster
    },
    url: BASE_URL_V2 + `/brokers/configuration/values`,
    method: 'get'
  })
}

export function updateBrokersDynamicConfiguration(cluster, configName, configValue) {
  return request({
    headers: {
      'x-pulsar-cluster': cluster
    },
    url: BASE_URL_V2 + `/brokers/configuration/${configName}/${configValue}`,
    method: 'post'
  })
}

export function fetchBrokersOwnedNamespaces(cluster, broker) {
  return request({
    headers: {
      'x-pulsar-broker': broker
    },
    url: BASE_URL_V2 + `/brokers/${cluster}/${broker}/ownedNamespaces`,
    method: 'get'
  })
}

export function fetchBrokersHealth(broker) {
  return request({
    headers: {
      'x-pulsar-broker': broker
    },
    url: BASE_URL_V2 + `/brokers/health`,
    method: 'get'
  })
}
