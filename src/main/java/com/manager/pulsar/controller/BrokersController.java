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

import com.manager.pulsar.entity.BrokersRepository;
import com.manager.pulsar.entity.EnvironmentsRepository;
import com.manager.pulsar.service.BrokersService;
import com.manager.pulsar.utils.EnvironmentTools;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import java.util.Map;

/**
 * Brokers rest api.
 */
@RequestMapping(value = "/pulsar-manager/admin/v2")
@Api(description = "Support more flexible queries to brokers.")
@Validated
@RestController
public class BrokersController {

    @Autowired
    private BrokersService brokersService;

    @Autowired
    private BrokersRepository brokersRepository;

    @Autowired
    private EnvironmentsRepository environmentsRepository;

    @Autowired
    private HttpServletRequest request;

    @ApiOperation(value = "Get the list of existing brokers, support paging, the default is 10 per page")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/brokers/{cluster}", method =  RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getBrokers(
            @ApiParam(value = "page_num", defaultValue = "1", example = "1")
            @RequestParam(name = "page_num", defaultValue = "1")
            @Min(value = 1, message = "page_num is incorrect, should be greater than 0.")
            Integer pageNum,
            @ApiParam(value = "page_size", defaultValue = "10", example = "10")
            @RequestParam(name="page_size", defaultValue = "10")
            @Range(min = 1, max = 1000, message = "page_size is incorrect, should be greater than 0 and less than 1000.")
            Integer pageSize,
            @PathVariable String cluster) {
        String requestHost = EnvironmentTools.getEnvironment(request, environmentsRepository);
        Map<String, Object> result = brokersService.getBrokersList(pageNum, pageSize, cluster, requestHost);
        return ResponseEntity.ok(result);
    }
//
//    @ApiOperation(value = "Query broker info")
//    @ApiResponses({
//            @ApiResponse(code = 200, message = "ok"),
//            @ApiResponse(code = 500, message = "Internal server error")
//    })
//    @RequestMapping(value = "/brokers/{broker}", method = RequestMethod.GET)
//    public ResponseEntity<Optional<BrokerEntity>> getBroker(
//            @ApiParam(value = "The name of broker")
//            @Size(min = 1, max = 255)
//            @PathVariable String broker) {
//        Optional<BrokerEntity> brokersEntity = brokersRepository.findByBroker(broker);
//        return ResponseEntity.ok(brokersEntity);
//    }

}
