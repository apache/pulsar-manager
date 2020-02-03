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
import org.apache.pulsar.manager.entity.UserInfoEntity;
import org.apache.pulsar.manager.entity.UsersRepository;
import org.apache.pulsar.manager.mapper.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UsersRepositoryImpl implements UsersRepository {

    private final UsersMapper usersMapper;

    @Autowired
    public UsersRepositoryImpl(UsersMapper usersMapper) {
        this.usersMapper = usersMapper;
    }

    @Override
    public long save(UserInfoEntity userInfoEntity) {
        this.usersMapper.save(userInfoEntity);
        return userInfoEntity.getUserId();
    }

    @Override
    public Optional<UserInfoEntity> findByUserName(String name) {
        return Optional.ofNullable(this.usersMapper.findByUserName(name));
    }

    @Override
    public Optional<UserInfoEntity> findByUserId(long userId) {
        return Optional.ofNullable(this.usersMapper.findByUserId(userId));
    }

    @Override
    public Optional<UserInfoEntity> findByAccessToken(String accessToken) {
        return Optional.ofNullable(this.usersMapper.findByAccessToken(accessToken));
    }

    @Override
    public Page<UserInfoEntity> findUsersList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return this.usersMapper.findUsersList();
    }

    @Override
    public List<UserInfoEntity> findUsersListByMultiUserId(List<Long> userIdList) {
        return this.usersMapper.findUsersListByMultiUserId(userIdList);
    }

    @Override
    public void update(UserInfoEntity userInfoEntity) {
        this.usersMapper.update(userInfoEntity);
    }

    @Override
    public void delete(String name) {
        this.usersMapper.delete(name);
    }
}
