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
