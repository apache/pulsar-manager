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
package org.apache.pulsar.manager.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Consumer Stats entity.
 */
@Getter
@Setter
@NoArgsConstructor
public class ConsumerStatsEntity {
    private long consumerStatsId;
    private long topicStatsId;
    private long replicationStatsId;
    private long subscriptionStatsId;
    private String consumer;
    private double msgRateOut;
    private double msgThroughputOut;
    private double msgRateRedeliver;
    private int availablePermits;
    private int unackedMessages;
    private String address;
    private String connectedSince;
    private String clientVersion;
    private String metadata;
    private long time_stamp;

    public long getTimestamp() {
        return time_stamp;
    }
}
