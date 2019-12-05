package org.apache.pulsar.manager.entity;

import com.github.pagehelper.Page;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleBindingRepository {

    /**
     * Create a role binding for user and role
     * @param roleBindingEntity
     * @return role binding id
     */
    long save(RoleBindingEntity roleBindingEntity);

    /**
     * Get a role id list from table role binding
     * @param userId The user id
     * @return RoleBindingEntity
     */
    Page<RoleBindingEntity> findByUserId(Integer pageNum, Integer pageSize, long userId);

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
