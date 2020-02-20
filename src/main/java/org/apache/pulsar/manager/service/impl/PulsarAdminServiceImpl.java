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
package org.apache.pulsar.manager.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminBuilder;
import org.apache.pulsar.client.api.AuthenticationFactory;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.manager.controller.exception.PulsarAdminOperationException;
import org.apache.pulsar.manager.service.PulsarAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

@Service
public class PulsarAdminServiceImpl implements PulsarAdminService {

    private static final Logger log = LoggerFactory.getLogger(PulsarAdminServiceImpl.class);

    @Value("${backend.broker.pulsarAdmin.authPlugin:}")
    private String authPlugin;

    @Value("${backend.broker.pulsarAdmin.authParams:}")
    private String authParams;

    @Value("${backend.broker.pulsarAdmin.tlsAllowInsecureConnection:false}")
    private Boolean tlsAllowInsecureConnection;

    @Value("${backend.broker.pulsarAdmin.tlsTrustCertsFilePath:}")
    private String tlsTrustCertsFilePath;

    @Value("${backend.broker.pulsarAdmin.tlsEnableHostnameVerification:false}")
    private Boolean tlsEnableHostnameVerification;

    @Value("${backend.jwt.token:}")
    private String pulsarJwtToken;

    private Map<String, PulsarAdmin> pulsarAdmins = new HashMap<>();

    @PreDestroy
    public void destroy() {
        pulsarAdmins.values().forEach(value -> value.close());
    }

    public synchronized PulsarAdmin getPulsarAdmin(String url) {
        if (!pulsarAdmins.containsKey(url)) {
            pulsarAdmins.put(url, this.createPulsarAdmin(url));
        }
        return pulsarAdmins.get(url);
    }

    private PulsarAdmin createPulsarAdmin(String url) {
        try {
            log.info("Create Pulsar Admin instance. url={}, authPlugin={}, authParams={}, tlsAllowInsecureConnection={}, tlsTrustCertsFilePath={}, tlsEnableHostnameVerification={}",
                    url, authPlugin, authParams, tlsAllowInsecureConnection, tlsTrustCertsFilePath, tlsEnableHostnameVerification);
            PulsarAdminBuilder pulsarAdminBuilder = PulsarAdmin.builder();
            pulsarAdminBuilder.serviceHttpUrl(url);
            if (StringUtils.isNotBlank(pulsarJwtToken)) {
                pulsarAdminBuilder.authentication(AuthenticationFactory.token(pulsarJwtToken));
            } else {
                pulsarAdminBuilder.authentication(authPlugin, authParams);
            }
            pulsarAdminBuilder.allowTlsInsecureConnection(tlsAllowInsecureConnection);
            pulsarAdminBuilder.tlsTrustCertsFilePath(tlsTrustCertsFilePath);
            pulsarAdminBuilder.enableTlsHostnameVerification(tlsEnableHostnameVerification);
            return pulsarAdminBuilder.build();
        } catch (PulsarClientException e) {
            PulsarAdminOperationException pulsarAdminOperationException = new PulsarAdminOperationException("Failed to create Pulsar Admin instance.");
            log.error(pulsarAdminOperationException.getMessage(), e);
            throw pulsarAdminOperationException;
        }
    }
}
