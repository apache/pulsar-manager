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
import com.manager.pulsar.entity.TenantsEntity;
import com.manager.pulsar.entity.TenantsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TenantsController {

  @Autowired
  private TenantsRepository tenantsRepository;

  @RequestMapping(value = "/tenants", method =  RequestMethod.GET)
  public Page<TenantsEntity> getTenants(
          @RequestParam(name = "pageNum", defaultValue = "1")
          Integer pageNum,
          @RequestParam(name="pageSize", defaultValue = "10")
          Integer pageSize) {
      return tenantsRepository.getTenantsList(pageNum, pageSize);
  }
}
