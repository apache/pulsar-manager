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
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.pulsar.manager.entity.UserInfoEntity;
import org.apache.pulsar.manager.service.ThirdLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Callback function of third party platform login.
 */
@Controller
@RequestMapping(value = "/pulsar-manager/third-login")
@Validated
public class ThirdLoginCallbackController {

    @Value("${github.client.id}")
    private String githubClientId;

    @Value("${github.login.host}")
    private String githubLoginHost;

    @Value("${github.redirect.host}")
    private String githubRedirectHost;

    @Autowired
    private ThirdLoginService thirdLoginService;

    @ApiOperation(value = "Github callback.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/callback/github")
    public String GithubCallbackIndex(Model model, @RequestParam() String code) {
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("code", code);
        String accessToken = thirdLoginService.getAuthToken(parameters);
        Map<String, String> authenticationMap = Maps.newHashMap();
        authenticationMap.put("access_token", accessToken);
        UserInfoEntity userInfoEntity = thirdLoginService.getUserInfo(authenticationMap);
        if (userInfoEntity == null) {
            model.addAttribute("messages", "Authentication failed, please check carefully");
            model.addAttribute("flag", false);
            return "index";
        }
        model.addAttribute("message", "Authentication successful, logging in");
        model.addAttribute("flag", true);
        model.addAttribute("userInfo", userInfoEntity);
        return "index";
    }

    @ApiOperation(value = "Github login address.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/github/login", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<Map<String, Object>> getGithubLoginUrl() {
        Map<String, Object> result = Maps.newHashMap();
        String url = githubLoginHost + "?client_id=" + githubClientId +
                "&redirect_host=" + githubRedirectHost + "/pulsar-manager/third-login/callback/github";
        result.put("url", url);
        return ResponseEntity.ok(result);
    }
}
