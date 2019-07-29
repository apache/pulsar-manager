package com.manager.pulsar.controller;

import com.google.common.collect.Maps;
import com.manager.pulsar.service.JwtService;
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

    @Autowired
    private JwtService jwtService;

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
        if (userAccount != account && userPassword != password) {
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
