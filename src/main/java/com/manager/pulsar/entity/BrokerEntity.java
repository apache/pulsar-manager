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
 * Broker entity.
 */
@Getter
@Setter
@NoArgsConstructor
public class BrokerEntity {
    private long brokerId;
    private String broker;
    // URLs to satisfy contract of ServiceLookupData (used by NamespaceService).
    private String webServiceUrl;
    private String webServiceUrlTls;
    private String pulsarServiceUrl;
    private String pulsarServiceUrlTls;
    private boolean persistentTopicsEnabled = true;
    private boolean nonPersistentTopicsEnabled = true;

    private String brokerVersionString;

    private String loadReportType;

    private double maxResourceUsage;
}
