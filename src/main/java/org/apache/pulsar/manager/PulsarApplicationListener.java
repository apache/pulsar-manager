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

import java.util.Map;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.manager.entity.EnvironmentEntity;
import org.apache.pulsar.manager.entity.EnvironmentsRepository;
import org.apache.pulsar.manager.entity.UserInfoEntity;
import org.apache.pulsar.manager.entity.UsersRepository;
import org.apache.pulsar.manager.service.PulsarAdminService;
import org.apache.pulsar.manager.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.github.pagehelper.Page;

import lombok.extern.slf4j.Slf4j;


/**
 * PulsarApplicationListener do something after the spring framework initialization is complete.
 */
@Component
@Slf4j
public class PulsarApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    private final EnvironmentsRepository environmentsRepository;

    private final PulsarAdminService pulsarAdminService;

    private final UsersRepository usersRepository;

    private final UsersService usersService;

    @Value("${default.environment.name}")
    private String defaultEnvironmentName;

    @Value("${default.environment.service_url}")
    private String defaultEnvironmentServiceUrl;

    @Value("${default.environment.bookie_url}")
    private String defaultEnvironmentBookieUrl;

    @Value("${default.superuser.enabled}")
    private Boolean defaultSuperuserEnabled;

    @Value("${default.superuser.name}")
    private String defaultSuperuserName;

    @Value("${default.superuser.email}")
    private String defaultSuperuserEmail;

    @Value("${default.superuser.password}")
    private String defaultSuperuserPassword;

    @Autowired
    public PulsarApplicationListener(
        EnvironmentsRepository environmentsRepository, 
        PulsarAdminService pulsarAdminService,
        UsersRepository usersRepository,
        UsersService usersService
    ) {
        this.environmentsRepository = environmentsRepository;
        this.pulsarAdminService = pulsarAdminService;
        this.usersRepository = usersRepository;
        this.usersService = usersService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Start onApplicationEvent");
        
        seedDefaultSuperuser();
        seedDefaultEnvironment();
    }

    private void seedDefaultSuperuser() {
        if(defaultSuperuserEnabled == false) {
            log.info("Superuser seed via application.properties is disabled");
            return;
        }
        
        UserInfoEntity userInfoEntity = new UserInfoEntity();
        userInfoEntity.setName(defaultSuperuserName);
        userInfoEntity.setEmail(defaultSuperuserEmail);
        userInfoEntity.setPassword(defaultSuperuserPassword);

        Map<String, String> userValidateResult = usersService.validateUserInfo(userInfoEntity);
        if (userValidateResult.get("error") != null) {
            log.error("Superuser seed failed.", userValidateResult.get("error"));
            System.exit(-1);
        }
        if (StringUtils.isBlank(userInfoEntity.getPassword())) {
            log.error("Superuser seed failed. Password is required.");
            System.exit(-1);
        }
        
        Optional<UserInfoEntity> optionalUserEntity =  usersRepository.findByUserName(userInfoEntity.getName());
        if (optionalUserEntity.isPresent()) {
            log.warn("Superuser already exists.");
            return;
        }

        userInfoEntity.setPassword(DigestUtils.sha256Hex(userInfoEntity.getPassword()));
        usersRepository.save(userInfoEntity);

         log.info("Successfully added a default superuser: name = {}, email = {}, password = {}.",
                        defaultSuperuserName, defaultSuperuserEmail, defaultSuperuserPassword);
    }

    private void seedDefaultEnvironment() {
        Page<EnvironmentEntity> environmentEntities = environmentsRepository.getEnvironmentsList(1, 1);
        
        if (environmentEntities.getResult().size() <= 0) {
            Optional<EnvironmentEntity> environmentEntityOptional = environmentsRepository.findByName(defaultEnvironmentName);
            
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
                environmentEntity.setBookie(defaultEnvironmentBookieUrl);
                environmentEntity.setName(defaultEnvironmentName);
                environmentsRepository.save(environmentEntity);
                log.info("Successfully added a default environment: name = {}, service_url = {},bookie_url = {}.",
                        defaultEnvironmentName, defaultEnvironmentServiceUrl,defaultEnvironmentBookieUrl);
            } else {
                log.warn("The default environment already exists.");
            }
        }

        log.debug("Environments already exist.");
    }
}
