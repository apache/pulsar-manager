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
package io.streamnative.pulsar.manager.client;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import io.streamnative.pulsar.manager.client.config.ClientConfigurationData;
import org.apache.pulsar.client.api.ClientBuilder;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import java.util.concurrent.TimeUnit;

/**
 * Pulsar Client init class.
 */
public class Client implements AutoCloseable {

    private PulsarClient pulsarClient;

    private final ClientConfigurationData clientConfigurationData;

    public Client(ClientConfigurationData clientConfigurationData) {
        this.clientConfigurationData = clientConfigurationData;
    }

    public PulsarClient getPulsarClient() throws PulsarClientException {
        if (pulsarClient == null) {
            ClientBuilder clientBuilder = PulsarClient.builder();
            checkAndInitClientConfig(clientBuilder);
            pulsarClient = clientBuilder.build();
        }
        return pulsarClient;
    }

    public void checkAndInitClientConfig(ClientBuilder clientBuilder) {
        Preconditions.checkArgument(clientConfigurationData.getServiceUrl() != null
                && clientConfigurationData.getServiceUrl().startsWith("pulsar"), "Serviceurl is incorrect");
        clientBuilder.serviceUrl(clientConfigurationData.getServiceUrl());
        if (clientConfigurationData.getOperationTimeout() != null) {
            Preconditions.checkArgument(clientConfigurationData.getOperationTimeout() > 0,
                    "Parameter operationTimeout should be greater than 0");
            clientBuilder.operationTimeout(clientConfigurationData.getOperationTimeout(), TimeUnit.MILLISECONDS);
        }
        if (clientConfigurationData.getEnableTcpNoDelay() != null) {
            clientBuilder.enableTcpNoDelay(clientConfigurationData.getEnableTcpNoDelay());
        }
        if (clientConfigurationData.getIoThreads() != null) {
            Preconditions.checkArgument(clientConfigurationData.getIoThreads() > 0,
                    "Parameter ioThreads should be greater than 0");
            clientBuilder.ioThreads(clientConfigurationData.getIoThreads());
        }
        if (clientConfigurationData.getListenerThreads() != null) {
            Preconditions.checkArgument(clientConfigurationData.getListenerThreads() > 0,
                    "Parameter listenerThreads should be greater than 0");
            clientBuilder.listenerThreads(clientConfigurationData.getListenerThreads());
        }
        if (clientConfigurationData.getConnectionsPerBroker() != null) {
            Preconditions.checkArgument(clientConfigurationData.getConnectionsPerBroker() > 0,
                    "Parameter connectionsPerBroker should be greater than 0");
            clientBuilder.connectionsPerBroker(clientConfigurationData.getConnectionsPerBroker());
        }
        if (clientConfigurationData.getTlsTrustCertsFilePath() != null) {
            Preconditions.checkArgument(clientConfigurationData.getTlsTrustCertsFilePath().length() > 0,
                    "Parameter tlsTrustCertsFilePath should be set");
            clientBuilder.tlsTrustCertsFilePath(clientConfigurationData.getTlsTrustCertsFilePath());
        }
        if (clientConfigurationData.getAllowTlsInsecureConnection() != null) {
            clientBuilder.allowTlsInsecureConnection(clientConfigurationData.getAllowTlsInsecureConnection());
        }
        if (clientConfigurationData.getEnableTlsHostnameVerification() != null) {
            clientBuilder.enableTlsHostnameVerification(clientConfigurationData.getEnableTlsHostnameVerification());
        }
        if (clientConfigurationData.getStatsInterval() != null) {
            Preconditions.checkArgument(clientConfigurationData.getStatsInterval() > 0L,
                    "Parameter statsInterval should be greater than 0");
            clientBuilder.statsInterval(clientConfigurationData.getStatsInterval(), TimeUnit.SECONDS);
        }
        if (clientConfigurationData.getMaxConcurrentLookupRequests() != null) {
            Preconditions.checkArgument(clientConfigurationData.getMaxConcurrentLookupRequests() > 0,
                    "Parameter maxConcurrentLookupRequests should be greater than 0");
            clientBuilder.maxConcurrentLookupRequests(clientConfigurationData.getMaxConcurrentLookupRequests());
        }
        if (clientConfigurationData.getMaxLookupRequests() != null) {
            Preconditions.checkArgument(clientConfigurationData.getMaxLookupRequests() > 0,
                    "Parameter maxLookupRequests should be greater than 0");
            clientBuilder.maxLookupRequests(clientConfigurationData.getMaxLookupRequests());
        }
        if (clientConfigurationData.getMaxNumberOfRejectedRequestPerConnection() != null) {
            Preconditions.checkArgument(clientConfigurationData.getMaxNumberOfRejectedRequestPerConnection() > 0,
                    "Parameter maxNumberOfRejectedRequestPerConnection should be greater than 0");
            clientBuilder.maxNumberOfRejectedRequestPerConnection(clientConfigurationData.getMaxNumberOfRejectedRequestPerConnection());
        }
        if (clientConfigurationData.getKeepAliveInterval() != null) {
            Preconditions.checkArgument(clientConfigurationData.getKeepAliveInterval() > 0,
                    "Parameter keepAliveInterval should be greater than 0");
            clientBuilder.keepAliveInterval(clientConfigurationData.getKeepAliveInterval(), TimeUnit.SECONDS);
        }
        if (clientConfigurationData.getConnectionTimeout() != null) {
            Preconditions.checkArgument(clientConfigurationData.getConnectionTimeout() > 0,
                    "Parameter connectionTimeout should be greater than 0");
            clientBuilder.connectionTimeout(clientConfigurationData.getConnectionTimeout(), TimeUnit.MILLISECONDS);
        }
        if (clientConfigurationData.getStartingBackoffInterval() != null) {
            Preconditions.checkArgument(clientConfigurationData.getStartingBackoffInterval() > 0,
                    "Parameter startingBackoffInterval should be greater than 0");
            clientBuilder.startingBackoffInterval(clientConfigurationData.getStartingBackoffInterval(), TimeUnit.MILLISECONDS);
        }
        if (clientConfigurationData.getMaxBackoffInterval() != null) {
            Preconditions.checkArgument(clientConfigurationData.getMaxBackoffInterval() > 0,
                    "Parameter maxBackoffInterval should be greater than 0");
            clientBuilder.maxBackoffInterval(clientConfigurationData.getMaxBackoffInterval(), TimeUnit.MILLISECONDS);
        }
    }

