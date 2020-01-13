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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.pulsar.manager.entity.RoleBindingEntity;
import org.apache.pulsar.manager.entity.RoleBindingRepository;
import org.apache.pulsar.manager.entity.UserInfoEntity;
import org.apache.pulsar.manager.entity.UsersRepository;
import org.apache.pulsar.manager.service.RoleBindingService;
import org.apache.pulsar.manager.service.RolesService;
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

/**
 * Role binding Query class.
 */
@RestController
@RequestMapping(value = "/pulsar-manager")
@Api(description = "Support more flexible queries to role bind.")
@Validated
public class RoleBindingController {

    @Autowired
    private RoleBindingRepository roleBindingRepository;

    @Autowired
    private RolesService rolesService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RoleBindingService roleBindingService;

    @ApiOperation(value = "Get the list of role binding")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/role-binding", method =  RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getRoleBingList(
            @ApiParam(value = "page_num", defaultValue = "1", example = "1")
            @RequestParam(name = "page_num", defaultValue = "1")
            @Min(value = 1, message = "page_num is incorrect, should be greater than 0.")
                    Integer pageNum,
            @ApiParam(value = "page_size", defaultValue = "10", example = "10")
            @RequestParam(name="page_size", defaultValue = "10")
            @Range(min = 1, max = 1000, message = "page_size is incorrect, should be greater than 0 and less than 1000.")
                    Integer pageSize) {
        Map<String, Object> result = Maps.newHashMap();
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        String tenant = request.getHeader("tenant");
        if (rolesService.isSuperUser(token)) {
            List<Map<String, Object>> roleBindingList = roleBindingService.getAllRoleBindingList();
            result.put("total", roleBindingList.size());
            result.put("data", roleBindingList);
            return ResponseEntity.ok(result);
        }
        Map<String, String> validateResult = rolesService.validateCurrentTenant(token, tenant);
        if (validateResult.get("error") != null) {
            result.put("error", validateResult.get("error"));
            return ResponseEntity.ok(result);
        }
        List<Map<String, Object>> userRoleInfo = roleBindingService.getRoleBindingList(token, tenant);
        result.put("total", userRoleInfo.size());
        result.put("data", userRoleInfo);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Create a role binding")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/role-binding/{roleName}/{userName}", method =  RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> addRoleBinding(
            @PathVariable("roleName") String roleName,
            @PathVariable("userName") String userName,
            @RequestBody RoleBindingEntity roleBindingEntity) {
        Map<String, Object> result = Maps.newHashMap();
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        String tenant = request.getHeader("tenant");
        Map<String, Object> validateResult = roleBindingService.validateCreateRoleBinding(
                token, tenant, roleName, userName);
        if (validateResult.get("error") != null) {
            result.put("error", validateResult.get("error"));
            return ResponseEntity.ok(result);
        }
        /**
         * To do
         * If the bound user is not himself, the platform needs to send a notification to the other party,
         * and the other party agrees to decide whether to bind or not.
         */
        roleBindingEntity.setRoleId((Long) validateResult.get("roleId"));
        roleBindingEntity.setUserId((Long) validateResult.get("userId"));
        roleBindingRepository.save(roleBindingEntity);
        result.put("message", "Role binding create success");
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Create a role binding")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/role-binding/{roleName}/{userName}", method =  RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> updateRoleBinding(
            @PathVariable String roleName,
            @PathVariable String userName,
            @RequestBody RoleBindingEntity roleBindingEntity) {
        Map<String, Object> result = Maps.newHashMap();
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        Map<String, String> stringMap = roleBindingService.validateCurrentUser(token, roleBindingEntity);
        if (stringMap.get("error") != null) {
            result.put("error", stringMap.get("error"));
            return ResponseEntity.ok(result);
        }
        // check old role binding
        Optional<RoleBindingEntity> oldRoleBindingEntityOptional = roleBindingRepository.findByUserIdAndRoleId(
                roleBindingEntity.getUserId(), roleBindingEntity.getRoleId());
        if (!oldRoleBindingEntityOptional.isPresent()) {
            result.put("error", "Update failed, role binding no exist");
            return ResponseEntity.ok(result);
        }

        Optional<UserInfoEntity> checkUserInfoEntityOptional = usersRepository.findByUserName(userName);
        if (!checkUserInfoEntityOptional.isPresent()) {
            result.put("error", "User no exist.");
            return ResponseEntity.ok(result);
        }
        UserInfoEntity checkUserInfoEntity = checkUserInfoEntityOptional.get();
        // check new role biding
        Optional<RoleBindingEntity> newRoleBindingEntityOptional = roleBindingRepository.findByUserIdAndRoleId(
                checkUserInfoEntity.getUserId(), roleBindingEntity.getRoleId());
        if (newRoleBindingEntityOptional.isPresent()) {
            result.put("error", "This role binding is exist");
            return ResponseEntity.ok(result);
        }
        roleBindingEntity.setRoleBindingId(oldRoleBindingEntityOptional.get().getRoleBindingId());

        roleBindingEntity.setUserId(checkUserInfoEntity.getUserId());
        roleBindingRepository.update(roleBindingEntity);
        result.put("message", "Role binding update success");
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Delete a role binding")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/role-binding", method =  RequestMethod.DELETE)
    public ResponseEntity<Map<String, Object>> deleteRoleBinding(@RequestBody RoleBindingEntity roleBindingEntity) {
        Map<String, Object> result = Maps.newHashMap();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        Map<String, String> stringMap = roleBindingService.validateCurrentUser(token, roleBindingEntity);
        if (stringMap.get("error") != null) {
            result.put("error", stringMap.get("error"));
            return ResponseEntity.ok(result);
        }
        Optional<RoleBindingEntity> roleBindingEntityOptional = roleBindingRepository.findByUserIdAndRoleId(
                roleBindingEntity.getUserId(), roleBindingEntity.getRoleId());
        if (!roleBindingEntityOptional.isPresent()) {
            result.put("error", "This role binding no exist");
            return ResponseEntity.ok(result);
        }
        roleBindingRepository.delete(roleBindingEntity.getRoleId(), roleBindingEntity.getUserId());
        result.put("message", "Delete role binding success");
        return ResponseEntity.ok(result);
    }
}
