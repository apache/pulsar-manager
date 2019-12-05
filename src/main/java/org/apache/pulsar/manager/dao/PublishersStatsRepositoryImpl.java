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
import org.apache.pulsar.manager.entity.PublisherStatsEntity;
import org.apache.pulsar.manager.entity.PublishersStatsRepository;
import org.apache.pulsar.manager.mapper.PublishersStatsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PublishersStatsRepositoryImpl implements PublishersStatsRepository {

    private final PublishersStatsMapper publishersStatsMapper;

    @Autowired
    public PublishersStatsRepositoryImpl(PublishersStatsMapper publishersStatsMapper) {
        this.publishersStatsMapper = publishersStatsMapper;
    }

    public long save(PublisherStatsEntity publisherStatsEntity) {
        publishersStatsMapper.save(publisherStatsEntity);
        return publisherStatsEntity.getPublisherStatsId();
    }

    public Page<PublisherStatsEntity> findByTopicStatsId(Integer pageNum, Integer pageSize,
                                                  long topicStatsId, long timestamp) {
        PageHelper.startPage(pageNum, pageSize);
        return publishersStatsMapper.findByTopicStatsId(topicStatsId, timestamp);
    }

    public void remove(long timestamp, long timeInterval) {
        publishersStatsMapper.delete(timestamp - timeInterval);
    }

}
