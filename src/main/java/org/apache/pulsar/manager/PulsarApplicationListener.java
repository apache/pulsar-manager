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
package org.apache.pulsar.manager;

import com.github.pagehelper.Page;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.manager.entity.EnvironmentEntity;
import org.apache.pulsar.manager.entity.EnvironmentsRepository;
import org.apache.pulsar.manager.service.PulsarAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * PulsarApplicationListener do something after the spring framework initialization is complete.
 */
@Component
public class PulsarApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(PulsarApplicationListener.class);

    private final EnvironmentsRepository environmentsRepository;

    private final PulsarAdminService pulsarAdminService;

    @Value("${default.environment.name}")
    private String defaultEnvironmentName;

    @Value("${default.environment.service_url}")
    private String defaultEnvironmentServiceUrl;

    @Autowired
    public PulsarApplicationListener(EnvironmentsRepository environmentsRepository, PulsarAdminService pulsarAdminService) {
        this.environmentsRepository = environmentsRepository;
        this.pulsarAdminService = pulsarAdminService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Start onApplicationEvent");
        Page<EnvironmentEntity> environmentEntities = environmentsRepository
                .getEnvironmentsList(1, 1);
        if (environmentEntities.getResult().size() <= 0) {
            Optional<EnvironmentEntity> environmentEntityOptional = environmentsRepository
                    .findByName(defaultEnvironmentName);
            if (defaultEnvironmentName != null
                    && defaultEnvironmentServiceUrl != null
                    && defaultEnvironmentName.length() > 0
                    && defaultEnvironmentServiceUrl.length() > 0
                    && !environmentEntityOptional.isPresent()) {
                try {
                    pulsarAdminService.clusters(defaultEnvironmentServiceUrl).getClusters();
                } catch (PulsarAdminException e) {
                    log.error("Failed to get clusters list.", e);
                    log.error("Unable to connect default environment {} via {}, " +
                                    "please check if `environment.default.name` " +
                                    "and `environment.default.broker` are set correctly, " +
                                    "environmentDefaultName, environmentDefaultBroker",
                            defaultEnvironmentName, defaultEnvironmentServiceUrl);
                    System.exit(-1);
                }

                EnvironmentEntity environmentEntity = new EnvironmentEntity();
                environmentEntity.setBroker(defaultEnvironmentServiceUrl);
                environmentEntity.setName(defaultEnvironmentName);
                environmentsRepository.save(environmentEntity);
                log.info("Successfully added a default environment: name = {}, service_url = {}.",
                        defaultEnvironmentName, defaultEnvironmentServiceUrl);
            } else {
                log.warn("The default environment already exists.");
            }
        }
        log.debug("Environments already exist.");
    }
}
