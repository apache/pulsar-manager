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

const SPRING_BASE_URL = '/pulsar-manager'

export function fetchUsers(pageNum, pageSize) {
  return request({
    url: SPRING_BASE_URL + '/users',
    method: 'get',
    params: {
      page_num: pageNum,
      page_size: pageSize
    }
  })
}

export function putUser(data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: SPRING_BASE_URL + '/users/user',
    method: 'put',
    data
  })
}

export function updateUser(data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: SPRING_BASE_URL + '/users/user',
    method: 'post',
    data
  })
}

export function deleteUser(data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: SPRING_BASE_URL + '/users/user',
    method: 'delete',
    data
  })
}

export function getUserInfo() {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: SPRING_BASE_URL + '/users/userInfo',
    method: 'get'
  })
}
