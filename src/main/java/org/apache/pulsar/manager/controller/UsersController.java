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
import com.google.common.collect.Sets;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.pulsar.manager.entity.RoleBindingEntity;
import org.apache.pulsar.manager.entity.RoleBindingRepository;
import org.apache.pulsar.manager.entity.RoleInfoEntity;
import org.apache.pulsar.manager.entity.RolesRepository;
import org.apache.pulsar.manager.entity.UserInfoEntity;
import org.apache.pulsar.manager.entity.UsersRepository;
import org.apache.pulsar.manager.service.RolesService;
import org.apache.pulsar.manager.service.UsersService;
import org.apache.pulsar.manager.utils.ResourceType;
import org.apache.pulsar.manager.utils.ResourceVerbs;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Users management controller.
 */
@RestController
@RequestMapping(value = "/pulsar-manager")
@Api(description = "Functions under this class are available to super user.")
public class UsersController {

    @Value("${user.management.enable}")
    private boolean userManagementEnable;

    @Value("${pulsar-manager.account}")
    private String account;

    private final UsersRepository usersRepository;

    private final UsersService usersService;

    private final RolesRepository rolesRepository;

    private final RoleBindingRepository roleBindingRepository;

    private final RolesService rolesService;

    private final HttpServletRequest request;

    @Autowired
    public UsersController(
            UsersRepository usersRepository,
            UsersService usersService,
            RolesRepository rolesRepository,
            RoleBindingRepository roleBindingRepository,
            RolesService rolesService,
            HttpServletRequest request) {
        this.usersRepository = usersRepository;
        this.usersService = usersService;
        this.rolesRepository = rolesRepository;
        this.roleBindingRepository = roleBindingRepository;
        this.rolesService = rolesService;
        this.request = request;
    }

