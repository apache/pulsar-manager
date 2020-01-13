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
import org.apache.pulsar.manager.entity.EnvironmentEntity;
import org.apache.pulsar.manager.entity.EnvironmentsRepository;
import org.apache.pulsar.manager.mapper.EnvironmentsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * EnvironmentsRepositoryImpl implements EnvironmentsRepository for change environment.
 */
@Repository
public class EnvironmentsRepositoryImpl implements EnvironmentsRepository {

    private final EnvironmentsMapper environmentsMapper;

    @Autowired
    public EnvironmentsRepositoryImpl(EnvironmentsMapper environmentsMapper) {
        this.environmentsMapper = environmentsMapper;
    }

    @Override
    public Optional<EnvironmentEntity> findByBroker(String broker) {
        return Optional.ofNullable(environmentsMapper.findByBroker(broker));
    }

    @Override
    public Optional<EnvironmentEntity> findByName(String name) {
        return Optional.ofNullable((environmentsMapper.findByName(name)));
    }

    @Override
    public void save(EnvironmentEntity environmentEntity) {
        environmentsMapper.insert(environmentEntity);
    }

    @Override
    public Page<EnvironmentEntity> getEnvironmentsList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return environmentsMapper.findEnvironmentsList();
    }

    @Override
    public List<EnvironmentEntity> getAllEnvironments() {
        return environmentsMapper.findEnvironmentsList();
    }

    @Override
    public List<EnvironmentEntity> getAllEnvironments(List<String> envonmentNameList) {
        return environmentsMapper.findEnvironmentsListByMultiName(envonmentNameList);
    }

    @Override
    public void remove(String name) {
        environmentsMapper.delete(name);
    }

    @Override
    public void update(EnvironmentEntity environmentEntity) {
        environmentsMapper.update(environmentEntity);
    }
}
