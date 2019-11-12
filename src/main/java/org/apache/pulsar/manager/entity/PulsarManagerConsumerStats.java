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

import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
public class PulsarManagerConsumerStats {

    private String address;

    private String connectedSince;

    private String clientVersion;

    /** Total rate of messages delivered to the consumer. msg/s */
    private double msgRateOut;

    /** Total throughput delivered to the consumer. bytes/s */
    private double msgThroughputOut;

    /** Total rate of messages redelivered by this consumer. msg/s */
    private double msgRateRedeliver;

    /** Name of the consumer */
    private String consumerName;

    /** Number of available message permits for the consumer */
    private int availablePermits;

    /** Metadata (key/value strings) associated with this consumer */
    private HashMap<String, String> metadata;
}
