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

import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.manager.entity.EnvironmentEntity;
import org.apache.pulsar.manager.entity.EnvironmentsRepository;
import org.apache.pulsar.manager.service.EnvironmentCacheService;
import org.apache.pulsar.manager.service.PulsarAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Environments for dynamic change connection broker.
 */
@RequestMapping(value = "/pulsar-manager")
@Api(description = "Support change environments")
@Validated
@RestController
@Slf4j
public class EnvironmentsController {

    private final EnvironmentsRepository environmentsRepository;

    private final EnvironmentCacheService environmentCacheService;

    private final PulsarAdminService pulsarAdminService;

    private final HttpServletRequest request;

    @Autowired
    public EnvironmentsController(
            HttpServletRequest request,
            EnvironmentsRepository environmentsRepository,
            EnvironmentCacheService environmentCacheService,
            PulsarAdminService pulsarAdminService) {
        this.environmentsRepository = environmentsRepository;
        this.environmentCacheService = environmentCacheService;
        this.request = request;
        this.pulsarAdminService = pulsarAdminService;
    }

    @ApiOperation(value = "Get the list of existing environments, support paging, the default is 10 per page")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/environments", method =  RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getEnvironmentsList(
            @ApiParam(value = "page_num", defaultValue = "1", example = "1")
            @RequestParam(name = "page_num", defaultValue = "1")
            @Min(value = 1, message = "page_num is incorrect, should be greater than 0.")
            Integer pageNum,
            @ApiParam(value = "page_size", defaultValue = "10", example = "10")
            @RequestParam(name="page_size", defaultValue = "10")
            @Range(min = 1, max = 1000, message = "page_size is incorrect, should be greater than 0 and less than 1000.")
            Integer pageSize) {
        Map<String, Object> result = Maps.newHashMap();
        List<EnvironmentEntity> environmentEntities = environmentsRepository.getAllEnvironments();
        result.put("total", environmentEntities.size());
        result.put("data", environmentEntities);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Add environment")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/environments/environment", method =  RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> addEnvironment(
            @RequestBody EnvironmentEntity environmentEntity) {
        Map<String, Object> result = Maps.newHashMap();
        Optional<EnvironmentEntity> environmentEntityBrokerOptional = environmentsRepository
                .findByBroker(environmentEntity.getBroker());
        if (environmentEntityBrokerOptional.isPresent()) {
            result.put("error", "Broker is exist");
            return ResponseEntity.ok(result);
        }
        if (environmentEntity.getName() == null) {
            result.put("error", "Environment name is incorrect");
            return ResponseEntity.ok(result);
        }
        Optional<EnvironmentEntity> environmentEntityNameOptional = environmentsRepository
                .findByName(environmentEntity.getName());
        if (environmentEntityNameOptional.isPresent()) {
            result.put("error", "Environment is exist");
            return ResponseEntity.ok(result);
        }
        try {
            pulsarAdminService.clusters(environmentEntity.getBroker()).getClusters();
        } catch (PulsarAdminException e) {
            log.error("Failed to get clusters list.", e);
            result.put("error", "This environment is error. Please check it");
            return ResponseEntity.ok(result);
        }
        environmentsRepository.save(environmentEntity);
        environmentCacheService.reloadEnvironment(environmentEntity);
        result.put("message", "Add environment success");
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Update environment")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/environments/environment", method =  RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> updateEnvironment(@RequestBody EnvironmentEntity environmentEntity) {
        Map<String, Object> result = Maps.newHashMap();
        Optional<EnvironmentEntity> environmentEntityOptional = environmentsRepository
                .findByName(environmentEntity.getName());
        if (!environmentEntityOptional.isPresent()) {
            result.put("error", "Environment no exist");
            return ResponseEntity.ok(result);
        }
        try {
            pulsarAdminService.clusters(environmentEntity.getBroker()).getClusters();
        } catch (PulsarAdminException e) {
            log.error("Failed to get clusters list.", e);
            result.put("error", "This environment is error. Please check it");
            return ResponseEntity.ok(result);
        }
        environmentsRepository.update(environmentEntity);
        environmentCacheService.reloadEnvironment(environmentEntity);
        result.put("message", "Update environment success");
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Delete environment")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/environments/environment", method =  RequestMethod.DELETE)
    public ResponseEntity<Map<String, Object>> deleteEnvironment(@RequestBody EnvironmentEntity environmentEntity) {
        Map<String, Object> result = Maps.newHashMap();
        Optional<EnvironmentEntity> environmentEntityOptional = environmentsRepository
                .findByName(environmentEntity.getName());
        if (!environmentEntityOptional.isPresent()) {
            result.put("error", "Environment no exist");
            return ResponseEntity.ok(result);
        }
        environmentsRepository.remove(environmentEntity.getName());
        result.put("message", "Delete environment success");
        return ResponseEntity.ok(result);
    }
}
