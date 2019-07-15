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
package com.manager.pulsar.controller;

import com.github.pagehelper.Page;
import com.google.common.collect.Maps;
import com.manager.pulsar.entity.TopicEntity;
import com.manager.pulsar.entity.TopicsRepository;
import org.apache.pulsar.shade.io.swagger.annotations.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.Map;
import java.util.Optional;

/**
 * Topics rest api class.
 */
@RequestMapping(value = "/pulsar-manger/admin/v2")
@Api(description = "Support more flexible queries to namespaces.")
@Validated
@RestController
public class TopicsController {

    @Autowired
    private TopicsRepository topicsRepository;

    @ApiOperation(value = "Get the list of existing topics, support paging, the default is 10 per page")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/topics", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getTopics(
            @ApiParam(value = "page_num", defaultValue = "1", example = "1")
            @RequestParam(name = "page_num", defaultValue = "1")
            @Min(value = 1, message = "page_num is incorrect, should be greater than 0.")
                    Integer pageNum,
            @ApiParam(value = "page_size", defaultValue = "10", example = "10")
            @RequestParam(name="page_size", defaultValue = "10")
            @Range(min = 1, max = 1000, message = "page_size is incorrect, should be greater than 0 and less than 1000.")
                    Integer pageSize) {
        Page<TopicEntity> topicsEntityPage = topicsRepository.getTopicsList(pageNum, pageSize);
        Map<String, Object> result = Maps.newHashMap();
        result.put("total", topicsEntityPage.getTotal());
        result.put("data", topicsEntityPage);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Query list by the name of tenant or namespace or topic," +
            "support paging, the default is 10 per page")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/topics/{tenantOrNamespaceOrTopic}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getByTenantOrNamespaceOrTopic(
            @ApiParam(value = "The name of tenant or namespace or topic.")
            @Size(min = 1, max = 255)
            @PathVariable String tenantOrNamespaceOrTopic,
            @ApiParam(value = "page_num", defaultValue = "1", example = "1")
            @RequestParam(name = "page_num", defaultValue = "1")
            @Min(value = 1, message = "page_num is incorrect, should be greater than 0.")
                    Integer pageNum,
            @ApiParam(value = "page_size", defaultValue = "10", example = "10")
            @RequestParam(name="page_size", defaultValue = "10")
            @Range(min = 1, max = 1000, message = "page_size is incorrect, should be greater than 0 and less than 1000.")
                    Integer pageSize) {
        Page<TopicEntity> topicsEntityPage = topicsRepository
                .findByTenantOrNamespaceOrTopic(pageNum, pageSize, tenantOrNamespaceOrTopic);
        Map<String, Object> result = Maps.newHashMap();
        result.put("total", topicsEntityPage.getTotal());
        result.put("data", topicsEntityPage);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Query topic info by tenant and namespace and topic")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/topics/{tenant}/{namespace}/{topic}", method = RequestMethod.GET)
    public ResponseEntity<Optional<TopicEntity>> getTopicsByTenantNamespace(
            @ApiParam(value = "The name of tenant")
            @Size(min = 1, max = 255)
            @PathVariable String tenant,
            @ApiParam(value = "The name of namespace")
            @Size(min = 1, max = 255)
            @PathVariable String namespace,
            @ApiParam(value = "The name of topic")
            @Size(min = 1, max = 255)
            @PathVariable String topic) {
        Optional<TopicEntity> topicsEntity = topicsRepository.findByTenantNamespaceTopic(tenant, namespace, topic);
        return ResponseEntity.ok(topicsEntity);
    }
}