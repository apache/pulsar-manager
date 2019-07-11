package com.manager.pulsar.controller;

import com.github.pagehelper.Page;
import com.google.common.collect.Maps;
import com.manager.pulsar.entity.BundlesEntity;
import com.manager.pulsar.entity.BundlesRepository;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.Map;
import java.util.Optional;


@RequestMapping(value = "/pulsar-manager/admin/v2")
@Api(description = "Support more flexible queries to bundles.")
@Validated
@RestController
public class BundlesController {

    @Autowired
    private BundlesRepository bundlesRepository;

    @ApiOperation(value = "Get the list of existing bundles, support paging, the default is 10 per page")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/bundles", method =  RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getBundles(
            @ApiParam(value = "page_num", defaultValue = "1", example = "1")
            @RequestParam(name = "page_num", defaultValue = "1")
            @Min(value = 1, message = "page_num is incorrect, should be greater than 0.")
                    Integer pageNum,
            @ApiParam(value = "page_size", defaultValue = "10", example = "10")
            @RequestParam(name="page_size", defaultValue = "10")
            @Range(min = 1, max = 1000, message = "page_size is incorrect, should be greater than 0 and less than 1000.")
                    Integer pageSize) {
        Page<BundlesEntity> bundlesEntityPage = bundlesRepository.getBundlesList(pageNum, pageSize);
        Map<String, Object> result = Maps.newHashMap();
        result.put("total", bundlesEntityPage.getTotal());
        result.put("data", bundlesEntityPage);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Query list by the name of broker or tenant or namespace or bundle," +
            "support paging, the default is 10 per page")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/bundles/{brokerOrtenantsOrNamespaceOrbundle}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getBundlesBybtnb(
            @ApiParam(value = "The name of tenant or namespace.")
            @Size(min = 1, max = 255)
            @PathVariable String brokerOrtenantsOrNamespaceOrbundle,
            @ApiParam(value = "page_num", defaultValue = "1", example = "1")
            @RequestParam(name = "page_num", defaultValue = "1")
            @Min(value = 1, message = "page_num is incorrect, should be greater than 0.")
                    Integer pageNum,
            @ApiParam(value = "page_size", defaultValue = "10", example = "10")
            @RequestParam(name="page_size", defaultValue = "10")
            @Range(min = 1, max = 1000, message = "page_size is incorrect, should be greater than 0 and less than 1000.")
                    Integer pageSize) {
        Page<BundlesEntity> bundlesEntityPage = bundlesRepository
                .findByBrokerOrTenantOrNamespaceOrBundle(pageNum, pageSize, brokerOrtenantsOrNamespaceOrbundle);
        Map<String, Object> result = Maps.newHashMap();
        result.put("total", bundlesEntityPage.getTotal());
        result.put("data", bundlesEntityPage);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Query bundle info")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/bundles/{broker}/{tenant}/{namespace}/{bundle}", method = RequestMethod.GET)
    public ResponseEntity<Optional<BundlesEntity>> getBundle(
            @ApiParam(value = "The name of broker")
            @Size(min = 1, max = 255)
            @PathVariable String broker,
            @ApiParam(value = "The name of tenant")
            @Size(min = 1, max = 255)
            @PathVariable String tenant,
            @ApiParam(value = "The name of namespace")
            @Size(min = 1, max = 255)
            @PathVariable String namespace,
            @ApiParam(value = "The name of bundle")
            @Size(min = 1, max = 255)
            @PathVariable String bundle) {
        Optional<BundlesEntity> bundlesEntity = bundlesRepository.findByBrokerTenantNamespaceBundle(
                broker, tenant, namespace, bundle);
        return ResponseEntity.ok(bundlesEntity);
    }
}
