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

import org.apache.pulsar.manager.entity.NamespaceEntity;
import org.apache.pulsar.manager.entity.NamespacesRepository;
import org.apache.pulsar.manager.service.PulsarEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class PulsarEventImpl implements PulsarEvent {

    private static final String SEPARATOR = "/";

    private static final String HTTP_PUT = "PUT";

    private static final String HTTP_DELETE = "DELETE";

    private static final String TENANTS_PREFIX = "/admin/v2/tenants/";

    private static final String CLUSTERS_PREFIX = "/admin/v2/clusters";

    private static final String BROKERS_PREFIX = "/admin/v2/brokers";

    private static final String BROKERS_STATS_PREFIX = "/admin/v2/broker-stats";

    private static final String RESOURCES_QUOTAS_PREFIX = "/admin/v2/resource-quotas";

    private static final String NAMESPACES_PREFIX = "/admin/v2/namespaces";

    private static final String PULSAR_MANAGER_NAMESPACES_PREFIX = "/pulsar-manager/admin/v2/namespaces";

    private static final String PERSISTENT_TOPIC_PREFIX = "/admin/v2/persistent";

    private static final String NON_PERSISTENT_TOPIC_PREFIX = "/admin/v2/non-persistent";

    private static final String PULSAR_MANAGER_TOPIC_PREFIX = "/pulsar-manager/admin/v2/topics";

    private static final String PULSAR_MANAGER_PREFIX = "/pulsar-manager";

    @Autowired
    private NamespacesRepository namespacesRepository;

    private boolean isNamespace(String path, HttpServletRequest request) {
        if (request.getMethod().equals(HTTP_PUT) && path.startsWith(NAMESPACES_PREFIX)) {
            return true;
        }
        return false;
    }

    private boolean isNamespace(String path) {
        if (path.startsWith(NAMESPACES_PREFIX) || path.startsWith(PULSAR_MANAGER_NAMESPACES_PREFIX)) {
            return true;
        }
        return false;
    }

    private boolean isTopic(String path) {
        if (path.startsWith(PERSISTENT_TOPIC_PREFIX)
                || path.startsWith(NON_PERSISTENT_TOPIC_PREFIX)
                || path.startsWith(PULSAR_MANAGER_TOPIC_PREFIX)) {
            return true;
        }
        return false;
    }

    private boolean isTenant(String path) {
        if (path.startsWith(TENANTS_PREFIX)) {
            return true;
        }
        return false;
    }

    private boolean isCluster(String path) {
        if (path.startsWith(CLUSTERS_PREFIX)) {
            return true;
        }
        return false;
    }

    private boolean isBroker(String path) {
        if (path.startsWith(BROKERS_PREFIX)) {
            return true;
        }
        return false;
    }

    private boolean isBrokerStats(String path) {
        if (path.startsWith(BROKERS_STATS_PREFIX)) {
            return true;
        }
        return false;
    }

    private boolean isResourceQuota(String path) {
        if (path.startsWith(RESOURCES_QUOTAS_PREFIX)) {
            return true;
        }
        return false;
    }

    public void parsePulsarEvent(String path, HttpServletRequest request) {
        if (isNamespace(path, request)) {
            String[] tenantNamespace = path.split(SEPARATOR);
            NamespaceEntity namespaceEntity = new NamespaceEntity();
            namespaceEntity.setTenant(tenantNamespace[4]);
            namespaceEntity.setNamespace(tenantNamespace[5]);
            namespacesRepository.save(namespaceEntity);
        }
    }
}
