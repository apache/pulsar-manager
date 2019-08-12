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

import io.streamnative.pulsar.manager.client.config.ClientConfigurationData;
import org.apache.pulsar.client.api.ClientBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pulsar Client Test.
 */
public class ClientTest {

    @Test
    public void testClientInitConfig() {
        ClientBuilder clientBuilder = mock(ClientBuilder.class);
        ClientConfigurationData clientConfigurationData = new ClientConfigurationData();
        Client client = new Client(clientConfigurationData);

        clientConfigurationData.setServiceUrl(null);
        try {
            client.checkAndInitClientConfig(clientBuilder);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Serviceurl is incorrect", e.getMessage());
        }

        clientConfigurationData.setServiceUrl("http://xxx");

        try {
            client.checkAndInitClientConfig(clientBuilder);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Serviceurl is incorrect", e.getMessage());
        }

        clientConfigurationData.setServiceUrl("pulsar://localhost:6650");
        when(clientBuilder.serviceUrl(clientConfigurationData.getServiceUrl())).thenReturn(clientBuilder);

        clientConfigurationData.setOperationTimeout(-1);
        try {
            client.checkAndInitClientConfig(clientBuilder);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Parameter operationTimeout should be greater than 0", e.getMessage());
        }

        clientConfigurationData.setOperationTimeout(10);
        when(clientBuilder.operationTimeout(10, TimeUnit.MILLISECONDS)).thenReturn(clientBuilder);

        clientConfigurationData.setIoThreads(-10);
        try {
            client.checkAndInitClientConfig(clientBuilder);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Parameter ioThreads should be greater than 0", e.getMessage());
        }

        clientConfigurationData.setIoThreads(10);
        when(clientBuilder.ioThreads(10)).thenReturn(clientBuilder);

        clientConfigurationData.setListenerThreads(-10);
        try {
            client.checkAndInitClientConfig(clientBuilder);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Parameter listenerThreads should be greater than 0", e.getMessage());
        }

        clientConfigurationData.setListenerThreads(10);
        when(clientBuilder.listenerThreads(10)).thenReturn(clientBuilder);

        clientConfigurationData.setConnectionsPerBroker(-1);
        try {
            client.checkAndInitClientConfig(clientBuilder);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Parameter connectionsPerBroker should be greater than 0", e.getMessage());
        }

        clientConfigurationData.setConnectionsPerBroker(10);
        when(clientBuilder.connectionsPerBroker(10)).thenReturn(clientBuilder);

        clientConfigurationData.setTlsTrustCertsFilePath("");
        try {
            client.checkAndInitClientConfig(clientBuilder);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Parameter tlsTrustCertsFilePath should be set", e.getMessage());
        }

        clientConfigurationData.setTlsTrustCertsFilePath("/etc/tlspath/");
        when(clientBuilder.tlsTrustCertsFilePath("/etc/tlspath/")).thenReturn(clientBuilder);

        clientConfigurationData.setStatsInterval(-1L);
        try {
            client.checkAndInitClientConfig(clientBuilder);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Parameter statsInterval should be greater than 0", e.getMessage());
        }

        clientConfigurationData.setStatsInterval(10L);
        when(clientBuilder.statsInterval(10L, TimeUnit.SECONDS)).thenReturn(clientBuilder);

        clientConfigurationData.setMaxConcurrentLookupRequests(-1);
        try {
            client.checkAndInitClientConfig(clientBuilder);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Parameter maxConcurrentLookupRequests should be greater than 0", e.getMessage());
        }

        clientConfigurationData.setMaxConcurrentLookupRequests(10);
        when(clientBuilder.maxConcurrentLookupRequests(10)).thenReturn(clientBuilder);

        clientConfigurationData.setMaxLookupRequests(-1);
        try {
            client.checkAndInitClientConfig(clientBuilder);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Parameter maxLookupRequests should be greater than 0", e.getMessage());
        }

        clientConfigurationData.setMaxLookupRequests(10);
        when(clientBuilder.maxLookupRequests(10)).thenReturn(clientBuilder);

        clientConfigurationData.setMaxNumberOfRejectedRequestPerConnection(-1);
        try {
            client.checkAndInitClientConfig(clientBuilder);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Parameter maxNumberOfRejectedRequestPerConnection should be greater than 0",
                    e.getMessage());
        }

