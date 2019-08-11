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
package com.manager.pulsar.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Topic Stats entity.
 */
@Getter
@Setter
@NoArgsConstructor
public class TopicStatsEntity {
    private long topicStatsId;
    private String cluster;
    private String broker;
    private String tenant;
    private String namespace;
    private String bundle;
    private String persistent;
    private String topic;
    private int producerCount;
    private int subscriptionCount;
    private double msgRateIn;
    private double msgThroughputIn;
    private double msgRateOut;
    private double msgThroughputOut;
    private double averageMsgSize;
    private double storageSize;
    private long timestamp;
}
