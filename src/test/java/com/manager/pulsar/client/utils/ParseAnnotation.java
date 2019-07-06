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
import com.manager.pulsar.client.config.PulsarConsumerConfig;
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

    public static final List<PulsarConsumerConfig> pulsarConsumerConfigs = new ArrayList<>();

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
                    PulsarConsumerConfig pulsarConsumerConfig = new PulsarConsumerConfig();
                    pulsarConsumerConfig.setId(listener.id());
                    pulsarConsumerConfig.setTopic(listener.topic());
                    pulsarConsumerConfig.setAckTimeout(listener.ackTimeout());
                    pulsarConsumerConfig.setSubscriptionName(listener.subscriptionName());
                    pulsarConsumerConfig.setSubscriptionType(listener.subscriptionType());
                    pulsarConsumerConfig.setAcknowledgmentGroupTime(listener.acknowledgmentGroupTime());
                    pulsarConsumerConfig.setNegativeAckRedeliveryDelay(listener.negativeAckRedeliveryDelay());
                    pulsarConsumerConfig.setReceiverQueueSize(listener.receiverQueueSize());
                    pulsarConsumerConfig.setTopics(listener.topics());
                    pulsarConsumerConfig.setAcknowledgmentGroupTime(listener.acknowledgmentGroupTime());
                    pulsarConsumerConfig.setTopicsPattern(listener.topicsPattern());
                    pulsarConsumerConfig.setAutoUpdatePartitions(listener.autoUpdatePartitions());
                    pulsarConsumerConfig.setConsumerName(listener.consumerName());
                    pulsarConsumerConfig.setMethod(method);
                    pulsarConsumerConfig.setBean(bean);
                    pulsarConsumerConfig.setRegexSubscriptionMode(listener.regexSubscriptionMode());
                    pulsarConsumerConfig.setSchema(listener.schema());
                    pulsarConsumerConfig.setSchemaType(listener.schemaType());
                    pulsarConsumerConfigs.add(pulsarConsumerConfig);
                }
            }
        }
    }
}
