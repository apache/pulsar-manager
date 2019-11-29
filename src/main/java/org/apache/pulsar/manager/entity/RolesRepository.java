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

import java.util.Optional;

@Repository
public interface RolesRepository {
    /**
     * Create a role.
     * @param roleInfoEntity
     * @return role id
     */
    long save(RoleInfoEntity roleInfoEntity);

    /**
     * Get a role information by role name. roleSource, and roleName uniquely locate a role.
     * @param roleName The role name
     * @param roleSource The user who created the role
     * @return RoleInfoEntity
     */
    Optional<RoleInfoEntity> findByRoleName(String roleName, String roleSource);


    /**
     * Get role list, support paging.
     * @param pageNum Get the data on which page.
     * @param pageSize The number of data per page
     * @return A list of RoleInfoEntity.
     */
    Page<RoleInfoEntity> findRolesList(Integer pageNum, Integer pageSize);

    /**
     * Update a role information.
     * @param roleInfoEntity RoleInfoEntity
     */
    void update(RoleInfoEntity roleInfoEntity);

    /**
     * Delete a role by role name.
     * @param roleName role name
     * @param roleSource The user who created the role
     */
    void delete(String roleName, String roleSource);
}
