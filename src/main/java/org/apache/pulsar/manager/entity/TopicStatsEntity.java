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

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Topic Stats entity.
 */
@Getter
@Setter
@NoArgsConstructor
@Data
public class TopicStatsEntity {

    @Data
    @NoArgsConstructor
    public static class TopicStatsSummary {

        private String topic;
        private int partitions;
        private String persistent;
        private int producerCount;
        private int subscriptionCount;
        private double msgRateIn;
        private double msgThroughputIn;
        private double msgRateOut;
        private double msgThroughputOut;
        private double averageMsgSize;
        private double storageSize;

        public TopicStatsSummary add(TopicStatsSummary other) {
            TopicStatsSummary total = new TopicStatsSummary();
            total.producerCount = producerCount + other.producerCount;
            total.subscriptionCount = subscriptionCount + other.subscriptionCount;
            total.msgRateIn = msgRateIn + other.msgRateIn;
            total.msgRateOut = msgRateOut + other.msgRateOut;
            total.msgThroughputIn = msgThroughputIn + other.msgThroughputIn;
            total.msgThroughputOut = msgThroughputOut + other.msgThroughputOut;
            total.averageMsgSize = (averageMsgSize + other.averageMsgSize) / 2;
            total.storageSize = storageSize + other.storageSize;
            return total;
        }
    }

    private long topicStatsId;
    private String environment;
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
    private long time_stamp;

    public long getTimestamp() {
        return time_stamp;
    }


    public TopicStatsSummary getSummary() {
        TopicStatsSummary summary = new TopicStatsSummary();
        summary.producerCount = producerCount;
        summary.subscriptionCount = subscriptionCount;
        summary.msgRateIn = msgRateIn;
        summary.msgRateOut = msgRateOut;
        summary.msgThroughputIn = msgThroughputIn;
        summary.msgThroughputOut = msgThroughputOut;
        summary.averageMsgSize = averageMsgSize;
        summary.storageSize = storageSize;
        return summary;
    }

}
