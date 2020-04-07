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
package org.apache.pulsar.manager.service;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.apache.pulsar.manager.PulsarManagerApplication;
import org.apache.pulsar.manager.entity.UserInfoEntity;
import org.apache.pulsar.manager.profiles.HerdDBTestProfile;
import org.apache.pulsar.manager.utils.HttpUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PowerMockIgnore( {"javax.*", "sun.*", "com.sun.*", "org.xml.*", "org.w3c.*"})
@PrepareForTest(HttpUtil.class)
@SpringBootTest(
        classes = {
                PulsarManagerApplication.class,
                HerdDBTestProfile.class
        }
)
@ActiveProfiles("test")
public class GithubLoginServiceImplTest {

    @Autowired
    private ThirdPartyLoginService thirdPartyLoginService;

    @Value("${github.client.id}")
    private String githubClientId;

    @Value("${github.client.secret}")
    private String githubClientSecret;

    @Value("${github.oauth.host}")
    private String githubAuthHost;

    @Value("${github.user.info}")
    private String githubUserInfo;

    @Test
    public void getAuthTokenTest() throws UnsupportedEncodingException {
        PowerMockito.mockStatic(HttpUtil.class);
        Map<String, String> header = Maps.newHashMap();
        header.put("Content-Type", "application/json");
        header.put("Accept", "application/json");
        Map<String, String> parameters = Maps.newHashMap();

        // Test no code
        String noCodeResult = thirdPartyLoginService.getAuthToken(parameters);
        Assert.assertNull(noCodeResult);

        // Test with code
        parameters.put("code", "test-code");
        Gson gson = new Gson();
        Map<String, String> body = Maps.newHashMap();
        body.put("code", parameters.get("code"));
        body.put("client_id", githubClientId);
        body.put("client_secret", githubClientSecret);
        PowerMockito.when(HttpUtil.doPost(githubAuthHost, header, gson.toJson(body)))
                .thenReturn("{" +
                    "\"access_token\": \"e72e16c7e42f292c6912e7710c838347ae178b4a\"," +
                    "\"scope\": \"repo,gist\"," +
                    "\"token_type\": \"bearer\"" +
                "}");
        String withCodeResult = thirdPartyLoginService.getAuthToken(parameters);
        Assert.assertEquals("e72e16c7e42f292c6912e7710c838347ae178b4a", withCodeResult);
    }

    @Test
    public void getUserInfoTest() {

        Map<String, String> authenticationMap = Maps.newHashMap();
        UserInfoEntity noTokenUserInfoEntity = thirdPartyLoginService.getUserInfo(authenticationMap);

        Assert.assertEquals(null, noTokenUserInfoEntity);

        authenticationMap.put("access_token", "test-user-token");
        PowerMockito.mockStatic(HttpUtil.class);
        Map<String, String> header = Maps.newHashMap();
        header.put("Content-Type", "application/json");
        header.put("Authorization", "token test-user-token");
        PowerMockito.when(HttpUtil.doGet(githubUserInfo, header))
                .thenReturn(null);
        UserInfoEntity withTokenNullUserInfoEntity = thirdPartyLoginService.getUserInfo(authenticationMap);
        Assert.assertNull(withTokenNullUserInfoEntity);
        PowerMockito.when(HttpUtil.doGet(githubUserInfo, header))
                .thenReturn("{\n" +
                        "\t\"login\": \"test1\",\n" +
                        "\t\"company\": bj,\n" +
                        "\t\"location\": \"nw\",\n" +
                        "\t\"email\": \"test@apache.org\",\n" +
                        "\t\"bio\": \"this is description\"" +
                        "}");
        UserInfoEntity withTokenUserInfoEntity = thirdPartyLoginService.getUserInfo(authenticationMap);
        Assert.assertEquals("test@apache.org", withTokenUserInfoEntity.getEmail());
        Assert.assertEquals("test1", withTokenUserInfoEntity.getName());
        Assert.assertEquals("bj", withTokenUserInfoEntity.getCompany());
        Assert.assertEquals("this is description", withTokenUserInfoEntity.getDescription());
        Assert.assertEquals("nw", withTokenUserInfoEntity.getLocation());
        Assert.assertEquals("test-user-token", withTokenUserInfoEntity.getAccessToken());
    }
}
