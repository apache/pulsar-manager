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

import com.manager.pulsar.client.config.PulsarClientConfig;
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

    private PulsarClientConfig pulsarClientConfig;

    @Value("${pulsar.client.serviceUrl}")
    private String pulsarServiceUrl;

    @Value("${pulsar.client.operationTimeout:0}")
    private int operationTimeout;

    @Value("${pulsar.client.ioThreads:0}")
    private int ioThreads;

    @Value("${pulsar.client.listenerThreads:0}")
    private int listenerThreads;

    @Value("${pulsar.client.connectionsPerBroker:0}")
    private int connectionsPerBroker;

    @Value("${pulsar.client.enableTcpNoDelay:false}")
    private boolean enableTcpNoDelay;

    @Value("${pulsar.client.tlsTrustCertsFilePath:}")
    private String tlsTrustCertsFilePath;

    @Value("${pulsar.client.allowTlsInsecureConnection:false}")
    private boolean allowTlsInsecureConnection;

    @Value("${pulsar.client.enableTlsHostnameVerification:false}")
    private boolean enableTlsHostnameVerification;

    @Value("${pulsar.client.statsInterval:0}")
    private long statsInterval;

    @Value("${pulsar.client.maxConcurrentLookupRequests:0}")
    private int maxConcurrentLookupRequests;

    @Value("${pulsar.client.maxLookupRequests:0}")
    private int maxLookupRequests;

    @Value("${pulsar.client.maxNumberOfRejectedRequestPerConnection:0}")
    private int maxNumberOfRejectedRequestPerConnection;

    @Value("${pulsar.client.keepAliveInterval:0}")
    private int keepAliveInterval;

    @Value("${pulsar.client.connectionTimeout:0}")
    private int connectionTimeout;

    @Value("${pulsar.client.startingBackoffInterval:0}")
    private int startingBackoffInterval;

    @Value("${pulsar.client.maxBackoffInterval:0}")
    private int maxBackoffInterval;

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
        pulsarClientConfig.setServiceUrl(pulsarServiceUrl);
        pulsarClientConfig.setAllowTlsInsecureConnection(allowTlsInsecureConnection);
        pulsarClientConfig.setEnableTcpNoDelay(enableTcpNoDelay);
        pulsarClientConfig.setIoThreads(ioThreads);
        pulsarClientConfig.setConnectionTimeout(connectionTimeout);
        pulsarClientConfig.setEnableTcpNoDelay(enableTcpNoDelay);
        pulsarClientConfig.setTlsTrustCertsFilePath(tlsTrustCertsFilePath);
        pulsarClientConfig.setEnableTlsHostnameVerification(enableTlsHostnameVerification);
        pulsarClientConfig.setStatsInterval(statsInterval);
        pulsarClientConfig.setMaxConcurrentLookupRequests(maxConcurrentLookupRequests);
        pulsarClientConfig.setMaxLookupRequests(maxLookupRequests);
        pulsarClientConfig.setMaxNumberOfRejectedRequestPerConnection(maxNumberOfRejectedRequestPerConnection);
        pulsarClientConfig.setKeepAliveInterval(keepAliveInterval);
        pulsarClientConfig.setStartingBackoffInterval(startingBackoffInterval);
        pulsarClientConfig.setMaxBackoffInterval(maxBackoffInterval);
    }

    public Client getClient() {
        if (client == null) {
            pulsarClientConfig = new PulsarClientConfig();
            this.initPulsarClientConfig();
            try {
                client = new Client(pulsarClientConfig);
                log.info("Init Pulsar Client success use configuration: {}", client.toString());
            } catch (PulsarClientException e) {
                log.error("Init Pulsar Client failed throws PulsarClientException, error: {}", e.getMessage());
            } catch (Exception e) {
                log.error("Init Pulsar Client failed because unknown error: {}", e.getMessage());
            }
        }
        return client;
    }
}
