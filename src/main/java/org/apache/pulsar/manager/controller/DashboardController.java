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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.pulsar.manager.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Dashboard rest api
 */
@RestController
@RequestMapping(value = "/pulsar-manager")
@Api(description = "Support dashboard query.")
@Validated
public class DashboardController {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(
            DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @ApiOperation(value = "Get the dashboard stats")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/dashboard", method =  RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getDashboardStats(
            @RequestBody List<String> environmentList) {
        Map<String, Object> result = dashboardService.getDashboardStats(environmentList);
        return ResponseEntity.ok(result);
    }
}
