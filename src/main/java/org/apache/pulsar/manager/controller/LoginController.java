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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.pulsar.manager.entity.RoleBindingEntity;
import org.apache.pulsar.manager.entity.RoleBindingRepository;
import org.apache.pulsar.manager.entity.RoleInfoEntity;
import org.apache.pulsar.manager.entity.RolesRepository;
import org.apache.pulsar.manager.entity.UserInfoEntity;
import org.apache.pulsar.manager.entity.UsersRepository;
import org.apache.pulsar.manager.service.JwtService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.pulsar.manager.service.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * User login and logout rest api.
 */
@Slf4j
@RequestMapping(value = "/pulsar-manager")
@Api(description = "Support user login and logout.")
@Validated
@RestController
public class LoginController {

    @Value("${user.management.enable}")
    private boolean userManagementEnable;

    @Value("${pulsar-manager.account}")
    private String account;

    @Value("${pulsar-manager.password}")
    private String password;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RolesService rolesService;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private RoleBindingRepository roleBindingRepository;

    @ApiOperation(value = "Login pulsar manager")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/login", method =  RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> body) {
        String userAccount = body.get("username");
        String userPassword = body.get("password");
        Map<String, Object> result = Maps.newHashMap();
        HttpHeaders headers = new HttpHeaders();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (userManagementEnable) {
            Optional<UserInfoEntity> userInfoEntityOptional = usersRepository.findByUserName(userAccount);
            if (!userInfoEntityOptional.isPresent()) {
                result.put("error", "The user is not exist");
                return ResponseEntity.ok(result);
            }
            UserInfoEntity userInfoEntity = userInfoEntityOptional.get();
            String password = DigestUtils.sha256Hex(userPassword);
            if (!password.equals(userInfoEntity.getPassword())) {
                result.put("error", "The user name or password not incorrect");
                return ResponseEntity.ok(result);
            }
            String token = jwtService.toToken(userAccount + password + System.currentTimeMillis());
            userInfoEntity.setAccessToken(token);
            usersRepository.update(userInfoEntity);
            result.put("login", "success");
            headers.add("token", token);
            headers.add("username", userAccount);
            headers.add("tenant", userAccount);
            jwtService.setToken(request.getSession().getId(), token);
            List<RoleBindingEntity> roleBindingEntities = roleBindingRepository.
                    findByUserId(userInfoEntity.getUserId());
            List<Long> roleIdList = new ArrayList<>();
            for (RoleBindingEntity roleBindingEntity : roleBindingEntities) {
                roleIdList.add(roleBindingEntity.getRoleId());
            }
            if (!roleIdList.isEmpty()) {
                List<RoleInfoEntity> roleInfoEntities = rolesRepository.findAllRolesByMultiId(roleIdList);
                for (RoleInfoEntity roleInfoEntity : roleInfoEntities) {
                    if (roleInfoEntity.getFlag() == 0) {
                        // Super users can access all types
                        return new ResponseEntity<>(result, headers, HttpStatus.OK);
                    }
                }
            }
            headers.add("role", "admin");
            return new ResponseEntity<>(result, headers, HttpStatus.OK);
        }
        if (userAccount.equals(account) && userPassword.equals(password)) {
            String token = jwtService.toToken(account + "-" + password);
            result.put("login", "success");
            headers.add("token", token);
            headers.add("username", userAccount);
            headers.add("tenant", userAccount);
            jwtService.setToken(request.getSession().getId(), token);
            return new ResponseEntity<>(result, headers, HttpStatus.OK);
        }
        result.put("error", "error");
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Logout pulsar manager")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/logout", method =  RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> result = Maps.newHashMap();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String username = request.getHeader("username");
        if (userManagementEnable) {
            usersRepository.findByUserName(username);
            Optional<UserInfoEntity> userInfoEntityOptional = usersRepository.findByUserName(username);
            if (!userInfoEntityOptional.isPresent()) {
                result.put("login", "The user is not exist");
                return ResponseEntity.ok(result);
            }
            UserInfoEntity userInfoEntity = userInfoEntityOptional.get();
            userInfoEntity.setAccessToken("");
            usersRepository.update(userInfoEntity);
        }
        result.put("logout", "success");
        jwtService.removeToken(request.getSession().getId());
        return ResponseEntity.ok(result);
    }
}
