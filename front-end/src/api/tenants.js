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

export function fetchTenantsAndNamespaces(query) {
  return request({
    url: '/tenants/namespaces',
    method: 'get',
    params: { query }
  })
}

export function fetchTenants(tenant) {
  return request({
    url: `/tenants/${tenant}`,
    method: 'get'
  })
}

export function fetchTenantsInfo(query) {
  return request({
    url: '/manage/tenants/list',
    method: 'get',
    params: { query }
  })
}

export function putTenant(tenant, data) {
  return request({
    url: `/tenants/${tenant}`,
    method: 'put',
    data
  })
}

export function updateTenant(tenant, data) {
  return request({
    url: `/tenants/${tenant}`,
    method: 'post',
    data
  })
}

export function deleteTenant(tenant) {
  return request({
    url: `/tenants/${tenant}`,
    method: 'delete'
  })
}
