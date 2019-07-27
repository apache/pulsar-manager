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

import com.manager.pulsar.entity.ClusterEntity;
import com.manager.pulsar.entity.ClustersRepository;
import com.manager.pulsar.service.ClustersService;
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

/**
 * Cluster rest api
 */
@RestController
@RequestMapping(value = "/pulsar-manager/admin/v2")
@Api(description = "Support more flexible queries to clusters.")
@Validated
public class ClustersController {

    @Autowired
    private ClustersRepository clustersRepository;

    @Autowired
    private ClustersService clusterService;

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
        Map<String, Object> result = clusterService.getClustersList(pageNum, pageSize);

        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Query cluster info")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/clusters/{cluster}", method = RequestMethod.GET)
    public ResponseEntity<Optional<ClusterEntity>> getCluster(
            @ApiParam(value = "The name of cluster")
            @Size(min = 1, max = 255)
            @PathVariable String cluster) {
        Optional<ClusterEntity> clustersEntity = clustersRepository.findByCluster(cluster);
        return ResponseEntity.ok(clustersEntity);
    }
}
