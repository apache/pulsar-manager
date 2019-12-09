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
import org.apache.pulsar.manager.entity.RoleInfoEntity;
import org.apache.pulsar.manager.entity.RolesRepository;
import org.apache.pulsar.manager.mapper.RolesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RolesRepositoryImpl implements RolesRepository {

    private final RolesMapper rolesMapper;

    @Autowired
    public RolesRepositoryImpl(RolesMapper rolesMapper) {
        this.rolesMapper = rolesMapper;
    }

    @Override
    public long save(RoleInfoEntity roleInfoEntity) {
        this.rolesMapper.save(roleInfoEntity);
        return roleInfoEntity.getRoleId();
    }

    @Override
    public Optional<RoleInfoEntity> findByRoleName(String name, String roleSource) {
        return Optional.ofNullable(this.rolesMapper.findByRoleName(name, roleSource));
    }

    @Override
    public Page<RoleInfoEntity> findRolesList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return this.rolesMapper.findRoleList();
    }

    @Override
    public List<RoleInfoEntity> findRolesListByRoleSource(String roleSource) {
        return this.rolesMapper.findRoleListByRoleSource(roleSource);
    }

    @Override
    public Page<RoleInfoEntity> findRolesMultiId(Integer pageNum, Integer pageSize, List<Long> idList) {
        PageHelper.startPage(pageNum, pageSize);
        return this.rolesMapper.findByMultiId(idList);
    }

    @Override
    public List<RoleInfoEntity> findAllRolesByMultiId(List<Long> idList) {
        return this.rolesMapper.findAllByMultiId(idList);
    }

    @Override
    public void update(RoleInfoEntity roleInfoEntity) {
        this.rolesMapper.update(roleInfoEntity);
    }

    @Override
    public void delete(String roleName, String roleSource) {
        this.rolesMapper.delete(roleName, roleSource);
    }
}
