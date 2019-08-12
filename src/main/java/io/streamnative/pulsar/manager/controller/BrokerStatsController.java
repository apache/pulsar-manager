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
package io.streamnative.pulsar.manager.controller;

import io.streamnative.pulsar.manager.service.BrokerStatsService;
import io.streamnative.pulsar.manager.service.EnvironmentCacheService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Broker Stats route foward
 */
@RequestMapping(value = "/pulsar-manager/admin/v2")
@Api(description = "Support more flexible queries to brokerStats.")
@Validated
@RestController
public class BrokerStatsController {

    @Autowired
    private BrokerStatsService brokerStatsService;

    @Autowired
    private EnvironmentCacheService environmentCacheService;

    @Autowired
    private HttpServletRequest request;

    @ApiOperation(value = "Get the broker stats metrics")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/broker-stats/metrics", method =  RequestMethod.GET)
    public ResponseEntity<String> getBrokerStatsMetrics(
            @RequestParam() String broker) {
        String requestHost = environmentCacheService.getServiceUrl(request);
        String result = brokerStatsService.forwarBrokerStatsMetrics(broker, requestHost);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Get the broker stats topics")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/broker-stats/topics", method =  RequestMethod.GET)
    public ResponseEntity<String> getBrokerStatsTopics(
            @RequestParam() String broker) {
        String requestHost = environmentCacheService.getServiceUrl(request);
        String result = brokerStatsService.forwardBrokerStatsTopics(broker, requestHost);
        return ResponseEntity.ok(result);
    }

}
