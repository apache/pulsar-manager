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
package io.streamnative.pulsar.manager.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.streamnative.pulsar.manager.entity.ReplicationStatsEntity;
import io.streamnative.pulsar.manager.entity.ReplicationsStatsRepository;
import io.streamnative.pulsar.manager.mapper.ReplicationsStatsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ReplicationsStatsRepositoryImpl implements ReplicationsStatsRepository {

    private final ReplicationsStatsMapper replicationsStatsMapper;

    @Autowired
    public ReplicationsStatsRepositoryImpl(ReplicationsStatsMapper replicationsStatsMapper) {
        this.replicationsStatsMapper = replicationsStatsMapper;
    }

    public long save(ReplicationStatsEntity replicationStatsEntity) {
        replicationsStatsMapper.save(replicationStatsEntity);
        return replicationStatsEntity.getReplicationStatsId();
    }

    public Page<ReplicationStatsEntity> findByTopicStatsId(Integer pageNum, Integer pageSize,
                                                    long topicStatsId, long timestamp) {
        PageHelper.startPage(pageNum, pageSize);
        Page<ReplicationStatsEntity> replicationStatsEntities = replicationsStatsMapper.findByTopicStatsId(
                topicStatsId, timestamp);
        return replicationStatsEntities;
    }

    public void remove(long timestamp, long timeInterval) {
        replicationsStatsMapper.delete(timestamp, timeInterval);
    }
}
