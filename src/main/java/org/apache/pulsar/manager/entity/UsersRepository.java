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
package org.apache.pulsar.manager.entity;

import com.github.pagehelper.Page;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository  {

    /**
     * Create a user.
     * @param userInfoEntity
     * @return user id
     */
    long save(UserInfoEntity userInfoEntity);

    /**
     * Get a user information by user name.
     * @param name The user name
     * @return UserInfoEntity
     */
    Optional<UserInfoEntity> findByUserName(String name);

    /**
     * Get a user information by user id.
     * @param userId The user id
     * @return UserInfoEntity
     */
    Optional<UserInfoEntity> findByUserId(long userId);

    /**
     * Get a user information by user access token.
     * @param accessToken The user token
     * @return UserInfoEntity
     */
    Optional<UserInfoEntity> findByAccessToken(String accessToken);

    /**
     * Get user list, support paging.
     * @param pageNum Get the data on which page.
     * @param pageSize The number of data per page
     * @return A list of UserInfoEntity.
     */
    Page<UserInfoEntity> findUsersList(Integer pageNum, Integer pageSize);

    /**
     * Get user list
     * @param userIdList A list of user id
     * @return A list of UserInfoEntity.
     */
    List<UserInfoEntity> findUsersListByMultiUserId(List<Long> userIdList);

    /**
     * Update a user information.
     * @param userInfoEntity UserInfoEntity
     */
    void update(UserInfoEntity userInfoEntity);

    /**
     * Delete a user by username.
     * @param name username
     */
    void delete(String name);
}
