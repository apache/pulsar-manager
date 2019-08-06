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

import com.github.pagehelper.Page;
import com.google.common.collect.Maps;
import com.manager.pulsar.entity.EnvironmentsRepository;
import com.manager.pulsar.entity.NamespaceEntity;
import com.manager.pulsar.entity.NamespacesRepository;
import com.manager.pulsar.service.NamespacesService;
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
import javax.validation.constraints.Size;
import java.util.Map;
import java.util.Optional;

/**
 * Namespace Controller class
 */
@RequestMapping(value = "/pulsar-manager/admin/v2")
@Api(description = "Support more flexible queries to namespaces.")
@Validated
@RestController
public class NamespacesController {

    @Autowired
    private NamespacesRepository namespacesRepository;

    @Autowired
    private NamespacesService namespacesService;

    @Autowired
    private EnvironmentsRepository environmentsRepository;

    @Autowired
    private HttpServletRequest request;

    @ApiOperation(value = "Get the list of existing namespaces, support paging, the default is 10 per page")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/namespaces", method =  RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getNamespaces(
            @ApiParam(value = "page_num", defaultValue = "1", example = "1")
            @RequestParam(name = "page_num", defaultValue = "1")
            @Min(value = 1, message = "page_num is incorrect, should be greater than 0.")
            Integer pageNum,
            @ApiParam(value = "page_size", defaultValue = "10", example = "10")
            @RequestParam(name="page_size", defaultValue = "10")
            @Range(min = 1, max = 1000, message = "page_size is incorrect, should be greater than 0 and less than 1000.")
            Integer pageSize) {
        Page<NamespaceEntity> namespacesEntities = namespacesRepository.getNamespacesList(pageNum, pageSize);
        Map<String, Object> result = Maps.newHashMap();
        result.put("total", namespacesEntities.getTotal());
        result.put("data", namespacesEntities);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Query list by the name of tenant or namespace, support paging, the default is 10 per page")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/namespaces/{tenantOrNamespace}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getNamespacesByTenant(
            @ApiParam(value = "The name of tenant or namespace.")
            @Size(min = 1, max = 255)
            @PathVariable String tenantOrNamespace,
            @ApiParam(value = "page_num", defaultValue = "1", example = "1")
            @RequestParam(name = "page_num", defaultValue = "1")
            @Min(value = 1, message = "page_num is incorrect, should be greater than 0.")
                    Integer pageNum,
            @ApiParam(value = "page_size", defaultValue = "10", example = "10")
            @RequestParam(name="page_size", defaultValue = "10")
            @Range(min = 1, max = 1000, message = "page_size is incorrect, should be greater than 0 and less than 1000.")
            Integer pageSize) {
        String requestHost = EnvironmentTools.getEnvironment(request, environmentsRepository);
        Map<String, Object> result = namespacesService.getNamespaceList(pageNum, pageSize, tenantOrNamespace, requestHost);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Query namespace info by tenant and namespace")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/namespaces/{tenant}/{namespace}", method = RequestMethod.GET)
    public ResponseEntity<Optional<NamespaceEntity>> getNamespacesByTenantNamespace(
            @ApiParam(value = "The name of tenant")
            @Size(min = 1, max = 255)
            @PathVariable String tenant,
            @ApiParam(value = "The name of namespace")
            @Size(min = 1, max = 255)
            @PathVariable String namespace) {
        Optional<NamespaceEntity> namespacesEntity = namespacesRepository.findByTenantNamespace(tenant, namespace);
        return ResponseEntity.ok(namespacesEntity);
    }

}
