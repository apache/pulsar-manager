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

import org.apache.pulsar.manager.service.ClustersService;
import org.apache.pulsar.manager.service.EnvironmentCacheService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import java.util.Map;

/**
 * Cluster rest api.
 */
@RestController
@RequestMapping(value = "/pulsar-manager/admin/v2")
@Api(description = "Support more flexible queries to clusters.")
@Validated
public class ClustersController {

    private final ClustersService clusterService;
    private final EnvironmentCacheService environmentCacheService;
    private final HttpServletRequest request;

    @Autowired
    public ClustersController(
            ClustersService clusterService,
            EnvironmentCacheService environmentCacheService,
            HttpServletRequest request) {
        this.clusterService = clusterService;
        this.environmentCacheService = environmentCacheService;
        this.request = request;
    }

    @ApiOperation(value = "Get the list of existing clusters, support paging, the default is 10 per page")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/clusters", method =  RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getClusters(
            @ApiParam(value = "page_num", defaultValue = "1", example = "1")
            @RequestParam(name = "page_num", defaultValue = "1")
            @Min(value = 1, message = "page_num is incorrect, should be greater than 0.")
                    Integer pageNum,
            @ApiParam(value = "page_size", defaultValue = "10", example = "10")
            @RequestParam(name="page_size", defaultValue = "10")
            @Range(min = 1, max = 1000, message = "page_size is incorrect, should be greater than 0 and less than 1000.")
                    Integer pageSize) {
        String requestHost = environmentCacheService.getServiceUrl(request);
        Map<String, Object> result = clusterService.getClustersList(
            pageNum, pageSize, requestHost, cluster -> {
                String environment = request.getHeader("environment");
                if (null == environment) {
                    return requestHost;
                } else {
                    return environmentCacheService.getServiceUrl(environment, cluster);
                }
            });

        return ResponseEntity.ok(result);
    }
}
