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
package org.apache.pulsar.manager.controller;

import com.google.common.collect.Maps;
import org.apache.pulsar.manager.entity.RoleBindingEntity;
import org.apache.pulsar.manager.entity.RoleBindingRepository;
import org.apache.pulsar.manager.entity.RoleInfoEntity;
import org.apache.pulsar.manager.entity.RolesRepository;
import org.apache.pulsar.manager.entity.TenantEntity;
import org.apache.pulsar.manager.entity.TenantsRepository;
import org.apache.pulsar.manager.entity.UserInfoEntity;
import org.apache.pulsar.manager.entity.UsersRepository;
import org.apache.pulsar.manager.service.EnvironmentCacheService;
import org.apache.pulsar.manager.service.RolesService;
import org.apache.pulsar.manager.service.TenantsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.pulsar.manager.utils.ResourceType;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Tenant Query class.
 */
@RestController
@RequestMapping(value = "/pulsar-manager/admin/v2")
@Api(description = "Support more flexible queries to tenants.")
@Validated
public class TenantsController {

    private final TenantsService tenantsService;

    private final EnvironmentCacheService environmentCacheService;

    private final HttpServletRequest request;

    private final UsersRepository usersRepository;

    private final RoleBindingRepository roleBindingRepository;

    private final RolesRepository rolesRepository;

    private final RolesService rolesService;

    private final TenantsRepository tenantsRepository;

    public TenantsController(
            HttpServletRequest request,
            TenantsService tenantsService,
            EnvironmentCacheService environmentCacheService,
            UsersRepository usersRepository,
            RoleBindingRepository roleBindingRepository,
            RolesRepository rolesRepository,
            RolesService rolesService,
            TenantsRepository tenantsRepository) {
        this.request = request;
        this.tenantsService = tenantsService;
        this.environmentCacheService = environmentCacheService;
        this.usersRepository = usersRepository;
        this.roleBindingRepository = roleBindingRepository;
        this.rolesRepository = rolesRepository;
        this.rolesService = rolesService;
        this.tenantsRepository = tenantsRepository;
    }

    @ApiOperation(value = "Get the list of existing tenants, support paging, the default is 10 per page")
    @ApiResponses({
        @ApiResponse(code = 200, message = "ok"),
        @ApiResponse(code = 404, message = "Not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/tenants", method =  RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getTenants(
        @ApiParam(value = "page_num", defaultValue = "1", example = "1")
        @RequestParam(name = "page_num", defaultValue = "1")
        @Min(value = 1, message = "page_num is incorrect, should be greater than 0.")
            Integer pageNum,
        @ApiParam(value = "page_size", defaultValue = "10", example = "10")
        @RequestParam(name="page_size", defaultValue = "10")
        @Range(min = 1, max = 1000, message = "page_size is incorrect, should be greater than 0 and less than 1000.")
            Integer pageSize) {
        String token = request.getHeader("token");
        Map<String, Object> result = Maps.newHashMap();
        if (!rolesService.isSuperUser(token)) {
            List<TenantEntity> tenantEntities = null;
            Optional<UserInfoEntity> userInfoEntityOptional = usersRepository.findByAccessToken(token);
            // There is no need to check whether the user exists again; the user must exist.
            UserInfoEntity userInfoEntity = userInfoEntityOptional.get();
            long userId = userInfoEntity.getUserId();
            List<RoleBindingEntity> roleBindingInfoEntities = roleBindingRepository.findByUserId(userId);
            List<Long> roleIdList = new ArrayList<>();
            for (RoleBindingEntity roleInfoEntity : roleBindingInfoEntities) {
                roleIdList.add(roleInfoEntity.getRoleId());
            }
            if (!roleIdList.isEmpty()) {
                List<RoleInfoEntity> roleInfoEntities = rolesRepository.findAllRolesByMultiId(roleIdList);
                List<Long> tenantsIdList = new ArrayList<>();
                for (RoleInfoEntity roleInfoEntity : roleInfoEntities) {
                    tenantsIdList.add(roleInfoEntity.getResourceId());
                }
                if (!tenantsIdList.isEmpty()) {
                    tenantEntities = tenantsRepository.findByMultiId(tenantsIdList);
                }
            }
            if (tenantEntities != null) {
                result.put("data", tenantEntities);
                result.put("total", tenantEntities.size());
                result.put("isPage", false);
                return ResponseEntity.ok(result);
            } else {
                result.put("error", "Get tenant error");
                return ResponseEntity.ok(result);
            }
        }
        String requestHost = environmentCacheService.getServiceUrl(request);
        result = tenantsService.getTenantsList(pageNum, pageSize, requestHost);
        return ResponseEntity.ok(result);
    }
}
