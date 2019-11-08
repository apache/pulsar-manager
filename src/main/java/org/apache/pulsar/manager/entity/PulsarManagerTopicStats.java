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

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
public class PulsarManagerTopicStats {

    /** Total rate of messages published on the topic. msg/s */
    private double msgRateIn;

    /** Total throughput of messages published on the topic. byte/s */
    private double msgThroughputIn;

    /** Total rate of messages dispatched for the topic. msg/s */
    private double msgRateOut;

    /** Total throughput of messages dispatched for the topic. byte/s */
    private double msgThroughputOut;

    /** Average size of published messages. bytes */
    private double averageMsgSize;

    /** Space used to store the messages for the topic. bytes */
    private long storageSize;

    private int pendingAddEntriesCount;

    /** List of connected publishers on this topic w/ their stats */
    private ArrayList<PulsarManagerPublisherStats> publishers;

    /** Map of subscriptions with their individual statistics */
    private HashMap<String, PulsarManagerSubscriptionStats> subscriptions;

    /** Map of replication statistics by remote cluster context */
    private HashMap<String, PulsarManagerReplicatorStats> replication;

    private String deduplicationStatus;

}
