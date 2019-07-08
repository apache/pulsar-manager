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
