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
import org.apache.pulsar.manager.service.BookiesService;
import io.swagger.annotations.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.Map;

/**
 * Bookies rest api
 */
@RestController
@RequestMapping(value = "/pulsar-manager/api/v1")
@Api(description = "Support bookies query.")
@Validated
public class BookiesController {

    private final BookiesService bookiesService;

    @Autowired
    public BookiesController(BookiesService bookiesService) {
        this.bookiesService = bookiesService;
    }

    @ApiOperation(value = "Get the list of existing bookies, support paging, the default is 10 per page")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/bookies/{cluster}", method =  RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getClusters(
            @ApiParam(value = "page_num", defaultValue = "1", example = "1")
            @RequestParam(name = "page_num", defaultValue = "1")
            @Min(value = 1, message = "page_num is incorrect, should be greater than 0.")
            Integer pageNum,
            @ApiParam(value = "page_size", defaultValue = "10", example = "10")
            @RequestParam(name="page_size", defaultValue = "10")
            @Range(min = 1, max = 1000, message = "page_size is incorrect, should be greater than 0 and less than 1000.")
            Integer pageSize,
            @PathVariable String cluster) {
        Map<String, Object> result = bookiesService.getBookiesList(pageNum, pageSize, cluster);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Forward heartbeat request")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/bookies/heartbeat/{bookie}", method =  RequestMethod.GET)
    public ResponseEntity<Map<String, String>> bookieHeartbeat(@PathVariable String bookie) {
        Map<String, String> result = Maps.newHashMap();
        String heartbeat = bookiesService.forwardBookiesHeartbeat(bookie);
        result.put("heartbeat", heartbeat);
        return ResponseEntity.ok(result);
    }
}
