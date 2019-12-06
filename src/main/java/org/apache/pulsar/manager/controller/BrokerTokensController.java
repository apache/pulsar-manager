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
import org.apache.pulsar.manager.entity.BrokerTokenEntity;
import org.apache.pulsar.manager.entity.BrokerTokensRepository;
import org.apache.pulsar.manager.service.JwtService;
import io.swagger.annotations.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.Map;
import java.util.Optional;

/**
 * Broker tokens controller for broker token create, delete, update and search
 */
@RequestMapping(value = "/pulsar-manager")
@Api(description = "Support more management token.")
@Validated
@RestController
public class BrokerTokensController {

    private final JwtService jwtService;
    private final BrokerTokensRepository brokerTokensRepository;

    @Autowired
    public BrokerTokensController(JwtService jwtService, BrokerTokensRepository brokerTokensRepository) {
        this.jwtService = jwtService;
        this.brokerTokensRepository = brokerTokensRepository;
    }

    @ApiOperation(value = "Get the list of existing broker tokens, support paging, the default is 10 per page")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/tokens", method =  RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getEnvironmentsList(
            @ApiParam(value = "page_num", defaultValue = "1", example = "1")
            @RequestParam(name = "page_num", defaultValue = "1")
            @Min(value = 1, message = "page_num is incorrect, should be greater than 0.")
                    Integer pageNum,
            @ApiParam(value = "page_size", defaultValue = "10", example = "10")
            @RequestParam(name="page_size", defaultValue = "10")
            @Range(min = 1, max = 1000, message = "page_size is incorrect, should be greater than 0 and less than 1000.")
                    Integer pageSize) {
        Page<BrokerTokenEntity> brokerTokenEntityPage = brokerTokensRepository.getBrokerTokensList(pageNum, pageSize);
        Map<String, Object> result = Maps.newHashMap();
        result.put("total", brokerTokenEntityPage.getTotal());
        result.put("data", brokerTokenEntityPage);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Add token for connect broker")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/tokens/token", method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> addBrokerToken(@RequestBody BrokerTokenEntity brokerTokenEntity) {
        Optional<BrokerTokenEntity> optionalBrokerTokenEntity = brokerTokensRepository.findTokenByRole(brokerTokenEntity.getRole());
        Map<String, Object> result = Maps.newHashMap();
        if (optionalBrokerTokenEntity.isPresent()) {
            result.put("error", "Role is exist");
            return ResponseEntity.ok(result);
        }
        String token = jwtService.createBrokerToken(brokerTokenEntity.getRole(), null);
        if (token == null) {
            result.put("error", "Token generate failed");
            return ResponseEntity.ok(result);
        }
        brokerTokenEntity.setToken(token);
        long brokerTokenId = brokerTokensRepository.save(brokerTokenEntity);
        if (brokerTokenId <= 0) {
            result.put("error", "Token save Failed");
            return ResponseEntity.ok(result);
        }
        result.put("message", "Token generate and save success");
        result.put("tokenId", brokerTokenId);
        result.put("token", token);
        return ResponseEntity.ok(result);
    }


    @ApiOperation(value = "Update token for connect broker")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/tokens/token", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> updateBrokerToken(@RequestBody BrokerTokenEntity brokerTokenEntity) {
        Optional<BrokerTokenEntity> optionalBrokerTokenEntity = brokerTokensRepository.findTokenByRole(brokerTokenEntity.getRole());
        Map<String, Object> result = Maps.newHashMap();
        if (!optionalBrokerTokenEntity.isPresent()) {
            result.put("error", "Role is not exist");
            return ResponseEntity.ok(result);
        }
        BrokerTokenEntity getBrokerTokenEntity = optionalBrokerTokenEntity.get();
        brokerTokenEntity.setToken(getBrokerTokenEntity.getToken());
        brokerTokensRepository.update(brokerTokenEntity);
        result.put("message", "Token generate and save success");
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Get token by role")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/tokens/{role}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getBrokerToken(@PathVariable String role) {
        Optional<BrokerTokenEntity> optionalBrokerTokenEntity = brokerTokensRepository.findTokenByRole(role);
        Map<String, Object> result = Maps.newHashMap();
        if (!optionalBrokerTokenEntity.isPresent()) {
            result.put("error", "Token not find");
            return ResponseEntity.ok(result);
        }
        BrokerTokenEntity brokerTokenEntity = optionalBrokerTokenEntity.get();
        result.put("token", brokerTokenEntity.getToken());
        result.put("description", brokerTokenEntity.getDescription());
        result.put("role", brokerTokenEntity.getRole());
        result.put("tokenId", brokerTokenEntity.getTokenId());
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Delete token")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/tokens/{role}", method = RequestMethod.DELETE)
    public ResponseEntity<Map<String, Object>> deleteBrokerToken(@PathVariable String role) {
        Optional<BrokerTokenEntity> optionalBrokerTokenEntity = brokerTokensRepository.findTokenByRole(role);
        Map<String, Object> result = Maps.newHashMap();
        if (!optionalBrokerTokenEntity.isPresent()) {
            result.put("error", "Token not find");
            return ResponseEntity.ok(result);
        }
        brokerTokensRepository.remove(role);
        result.put("message", "Delete broker token success");
        result.put("role", role);
        return ResponseEntity.ok(result);
    }
}
