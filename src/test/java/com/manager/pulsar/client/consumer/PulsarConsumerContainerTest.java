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
package com.manager.pulsar.client.consumer;

import com.manager.pulsar.client.Client;
import com.manager.pulsar.client.PulsarApplicationListener;
import com.manager.pulsar.client.annotation.PulsarListener;
import com.manager.pulsar.client.config.ConsumerConfigurationData;
import com.manager.pulsar.client.config.PulsarConsumerConfigRegister;
import com.manager.pulsar.client.utils.ParseAnnotation;
import com.manager.pulsar.client.utils.TestMessage;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.ConsumerBuilder;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.common.schema.SchemaType;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pulsar Consumer container test.
 */
public class PulsarConsumerContainerTest {

    private final TestMessage testMessage = new TestMessage();

    private static final String receiveMessage = "hello-world";

    static class Foo {
        private String field1;
        private String field2;
        private int field3;
    }

    private class PulsarListenerAnnotationByte {

        @PulsarListener(topics = "test", subscriptionName = "xxxx")
        public void testReceive(Message message) {
            Assert.assertEquals(new String(message.getData()), receiveMessage);
        }
    }

    private class PulsarListenerAnnotationAvro {

        @PulsarListener(id = "test-container", topics = "test2",
                subscriptionName = "xxxx2", schema = Foo.class, schemaType = SchemaType.AVRO)
        public void testReceive(Message message) {
            Foo foo = (Foo) message.getValue();
            Assert.assertEquals(foo.field1, "a");
            Assert.assertEquals(foo.field2, "b");
            Assert.assertEquals(foo.field3, 4);
        }
    }


    @Test
    public void testPulsarConsumerContainer() throws PulsarClientException, InterruptedException {
        Client client = mock(Client.class);
        PulsarClient pulsarClient = mock(PulsarClient.class);
        Consumer consumer = mock(Consumer.class);
        ConsumerBuilder consumerBuilder = mock(ConsumerBuilder.class);
        PulsarApplicationListener pulsarApplicationListener = mock(PulsarApplicationListener.class);
        PulsarConsumerConfigRegister pulsarConsumerConfigRegister = new PulsarConsumerConfigRegister();
        PulsarListenerAnnotationByte pulsarListenerAnnotation = new PulsarListenerAnnotationByte();
        when(pulsarApplicationListener.getClient()).thenReturn(client);
        pulsarConsumerConfigRegister.setPulsarApplicationListener(pulsarApplicationListener);
        ParseAnnotation.parse(pulsarListenerAnnotation);
        for (ConsumerConfigurationData consumerConfigurationData : ParseAnnotation.CONSUMER_CONFIGURATION_DATA) {
            pulsarConsumerConfigRegister.setConsumerContainer(consumerConfigurationData);
        }
        when(client.getPulsarClient()).thenReturn(pulsarClient);
        when(pulsarClient.newConsumer(Schema.BYTES)).thenReturn(consumerBuilder);
        when(consumerBuilder.subscribe()).thenReturn(consumer);
        when(consumer.toString()).thenReturn("consumer");
        testMessage.setData(receiveMessage.getBytes());
        when(consumer.receive()).thenReturn(testMessage);
        pulsarConsumerConfigRegister.afterPropertiesSet();
        Thread.sleep(10);
        pulsarConsumerConfigRegister.stopAllContainers();
        verify(consumer, atLeast(1)).receive();
        verify(consumer, atLeast(1)).acknowledgeAsync(testMessage);
    }

    @Test
    public void testPulsarConsumerContainerAvro() throws PulsarClientException, InterruptedException {
        Client client = mock(Client.class);
        PulsarClient pulsarClient = mock(PulsarClient.class);
        Consumer consumer = mock(Consumer.class);
        ConsumerBuilder consumerBuilder = mock(ConsumerBuilder.class);
        PulsarApplicationListener pulsarApplicationListener = mock(PulsarApplicationListener.class);
        PulsarConsumerConfigRegister pulsarConsumerConfigRegister = new PulsarConsumerConfigRegister();
        PulsarListenerAnnotationAvro pulsarListenerAnnotation = new PulsarListenerAnnotationAvro();
        when(pulsarApplicationListener.getClient()).thenReturn(client);
        pulsarConsumerConfigRegister.setPulsarApplicationListener(pulsarApplicationListener);
        ParseAnnotation.parse(pulsarListenerAnnotation);
        for (ConsumerConfigurationData consumerConfigurationData : ParseAnnotation.CONSUMER_CONFIGURATION_DATA) {
            pulsarConsumerConfigRegister.setConsumerContainer(consumerConfigurationData);
        }
        when(client.getPulsarClient()).thenReturn(pulsarClient);

        Schema fooSchema = Schema.AVRO(Foo.class);
        PulsarConsumerContainer pulsarConsumerContainer = pulsarConsumerConfigRegister
                .getConsumerContainer("test-container");
        pulsarConsumerContainer.getPulsarConsumer().setSchema(fooSchema);
        when(pulsarClient.newConsumer(fooSchema)).thenReturn(consumerBuilder);
        when(consumerBuilder.subscribe()).thenReturn(consumer);
        when(consumer.toString()).thenReturn("consumer");
        Foo foo = new Foo();
        foo.field1 = "a";
        foo.field2 = "b";
        foo.field3 = 4;
        testMessage.setValue(foo);
        when(consumer.receive()).thenReturn(testMessage);
        pulsarConsumerConfigRegister.afterPropertiesSet();
        Thread.sleep(10);
        pulsarConsumerConfigRegister.stopAllContainers();
        verify(consumer, atLeast(1)).receive();
        verify(consumer, atLeast(1)).acknowledgeAsync(testMessage);
    }
}
