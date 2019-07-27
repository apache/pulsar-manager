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

const SPRING_BASE_URL_V2 = '/pulsar-manager/admin/v2'

export function fetchBrokerStatsTopics(broker) {
  return request({
    url: SPRING_BASE_URL_V2 + `/broker-stats/topics?broker=` + broker,
    method: 'get'
  })
}

export function fetchBrokerStatsMetrics(broker) {
  return request({
    url: SPRING_BASE_URL_V2 + `/broker-stats/metrics?broker=` + broker,
    method: 'get'
  })
}
