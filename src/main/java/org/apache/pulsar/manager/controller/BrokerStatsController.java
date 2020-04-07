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
package org.apache.pulsar.manager.controller;

import org.apache.pulsar.manager.service.BrokerStatsService;
import org.apache.pulsar.manager.service.EnvironmentCacheService;
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
 * Broker Stats route forward.
 */
@RequestMapping(value = "/pulsar-manager/admin/v2")
@Api(description = "Support more flexible queries to brokerStats.")
@Validated
@RestController
public class BrokerStatsController {

    private final BrokerStatsService brokerStatsService;
    private final EnvironmentCacheService environmentCacheService;
    private final HttpServletRequest request;

    @Autowired
    public BrokerStatsController(
            BrokerStatsService brokerStatsService,
            EnvironmentCacheService environmentCacheService,
            HttpServletRequest request) {
        this.brokerStatsService = brokerStatsService;
        this.environmentCacheService = environmentCacheService;
        this.request = request;
    }

    @ApiOperation(value = "Get the broker stats metrics")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/broker-stats/metrics", method =  RequestMethod.GET)
    public ResponseEntity<String> getBrokerStatsMetrics(
            @RequestParam() String broker) {
        String requestHost = environmentCacheService.getServiceUrl(request);
        String result = brokerStatsService.forwardBrokerStatsMetrics(broker, requestHost);
        return ResponseEntity.ok(result);
    }
}
