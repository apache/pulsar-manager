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
package org.apache.pulsar.manager.service.impl;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.manager.entity.GithubAuthEntity;
import org.apache.pulsar.manager.entity.GithubUserInfoEntity;
import org.apache.pulsar.manager.entity.UserInfoEntity;
import org.apache.pulsar.manager.service.ThirdPartyLoginService;
import org.apache.pulsar.manager.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Github login for get user information.
 */
@Slf4j
@Service
public class GithubLoginServiceImpl implements ThirdPartyLoginService {

    @Value("${github.client.id}")
    private String githubClientId;

    @Value("${github.client.secret}")
    private String githubClientSecret;

    @Value("${github.oauth.host}")
    private String githubAuthHost;

    @Value("${github.user.info}")
    private String githubUserInfo;

    /**
     * Get user access token from github.
     * @param parameters For get code to github.
     *          GitHub redirects back to local site with a temporary code in a code parameter as well as the state
     *          you provided in the previous step in a state parameter.The temporary code will expire after 10 minutes.
     * @return access_token of string type
     */
    public String getAuthToken(Map<String, String> parameters) {
        Map<String, String> header = Maps.newHashMap();
        header.put("Content-Type", "application/json");
        header.put("Accept", "application/json");
        Map<String, Object> body = Maps.newHashMap();
        body.put("client_id", githubClientId);
        body.put("client_secret", githubClientSecret);
        if (!parameters.containsKey("code")) {
            log.error("Parameter does not contain code field, which is illegal.");
            return null;
        }
        body.put("code", parameters.get("code"));
        Gson gson = new Gson();
        try {
            // result example: access_token=your-token&token_type=bearer
            String result = HttpUtil.doPost(githubAuthHost, header, gson.toJson(body));
            GithubAuthEntity githubAuthEntity = gson.fromJson(result, GithubAuthEntity.class);
            log.info("Success get access token from github");
            return githubAuthEntity.getAccessToken();
        } catch (UnsupportedEncodingException e) {
            log.error("Failed get access token from github, error stack: {}", e.getCause());
            return null;
        }
    }

    /**
     * Get user information from github by access token.
     * @param authenticationMap Authentication mark requesting user information.
     * @return UserInfoEntity
     */
    public UserInfoEntity getUserInfo(Map<String, String> authenticationMap) {
        Map<String, String> header = Maps.newHashMap();
        header.put("Content-Type", "application/json");
        if (!authenticationMap.containsKey("access_token")) {
            log.error("The authenticationMap does not contain access_token field, which is illegal.");
            return null;
        }
        header.put("Authorization", "token " + authenticationMap.get("access_token"));
        String result = HttpUtil.doGet(githubUserInfo, header);
        Gson gson = new Gson();
        GithubUserInfoEntity githubUserInfoEntity = gson.fromJson(result, GithubUserInfoEntity.class);
        if (githubUserInfoEntity == null) {
            log.error("Get user information from github failed.");
            return null;
        }
        UserInfoEntity userInfoEntity = new UserInfoEntity();
        userInfoEntity.setCompany(githubUserInfoEntity.getCompany());
        // User 'login' field of github as platform name, because name field of github often empty.
        // Github's name field is more like an alias.
        userInfoEntity.setName(githubUserInfoEntity.getLogin());
        userInfoEntity.setDescription(githubUserInfoEntity.getBio());
        userInfoEntity.setEmail(githubUserInfoEntity.getEmail());
        userInfoEntity.setLocation(githubUserInfoEntity.getLocation());
        userInfoEntity.setAccessToken(authenticationMap.get("access_token"));
        return userInfoEntity;
    }
}
