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

import javax.annotation.PreDestroy;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;
import org.apache.pulsar.client.admin.BrokerStats;
import org.apache.pulsar.client.admin.Brokers;
import org.apache.pulsar.client.admin.Clusters;
import org.apache.pulsar.client.admin.Namespaces;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminBuilder;
import org.apache.pulsar.client.admin.Tenants;
import org.apache.pulsar.client.admin.Topics;
import org.apache.pulsar.client.api.Authentication;
import org.apache.pulsar.client.api.AuthenticationDataProvider;
import org.apache.pulsar.client.api.AuthenticationFactory;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.manager.controller.exception.PulsarAdminOperationException;
import org.apache.pulsar.manager.service.PulsarAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    public BrokerStats brokerStats(String url) {
        return getPulsarAdmin(url).brokerStats();
    }

    public Clusters clusters(String url) {
        return getPulsarAdmin(url).clusters();
    }

    public Brokers brokers(String url) {
        return getPulsarAdmin(url).brokers();
    }

    public Tenants tenants(String url) {
        return getPulsarAdmin(url).tenants();
    }

    public Namespaces namespaces(String url) {
        return getPulsarAdmin(url).namespaces();
    }

    public Topics topics(String url) {
        return getPulsarAdmin(url).topics();
    }

    public Map<String, String> getAuthHeader(String url) {
        Authentication authentication = getPulsarAdmin(url).getClientConfigData().getAuthentication();
        Map<String, String> result = new HashMap<>();

        try {
            CompletableFuture<Map<String, String>> authFuture = new CompletableFuture<>();
            AuthenticationDataProvider authData = authentication.getAuthData(new URL(url).getHost());
            if (authData.hasDataForHttp()) {
                authentication.authenticationStage(url, authData, null, authFuture);
            } else {
                return result;
            }

            try {
                Map<String, String> responseHeader = authFuture.get();
                Set<Map.Entry<String, String>> headers =
                        authentication.newRequestHeader(url, authData, responseHeader);
                if (headers != null) {
                    headers.forEach(entry -> result.put(entry.getKey(), entry.getValue()));
                }
            } catch (Exception e) {
                log.error("Failed to get headers", e);
            }
        } catch (Exception e) {
            log.error("Failed to run getAuthHeader", e);
        }

        return result;
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
