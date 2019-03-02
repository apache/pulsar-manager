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

import com.manager.pulsar.entity.TenantsEntity;
import com.manager.pulsar.entity.TenantsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class TenantsController {

  @Autowired
  private TenantsRepository tenantsRepository;

  @RequestMapping(value = "/tenants/{tenant}", method = RequestMethod.POST)
  public ResponseEntity create(@PathVariable("tenant") String tenant) {
//    checkInput(createUserParam, bindingResult);
      TenantsEntity tenantEntity = new TenantsEntity(tenant);
      Integer tenantId = tenantsRepository.save(tenantEntity);
      return ResponseEntity.ok(new HashMap<String, Object>() {{
        put("tenant", tenantsRepository.findById(tenantId));
      }});
  }
}
