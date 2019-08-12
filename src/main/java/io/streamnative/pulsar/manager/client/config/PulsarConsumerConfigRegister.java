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
package io.streamnative.pulsar.manager.client.config;

import io.streamnative.pulsar.manager.client.PulsarApplicationListener;
import io.streamnative.pulsar.manager.client.consumer.PulsarConsumerContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ConsumerConfigurationData register class.
 */
public class PulsarConsumerConfigRegister implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(PulsarConsumerConfigRegister.class);

    private final Map<String, PulsarConsumerContainer> consumerContainers = new ConcurrentHashMap<>();

    private PulsarApplicationListener pulsarApplicationListener;

    public void setPulsarApplicationListener(PulsarApplicationListener pulsarApplicationListener) {
        this.pulsarApplicationListener = pulsarApplicationListener;
    }

    public PulsarApplicationListener getPulsarApplicationListener() {
        return this.pulsarApplicationListener;
    }

    public void setConsumerContainer(ConsumerConfigurationData consumerConfigurationData) {
        synchronized (this.consumerContainers) {
            log.info("Start init consumer execute container");
            PulsarConsumerContainer pulsarConsumerContainer = new PulsarConsumerContainer(
                    pulsarApplicationListener.getClient(), consumerConfigurationData);
            this.consumerContainers.put(consumerConfigurationData.getId(), pulsarConsumerContainer);
        }
    }

    @Override
    public void afterPropertiesSet() {
        startAllContainers();
    }

    public void startAllContainers() {
        synchronized (this.consumerContainers) {
            this.consumerContainers.forEach((k, v) -> {
                v.start();
            });
        }
    }

    public void stopAllContainers() {
        synchronized (this.consumerContainers) {
            this.consumerContainers.forEach((k, v) -> {
                v.stop(null);
            });
        }
    }

    public PulsarConsumerContainer getConsumerContainer(String id) {
        return this.consumerContainers.get(id);
    }
}
