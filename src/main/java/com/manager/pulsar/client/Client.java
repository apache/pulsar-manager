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
package com.manager.pulsar.client;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.manager.pulsar.client.config.PulsarClientConfig;
import org.apache.pulsar.client.api.ClientBuilder;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import java.util.concurrent.TimeUnit;

/**
 * Pulsar Client init class.
 */
public class Client implements AutoCloseable {

    private final PulsarClient pulsarClient;

    private final PulsarClientConfig pulsarClientConfig;

    public Client(PulsarClientConfig pulsarClientConfig) throws PulsarClientException {
        this.pulsarClientConfig = pulsarClientConfig;
        ClientBuilder clientBuilder = PulsarClient.builder();
        checkAndInitClientConfig(clientBuilder);
        pulsarClient = clientBuilder.build();
    }

    public PulsarClient getPulsarClient() {
        return pulsarClient;
    }

    private void checkAndInitClientConfig(ClientBuilder clientBuilder) {
        Preconditions.checkArgument(pulsarClientConfig.getServiceUrl() != null
                && pulsarClientConfig.getServiceUrl().startsWith("pulsar"));
        clientBuilder.serviceUrl(pulsarClientConfig.getServiceUrl());
        if (pulsarClientConfig.getOperationTimeout() > 0) {
            clientBuilder.operationTimeout(pulsarClientConfig.getOperationTimeout(), TimeUnit.MILLISECONDS);
        }
        if (pulsarClientConfig.isEnableTcpNoDelay()) {
            clientBuilder.enableTcpNoDelay(true);
        }
        if (pulsarClientConfig.getIoThreads() > 0) {
            clientBuilder.ioThreads(pulsarClientConfig.getIoThreads());
        }
        if (pulsarClientConfig.getListenerThreads() > 0) {
            clientBuilder.listenerThreads(pulsarClientConfig.getListenerThreads());
        }
        if (pulsarClientConfig.getConnectionsPerBroker() > 0) {
            clientBuilder.connectionsPerBroker(pulsarClientConfig.getConnectionsPerBroker());
        }
        if (pulsarClientConfig.getTlsTrustCertsFilePath() != null
                && pulsarClientConfig.getTlsTrustCertsFilePath().length() > 0) {
            clientBuilder.tlsTrustCertsFilePath(pulsarClientConfig.getTlsTrustCertsFilePath());
        }
        if (pulsarClientConfig.isAllowTlsInsecureConnection()) {
            clientBuilder.allowTlsInsecureConnection(true);
        }
        if (pulsarClientConfig.isEnableTlsHostnameVerification()) {
            clientBuilder.enableTlsHostnameVerification(true);
        }
        if (pulsarClientConfig.getStatsInterval() > 0) {
            clientBuilder.statsInterval(pulsarClientConfig.getStatsInterval(), TimeUnit.SECONDS);
        }
        if (pulsarClientConfig.getMaxConcurrentLookupRequests() > 0) {
            clientBuilder.maxConcurrentLookupRequests(pulsarClientConfig.getMaxConcurrentLookupRequests());
        }
        if (pulsarClientConfig.getMaxLookupRequests() > 0) {
            clientBuilder.maxLookupRequests(pulsarClientConfig.getMaxLookupRequests());
        }
        if (pulsarClientConfig.getMaxNumberOfRejectedRequestPerConnection() > 0) {
            clientBuilder.maxNumberOfRejectedRequestPerConnection(
                    pulsarClientConfig.getMaxNumberOfRejectedRequestPerConnection());
        }
        if (pulsarClientConfig.getKeepAliveInterval() > 0) {
            clientBuilder.keepAliveInterval(pulsarClientConfig.getKeepAliveInterval(), TimeUnit.SECONDS);
        }
        if (pulsarClientConfig.getConnectionTimeout() > 0) {
            clientBuilder.connectionTimeout(pulsarClientConfig.getConnectionTimeout(), TimeUnit.MILLISECONDS);
        }
        if (pulsarClientConfig.getStartingBackoffInterval() > 0) {
            clientBuilder.startingBackoffInterval(pulsarClientConfig.getStartingBackoffInterval(), TimeUnit.MILLISECONDS);
        }
        if (pulsarClientConfig.getMaxBackoffInterval() > 0) {
            clientBuilder.maxBackoffInterval(pulsarClientConfig.getMaxBackoffInterval(), TimeUnit.MILLISECONDS);
        }
    }

    public void close() throws Exception {
        if (pulsarClient != null) {
            pulsarClient.close();
        }
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("serviceUrl", pulsarClientConfig.getServiceUrl())
                .add("operationTimeout", pulsarClientConfig.getOperationTimeout())
                .add("ioThreads", pulsarClientConfig.getIoThreads())
                .add("listenerThreads", pulsarClientConfig.getListenerThreads())
                .add("connectionsPerBroker", pulsarClientConfig.getConnectionsPerBroker())
                .add("tlsTrustCertsFilePath", pulsarClientConfig.getTlsTrustCertsFilePath())
                .add("allowTlsInsecureConnection", pulsarClientConfig.isAllowTlsInsecureConnection())
                .add("enableTlsHostnameVerification", pulsarClientConfig.isEnableTlsHostnameVerification())
                .add("statsInterval", pulsarClientConfig.getStatsInterval())
                .add("maxConcurrentLookupRequests", pulsarClientConfig.getMaxConcurrentLookupRequests())
                .add("maxLookupRequests", pulsarClientConfig.getMaxLookupRequests())
                .add("maxNumberOfRejectedRequestPerConnection", pulsarClientConfig.getMaxNumberOfRejectedRequestPerConnection())
                .add("keepAliveInterval", pulsarClientConfig.getKeepAliveInterval())
                .add("connectionTimeout", pulsarClientConfig.getConnectionTimeout())
                .add("startingBackoffInterval", pulsarClientConfig.getStartingBackoffInterval())
                .add("maxBackoffInterval", pulsarClientConfig.getMaxBackoffInterval()).toString();
    }

}
