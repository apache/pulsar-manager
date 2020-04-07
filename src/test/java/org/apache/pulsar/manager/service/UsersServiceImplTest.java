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

import org.apache.pulsar.manager.PulsarManagerApplication;
import org.apache.pulsar.manager.entity.UserInfoEntity;
import org.apache.pulsar.manager.profiles.HerdDBTestProfile;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PowerMockIgnore( {"javax.*", "sun.*", "com.sun.*", "org.xml.*", "org.w3c.*"})
@SpringBootTest(
        classes = {
                PulsarManagerApplication.class,
                HerdDBTestProfile.class
        }
)
@ActiveProfiles("test")
public class UsersServiceImplTest {

    @Autowired
    private UsersService usersService;

    @Test
    public void validateUserInfoTest() {
        UserInfoEntity userInfoEntity = new UserInfoEntity();
        userInfoEntity.setName(" ");
        Map<String, String> validateNameEmpty = usersService.validateUserInfo(userInfoEntity);
        Assert.assertEquals("User name cannot be empty", validateNameEmpty.get("error"));
        userInfoEntity.setName("----====");
        Map<String, String> validateNameIllegal = usersService.validateUserInfo(userInfoEntity);
        Assert.assertEquals("User name illegal", validateNameIllegal.get("error"));

        userInfoEntity.setName("test");
        userInfoEntity.setEmail("  ");
        Map<String, String> validateEmailEmpty = usersService.validateUserInfo(userInfoEntity);
        Assert.assertEquals("User email cannot be empty", validateEmailEmpty.get("error"));
        userInfoEntity.setEmail("xxxx@");
        Map<String, String> validateEmailIllegal = usersService.validateUserInfo(userInfoEntity);
        Assert.assertEquals("Email address illegal", validateEmailIllegal.get("error"));

        userInfoEntity.setEmail("test@apache.org");
        userInfoEntity.setPassword("  ");
        userInfoEntity.setAccessToken(" ");
        Map<String, String> validatePasswordAndTokenBlank = usersService.validateUserInfo(userInfoEntity);
        Assert.assertEquals("Fields password and access token cannot be empty at the same time.",
                validatePasswordAndTokenBlank.get("error"));

        userInfoEntity.setPassword("password");
        userInfoEntity.setAccessToken("token");
        Map<String, String> validateSuccess = usersService.validateUserInfo(userInfoEntity);
        Assert.assertEquals("Validate user success", validateSuccess.get("message"));

    }
}
