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
package com.manager.pulsar.dao;

import com.github.pagehelper.Page;
import com.manager.pulsar.entity.TopicEntity;
import com.manager.pulsar.entity.TopicsRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Topic test class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TopicsRepositoryImplTest {

    @Autowired
    private TopicsRepository topicsRepository;

    private void initTopicsEntity(TopicEntity topicsEntity) {
        topicsEntity.setTopicId(1);
        topicsEntity.setNamespaceId(1);
        topicsEntity.setTenant("public");
        topicsEntity.setNamespace("default");
        topicsEntity.setTopic("test-topic");
        topicsEntity.setPersistent(true);
        topicsEntity.setPartitions(1);
    }

    private void checkResult(Page<TopicEntity> topicsEntityPage) {
        long total = topicsEntityPage.getTotal();
        Assert.assertEquals(total, 1);
        topicsEntityPage.getResult().forEach((result) -> {
            Assert.assertEquals(result.getTopicId(), 1);
            Assert.assertEquals(result.getNamespaceId(), 1);
            Assert.assertEquals(result.getTenant(), "public");
            Assert.assertEquals(result.getNamespace(), "default");
            Assert.assertEquals(result.getTopic(), "test-topic");
            Assert.assertEquals(result.isPersistent(), true);
            Assert.assertEquals(result.getPartitions(), 1);
        });
    }

    private void checkDeleteResult(Page<TopicEntity> topicsEntityPage) {
        long total = topicsEntityPage.getTotal();
        Assert.assertEquals(total, 0);
    }

    @Test
    public void getTopicsList() {
        TopicEntity topicsEntity = new TopicEntity();
        initTopicsEntity(topicsEntity);
        topicsRepository.save(topicsEntity);
        Page<TopicEntity> topicsEntityPage = topicsRepository.getTopicsList(1, 2);
        topicsEntityPage.count(true);
        checkResult(topicsEntityPage);
        topicsEntityPage.getResult().forEach((result) -> {
            topicsRepository.remove(result.getTenant(), result.getNamespace(), result.getTopic(), result.isPersistent());
        });
        Page<TopicEntity> deleteTopics = topicsRepository.getTopicsList(1, 2);
        deleteTopics.count(true);
        checkDeleteResult(deleteTopics);
    }

    @Test
    public void getByTenantOrNamespaceOrTopic() {
        TopicEntity topicsEntity = new TopicEntity();
        initTopicsEntity(topicsEntity);
        topicsRepository.save(topicsEntity);
        String tenant = "public";
        Page<TopicEntity> topicsEntityPageByTenant = topicsRepository
                .findByTenantOrNamespaceOrTopic(1, 2, tenant);
        topicsEntityPageByTenant.count(true);
        checkResult(topicsEntityPageByTenant);
        String namespace = "default";
        Page<TopicEntity> topicsEntityPageByNamespace = topicsRepository
                .findByTenantOrNamespaceOrTopic(1, 2, namespace);
        topicsEntityPageByNamespace.count(true);
        checkResult(topicsEntityPageByNamespace);
        String topic = "test-topic";
        Page<TopicEntity> topicsEntityPageByTopic = topicsRepository
                .findByTenantOrNamespaceOrTopic(1, 2, topic);
        topicsEntityPageByTopic.count(true);
        checkResult(topicsEntityPageByTopic);
        topicsEntityPageByNamespace.getResult().forEach((result) -> {
            topicsRepository.remove(result.getTenant(), result.getNamespace(), result.getTopic(), result.isPersistent());
        });
        Page<TopicEntity> deleteNamespace = topicsRepository.getTopicsList(1, 2);
        deleteNamespace.count(true);
        checkDeleteResult(deleteNamespace);
    }

}
