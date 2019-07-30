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
package com.manager.pulsar.service.impl;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.manager.pulsar.entity.NamespacesRepository;
import com.manager.pulsar.entity.TenantsRepository;
import com.manager.pulsar.service.TenantsService;
import com.manager.pulsar.utils.HttpUtil;
import org.apache.pulsar.common.policies.data.TenantInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class TenantsServiceImpl implements TenantsService {

    private static final Logger log = LoggerFactory.getLogger(TenantsServiceImpl.class);


    @Value("${backend.directRequestBroker}")
    private boolean directRequestBroker;

    @Value("${backend.directRequestHost}")
    private String directRequestHost;

    @Autowired
    private TenantsRepository tenantsRepository;

    @Autowired
    private NamespacesRepository namespacesRepository;

    public Map<String, Object> getTenantsList(Integer pageNum, Integer pageSize) {
        Map<String, Object> tenantsMap = Maps.newHashMap();
        List<Map<String, Object>> tenantsArray = new ArrayList<>();
        if (directRequestBroker) {
            Gson gson = new Gson();
            Map<String, String> header = Maps.newHashMap();
            header.put("Content-Type", "application/json");
            String result = HttpUtil.doGet( directRequestHost + "/admin/v2/tenants", header);
            if (result != null) {
                List<String> tenantsList = gson.fromJson(result, new TypeToken<List<String>>(){}.getType());
                for (String tenant : tenantsList) {
                    Map<String, Object> tenantEntity = Maps.newHashMap();
                    String info = HttpUtil.doGet( directRequestHost + "/admin/v2/tenants/" + tenant, header);
                    TenantInfo tenantInfo = gson.fromJson(info, TenantInfo.class);
                    tenantEntity.put("tenant", tenant);
                    tenantEntity.put("adminRoles", String.join(",", tenantInfo.getAdminRoles()));
                    tenantEntity.put("allowedClusters", String.join(",",  tenantInfo.getAllowedClusters()));
                    String namespace = HttpUtil.doGet(directRequestHost + "/admin/v2/namespaces/" + tenant, header);
                    if (namespace != null) {
                        List<String> namespacesList = gson.fromJson(namespace, new TypeToken<List<String>>(){}.getType());
                        tenantEntity.put("namespaces", namespacesList.size());
                    } else {
                        tenantEntity.put("namespaces", 0);
                    }
                    tenantsArray.add(tenantEntity);
                }
                tenantsMap.put("isPage", false);
                tenantsMap.put("total", tenantsList.size());
                tenantsMap.put("data", tenantsArray);
                tenantsMap.put("pageNum", 1);
                tenantsMap.put("pageSize", tenantsList.size());
            }
        } else {
           // to do query from local database
        }
        return tenantsMap;
    }
}
