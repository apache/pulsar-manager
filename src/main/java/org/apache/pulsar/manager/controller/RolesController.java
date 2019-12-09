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
import com.google.common.collect.Sets;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.pulsar.manager.entity.NamespaceEntity;
import org.apache.pulsar.manager.entity.NamespacesRepository;
import org.apache.pulsar.manager.entity.RoleInfoEntity;
import org.apache.pulsar.manager.entity.RolesRepository;
import org.apache.pulsar.manager.service.RolesService;
import org.apache.pulsar.manager.utils.ResourceType;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Roles management controller.
 */
@RestController
@RequestMapping(value = "/pulsar-manager")
@Validated
public class RolesController {

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private RolesService rolesService;

    @Autowired
    private NamespacesRepository namespacesRepository;

    @ApiOperation(value = "Get the list of existing roles, support paging, the default is 10 per page")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/roles", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getRoles(
            @ApiParam(value = "page_num", defaultValue = "1", example = "1")
            @RequestParam(name = "page_num", defaultValue = "1")
            @Min(value = 1, message = "page_num is incorrect, should be greater than 0.")
                    Integer pageNum,
            @ApiParam(value = "page_size", defaultValue = "10", example = "10")
            @RequestParam(name = "page_size", defaultValue = "10")
            @Range(min = 1, max = 1000, message = "page_size is incorrect, should be greater than 0 and less than 1000.")
                    Integer pageSize) {
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        Map<String, Object> result = Maps.newHashMap();
        String tenant = request.getHeader("tenant");
        Map<String, String> validateResult = rolesService.validateCurrentTenant(token, tenant);
        if (validateResult.get("error") != null) {
            result.put("error", validateResult.get("error"));
            return ResponseEntity.ok(result);
        }
        List<RoleInfoEntity> roleInfoLists = rolesRepository.findRolesListByRoleSource(tenant);

        result.put("total", roleInfoLists.size());
        result.put("data", roleInfoLists);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Create a role")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/roles/role", method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> addRole(
            @RequestBody RoleInfoEntity roleInfoEntity) {
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        Map<String, Object> result = Maps.newHashMap();
        String tenant = request.getHeader("tenant");
        Map<String, String> validateResult = rolesService.validateCurrentTenant(token, tenant);
        if (validateResult.get("error") != null) {
            result.put("error", validateResult.get("error"));
            return ResponseEntity.ok(result);
        }

        Optional<RoleInfoEntity> optionalRoleInfoEntity = rolesRepository.findByRoleName(
                roleInfoEntity.getRoleName(), tenant);
        if (optionalRoleInfoEntity.isPresent()) {
            result.put("error", "Failed add a role, role already exists.");
            return ResponseEntity.ok(result);
        }
        /**
         * Set the role flag,
         * 0 for super user, will be initialized when the platform is established, can access all resources.
         * 1 for ordinary users logged in from the platform, can access limited resources.
         */
        roleInfoEntity.setFlag(1);

        Map<String, String> roleInfoEntityValidate = rolesService.validateRoleInfoEntity(roleInfoEntity);
        if (roleInfoEntityValidate.get("error") != null) {
            result.put("error", roleInfoEntityValidate.get("error"));
            return ResponseEntity.ok(result);
        }
        roleInfoEntity.setRoleSource(tenant);
        rolesRepository.save(roleInfoEntity);
        result.put("message", "Create a role success");
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Update a role")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/roles/role", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> updateRole(@RequestBody RoleInfoEntity roleInfoEntity) {

        Map<String, Object> result = Maps.newHashMap();
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        String tenant = request.getHeader("tenant");
        Map<String, String> validateResult = rolesService.validateCurrentTenant(token, tenant);
        if (validateResult.get("error") != null) {
            result.put("error", validateResult.get("error"));
            return ResponseEntity.ok(result);
        }
        Optional<RoleInfoEntity> optionalRoleInfoEntity = rolesRepository.findByRoleName(
                roleInfoEntity.getRoleName(), tenant);
        if (!optionalRoleInfoEntity.isPresent()) {
            result.put("error", "Failed update a role, role does not exist.");
            return ResponseEntity.ok(result);
        }
        RoleInfoEntity roleInfo = optionalRoleInfoEntity.get();
        if (ResourceType.NAMESPACES.name().equals(roleInfoEntity.getResourceType())
                || ResourceType.TOPICS.name().equals(roleInfoEntity.getResourceType())) {
            // More resource type need be added
            Map<String, String> roleInfoEntityValidate = rolesService.validateRoleInfoEntity(roleInfoEntity);
            if (roleInfoEntityValidate.get("error") != null) {
                result.put("error", roleInfoEntityValidate.get("error"));
                return ResponseEntity.ok(result);
            }
            roleInfoEntity.setFlag(roleInfo.getFlag());
            roleInfoEntity.setRoleSource(tenant);
            rolesRepository.update(roleInfoEntity);
            result.put("message", "Update a role success");
            return ResponseEntity.ok(result);
        }
        result.put("error", "Unsupported resource types");
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Delete a role")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/roles/role", method = RequestMethod.DELETE)
    public ResponseEntity<Map<String, Object>> deleteRole(@RequestBody RoleInfoEntity roleInfoEntity) {
        Map<String, Object> result = Maps.newHashMap();
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        String tenant = request.getHeader("tenant");
        Map<String, String> validateResult = rolesService.validateCurrentTenant(token, tenant);
        if (validateResult.get("error") != null) {
            result.put("error", validateResult.get("error"));
            return ResponseEntity.ok(result);
        }
        Optional<RoleInfoEntity> optionalRoleInfoEntity = rolesRepository.findByRoleName(
                roleInfoEntity.getRoleName(), tenant);
        if (!optionalRoleInfoEntity.isPresent()) {
            result.put("error", "Failed delete a role, role does not exist.");
            return ResponseEntity.ok(result);
        }
        // Cancel a permission
        rolesRepository.delete(roleInfoEntity.getRoleName(), tenant);
        result.put("message", "Delete a role success");
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Get resource type")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/role/resourceType", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getResourceType() {
        Map<String, Object> result = Maps.newHashMap();
        Set<String> resourceTypeList = Sets.newHashSet();
        resourceTypeList.add(ResourceType.NAMESPACES.name());
        resourceTypeList.add(ResourceType.TOPICS.name());
        resourceTypeList.add(ResourceType.SCHEMAS.name());
        resourceTypeList.add(ResourceType.FUNCTIONS.name());
        result.put("resourceType", resourceTypeList);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Get resource list by user id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/role/resource/{resourceType}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getResource(@PathVariable String resourceType) {
        Map<String, Object> result = Maps.newHashMap();
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        String tenant = request.getHeader("tenant");
        Map<String, String> validateResult = rolesService.validateCurrentTenant(token, tenant);
        if (validateResult.get("error") != null) {
            result.put("error", validateResult.get("error"));
            return ResponseEntity.ok(result);
        }
        List<NamespaceEntity> namespaceEntities = namespacesRepository.findByTenant(tenant);
        Set<Map<String, Object>> nameSet = Sets.newHashSet();
        for (NamespaceEntity namespaceEntity : namespaceEntities) {
            Map<String, Object> namespace = Maps.newHashMap();
            namespace.put("name", namespaceEntity.getNamespace());
            namespace.put("id", namespaceEntity.getNamespaceId());
            nameSet.add(namespace);
        }
        result.put("data", nameSet);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Get resource verbs by resource type and resource name")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/role/resourceVerbs/{resourceType}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getResourceVerbs(
            @PathVariable String resourceType) {
        Map<String, Object> result = Maps.newHashMap();
        Set<String> verbsSet = rolesService.getResourceVerbs(resourceType);
        result.put("data", verbsSet);
        return ResponseEntity.ok(result);
    }
}
