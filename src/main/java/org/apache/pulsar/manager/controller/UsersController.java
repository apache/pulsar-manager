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
import org.apache.pulsar.manager.entity.UserInfoEntity;
import org.apache.pulsar.manager.entity.UsersRepository;
import org.apache.pulsar.manager.service.UsersService;
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

    private final HttpServletRequest request;

    @Autowired
    public UsersController(
            UsersRepository usersRepository,
            UsersService usersService,
            HttpServletRequest request) {
        this.usersRepository = usersRepository;
        this.usersService = usersService;
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
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        Optional<UserInfoEntity> userInfoEntityOptional = usersRepository.findByAccessToken(token);
        if (!userInfoEntityOptional.isPresent()) {
            result.put("error", "User is no exist");
            return ResponseEntity.ok(result);
        }
        UserInfoEntity userInfoEntity = userInfoEntityOptional.get();
        result.put("message", "Get user info success");
        result.put("userName", userInfoEntity.getName());
        result.put("description", userInfoEntity.getDescription());
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
        Map<String, String> userValidateResult = usersService.validateUserInfo(userInfoEntity);
        if (userValidateResult.get("error") != null) {
            result.put("error", userValidateResult.get("error"));
            return ResponseEntity.ok(result);
        }
        if (StringUtils.isBlank(userInfoEntity.getPassword())) {
            result.put("error", "Please provider password");
            return ResponseEntity.ok(result);
        }

        userInfoEntity.setPassword(DigestUtils.sha256Hex(userInfoEntity.getPassword()));
        usersRepository.save(userInfoEntity);
        result.put("message", "Add super user success, please login");
        return ResponseEntity.ok(result);
    }
}
