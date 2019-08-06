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

const SPRING_BASE_URL_V1 = '/pulsar-manager/api/v1'

export function getBookiesList(cluster, query) {
  return request({
    url: SPRING_BASE_URL_V1 + `/bookies/${cluster}`,
    method: 'get',
    params: { query }
  })
}

export function racksInfo() {
  return request({
    url: BASE_URL_V2 + `/bookies/racks-info`,
    method: 'get'
  })
}

export function racksInfoByBookie(bookie) {
  return request({
    url: BASE_URL_V2 + `/bookies/racks-info/${bookie}`,
    method: 'get'
  })
}

export function updateRacksByBookie(bookie, group, data) {
  return request({
    url: BASE_URL_V2 + `/bookies/racks-info/${bookie}?group=${group}`,
    method: 'post',
    data
  })
}

export function deleteRacksByBookie(bookie) {
  return request({
    url: BASE_URL_V2 + `/bookies/racks-info/${bookie}`,
    method: 'delete'
  })
}

export function heartbeat(bookie) {
  return request({
    url: SPRING_BASE_URL_V1 + `/bookies/heartbeat/${bookie}`,
    method: 'get'
  })
}

