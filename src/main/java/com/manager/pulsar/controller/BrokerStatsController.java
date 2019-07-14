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
import com.manager.pulsar.entity.BrokerStatsEntity;
import com.manager.pulsar.entity.BrokerStatsRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
 * Broker stats rest api.
 */
@RequestMapping(value = "/pulsar-manager/admin/v2")
@Api(description = "Support more flexible queries to brokers stats.")
@Validated
@RestController
public class BrokerStatsController {

    @Autowired
    private BrokerStatsRepository brokerStatsRepository;

    @ApiOperation(value = "Get the list of existing broker stats, support paging, the default is 10 per page")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/brokerStats", method =  RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getBrokerStats(
            @ApiParam(value = "page_num", defaultValue = "1", example = "1")
            @RequestParam(name = "page_num", defaultValue = "1")
            @Min(value = 1, message = "page_num is incorrect, should be greater than 0.")
                    Integer pageNum,
            @ApiParam(value = "page_size", defaultValue = "10", example = "10")
            @RequestParam(name="page_size", defaultValue = "10")
            @Range(min = 1, max = 1000, message = "page_size is incorrect, should be greater than 0 and less than 1000.")
                    Integer pageSize) {
        Page<BrokerStatsEntity> brokerStatsEntityPage = brokerStatsRepository.getBrokerStatsList(pageNum, pageSize);
        Map<String, Object> result = Maps.newHashMap();
        result.put("total", brokerStatsEntityPage.getTotal());
        result.put("data", brokerStatsEntityPage);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Query broker stats info")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/brokerStats/{broker}", method = RequestMethod.GET)
    public ResponseEntity<Optional<BrokerStatsEntity>> getBroker(
            @ApiParam(value = "The name of broker")
            @Size(min = 1, max = 255)
            @PathVariable String broker) {
        Optional<BrokerStatsEntity> brokerStatsEntity = brokerStatsRepository.findByBroker(broker);
        return ResponseEntity.ok(brokerStatsEntity);
    }
}
