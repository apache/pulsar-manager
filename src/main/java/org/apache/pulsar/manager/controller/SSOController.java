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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.Asserts;
import org.apache.pulsar.manager.entity.RoleBindingEntity;
import org.apache.pulsar.manager.entity.RoleBindingRepository;
import org.apache.pulsar.manager.entity.RolesRepository;
import org.apache.pulsar.manager.entity.UserInfoEntity;
import org.apache.pulsar.manager.entity.UsersRepository;
import org.apache.pulsar.manager.service.JwtService;
import org.apache.pulsar.manager.utils.SAMLParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/pulsar-manager/saml")
@Api(description = "Calling the request below this class does not require authentication because " +
        "the user has not logged in yet.")
@Validated
public class SSOController {


    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private JwtService jwtService;

    @Resource
    private RolesRepository rolesRepository;

    @Resource
    private RoleBindingRepository roleBindingRepository;

    @Value("${sso.redirect.host}")
    private String redirectHost;

    @Value("${sso.redirect.port}")
    private int redirectPort;

    @Value("${sso.redirect.scheme}")
    private String redirectUrlScheme;

    @Value("${sso.certificate}")
    private String certificate;

    @Value("${sso.whitelisted.domain}")
    private String whiteListedDomain;

    @ApiOperation(value = "Verify SAML Response")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/sso", method = RequestMethod.POST)
    @ResponseBody
    public void loginByIDP(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String samlResponse = request.getParameter("SAMLResponse");
        SAMLParser samlParser = new SAMLParser(certificate);
        Map<String,String> userDetails = samlParser.getUserDetailsFromSAMLResponse(samlResponse);
        Asserts.check(StringUtils.contains(userDetails.get("email"),whiteListedDomain),"Login with domain not supported");
        Optional<UserInfoEntity> userInfoEntity =  usersRepository.findByEmail(userDetails.get("email"));
        if (!userInfoEntity.isPresent()) {
            UserInfoEntity userEntity = new UserInfoEntity();
            userEntity.setName(getUsername(userDetails.get("email")));
            userEntity.setEmail(userDetails.get("email"));
            userEntity.setPassword(DigestUtils.sha256Hex("pulsar"));
            usersRepository.save(userEntity);
            userInfoEntity =  usersRepository.findByEmail(userDetails.get("email"));
            long superUserRoleID = rolesRepository.findByRoleFlag(0).get().getRoleId();
            RoleBindingEntity roleBindingEntity = new RoleBindingEntity();
            roleBindingEntity.setRoleId(superUserRoleID);
            roleBindingEntity.setUserId(userEntity.getUserId());
            roleBindingEntity.setName("Role created after sso signin");
            roleBindingRepository.save(roleBindingEntity);

        }
        UserInfoEntity user = userInfoEntity.get();
        String userAccount = user.getName();
        String password = user.getPassword();
        String token = jwtService.toToken(userAccount + password + System.currentTimeMillis());
        user.setAccessToken(token);
        usersRepository.update(user);
        jwtService.setToken(request.getSession().getId(), token);
        URI uri = new URIBuilder().setScheme(redirectUrlScheme).setHost(redirectHost).setPort(redirectPort).setPath("/login")
                .addParameter("token",token)
                .build();
        response.setStatus(302);
        response.sendRedirect(uri.toString());
    }

    private String getUsername(String email) {
        String userName = StringUtils.split(email,"@")[0];
        return StringUtils.replace(userName,".","");
    }

}

