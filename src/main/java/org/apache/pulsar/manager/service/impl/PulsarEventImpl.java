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

    private static final String NAMESPACES_PREFIX = "/admin/v2/namespaces";

    @Autowired
    private NamespacesRepository namespacesRepository;

    private boolean isNamespace(String path, HttpServletRequest request) {
        if (request.getMethod().equals(HTTP_PUT) && path.startsWith(NAMESPACES_PREFIX)) {
            return true;
        }
        return false;
    }

    public void parsePulsarEvent(String path, HttpServletRequest request) {
        if (isNamespace(path, request)) {
            String[] tenantNamespace = path.split(SEPARATOR);
            System.out.println(tenantNamespace);
            System.out.println(tenantNamespace[0]);
            System.out.println(tenantNamespace[1]);
            System.out.println(tenantNamespace[2]);
            NamespaceEntity namespaceEntity = new NamespaceEntity();
            System.out.println(tenantNamespace[4]);
            System.out.println(tenantNamespace[5]);
            namespaceEntity.setTenant(tenantNamespace[4]);
            namespaceEntity.setNamespace(tenantNamespace[5]);
            namespacesRepository.save(namespaceEntity);
        }
    }
}