    public void close() throws Exception {
        if (pulsarClient != null) {
            pulsarClient.close();
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("serviceUrl", clientConfigurationData.getServiceUrl())
                .add("operationTimeout", clientConfigurationData.getOperationTimeout())
                .add("ioThreads", clientConfigurationData.getIoThreads())
                .add("listenerThreads", clientConfigurationData.getListenerThreads())
                .add("connectionsPerBroker", clientConfigurationData.getConnectionsPerBroker())
                .add("tlsTrustCertsFilePath", clientConfigurationData.getTlsTrustCertsFilePath())
                .add("allowTlsInsecureConnection", clientConfigurationData.getAllowTlsInsecureConnection())
                .add("enableTlsHostnameVerification", clientConfigurationData.getEnableTlsHostnameVerification())
                .add("statsInterval", clientConfigurationData.getStatsInterval())
                .add("maxConcurrentLookupRequests", clientConfigurationData.getMaxConcurrentLookupRequests())
                .add("maxLookupRequests", clientConfigurationData.getMaxLookupRequests())
                .add("maxNumberOfRejectedRequestPerConnection", clientConfigurationData.getMaxNumberOfRejectedRequestPerConnection())
                .add("keepAliveInterval", clientConfigurationData.getKeepAliveInterval())
                .add("connectionTimeout", clientConfigurationData.getConnectionTimeout())
                .add("startingBackoffInterval", clientConfigurationData.getStartingBackoffInterval())
                .add("maxBackoffInterval", clientConfigurationData.getMaxBackoffInterval()).toString();
    }

}
