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
public class PulsarManagerSubscriptionStats {

    private long numberOfEntriesSinceFirstNotAckedMessage;
    private long totalNonContiguousDeletedMessagesRange;

    /** Total rate of messages delivered on this subscription. msg/s */
    private double msgRateOut;

    /** Total throughput delivered on this subscription. bytes/s */
    private double msgThroughputOut;

    /** Total rate of messages redelivered on this subscription. msg/s */
    private double msgRateRedeliver;

    /** Number of messages in the subscription backlog */
    private long msgBacklog;

    /** Whether this subscription is Exclusive or Shared or Failover */
    private String type;

    /** Total rate of messages expired on this subscription. msg/s */
    private double msgRateExpired;

    /** List of connected consumers on this subscription w/ their stats */
    private ArrayList<PulsarManagerConsumerStats> consumers;

    /** Mark that the subscription state is kept in sync across different regions */
    private boolean isReplicated;

}
