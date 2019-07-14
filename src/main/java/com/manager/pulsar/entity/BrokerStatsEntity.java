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
 * BrokerStats entity.
 */
@Getter
@Setter
@NoArgsConstructor
public class BrokerStatsEntity {

    private String broker;
    // Most recently available system resource usage.
    private double cpuUsage;
    private double cpuLimit;
    private double memoryUsage;
    private double memoryLimit;
    private double directMemoryUsage;
    private double directMemoryLimit;

    private double bandwidthInUsage;
    private double bandwidthInLimit;
    private double bandwidthOutUsage;
    private double bandwidthOutLimit;

    // Message data from the most recent namespace bundle stats.
    private double msgThroughputIn;
    private double msgThroughputOut;
    private double msgRateIn;
    private double msgRateOut;

    // Timestamp of last update.
    private long lastUpdate;

    // The stats given in the most recent invocation of update.
    private String lastStats;

    private String bundleStats;

    private int numTopics;
    private int numBundles;
    private int numConsumers;
    private int numProducers;

    // All bundles belonging to this broker.
    private String bundles;

    // The bundles gained since the last invocation of update.
    private String lastBundleGains;

    // The bundles lost since the last invocation of update.
    private String lastBundleLosses;
}
