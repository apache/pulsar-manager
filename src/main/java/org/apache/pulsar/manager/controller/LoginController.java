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
import org.apache.pulsar.manager.service.JwtService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
import java.util.Map;

/**
 * User login and logout rest api.
 */
@RequestMapping(value = "/pulsar-manager")
@Api(description = "Support user login and logout.")
@Validated
@RestController
public class LoginController {

    @Value("${pulsar-manager.account}")
    private String account;

    @Value("${pulsar-manager.password}")
    private String password;

    private final JwtService jwtService;

    @Autowired
    public LoginController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

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
        if (userAccount.equals(account) && userPassword.equals(password)) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = jwtService.toToken(account + "-" + password);
            result.put("login", "success");
            HttpHeaders headers = new HttpHeaders();
            headers.add("token", token);
            jwtService.setToken(request.getSession().getId(), token);
            return new ResponseEntity(result, headers, HttpStatus.OK);
        }
        result.put("login", "error");
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
        result.put("logout", "success");
        jwtService.removeToken(request.getSession().getId());
        return ResponseEntity.ok(result);
    }
}
