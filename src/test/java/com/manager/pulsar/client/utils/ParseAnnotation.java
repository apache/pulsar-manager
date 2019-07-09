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
package com.manager.pulsar.client.utils;

import com.manager.pulsar.client.annotation.PulsarListener;
import com.manager.pulsar.client.config.ConsumerConfigurationData;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A tool to test after parse custom annotation.
 */
public class ParseAnnotation {

    public static final List<ConsumerConfigurationData> CONSUMER_CONFIGURATION_DATA = new ArrayList<>();

    public static void parse(final Object bean) {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        Map<Method, Set<PulsarListener>> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
                (MethodIntrospector.MetadataLookup<Set<PulsarListener>>) method -> {
                    Set<PulsarListener> listenerMethods = new HashSet<>();
                    PulsarListener ann = AnnotatedElementUtils.findMergedAnnotation(method, PulsarListener.class);
                    if (ann != null) {
                        listenerMethods.add(ann);
                    }
                    return (!listenerMethods.isEmpty() ? listenerMethods : null);
                });
        if (!annotatedMethods.isEmpty()) {
            for (Map.Entry<Method, Set<PulsarListener>> entry : annotatedMethods.entrySet()) {
                Method method = entry.getKey();
                for (PulsarListener listener : entry.getValue()) {
                    ConsumerConfigurationData consumerConfigurationData = new ConsumerConfigurationData();
                    consumerConfigurationData.setId(listener.id());
                    consumerConfigurationData.setTopics(listener.topics());
                    consumerConfigurationData.setAckTimeout(listener.ackTimeout());
                    consumerConfigurationData.setSubscriptionName(listener.subscriptionName());
                    consumerConfigurationData.setSubscriptionType(listener.subscriptionType());
                    consumerConfigurationData.setAcknowledgmentGroupTime(listener.acknowledgmentGroupTime());
                    consumerConfigurationData.setNegativeAckRedeliveryDelay(listener.negativeAckRedeliveryDelay());
                    consumerConfigurationData.setReceiverQueueSize(listener.receiverQueueSize());
                    consumerConfigurationData.setTopics(listener.topics());
                    consumerConfigurationData.setAcknowledgmentGroupTime(listener.acknowledgmentGroupTime());
                    consumerConfigurationData.setTopicsPattern(listener.topicsPattern());
                    consumerConfigurationData.setAutoUpdatePartitions(listener.autoUpdatePartitions());
                    consumerConfigurationData.setConsumerName(listener.consumerName());
                    consumerConfigurationData.setMethod(method);
                    consumerConfigurationData.setBean(bean);
                    consumerConfigurationData.setRegexSubscriptionMode(listener.regexSubscriptionMode());
                    consumerConfigurationData.setSchema(listener.schema());
                    consumerConfigurationData.setSchemaType(listener.schemaType());
                    CONSUMER_CONFIGURATION_DATA.add(consumerConfigurationData);
                }
            }
        }
    }
}
