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

@Getter
@Setter
@NoArgsConstructor
public class PulsarManagerReplicatorStats {
    /** Total rate of messages received from the remote cluster. msg/s */
    private double msgRateIn;

    /** Total throughput received from the remote cluster. bytes/s */
    private double msgThroughputIn;

    /** Total rate of messages delivered to the replication-subscriber. msg/s */
    private double msgRateOut;

    /** Total throughput delivered to the replication-subscriber. bytes/s */
    private double msgThroughputOut;

    /** Total rate of messages expired. msg/s */
    private double msgRateExpired;

    /** Number of messages pending to be replicated to remote cluster */
    private long replicationBacklog;

    /** is the replication-subscriber up and running to replicate to remote cluster */
    private boolean connected;

    /** Time in seconds from the time a message was produced to the time when it is about to be replicated */
    private long replicationDelayInSeconds;

    /** Address of incoming replication connection */
    private String inboundConnection;

    /** Timestamp of incoming connection establishment time */
    private String inboundConnectedSince;

    /** Address of outbound replication connection */
    private String outboundConnection;

    /** Timestamp of outbound connection establishment time */
    private String outboundConnectedSince;

    private ArrayList<PulsarManagerConsumerStats> consumers;

}
