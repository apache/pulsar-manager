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

import com.github.pagehelper.Page;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.pulsar.manager.entity.EnvironmentEntity;
import org.apache.pulsar.manager.entity.EnvironmentsRepository;
import org.apache.pulsar.manager.entity.RoleInfoEntity;
import org.apache.pulsar.manager.entity.RolesRepository;
import org.apache.pulsar.manager.entity.TenantEntity;
import org.apache.pulsar.manager.entity.TenantsRepository;
import org.apache.pulsar.manager.service.ClustersService;
import org.apache.pulsar.manager.service.RolesService;
import org.apache.pulsar.manager.service.TenantsService;
import org.apache.pulsar.manager.utils.ResourceType;
import org.apache.pulsar.manager.utils.ResourceVerbs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class RolesServiceImpl implements RolesService {

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private EnvironmentsRepository environmentsRepository;

    @Autowired
    private TenantsService tenantsService;

    @Autowired
    private TenantsRepository tenantsRepository;

    @Autowired
    private ClustersService clustersService;

    private final String VERBS_SEPARATOR = ",";

    public Map<String, String> validateRoleInfoEntity(RoleInfoEntity roleInfoEntity) {
        Map<String, String> validateResult = Maps.newHashMap();
        Set<String> resourceVerbs = new HashSet<>(
                Arrays.asList(roleInfoEntity.getResourceVerbs().split(VERBS_SEPARATOR)));
        for (String verb : resourceVerbs) {
            if (!EnumUtils.isValidEnum(ResourceVerbs.class, verb)) {
                validateResult.put("error",
                        "Verb " + verb + "does not belong to this class, Optional (admin, produce, consume, function)");
                return validateResult;
            }
        }
        ResourceType resourceType = ResourceType.valueOf(roleInfoEntity.getResourceType());
        if (resourceType != ResourceType.NAMESPACES && resourceType != ResourceType.ALL) {
            if (resourceType == ResourceType.TOPICS) {
                if (resourceVerbs.contains(ResourceVerbs.ADMIN.name())) {
                    validateResult.put("error", "admin should not be excluded for resources of type topic");
                    return validateResult;
                }
            }
            if (resourceVerbs.contains(ResourceVerbs.PRODUCE.name())
                    || resourceVerbs.contains(ResourceVerbs.CONSUME.name())
                    || resourceVerbs.contains(ResourceVerbs.FUNCTION.name())) {
                validateResult.put("error", "Type " + resourceType + " include not supported verbs");
                return validateResult;
            }
        }
        validateResult.put("message", "Role validate success");
        return validateResult;
    }

    public void createDefaultRoleAndTenant(String tenant) {
        Optional<RoleInfoEntity> roleInfoEntityOptional = rolesRepository.findByRoleName(tenant, tenant);
        if (!roleInfoEntityOptional.isPresent()) {
            // Create role, create default tenant for user
            RoleInfoEntity roleInfoEntity = new RoleInfoEntity();
            roleInfoEntity.setRoleName(tenant);
            roleInfoEntity.setRoleSource(tenant);
            roleInfoEntity.setResourceType(ResourceType.TENANTS.name());
            roleInfoEntity.setResourceName(tenant);
            roleInfoEntity.setResourceVerbs(ResourceVerbs.ADMIN.name());
            roleInfoEntity.setFlag(1);
            try {
                Page<EnvironmentEntity> environmentListPage =
                        environmentsRepository.getEnvironmentsList(1, 1);
                EnvironmentEntity environmentEntity = environmentListPage.get(0);
                String broker = environmentEntity.getBroker();
                List<String> clusterList = clustersService.getClusterByAnyBroker(broker);
                tenantsService.createTenant(tenant, tenant, clusterList.get(0), broker);

                // Cache default tenant
                TenantEntity tenantEntity = new TenantEntity();
                tenantEntity.setTenant(tenant);
                tenantEntity.setAdminRoles(tenant);
                tenantEntity.setAllowedClusters(clusterList.get(0));
                tenantsRepository.save(tenantEntity);
                Optional<TenantEntity> tenantEntityOptional = tenantsRepository.findByName(tenant);
                roleInfoEntity.setResourceId(tenantEntityOptional.get().getTenantId());
                rolesRepository.save(roleInfoEntity);
            } catch (Exception e) {
                /**
                 * TO DO
                 * Send a notification to the administrator so that the administrator can complete subsequent
                 * operations without blocking user login.
                 */
                log.error("Create tenant failed: {}", e.getCause());
            }
        }
    }
}
