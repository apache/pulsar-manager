package com.manager.pulsar.controller;

import com.github.pagehelper.Page;
import com.google.common.collect.Maps;
import com.manager.pulsar.entity.BrokersEntity;
import com.manager.pulsar.entity.BrokersRepository;
import io.swagger.annotations.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.Map;
import java.util.Optional;

@RequestMapping(value = "/pulsar-manager/admin/v2")
@Api(description = "Support more flexible queries to brokers.")
@Validated
@RestController
public class BrokerController {

    @Autowired
    private BrokersRepository brokersRepository;

    @ApiOperation(value = "Get the list of existing brokers, support paging, the default is 10 per page")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/brokers", method =  RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getBrokers(
            @ApiParam(value = "page_num", defaultValue = "1", example = "1")
            @RequestParam(name = "page_num", defaultValue = "1")
            @Min(value = 1, message = "page_num is incorrect, should be greater than 0.")
                    Integer pageNum,
            @ApiParam(value = "page_size", defaultValue = "10", example = "10")
            @RequestParam(name="page_size", defaultValue = "10")
            @Range(min = 1, max = 1000, message = "page_size is incorrect, should be greater than 0 and less than 1000.")
                    Integer pageSize) {
        Page<BrokersEntity> brokersEntityPage = brokersRepository.getBrokersList(pageNum, pageSize);
        Map<String, Object> result = Maps.newHashMap();
        result.put("total", brokersEntityPage.getTotal());
        result.put("data", brokersEntityPage);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Query broker info")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/brokers/{broker}", method = RequestMethod.GET)
    public ResponseEntity<Optional<BrokersEntity>> getBroker(
            @ApiParam(value = "The name of broker")
            @Size(min = 1, max = 255)
            @PathVariable String broker) {
        Optional<BrokersEntity> brokersEntity = brokersRepository.findByBroker(broker);
        return ResponseEntity.ok(brokersEntity);
    }

}
