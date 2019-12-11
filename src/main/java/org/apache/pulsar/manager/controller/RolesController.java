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


import com.github.pagehelper.Page;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.pulsar.manager.entity.RoleInfoEntity;
import org.apache.pulsar.manager.entity.RolesRepository;
import org.apache.pulsar.manager.entity.UserInfoEntity;
import org.apache.pulsar.manager.entity.UsersRepository;
import org.apache.pulsar.manager.service.RolesService;
import org.apache.pulsar.manager.utils.ResourceType;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Map;
import java.util.Optional;

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
    private UsersRepository usersRepository;

    @Autowired
    private RolesService rolesService;

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
        Page<RoleInfoEntity> roleInfoList = rolesRepository.findRolesList(pageNum, pageSize);
        Map<String, Object> result = Maps.newHashMap();
        result.put("total", roleInfoList.getTotal());
        result.put("data", roleInfoList.getResult());
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
            @RequestBody RoleInfoEntity roleInfoEntity
    ) {
        Optional<RoleInfoEntity> optionalRoleInfoEntity = rolesRepository.findByRoleName(
                roleInfoEntity.getRoleName(), roleInfoEntity.getRoleSource());
        Map<String, Object> result = Maps.newHashMap();
        if (optionalRoleInfoEntity.isPresent()) {
            result.put("error", "Failed add a role, role already exists.");
            return ResponseEntity.ok(result);
        }
        /**
         * Set the role flag, 0 for super user, will be initialized when the platform is established, can access all
         * resources. 1 for ordinary users logged in from the platform, can access limited resources.
         */
        roleInfoEntity.setFlag(1);
        if (roleInfoEntity.getRoleName().isEmpty() || !roleInfoEntity.getRoleSource().isEmpty()) {
            result.put("error", "Role name and role source cannot be empty.");
            return ResponseEntity.ok(result);
        }
        Optional<UserInfoEntity> userInfoEntityOptional = usersRepository.findByUserName(
                roleInfoEntity.getRoleSource());
        if (!userInfoEntityOptional.isPresent()) {
            result.put("error", "User does not exist, please check.");
            return ResponseEntity.ok(result);
        }
        Map<String, String> roleInfoEntityValidate = rolesService.validateRoleInfoEntity(roleInfoEntity);
        if (roleInfoEntityValidate.get("error") != null) {
            result.put("error", roleInfoEntityValidate.get("error"));
            return ResponseEntity.ok(result);
        }
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
        Optional<RoleInfoEntity> optionalRoleInfoEntity = rolesRepository.findByRoleName(
                roleInfoEntity.getRoleName(), roleInfoEntity.getRoleSource());
        Map<String, Object> result = Maps.newHashMap();
        if (!optionalRoleInfoEntity.isPresent()) {
            result.put("error", "Failed update a role, role does not exist.");
            return ResponseEntity.ok(result);
        }
        Map<String, String> roleInfoEntityValidate = rolesService.validateRoleInfoEntity(roleInfoEntity);
        if (roleInfoEntityValidate.get("error") != null) {
            result.put("error", roleInfoEntityValidate.get("error"));
            return ResponseEntity.ok(result);
        }
        rolesRepository.update(roleInfoEntity);
        result.put("message", "Update a role success");
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
        Optional<RoleInfoEntity> optionalRoleInfoEntity = rolesRepository.findByRoleName(
                roleInfoEntity.getRoleName(), roleInfoEntity.getRoleSource());
        Map<String, Object> result = Maps.newHashMap();
        if (!optionalRoleInfoEntity.isPresent()) {
            result.put("error", "Failed update a role, role does not exist.");
            return ResponseEntity.ok(result);
        }
        // Cancel a permission
        rolesRepository.delete(roleInfoEntity.getRoleName(), roleInfoEntity.getRoleSource());
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
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String username = request.getHeader("username");
        Optional<UserInfoEntity> userInfoEntityOptional = usersRepository.findByUserName(username);
        result.put("resourceType", ResourceType.values());
        return ResponseEntity.ok(result);
    }
}
