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
import org.apache.pulsar.manager.entity.NamespaceEntity;
import org.apache.pulsar.manager.entity.NamespacesRepository;
import org.apache.pulsar.manager.entity.RoleBindingEntity;
import org.apache.pulsar.manager.entity.RoleBindingRepository;
import org.apache.pulsar.manager.entity.RoleInfoEntity;
import org.apache.pulsar.manager.entity.RolesRepository;
import org.apache.pulsar.manager.entity.TenantEntity;
import org.apache.pulsar.manager.entity.TenantsRepository;
import org.apache.pulsar.manager.entity.UserInfoEntity;
import org.apache.pulsar.manager.entity.UsersRepository;
import org.apache.pulsar.manager.service.ClustersService;
import org.apache.pulsar.manager.service.RolesService;
import org.apache.pulsar.manager.service.TenantsService;
import org.apache.pulsar.manager.utils.ResourceType;
import org.apache.pulsar.manager.utils.ResourceVerbs;
import org.assertj.core.util.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private UsersRepository usersRepository;

    @Autowired
    private TenantsService tenantsService;

    @Autowired
    private TenantsRepository tenantsRepository;

    @Autowired
    private ClustersService clustersService;

    @Autowired
    private RoleBindingRepository roleBindingRepository;

    @Autowired
    private NamespacesRepository namespacesRepository;

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
                long tenantId = tenantsRepository.save(tenantEntity);
                roleInfoEntity.setResourceId(tenantId);
                long roleId = rolesRepository.save(roleInfoEntity);
                RoleBindingEntity roleBindingEntity = new RoleBindingEntity();
                roleBindingEntity.setName(tenant);
                roleBindingEntity.setDescription("This init binding for tenant");
                roleBindingEntity.setRoleId(roleId);
                Optional<UserInfoEntity> userInfoEntity = usersRepository.findByUserName(tenant);
                roleBindingEntity.setUserId(userInfoEntity.get().getUserId());
                roleBindingRepository.save(roleBindingEntity);
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

    public Set<String> getResourceByResourceType(long userId, String resourceType) {
        Page<RoleBindingEntity> roleBindingRepositoryPage = roleBindingRepository.findByUserId(
                1, 1024, userId);
        List<Long> roleIdList = new ArrayList<>();
        roleBindingRepositoryPage.getResult().forEach((r) -> {
            roleIdList.add(r.getRoleId());
        });
        List<RoleInfoEntity> roleInfoEntities = rolesRepository.findAllRolesByMultiId(roleIdList);
        List<Long> resourceIdList = new ArrayList<>();
        for (RoleInfoEntity roleInfoEntity : roleInfoEntities) {
            if (roleInfoEntity.getResourceType().equals(resourceType)) {
                resourceIdList.add(roleInfoEntity.getResourceId());
            }
        }
        Set<String> nameSet = Sets.newHashSet();
        if (ResourceType.TENANTS.name().equals(resourceType)) {
            if (!resourceIdList.isEmpty()) {
                List<TenantEntity> tenantEntities = tenantsRepository.findByMultiId(resourceIdList);
                tenantEntities.forEach((r) -> {
                    nameSet.add(r.getTenant());
                });
            }
        }
        if (ResourceType.NAMESPACES.name().equals(resourceType)) {
            if (!resourceIdList.isEmpty()) {
                List<NamespaceEntity> namespaceEntities = namespacesRepository.findByMultiId(resourceIdList);
                namespaceEntities.forEach((r) -> {
                    nameSet.add(r.getNamespace());
                });
            }
        }
        return nameSet;
    }

    public Set<String> getResourceVerbs(String resourceType) {
        Set<String> verbsSet = Sets.newHashSet();
        if (ResourceType.TENANTS.name().equals(resourceType)) {
            verbsSet.add(ResourceVerbs.ADMIN.name());
        }
        if (ResourceType.NAMESPACES.name().equals(resourceType)) {
            verbsSet.add(ResourceVerbs.ADMIN.name());
            verbsSet.add(ResourceVerbs.CONSUME.name());
            verbsSet.add(ResourceVerbs.PRODUCE.name());
            verbsSet.add(ResourceVerbs.FUNCTION.name());
        }
        if (ResourceType.ALL.name().equals(resourceType)) {
            verbsSet.add(ResourceVerbs.SUPER_USER.name());
            verbsSet.add(ResourceVerbs.ADMIN.name());
            verbsSet.add(ResourceVerbs.CONSUME.name());
            verbsSet.add(ResourceVerbs.PRODUCE.name());
            verbsSet.add(ResourceVerbs.FUNCTION.name());
        }
        return verbsSet;
    }
}
