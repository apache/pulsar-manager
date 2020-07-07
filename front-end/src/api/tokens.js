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

export function fetchTokens(query) {
  return request({
    url: SPRING_BASE_URL + '/tokens',
    method: 'get',
    params: { query }
  })
}

export function putToken(data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: SPRING_BASE_URL + '/tokens/token',
    method: 'put',
    data
  })
}

export function updateToken(data) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: SPRING_BASE_URL + '/tokens/token',
    method: 'post',
    data
  })
}

export function deleteToken(role) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: SPRING_BASE_URL + `/tokens/${role}`,
    method: 'delete'
  })
}

export function getToken(role) {
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: SPRING_BASE_URL + `/tokens/${role}`,
    method: 'get'
  })
}

export function getCsrfToken() {
  return request({
    url: SPRING_BASE_URL + '/csrf-token',
    method: 'get'
  })
}
