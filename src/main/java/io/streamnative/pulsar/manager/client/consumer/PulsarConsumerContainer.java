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
package io.streamnative.pulsar.manager.client.consumer;

import com.google.common.base.Preconditions;
import io.streamnative.pulsar.manager.client.Client;
import io.streamnative.pulsar.manager.client.config.ConsumerConfigurationData;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.SchedulingAwareRunnable;

/**
 * Start PulsarConsumer
 */
public class PulsarConsumerContainer implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(PulsarConsumerContainer.class);

    private volatile boolean running = false;

    private final Client client;

    private final ConsumerConfigurationData consumerConfigurationData;

    private final ListenerConsumer listenerConsumer;

    public PulsarConsumerContainer(Client client, ConsumerConfigurationData consumerConfigurationData) {
        this.client = client;
        this.consumerConfigurationData = consumerConfigurationData;
        this.listenerConsumer = new ListenerConsumer(this.client, this.consumerConfigurationData);
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public final void stop() {}

    @Override
    public void stop(Runnable callback) {
        this.setRunning(false);
        try {
            if (this.listenerConsumer != null) {
                this.listenerConsumer.close();
            }
            log.info("Close consumer success");
        } catch (PulsarClientException e) {
            log.error("Close consumer failed, because: {}", e.getMessage());
        }
    }

    @Override
    public final void start() {
        if (isRunning()) {
            return;
        }
        log.info("Start thread to received messages");
        SimpleAsyncTaskExecutor consumerExecutor = new SimpleAsyncTaskExecutor();
        this.setRunning(true);
        consumerExecutor.submitListenable(listenerConsumer);
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    protected void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public int getPhase() {
        return 0;
    }

    public PulsarConsumer getPulsarConsumer() {
        return this.listenerConsumer.getPulsarConsumer();
    }

    private final class ListenerConsumer implements SchedulingAwareRunnable {

        private final PulsarConsumer pulsarConsumer;

        ListenerConsumer(Client client, ConsumerConfigurationData consumerConfigurationData) {
            pulsarConsumer = new PulsarConsumer(client, consumerConfigurationData);
        }

        public PulsarConsumer getPulsarConsumer() {
            return pulsarConsumer;
        }

        @Override
        public boolean isLongLived() {
            return true;
        }

        public void close() throws PulsarClientException{
            pulsarConsumer.close();
        }

        @Override
        public void run() {
            Consumer consumer = pulsarConsumer.getConsumer();
            Preconditions.checkNotNull(consumer, "Consumer is null, this is not allowed");
            while (isRunning()) {
                try {
                    Message msg = consumer.receive();
                    try {
                        consumerConfigurationData.getMethod().invoke(consumerConfigurationData.getBean(), msg);
                        consumer.acknowledgeAsync(msg);
                    } catch (Exception e) {
                        consumer.negativeAcknowledge(msg);
                        log.warn("Message handle failed: {}, redeliver later", msg.toString());
                    }
                } catch (PulsarClientException e) {
                    log.error("Received message has a error: {}", e.getMessage());
                }
            }
        }
    }
}
