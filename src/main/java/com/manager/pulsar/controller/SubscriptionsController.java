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
import com.manager.pulsar.entity.SubscriptionEntity;
import com.manager.pulsar.entity.SubscriptionsRepository;
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
 * Subscription rest api class.
 */
@RequestMapping(value = "/puslar-manger/admin/v2")
@Api(description = "Support more flexible queries to subscriptions.")
@Validated
@RestController
public class SubscriptionsController {

    @Autowired
    private SubscriptionsRepository subscriptionsRepository;

    @ApiOperation(value = "Get the list of existing subscriptions, support paging, the default is 10 per page")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/subscriptions", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getSubscriptions(
            @ApiParam(value = "page_num", defaultValue = "1", example = "1")
            @RequestParam(name = "page_num", defaultValue = "1")
            @Min(value = 1, message = "page_num is incorrent, should be greater than 0.")
                    Integer pageNum,
            @ApiParam(value = "page_size", defaultValue = "10", example = "10")
            @RequestParam(name="page_size", defaultValue = "10")
            @Range(min = 1, max = 1000, message = "page_size is incorrect, should be greater than 0 and less than 1000.")
                    Integer pageSize) {
        Page<SubscriptionEntity> subscriptionsEntityPage = subscriptionsRepository
                .getSubscriptionsList(pageNum, pageSize);
        Map<String, Object> result = Maps.newHashMap();
        result.put("total", subscriptionsEntityPage.getTotal());
        result.put("data", subscriptionsEntityPage);
        return ResponseEntity.ok(result);
    }

    /**
     * Get subscripton info by tenant, namespace, topic or subscription.
     * tnts => tenant namespace topic subscription first letter.
     * @return ResponseEntity<Map<String, Object>>
     */
    @ApiOperation(value = "Query list by the name of tenant, namespace, topic or subscription," +
            "support paging, the default is 10 per page")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/namespaces/{tenantOrNamespaceOrTopicOrSub", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getSubByTnts(
            @ApiParam(value = "The name of tenant or namespace.")
            @Size(min = 1, max = 255)
            @PathVariable String tenantOrNamespaceOrTopicOrSub,
            @ApiParam(value = "page_num", defaultValue = "1", example = "1")
            @RequestParam(name = "page_num", defaultValue = "1")
            @Min(value = 1, message = "page_num is incorrect, should be greater than 0.")
                    Integer pageNum,
            @ApiParam(value = "page_size", defaultValue = "10", example = "10")
            @RequestParam(name="page_size", defaultValue = "10")
            @Range(min = 1, max = 1000, message = "page_size is incorrect, should be greater than 0 and less than 1000.")
                    Integer pageSize) {
        Page<SubscriptionEntity> subscriptionsEntities = subscriptionsRepository
                .findByTenantOrNamespaceOrTopicOrSubscription(pageNum, pageSize, tenantOrNamespaceOrTopicOrSub);
        Map<String, Object> result = Maps.newHashMap();
        result.put("total", subscriptionsEntities.getTotal());
        result.put("data", subscriptionsEntities);
        return ResponseEntity.ok(result);
    }

    /**
     * tnts => tenant namespace topic subscription first letter.
     * @param tenant The name of tenant
     * @param namespace The name of namespace
     * @param topic The name of topic
     * @param subscription The name of subscription
     * @return SubscritpionEntity
     */
    @ApiOperation(value = "Query subscription info by tenant and namespace and topic and subscription")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/namespace/{tenant}/{namespace}/{topic}/{subscription}", method = RequestMethod.GET)
    public ResponseEntity<Optional<SubscriptionEntity>> getSubscriptionByTnts(
            @ApiParam(value = "The name of tenant")
            @Size(min = 1, max = 255)
            @PathVariable String tenant,
            @ApiParam(value = "The name of namespace")
            @Size(min = 1, max = 255)
            @PathVariable String namespace,
            @ApiParam(value = "The name of topic")
            @Size(min = 1, max = 255)
            @PathVariable String topic,
            @ApiParam(value = "The name of subscription")
            @Size(min = 1, max = 255)
            @PathVariable String subscription) {
        Optional<SubscriptionEntity> subscriptionsEntity = subscriptionsRepository
                .findByTenantNamespaceTopicSubscription(tenant, namespace, topic, subscription);
        return ResponseEntity.ok(subscriptionsEntity);
    }
}