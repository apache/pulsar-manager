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

import com.manager.pulsar.client.config.ClientConfigurationData;
import org.apache.pulsar.client.api.PulsarClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * PulsarApplicationListener do something after the spring framework initialization is complete.
 */
@Component
public class PulsarApplicationListener implements ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(PulsarApplicationListener.class);

    private ConfigurableApplicationContext applicationContext;

    private Client client;

    private ClientConfigurationData clientConfigurationData;

    @Value("${pulsar.client.serviceUrl}")
    private String pulsarServiceUrl;

    @Value("${pulsar.client.operationTimeout:30000}")
    private Integer operationTimeout;

    @Value("${pulsar.client.ioThreads:1}")
    private Integer ioThreads;

    @Value("${pulsar.client.listenerThreads:1}")
    private Integer listenerThreads;

    @Value("${pulsar.client.connectionsPerBroker:1}")
    private Integer connectionsPerBroker;

    @Value("${pulsar.client.enableTcpNoDelay:false}")
    private Boolean enableTcpNoDelay;

    @Value("${pulsar.client.tlsTrustCertsFilePath:}")
    private String tlsTrustCertsFilePath;

    @Value("${pulsar.client.allowTlsInsecureConnection:false}")
    private Boolean allowTlsInsecureConnection;

    @Value("${pulsar.client.enableTlsHostnameVerification:false}")
    private Boolean enableTlsHostnameVerification;

    @Value("${pulsar.client.statsInterval:60}")
    private Long statsInterval;

    @Value("${pulsar.client.maxConcurrentLookupRequests:5000}")
    private Integer maxConcurrentLookupRequests;

    @Value("${pulsar.client.maxLookupRequests:50000}")
    private Integer maxLookupRequests;

    @Value("${pulsar.client.maxNumberOfRejectedRequestPerConnection:50}")
    private Integer maxNumberOfRejectedRequestPerConnection;

    @Value("${pulsar.client.keepAliveInterval:30}")
    private Integer keepAliveInterval;

    @Value("${pulsar.client.connectionTimeout:100000}")
    private Integer connectionTimeout;

    @Value("${pulsar.client.startingBackoffInterval:100}")
    private Integer startingBackoffInterval;

    @Value("${pulsar.client.maxBackoffInterval:30}")
    private Integer maxBackoffInterval;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext instanceof ConfigurableApplicationContext) {
            this.applicationContext = (ConfigurableApplicationContext) applicationContext;
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Start onApplicationEvent");
    }

    private void initPulsarClientConfig() {
        clientConfigurationData.setServiceUrl(pulsarServiceUrl);
        clientConfigurationData.setOperationTimeout(operationTimeout);
        clientConfigurationData.setAllowTlsInsecureConnection(allowTlsInsecureConnection);
        clientConfigurationData.setEnableTcpNoDelay(enableTcpNoDelay);
        clientConfigurationData.setIoThreads(ioThreads);
        clientConfigurationData.setListenerThreads(listenerThreads);
        clientConfigurationData.setConnectionTimeout(connectionTimeout);
        clientConfigurationData.setEnableTcpNoDelay(enableTcpNoDelay);
        if (tlsTrustCertsFilePath != null && tlsTrustCertsFilePath.length() > 0) {
            clientConfigurationData.setTlsTrustCertsFilePath(tlsTrustCertsFilePath);
        }
        clientConfigurationData.setEnableTlsHostnameVerification(enableTlsHostnameVerification);
        clientConfigurationData.setStatsInterval(statsInterval);
        clientConfigurationData.setMaxConcurrentLookupRequests(maxConcurrentLookupRequests);
        clientConfigurationData.setMaxLookupRequests(maxLookupRequests);
        clientConfigurationData.setMaxNumberOfRejectedRequestPerConnection(maxNumberOfRejectedRequestPerConnection);
        clientConfigurationData.setKeepAliveInterval(keepAliveInterval);
        clientConfigurationData.setStartingBackoffInterval(startingBackoffInterval);
        clientConfigurationData.setMaxBackoffInterval(maxBackoffInterval);
    }

    public Client getClient() {
        if (client == null) {
            clientConfigurationData = new ClientConfigurationData();
            this.initPulsarClientConfig();
            try {
                client = new Client(clientConfigurationData);
                log.info("Init Pulsar Client success use configuration: {}", client.toString());
            } catch (PulsarClientException e) {
                log.error("Init Pulsar Client failed throws PulsarClientException, error: {}", e.getMessage());
                throw new RuntimeException("Init Pulsar Client failed.", e);
            } catch (Exception e) {
                log.error("Init Pulsar Client failed because unknown error: {}", e.getMessage());
                throw new RuntimeException("Init Pulsar Client failed because unknown error.", e);
            }
        }
        return client;
    }
}
