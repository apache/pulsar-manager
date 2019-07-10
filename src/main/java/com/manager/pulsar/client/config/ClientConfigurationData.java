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
package com.manager.pulsar.client.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Pulsar Client Configuration.
 */
@Getter
@Setter
@ToString
public class ClientConfigurationData {

    private String serviceUrl;

    private Integer operationTimeout;

    private Integer ioThreads;

    private Integer listenerThreads;

    private Integer connectionsPerBroker;

    private Boolean enableTcpNoDelay;

    private String tlsTrustCertsFilePath;

    private Boolean allowTlsInsecureConnection;

    private Boolean enableTlsHostnameVerification;

    private Long statsInterval;

    private Integer maxConcurrentLookupRequests;

    private Integer maxLookupRequests;

    private Integer maxNumberOfRejectedRequestPerConnection;

    private Integer keepAliveInterval;

    private Integer connectionTimeout;

    private Long startingBackoffInterval;

    private Long maxBackoffInterval;
}
