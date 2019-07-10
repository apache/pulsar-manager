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
package com.manager.pulsar.client.config;

import com.manager.pulsar.client.PulsarApplicationListener;
import com.manager.pulsar.client.annotation.PulsarListenerPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Initialize two bean components, PULSAR_APPLICATION_LISTENER and PULSAR_LISTENER_POST_PROCESSOR.
 * PULSAR_APPLICATION_LISTENER for set pulsarClient.
 * PULSAR_LISTENER_POST_PROCESSOR for parse annotation.
 */
@Configuration
public class PulsarBootstrapConfiguration {

    public static final String PULSAR_LISTENER_POST_PROCESSOR = "PULSAR_LISTENER_POST_PROCESSOR";

    public static final String PULSAR_APPLICATION_LISTENER = "PULSAR_APPLICATION_LISTENER";


    @Bean(name = PULSAR_APPLICATION_LISTENER)
    public PulsarApplicationListener defaultPulsarApplicationListener() {
        return new PulsarApplicationListener();
    }

    @Bean(name = PULSAR_LISTENER_POST_PROCESSOR)
    public PulsarListenerPostProcessor defaultPulsarListenerPostProcessor() { return new PulsarListenerPostProcessor(); }

}
