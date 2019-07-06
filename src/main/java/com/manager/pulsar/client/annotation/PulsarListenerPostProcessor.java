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
package com.manager.pulsar.client.annotation;

import com.manager.pulsar.client.PulsarApplicationListener;
import com.manager.pulsar.client.config.PulsarBootstrapConfiguration;
import com.manager.pulsar.client.config.PulsarConsumerConfig;
import com.manager.pulsar.client.config.PulsarConsumerConfigRegistrar;
import com.manager.pulsar.client.consumer.PulsarConsumerContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Parse annotation, for example PulsarListener.
 */
public class PulsarListenerPostProcessor implements BeanPostProcessor, BeanFactoryAware, SmartInitializingSingleton {

    private static final Logger log = LoggerFactory.getLogger(PulsarListenerPostProcessor.class);

    private BeanFactory beanFactory;

    private final PulsarConsumerConfigRegistrar pulsarConsumerConfigRegistrar = new PulsarConsumerConfigRegistrar();

    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(
            new ConcurrentHashMap<>(64));

    private static final String PREFIX_ID = "spring.pulsar.container#";

    private final AtomicInteger counter = new AtomicInteger();

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        log.info("Start set bean Factory");
        this.beanFactory = beanFactory;
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            PulsarApplicationListener pulsarApplicationListener = this.beanFactory.getBean(
                    PulsarBootstrapConfiguration.PULSAR_APPLICATION_LISTENER, PulsarApplicationListener.class);
            this.pulsarConsumerConfigRegistrar.setPulsarApplicationListener(pulsarApplicationListener);
        }
    }

    @Override
    public void afterSingletonsInstantiated() {
        this.pulsarConsumerConfigRegistrar.afterPropertiesSet();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    // Parse annotation
    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        if (!this.nonAnnotatedClasses.contains(bean.getClass())) {
            Class<?> targetClass = AopUtils.getTargetClass(bean);
            Map<Method, Set<PulsarListener>> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
                    (MethodIntrospector.MetadataLookup<Set<PulsarListener>>) method -> {
                        Set<PulsarListener> listenerMethods = findListenerAnnotations(method);
                        return (!listenerMethods.isEmpty() ? listenerMethods : null);
                    });
            if (annotatedMethods.isEmpty()) {
                this.nonAnnotatedClasses.add(bean.getClass());
            } else {
                // Custom annotation
                for (Map.Entry<Method, Set<PulsarListener>> entry : annotatedMethods.entrySet()) {
                    Method method = entry.getKey();
                    for (PulsarListener listener : entry.getValue()) {
                        processPulsarListener(listener, method, bean, beanName);
                    }
                }
            }
        }
        return bean;
    }

    private Set<PulsarListener> findListenerAnnotations(Method method) {
        Set<PulsarListener> listeners = new HashSet<>();
        PulsarListener ann = AnnotatedElementUtils.findMergedAnnotation(method, PulsarListener.class);
        if (ann != null) {
            listeners.add(ann);
        }
        return listeners;
    }

    private void processPulsarListener(PulsarListener pulsarListener, Method method, Object bean, String beanName) {
        PulsarConsumerConfig pulsarConsumerConfig = new PulsarConsumerConfig();
        if (pulsarListener.id().length() > 0) {
            pulsarConsumerConfig.setId(pulsarListener.id());
        } else {
            pulsarConsumerConfig.setId(PREFIX_ID + this.counter.getAndIncrement());
        }
        pulsarConsumerConfig.setTopic(pulsarListener.topic());
        pulsarConsumerConfig.setSubscriptionName(pulsarListener.subscriptionName());
        pulsarConsumerConfig.setSubscriptionType(pulsarListener.subscriptionType());
        pulsarConsumerConfig.setAckTimeout(pulsarListener.ackTimeout());
        pulsarConsumerConfig.setAcknowledgmentGroupTime(pulsarListener.acknowledgmentGroupTime());
        pulsarConsumerConfig.setNegativeAckRedeliveryDelay(pulsarListener.negativeAckRedeliveryDelay());
        pulsarConsumerConfig.setReceiverQueueSize(pulsarListener.receiverQueueSize());
        pulsarConsumerConfig.setAcknowledgmentGroupTime(pulsarListener.acknowledgmentGroupTime());
        pulsarConsumerConfig.setTopics(pulsarListener.topics());
        pulsarConsumerConfig.setTopicsPattern(pulsarListener.topicsPattern());
        pulsarConsumerConfig.setAutoUpdatePartitions(pulsarListener.autoUpdatePartitions());
        pulsarConsumerConfig.setConsumerName(pulsarListener.consumerName());
        pulsarConsumerConfig.setRegexSubscriptionMode(pulsarListener.regexSubscriptionMode());
        pulsarConsumerConfig.setMethod(method);
        pulsarConsumerConfig.setBean(bean);
        pulsarConsumerConfig.setSchema(pulsarListener.schema());
        pulsarConsumerConfig.setSchemaType(pulsarListener.schemaType());
        log.info("Initialization configured to {} use configuration {}", beanName, pulsarConsumerConfig.toString());
        this.pulsarConsumerConfigRegistrar.setConsumerContainer(pulsarConsumerConfig);
    }

    public PulsarConsumerContainer getPulsarConsumerContainer(String id) {
        return this.pulsarConsumerConfigRegistrar.getConsumerContainer(id);
    }
}
