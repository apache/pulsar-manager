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
import com.manager.pulsar.service.NamespacesService;
import com.manager.pulsar.service.TopicsService;
import com.manager.pulsar.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class NamespacesServiceImpl implements NamespacesService {

    @Value("${backend.directRequestBroker}")
    private boolean directRequestBroker;

    @Value("${backend.directRequestHost}")
    private String directRequestHost;

    @Autowired
    private TopicsService topicsService;

    public Map<String, Object> getNamespaceList(Integer pageNum, Integer pageSize, String tenant) {
        Map<String, Object> namespacesMap = Maps.newHashMap();
        List<Map<String, Object>> namespacesArray = new ArrayList<>();
        if (directRequestBroker) {
            Gson gson = new Gson();
            Map<String, String> header = Maps.newHashMap();
            header.put("Content-Type", "application/json");
            String result = HttpUtil.doGet(directRequestHost + "/admin/v2/namespaces/" + tenant, header);
            if (result != null) {
                List<String> namespacesList = gson.fromJson(result, new TypeToken<List<String>>(){}.getType());
                for (String tenantNamespace : namespacesList) {
                    String namespace = tenantNamespace.split("/")[1];
                    Map<String, Object> topicsEntity = Maps.newHashMap();
                    Map<String, Object> topics = topicsService.getTopicsList(
                            0, 0, tenant, namespace);
                    topicsEntity.put("topics", topics.get("total"));
                    topicsEntity.put("namespace", namespace);
                    namespacesArray.add(topicsEntity);
                }
                namespacesMap.put("isPage", false);
                namespacesMap.put("total", namespacesList.size());
                namespacesMap.put("data", namespacesArray);
                namespacesMap.put("pageNum", 1);
                namespacesMap.put("pageSize", namespacesArray.size());
            }
        }
        return namespacesMap;
    }
}