    @ApiOperation(value = "Get users list")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getUsers(
            @ApiParam(value = "page_num", defaultValue = "1", example = "1")
            @RequestParam(name = "page_num", defaultValue = "1")
            @Min(value = 1, message = "page_num is incorrect, should be greater than 0.")
            Integer pageNum,
            @ApiParam(value = "page_size", defaultValue = "10", example = "10")
            @RequestParam(name = "page_size", defaultValue = "10")
            @Range(min = 1, max = 1000, message = "page_size is incorrect, should be greater than 0 and less than 1000.")
            Integer pageSize) {
        Page<UserInfoEntity> userInfoEntities = usersRepository.findUsersList(pageNum, pageSize);
        Map<String, Object> result = Maps.newHashMap();
        result.put("total", userInfoEntities.getTotal());
        result.put("data", userInfoEntities.getResult());
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Create user by super user")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/users/user", method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> addUser(
            @RequestBody @Valid UserInfoEntity userInfoEntity) {
        Map<String, Object> result = Maps.newHashMap();
        Map<String, String> validateResult = usersService.validateUserInfo(userInfoEntity);
        if (validateResult.get("error") != null) {
            result.put("error", validateResult.get("error"));
            return ResponseEntity.ok(result);
        }
        Optional<UserInfoEntity> optionalUserEntity =  usersRepository.findByUserName(userInfoEntity.getName());
        if (optionalUserEntity.isPresent()) {
            result.put("error", "User already exist, please check");
            return ResponseEntity.ok(result);
        }
        userInfoEntity.setPassword(DigestUtils.sha256Hex(userInfoEntity.getPassword()));
        usersRepository.save(userInfoEntity);
        // Create default role and tenant
        Map<String, String> defaultRoleCreate = rolesService.createDefaultRoleAndTenant(
                userInfoEntity.getName(), request.getHeader("environment"));
        if (defaultRoleCreate.get("error") != null) {
            result.put("error", defaultRoleCreate.get("error"));
            return ResponseEntity.ok(result);
        }
        result.put("message", "Create user success");
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Update user by super user")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/users/user", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> updateUser(@RequestBody @Valid UserInfoEntity userInfoEntity) {
        Map<String, Object> result = Maps.newHashMap();
        Optional<UserInfoEntity> userInfoEntityOptional = usersRepository.findByUserName(userInfoEntity.getName());
        if (!userInfoEntityOptional.isPresent()) {
            result.put("error", "Failed update a user, user does not exist");
            return ResponseEntity.ok(result);
        }
        if (StringUtils.isBlank(userInfoEntity.getEmail())) {
            result.put("error", "Failed update a user, email is not be empty");
            return ResponseEntity.ok(result);
        }
        if (!EmailValidator.getInstance().isValid(userInfoEntity.getEmail())) {
            result.put("error", "Email address illegal");
            return ResponseEntity.ok(result);
        }
        UserInfoEntity existUerInfoEntity = userInfoEntityOptional.get();
        userInfoEntity.setPassword(existUerInfoEntity.getPassword());
        userInfoEntity.setAccessToken(existUerInfoEntity.getAccessToken());
        usersRepository.update(userInfoEntity);
        result.put("message", "Update a user success");
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Delete a user by super user")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/users/user", method = RequestMethod.DELETE)
    public ResponseEntity<Map<String, Object>> deleteUser(@RequestBody UserInfoEntity userInfoEntity) {
        Map<String, Object> result = Maps.newHashMap();
        Optional<UserInfoEntity> userInfoEntityOptional = usersRepository.findByUserName(userInfoEntity.getName());
        if (!userInfoEntityOptional.isPresent()) {
            result.put("error", "Failed update a user, user does not exist");
            return ResponseEntity.ok(result);
        }
        usersRepository.delete(userInfoEntity.getName());
        result.put("message", "Delete a user success");
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Get user info")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/users/userInfo", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getUserInfo() {
        Map<String, Object> result = Maps.newHashMap();
        Set<String> roles  = Sets.newHashSet();
        if (userManagementEnable) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("token");
            Optional<UserInfoEntity> userInfoEntityOptional = usersRepository.findByAccessToken(token);
            if (!userInfoEntityOptional.isPresent()) {
                result.put("error", "User is no exist");
                return ResponseEntity.ok(result);
            }
            UserInfoEntity userInfoEntity = userInfoEntityOptional.get();
            List<RoleBindingEntity> roleBindingEntities = roleBindingRepository.findByUserId(userInfoEntity.getUserId());
            List<Long> roleIdList = new ArrayList<>();
            for (RoleBindingEntity roleBindingEntity : roleBindingEntities) {
                roleIdList.add(roleBindingEntity.getRoleId());
            }
            List<RoleInfoEntity> roleInfoEntities = rolesRepository.findAllRolesByMultiId(roleIdList);
            for (RoleInfoEntity roleInfoEntity : roleInfoEntities) {
                if (roleInfoEntity.getFlag() == 0) {
                    result.put("message", "Get user info success");
                    result.put("userName", userInfoEntity.getName());
                    result.put("description", userInfoEntity.getDescription());
                    roles.add("super");
                    result.put("roles", roles);
                    return ResponseEntity.ok(result);
                }
            }
            result.put("message", "Get user info success");
            result.put("userName", userInfoEntity.getName());
            result.put("description", userInfoEntity.getDescription());
            roles.add("admin");
            result.put("roles", roles);
            return ResponseEntity.ok(result);
        }
        result.put("message", "Get user info success");
        result.put("description", "This is super account");
        result.put("userName", account);
        roles.add("super");
        result.put("roles", roles);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Add a super user, only used when the platform is initialized for the first time.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/users/superuser", method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> createSuperUser(@RequestBody UserInfoEntity userInfoEntity) {
        Map<String, Object> result = Maps.newHashMap();
        // 0 is super role
        Optional<RoleInfoEntity> roleInfoEntityOptional = rolesRepository.findByRoleFlag(0);
        if (roleInfoEntityOptional.isPresent()) {
            result.put("error", "Super user role is exist, this interface is no longer available");
            return ResponseEntity.ok(result);
        }
        Map<String, String> userValidateResult = usersService.validateUserInfo(userInfoEntity);
        if (userValidateResult.get("error") != null) {
            result.put("error", userValidateResult.get("error"));
            return ResponseEntity.ok(result);
        }
        if (StringUtils.isBlank(userInfoEntity.getPassword())) {
            result.put("error", "Please provider password");
            return ResponseEntity.ok(result);
        }

        RoleInfoEntity roleInfoEntity = new RoleInfoEntity();
        roleInfoEntity.setRoleName(userInfoEntity.getName());
        roleInfoEntity.setResourceId(0);
        roleInfoEntity.setRoleSource(roleInfoEntity.getRoleName());
        roleInfoEntity.setResourceType(ResourceType.ALL.name());
        roleInfoEntity.setResourceName("superuser");
        roleInfoEntity.setResourceVerbs(ResourceVerbs.SUPER_USER.name());
        roleInfoEntity.setFlag(0);
        roleInfoEntity.setDescription("This is super role");
        long roleId = rolesRepository.save(roleInfoEntity);
        userInfoEntity.setPassword(DigestUtils.sha256Hex(userInfoEntity.getPassword()));
        long userId = usersRepository.save(userInfoEntity);
        RoleBindingEntity roleBindingEntity = new RoleBindingEntity();
        roleBindingEntity.setDescription("This is super role binding");
        roleBindingEntity.setName("super_user_role_binding");
        roleBindingEntity.setRoleId(roleId);
        roleBindingEntity.setUserId(userId);
        roleBindingRepository.save(roleBindingEntity);
        result.put("message", "Add super user success, please login");
        return ResponseEntity.ok(result);
    }
}
