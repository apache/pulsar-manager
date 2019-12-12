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
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

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

    private static final Pattern pattern = Pattern.compile("[A-Za-z0-9_]+");

    public Map<String, String> validateRoleInfoEntity(RoleInfoEntity roleInfoEntity) {
        Map<String, String> validateResult = Maps.newHashMap();

        if (StringUtils.isBlank(roleInfoEntity.getRoleName())) {
            validateResult.put("error", "Role name cannot be empty");
            return validateResult;
        }

        if (StringUtils.isBlank(roleInfoEntity.getResourceName())) {
            validateResult.put("error", "Resource name cannot be empty");
            return validateResult;
        }

        if (!(pattern.matcher(roleInfoEntity.getRoleName()).matches())) {
            validateResult.put("error", "Role name is illegal");
            return validateResult;
        }

        if (!(pattern.matcher(roleInfoEntity.getResourceName()).matches())) {
            validateResult.put("error", "Resource Name is illegal");
            return validateResult;
        }

        if (!EnumUtils.isValidEnum(ResourceType.class, roleInfoEntity.getResourceType())) {
            validateResult.put("error", "Resource type is illegal");
            return validateResult;
        }

        if (ResourceType.TENANTS.name().equals(roleInfoEntity.getResourceType())) {
            Optional<TenantEntity> tenantEntity = tenantsRepository.findByTenantId(roleInfoEntity.getResourceId());
            if (!tenantEntity.isPresent()) {
                validateResult.put("error", "Tenant no exist, please check");
                return validateResult;
            }
        }

        if (ResourceType.NAMESPACES.name().equals(roleInfoEntity.getResourceType())) {
            Optional<NamespaceEntity> namespaceEntity = namespacesRepository.findByNamespaceId(
                    roleInfoEntity.getResourceId());
            if (!namespaceEntity.isPresent()) {
                validateResult.put("error", "Namespace no exist, please check");
                return validateResult;
            }
        }
        Set<String> resourceVerbs = new HashSet<>(
                Arrays.asList(roleInfoEntity.getResourceVerbs().split(VERBS_SEPARATOR)));
        for (String verb : resourceVerbs) {
            if (!EnumUtils.isValidEnum(ResourceVerbs.class, verb)) {
                validateResult.put("error",
                        "Verb " + verb + "does not belong to this class, Optional (ADMIN, PRODUCE, CONSUME, FUNCTION)");
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

    public Map<String, String> validateCurrentTenant(String token, String tenant) {
        Map<String, String> result = Maps.newHashMap();
        Optional<UserInfoEntity> userInfoEntityOptional = usersRepository.findByAccessToken(token);
        if (!userInfoEntityOptional.isPresent()) {
            result.put("error", "User no exist.");
            return result;
        }
        UserInfoEntity userInfoEntity = userInfoEntityOptional.get();
        List<RoleBindingEntity> roleBindingEntities = roleBindingRepository.findByUserId(userInfoEntity.getUserId());
        List<Long> roleIdList = new ArrayList<>();
        for (RoleBindingEntity roleBindingEntity : roleBindingEntities) {
            roleIdList.add(roleBindingEntity.getRoleId());
        }
        List<RoleInfoEntity> roleInfoEntities = rolesRepository.findAllRolesByMultiId(roleIdList);
        List<Long> resourceIdList = new ArrayList<>();
        for (RoleInfoEntity infoEntity : roleInfoEntities) {
            resourceIdList.add(infoEntity.getResourceId());
        }
        List<TenantEntity> tenantEntities = tenantsRepository.findByMultiId(resourceIdList);
        Set<String> tenantNameList = Sets.newHashSet();
        for (TenantEntity tenantEntity : tenantEntities) {
            tenantNameList.add(tenantEntity.getTenant());
        }
        if (!tenantNameList.contains(tenant)) {
            result.put("error", "This user no include this tenant");
            return result;
        }
        result.put("message", "Validate tenant success");
        return result;
    }
}
