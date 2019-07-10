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
