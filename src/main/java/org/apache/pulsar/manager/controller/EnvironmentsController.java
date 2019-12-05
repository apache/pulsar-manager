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

import com.github.pagehelper.Page;
import com.google.common.collect.Maps;
import org.apache.pulsar.manager.entity.EnvironmentEntity;
import org.apache.pulsar.manager.entity.EnvironmentsRepository;
import org.apache.pulsar.manager.service.EnvironmentCacheService;
import org.apache.pulsar.manager.utils.HttpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import java.util.Map;
import java.util.Optional;

/**
 * Environments for dynamic change connection broker.
 */
@RequestMapping(value = "/pulsar-manager")
@Api(description = "Support change environments")
@Validated
@RestController
public class EnvironmentsController {

    @Autowired
    private EnvironmentsRepository environmentsRepository;

    @Value("${backend.jwt.token}")
    private String pulsarJwtToken;

    @Autowired
    private EnvironmentCacheService environmentCacheService;

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
        Page<EnvironmentEntity> environmentEntityPage = environmentsRepository.getEnvironmentsList(pageNum, pageSize);
        Map<String, Object> result = Maps.newHashMap();
        result.put("total", environmentEntityPage.getTotal());
        result.put("data", environmentEntityPage);
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
        Optional<EnvironmentEntity> environmentEntityBrokerOptional = environmentsRepository
                .findByBroker(environmentEntity.getBroker());
        Map<String, Object> result = Maps.newHashMap();
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
        Map<String, String> header = Maps.newHashMap();
        header.put("Content-Type", "application/json");
        if (StringUtils.isNotBlank(pulsarJwtToken)) {
            header.put("Authorization", String.format("Bearer %s", pulsarJwtToken));
        }
        String httpTestResult = HttpUtil.doGet(environmentEntity.getBroker() + "/metrics", header);
        if (httpTestResult == null) {
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
        Optional<EnvironmentEntity> environmentEntityOptional = environmentsRepository
                .findByName(environmentEntity.getName());
        Map<String, Object> result = Maps.newHashMap();
        if (!environmentEntityOptional.isPresent()) {
            result.put("error", "Environment no exist");
            return ResponseEntity.ok(result);
        }
        Map<String, String> header = Maps.newHashMap();
        header.put("Content-Type", "application/json");
        if (StringUtils.isNotBlank(pulsarJwtToken)) {
            header.put("Authorization", String.format("Bearer %s", pulsarJwtToken));
        }
        String httpTestResult = HttpUtil.doGet(environmentEntity.getBroker() + "/metrics", header);
        if (httpTestResult == null) {
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
        Optional<EnvironmentEntity> environmentEntityOptional = environmentsRepository
                .findByName(environmentEntity.getName());
        Map<String, Object> result = Maps.newHashMap();
        if (!environmentEntityOptional.isPresent()) {
            result.put("error", "Environment no exist");
            return ResponseEntity.ok(result);
        }
        environmentsRepository.remove(environmentEntity.getName());
        result.put("message", "Delete environment success");
        return ResponseEntity.ok(result);
    }
}
