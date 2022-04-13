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

export function loginByUsername(username, password) {
  const data = {
    username,
    password
  }
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: '/pulsar-manager/login',
    method: 'post',
    data
  })
}

export function loginByCasdoor(code, state) {
  const data = {
    code,
    state
  }
  return request({
    headers: { 'Content-Type': 'application/json' },
    url: '/pulsar-manager/casdoor',
    method: 'post',
    data
  })
}

export function logout() {
  return request({
    url: '/pulsar-manager/logout',
    method: 'post'
  })
}

export function getUserInfo(token) {
  return request({
    url: '/user/info',
    method: 'get',
    params: { token }
  })
}