        clientConfigurationData.setMaxNumberOfRejectedRequestPerConnection(10);
        when(clientBuilder.maxNumberOfRejectedRequestPerConnection(10)).thenReturn(clientBuilder);

        clientConfigurationData.setKeepAliveInterval(-1);
        try {
            client.checkAndInitClientConfig(clientBuilder);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Parameter keepAliveInterval should be greater than 0", e.getMessage());
        }

        clientConfigurationData.setKeepAliveInterval(10);
        when(clientBuilder.keepAliveInterval(10, TimeUnit.SECONDS)).thenReturn(clientBuilder);

        clientConfigurationData.setConnectionTimeout(-1);
        try {
            client.checkAndInitClientConfig(clientBuilder);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Parameter connectionTimeout should be greater than 0", e.getMessage());
        }

        clientConfigurationData.setConnectionTimeout(10);
        when(clientBuilder.connectionTimeout(10, TimeUnit.MILLISECONDS)).thenReturn(clientBuilder);

        clientConfigurationData.setStartingBackoffInterval(-1L);
        try {
            client.checkAndInitClientConfig(clientBuilder);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Parameter startingBackoffInterval should be greater than 0", e.getMessage());
        }

        clientConfigurationData.setStartingBackoffInterval(10L);
        when(clientBuilder.startingBackoffInterval(10L, TimeUnit.MILLISECONDS)).thenReturn(clientBuilder);

        clientConfigurationData.setMaxBackoffInterval(-1L);
        try {
            client.checkAndInitClientConfig(clientBuilder);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Parameter maxBackoffInterval should be greater than 0", e.getMessage());
        }

        clientConfigurationData.setMaxBackoffInterval(10L);
        when(clientBuilder.maxBackoffInterval(10L, TimeUnit.MILLISECONDS)).thenReturn(clientBuilder);


        clientConfigurationData.setEnableTcpNoDelay(false);
        when(clientBuilder.enableTcpNoDelay(false)).thenReturn(clientBuilder);

        clientConfigurationData.setAllowTlsInsecureConnection(false);
        when(clientBuilder.allowTlsInsecureConnection(false)).thenReturn(clientBuilder);

        clientConfigurationData.setEnableTlsHostnameVerification(false);
        when(clientBuilder.enableTlsHostnameVerification(false)).thenReturn(clientBuilder);

        client.checkAndInitClientConfig(clientBuilder);

        // start check
        verify(clientBuilder, atLeast(1)).enableTlsHostnameVerification(false);
        verify(clientBuilder, atLeast(1)).allowTlsInsecureConnection(false);
        verify(clientBuilder, atLeast(1)).enableTcpNoDelay(false);
        verify(clientBuilder, atLeast(1)).maxBackoffInterval(10L, TimeUnit.MILLISECONDS);
        verify(clientBuilder, atLeast(1)).startingBackoffInterval(10L, TimeUnit.MILLISECONDS);
        verify(clientBuilder, atLeast(1)).connectionTimeout(10, TimeUnit.MILLISECONDS);
        verify(clientBuilder, atLeast(1)).keepAliveInterval(10, TimeUnit.SECONDS);
        verify(clientBuilder, atLeast(1)).maxNumberOfRejectedRequestPerConnection(10);
        verify(clientBuilder, atLeast(1)).maxLookupRequests(10);
        verify(clientBuilder, atLeast(1)).maxConcurrentLookupRequests(10);
        verify(clientBuilder, atLeast(1)).statsInterval(10, TimeUnit.SECONDS);
        verify(clientBuilder, atLeast(1)).tlsTrustCertsFilePath("/etc/tlspath/");
        verify(clientBuilder, atLeast(1)).connectionsPerBroker(10);
        verify(clientBuilder, atLeast(1)).ioThreads(10);
        verify(clientBuilder, atLeast(1)).operationTimeout(10, TimeUnit.MILLISECONDS);
        verify(clientBuilder, atLeast(1)).serviceUrl(clientConfigurationData.getServiceUrl());
    }
}
