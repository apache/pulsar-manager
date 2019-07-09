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

export function fetchFunctions(tenant, namespace) {
  return request({
    url: BASE_URL_V3 + `/functions/${tenant}/${namespace}`,
    method: 'get'
  })
}

export function fetchFunctionStats(tenant, namespace, functionName) {
  return request({
    url: BASE_URL_V3 + `/functions/${tenant}/${namespace}/${functionName}/stats`,
    method: 'get'
  })
}

export function fetchFunctionStatus(tenant, namespace, functionName) {
  return request({
    url: BASE_URL_V3 + `/functions/${tenant}/${namespace}/${functionName}/status`,
    method: 'get'
  })
}

export function createFunction(tenant, namespace, functionName, data) {
  return request({
    headers: { 'Content-Type': 'multipart/form-data' },
    // headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V3 + `/functions/${tenant}/${namespace}/${functionName}`,
    method: 'post',
    data
  })
}

export function updateFunction(tenant, namespace, functionName, data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V3 + `/functions/${tenant}/${namespace}/${functionName}`,
    method: 'put',
    data
  })
}

export function deleteFunction(tenant, namespace, functionName) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V3 + `/functions/${tenant}/${namespace}/${functionName}`,
    method: 'delete'
  })
}

export function restartFunctionInstance(tenant, namespace, functionName, instanceId) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V3 + `/functions/${tenant}/${namespace}/${functionName}/${instanceId}/restart`,
    method: 'post'
  })
}

export function startFunction(tenant, namespace, functionName) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V3 + `/functions/${tenant}/${namespace}/${functionName}/start`,
    method: 'post'
  })
}

export function stopFunction(tenant, namespace, functionName) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V3 + `/functions/${tenant}/${namespace}/${functionName}/stop`,
    method: 'post'
  })
}

export function fetchFunctionInstanceStats(tenant, namespace, functionName, instanceId) {
  return request({
    url: BASE_URL_V3 + `/functions/${tenant}/${namespace}/${functionName}/${instanceId}/stats`,
    method: 'get'
  })
}

export function fetchFunctionInstanceStatus(tenant, namespace, functionName, instanceId) {
  return request({
    url: BASE_URL_V3 + `/functions/${tenant}/${namespace}/${functionName}/${instanceId}/status`,
    method: 'get'
  })
}

export function startFunctionInstance(tenant, namespace, functionName, instanceId) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V3 + `/functions/${tenant}/${namespace}/${functionName}/${instanceId}/start`,
    method: 'post'
  })
}

export function stopFunctionInstance(tenant, namespace, functionName, instanceId) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: BASE_URL_V3 + `/functions/${tenant}/${namespace}/${functionName}/${instanceId}/stop`,
    method: 'post'
  })
}

export function triggerFunction(tenant, namespace, functionName, data) {
  return request({
    headers: { 'Content-Type': 'multipart/form-data' },
    url: BASE_URL_V3 + `/functions/${tenant}/${namespace}/${functionName}/trigger`,
    method: 'post',
    data
  })
}

