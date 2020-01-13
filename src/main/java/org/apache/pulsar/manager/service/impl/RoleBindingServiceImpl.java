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
import org.apache.pulsar.manager.entity.RoleBindingEntity;
import org.apache.pulsar.manager.entity.RoleBindingRepository;
import org.apache.pulsar.manager.entity.RoleInfoEntity;
import org.apache.pulsar.manager.entity.RolesRepository;
import org.apache.pulsar.manager.entity.UserInfoEntity;
import org.apache.pulsar.manager.entity.UsersRepository;
import org.apache.pulsar.manager.service.RoleBindingService;
import org.apache.pulsar.manager.service.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RoleBindingServiceImpl implements RoleBindingService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RoleBindingRepository roleBindingRepository;

    @Autowired
    private RolesService rolesService;

    @Autowired
    private RolesRepository rolesRepository;

    public Map<String, String> validateCurrentUser(String token, RoleBindingEntity roleBindingEntity) {
        Map<String, String> result = Maps.newHashMap();
        Optional<UserInfoEntity> userInfoEntityOptional = usersRepository.findByAccessToken(token);
        if (!userInfoEntityOptional.isPresent()) {
            result.put("error", "User no exist.");
            return result;
        }
        UserInfoEntity userInfoEntity = userInfoEntityOptional.get();
        List<RoleBindingEntity> roleBindingEntities = roleBindingRepository.findByUserId(userInfoEntity.getUserId());
        List<Long> roleIdList = new ArrayList<>();
        for (RoleBindingEntity r : roleBindingEntities) {
            roleIdList.add(r.getRoleId());
        }
        if (!roleIdList.contains(roleBindingEntity.getRoleId())) {
            result.put("error", "This operation is illegal for this user");
            return result;
        }
        result.put("message", "Validate current user success");
        return result;
    }

    public Map<String, Object> validateCreateRoleBinding(
            String token, String tenant, String roleName, String userName) {
        Map<String, Object> result = Maps.newHashMap();

        Optional<UserInfoEntity> userInfoEntityOptional = usersRepository.findByUserName(userName);
        if (!userInfoEntityOptional.isPresent()) {
            result.put("error", "The user is not exist");
            return result;
        }
        Map<String, String> validateResult = rolesService.validateCurrentTenant(token, tenant);
        if (validateResult.get("error") != null) {
            result.put("error", validateResult.get("error"));
            return result;
        }

        Optional<RoleInfoEntity> roleInfoEntityOptional = rolesRepository.findByRoleName(roleName, tenant);
        if (!roleInfoEntityOptional.isPresent()) {
            result.put("error", "This role is no exist");
            return result;
        }

        Optional<RoleBindingEntity> roleBindingEntityOptional = roleBindingRepository.findByUserIdAndRoleId(
                userInfoEntityOptional.get().getUserId(), roleInfoEntityOptional.get().getRoleId());
        if (roleBindingEntityOptional.isPresent()) {
            result.put("error", "Role binding already exist");
            return result;
        }
        result.put("message", "Validate create role success");
        result.put("roleId", roleInfoEntityOptional.get().getRoleId());
        result.put("userId", userInfoEntityOptional.get().getUserId());
        return result;
    }

    public List<Map<String, Object>> getRoleBindingList(String token, String tenant) {
        Optional<UserInfoEntity> userInfoEntityOptional = usersRepository.findByAccessToken(token);
        List<RoleBindingEntity> roleBindingEntityList = roleBindingRepository.findByUserId(
                userInfoEntityOptional.get().getUserId());
        List<Long> roleIdList = new ArrayList<>();
        roleBindingEntityList.forEach((roleBinding) -> {
            roleIdList.add(roleBinding.getRoleId());
        });
        List<RoleBindingEntity> roleBindingEntities = roleBindingRepository.findByMultiRoleId(roleIdList);
        List<Long> userIdList = new ArrayList<>();
        for (RoleBindingEntity roleBindingEntity : roleBindingEntities) {
            userIdList.add(roleBindingEntity.getUserId());
        }
        List<UserInfoEntity> userInfoEntities = usersRepository.findUsersListByMultiUserId(userIdList);
        Map<Long, UserInfoEntity> userInfoEntityMap = Maps.newHashMap();
        userInfoEntities.forEach((u) -> {
            userInfoEntityMap.put(u.getUserId(), u);
        });
        List<RoleInfoEntity> roleInfoEntities = rolesRepository.findAllRolesByMultiId(roleIdList);
        Map<Long, RoleInfoEntity> roleInfoEntityMap = Maps.newHashMap();
        roleInfoEntities.forEach((r) -> {
            roleInfoEntityMap.put(r.getRoleId(), r);
        });
        List<Map<String, Object>> userRoleInfo = new ArrayList<>();
        roleBindingEntities.forEach((binding) -> {
            RoleInfoEntity roleInfoEntity = roleInfoEntityMap.get(binding.getRoleId());
            if (roleInfoEntity != null && roleInfoEntity.getRoleSource().equals(tenant)) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("name", binding.getName());
                map.put("description", binding.getDescription());
                map.put("userId", binding.getUserId());
                if (userInfoEntityMap.get(binding.getUserId()) != null) {
                    map.put("userName", userInfoEntityMap.get(binding.getUserId()).getName());
                }
                map.put("roleId", binding.getRoleId());
                map.put("roleName", roleInfoEntity.getRoleName());
                userRoleInfo.add(map);
            }
        });
        return userRoleInfo;
    }
    public List<Map<String, Object>> getAllRoleBindingList() {
        List<RoleBindingEntity> roleBindingEntityList = roleBindingRepository.findAllRoleBindingList();
        List<Long> roleIdList = new ArrayList<>();
        List<Long> userIdList = new ArrayList<>();
        roleBindingEntityList.forEach((roleBinding) -> {
            roleIdList.add(roleBinding.getRoleId());
            userIdList.add(roleBinding.getUserId());
        });
        List<UserInfoEntity> userInfoEntities = usersRepository.findUsersListByMultiUserId(userIdList);
        Map<Long, UserInfoEntity> userInfoEntityMap = Maps.newHashMap();
        userInfoEntities.forEach((u) -> {
            userInfoEntityMap.put(u.getUserId(), u);
        });
        List<RoleInfoEntity> roleInfoEntities = rolesRepository.findAllRolesByMultiId(roleIdList);
        Map<Long, RoleInfoEntity> roleInfoEntityMap = Maps.newHashMap();
        roleInfoEntities.forEach((r) -> {
            roleInfoEntityMap.put(r.getRoleId(), r);
        });
        List<Map<String, Object>> userRoleInfo = new ArrayList<>();
        roleBindingEntityList.forEach((binding) -> {
            RoleInfoEntity roleInfoEntity = roleInfoEntityMap.get(binding.getRoleId());
            Map<String, Object> map = Maps.newHashMap();
            map.put("name", binding.getName());
            map.put("description", binding.getDescription());
            map.put("userId", binding.getUserId());
            if (userInfoEntityMap.get(binding.getUserId()) != null) {
                map.put("userName", userInfoEntityMap.get(binding.getUserId()).getName());
            }
            map.put("roleId", binding.getRoleId());
            map.put("roleName", roleInfoEntity.getRoleName());
            userRoleInfo.add(map);
        });
        return userRoleInfo;
    }
}
