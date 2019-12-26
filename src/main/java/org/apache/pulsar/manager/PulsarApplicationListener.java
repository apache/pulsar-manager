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
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.pulsar.manager.entity.EnvironmentEntity;
import org.apache.pulsar.manager.entity.EnvironmentsRepository;
import org.apache.pulsar.manager.utils.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * PulsarApplicationListener do something after the spring framework initialization is complete.
 */
@Component
public class PulsarApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(PulsarApplicationListener.class);

    private final EnvironmentsRepository environmentsRepository;

    @Value("${environment.default.name}")
    private String environmentDefaultName;

    @Value("${environment.default.broker}")
    private String environmentDefaultBroker;

    @Value("${backend.jwt.token}")
    private String pulsarJwtToken;

    public PulsarApplicationListener(EnvironmentsRepository environmentsRepository) {
        this.environmentsRepository = environmentsRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Start onApplicationEvent");
        Page<EnvironmentEntity> environmentEntities = environmentsRepository
                .getEnvironmentsList(1, 1);
        if (environmentEntities.getResult().size() <= 0) {
            Optional<EnvironmentEntity> environmentEntityOptional = environmentsRepository
                    .findByName(environmentDefaultName);
            if (environmentDefaultName != null
                    && environmentDefaultBroker != null
                    && environmentDefaultName.length() > 0
                    && environmentDefaultBroker.length() > 0
                    && !environmentEntityOptional.isPresent()) {
                Map<String, String> header = Maps.newHashMap();
                header.put("Content-Type", "application/json");
                if (StringUtils.isNotBlank(pulsarJwtToken)) {
                    header.put("Authorization", String.format("Bearer %s", pulsarJwtToken));
                }
                String httpTestResult = HttpUtil.doGet(environmentDefaultBroker + "/metrics", header);
                if (httpTestResult != null) {
                    EnvironmentEntity environmentEntity = new EnvironmentEntity();
                    environmentEntity.setBroker(environmentDefaultBroker);
                    environmentEntity.setName(environmentDefaultName);
                    environmentsRepository.save(environmentEntity);
                    log.info("The default environment set successful");
                } else {
                    log.error("Please check if the network is not working");
                }
            } else {
                log.warn("The default environment already exists.");
            }
        }
        log.info("The environment already exists.");
    }

}
