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
