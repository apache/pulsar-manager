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
import java.util.Map;


@Getter
@Setter
@NoArgsConstructor
public class PulsarManagerPublisherStats {

    /** Total rate of messages published by this publisher. msg/s */
    private double msgRateIn;

    /** Total throughput of messages published by this publisher. byte/s */
    private double msgThroughputIn;

    /** Average message size published by this publisher */
    private double averageMsgSize;

    private String address;

    /** Id of this publisher */
    private long producerId;

    private String producerName;

    private String connectedSince;

    private String clientVersion;

    private HashMap<String, String> metadata;
}
