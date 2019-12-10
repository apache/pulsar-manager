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
public interface RoleBindingRepository {

    /**
     * Create a role binding for user and role
     * @param roleBindingEntity
     * @return role binding id
     */
    long save(RoleBindingEntity roleBindingEntity);

    /**
     * Get a RoleBindingEntity
     * @param userId The user id
     * @param roleId The role id
     * @return RoleBindingEntity
     */
    Optional<RoleBindingEntity> findByUserIdAndRoleId(long userId, long roleId);

    /**
     * Get a role id list from table role binding
     * @param userId The user id
     * @return RoleBindingEntity
     */
    Page<RoleBindingEntity> findByUserId(Integer pageNum, Integer pageSize, long userId);

    /**
     * Get a role id list from table role binding
     * @param userId The user id
     * @return list of RoleBindingEntity
     */
    List<RoleBindingEntity> findByUserId(long userId);

    /**
     * Get a role id list from table role binding
     * @param roleIdList The role id list
     * @return list of RoleBindingEntity
     */
    List<RoleBindingEntity> findByMultiRoleId(List<Long> roleIdList);

    /**
     * Get role binding list, support paging.
     * @param pageNum Get the data on which page.
     * @param pageSize The number of data per page
     * @return A list of RoleBindingEntity
     */
    Page<RoleBindingEntity> getRoleBindingList(Integer pageNum, Integer pageSize);

    /**
     * Get all role binding list.
     * @return A list of RoleBindingEntity
     */
    List<RoleBindingEntity> findAllRoleBindingList();
    /**
     * Update a role binding information
     * @param roleBindingEntity RoleInfoEntity
     */
    void update(RoleBindingEntity roleBindingEntity);

    /**
     * Delete a role binding by role id and user id
     * @param roleId role id
     * @param userId userId
     */
    void delete(long roleId, long userId);
}
