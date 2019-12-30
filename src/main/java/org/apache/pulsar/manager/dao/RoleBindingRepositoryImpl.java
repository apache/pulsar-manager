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
import com.github.pagehelper.PageHelper;
import org.apache.pulsar.manager.entity.RoleBindingEntity;
import org.apache.pulsar.manager.entity.RoleBindingRepository;
import org.apache.pulsar.manager.mapper.RoleBindingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RoleBindingRepositoryImpl implements RoleBindingRepository {

    private RoleBindingMapper roleBindingMapper;

    @Autowired
    public RoleBindingRepositoryImpl(RoleBindingMapper roleBindingMapper) {
        this.roleBindingMapper = roleBindingMapper;
    }

    @Override
    public long save(RoleBindingEntity roleBindingEntity) {
        this.roleBindingMapper.insert(roleBindingEntity);
        return roleBindingEntity.getRoleBindingId();
    }

    @Override
    public Optional<RoleBindingEntity> findByUserIdAndRoleId(long userId, long roleId) {
        return Optional.ofNullable(this.roleBindingMapper.findByUserIdAndRoleId(userId, roleId));
    }

    @Override
    public Page<RoleBindingEntity> findByUserId(Integer pageNum, Integer pageSize, long userId) {
        PageHelper.startPage(pageNum, pageSize);
        return this.roleBindingMapper.findByUserId(userId);
    }

    @Override
    public Page<RoleBindingEntity> getRoleBindingList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return this.roleBindingMapper.findRoleBindinglist();
    }

    @Override
    public List<RoleBindingEntity> findByUserId(long userId) {
        return this.roleBindingMapper.findAllByUserId(userId);
    }

    @Override
    public List<RoleBindingEntity> findByMultiRoleId(List<Long> roleIdList) {
        return this.roleBindingMapper.findByMultiRoleId(roleIdList);
    }

    @Override
    public void update(RoleBindingEntity roleBindingEntity) {
        this.roleBindingMapper.update(roleBindingEntity);
    }

    @Override
    public void delete(long roleId, long userId) {
        this.roleBindingMapper.delete(roleId, userId);
    }
}
