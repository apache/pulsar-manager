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
package org.apache.pulsar.manager.dao;

import com.github.pagehelper.Page;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.pulsar.manager.PulsarManagerApplication;
import org.apache.pulsar.manager.entity.UserInfoEntity;
import org.apache.pulsar.manager.entity.UsersRepository;
import org.apache.pulsar.manager.profiles.HerdDBTestProfile;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {
                PulsarManagerApplication.class,
                HerdDBTestProfile.class
        }
)
@ActiveProfiles("test")
public class UsersRepositoryImplTest {

    @Autowired
    private UsersRepository usersRepository;

    private void initUser(UserInfoEntity userInfoEntity) {
        userInfoEntity.setAccessToken("test-access-token");
        userInfoEntity.setEmail("test@apache.org");
        userInfoEntity.setLocation("bj");
        userInfoEntity.setDescription("test-description");
        userInfoEntity.setName("test-user");
        userInfoEntity.setExpire(157900045678l);
        userInfoEntity.setPhoneNumber("1356789023456");
        userInfoEntity.setPassword(DigestUtils.sha256Hex("hello-world"));
    }

    private void validateUser(UserInfoEntity user, boolean list) {
        Assert.assertEquals(user.getName(), "test-user");
        Assert.assertEquals(user.getExpire(), 157900045678l);
        Assert.assertEquals(user.getPhoneNumber(), "1356789023456");
        Assert.assertEquals(user.getDescription(), "test-description");
        Assert.assertEquals(user.getLocation(), "bj");
        Assert.assertEquals(user.getEmail(), "test@apache.org");
        Assert.assertEquals(user.getAccessToken(), "test-access-token");
        if (!list) {
            Assert.assertEquals(user.getPassword(), DigestUtils.sha256Hex("hello-world"));
        }
    }

    @Test
    public void getUsersListTest() {
        UserInfoEntity userInfoEntity = new UserInfoEntity();
        initUser(userInfoEntity);

        usersRepository.save(userInfoEntity);

        Page<UserInfoEntity> userInfoEntities = usersRepository.findUsersList(1, 10);
        userInfoEntities.count(true);
        userInfoEntities.getResult().forEach((user) -> {
            validateUser(user, true);
            usersRepository.delete(user.getName());
        });
    }

    @Test
    public void getAndUpdateUserTest() {
        UserInfoEntity userInfoEntity = new UserInfoEntity();
        initUser(userInfoEntity);
        usersRepository.save(userInfoEntity);
        Optional<UserInfoEntity> userInfoEntityOptional = usersRepository.findByUserName(userInfoEntity.getName());
        UserInfoEntity getUserInfoEntity = userInfoEntityOptional.get();
        validateUser(getUserInfoEntity, false);
        userInfoEntity.setPhoneNumber("1356789023456");
        userInfoEntity.setEmail("test2@apache.org");
        usersRepository.update(userInfoEntity);

        userInfoEntityOptional = usersRepository.findByUserName(userInfoEntity.getName());
        UserInfoEntity updateUserInfoEntity = userInfoEntityOptional.get();
        Assert.assertEquals(updateUserInfoEntity.getPhoneNumber(), "1356789023456");
        Assert.assertEquals(updateUserInfoEntity.getEmail(), "test2@apache.org");

        usersRepository.delete(updateUserInfoEntity.getName());
        userInfoEntityOptional = usersRepository.findByUserName(userInfoEntity.getName());
        Assert.assertFalse(userInfoEntityOptional.isPresent());
    }

    @Test
    public void findUserByAccessTokenTest() {
        UserInfoEntity userInfoEntity = new UserInfoEntity();
        initUser(userInfoEntity);
        usersRepository.save(userInfoEntity);
        Optional<UserInfoEntity> userInfoEntityOptional = usersRepository.findByAccessToken(
                userInfoEntity.getAccessToken());
        UserInfoEntity getUserInfoEntity = userInfoEntityOptional.get();
        validateUser(getUserInfoEntity, true);
        usersRepository.delete(userInfoEntity.getName());
    }
}
