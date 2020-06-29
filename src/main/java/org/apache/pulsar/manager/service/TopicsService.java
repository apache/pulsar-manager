/**
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
package org.apache.pulsar.manager.service;

import java.util.List;
import java.util.Map;

public interface TopicsService {

    Map<String, Object> getTopicsList(
            Integer pageNum, Integer pageSize, String tenant, String namespace, String requestHost);

    Map<String, Object> getTopicStats(
            Integer pageNum, Integer pageSize,
            String tenant, String namespace,
            String env, String serviceUrl);

    List<Map<String, Object>> getTopicsStatsList(String env, String tenant, String namespace,
                                                 String persistent, List<Map<String, String>> topics);

    List<Map<String, Object>> peekMessages(
            String persistent, String tenant,
            String namespace, String topic,
            String subName, Integer messagePosition,
            String requestHost);
}
