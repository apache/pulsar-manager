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
public class PulsarClientConfig {

    private String serviceUrl;

    private int operationTimeout;

    private int ioThreads;

    private int listenerThreads;

    private int connectionsPerBroker;

    private boolean enableTcpNoDelay;

    private String tlsTrustCertsFilePath;

    private boolean allowTlsInsecureConnection;

    private boolean enableTlsHostnameVerification;

    private long statsInterval;

    private int maxConcurrentLookupRequests;

    private int maxLookupRequests;

    private int maxNumberOfRejectedRequestPerConnection;

    private int keepAliveInterval;

    private int connectionTimeout;

    private int startingBackoffInterval;

    private int maxBackoffInterval;
}
