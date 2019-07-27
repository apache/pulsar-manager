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
import com.manager.pulsar.service.BrokersService;
import com.manager.pulsar.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BrokersServiceImpl implements BrokersService {

    @Value("${backend.directRequestBroker}")
    private boolean directRequestBroker;

    @Value("${backend.directRequestHost}")
    private String directRequestHost;

    public Map<String, Object> getBrokersList(Integer pageNum, Integer pageSize, String cluster) {
        Map<String, Object> brokersMap = Maps.newHashMap();
        List<Map<String, Object>> brokersArray = new ArrayList<>();
        if (directRequestBroker) {
            Gson gson = new Gson();
            Map<String, String> header = Maps.newHashMap();
            header.put("Content-Type", "application/json");
            String failureDomainsResult = HttpUtil.doGet(
                    directRequestHost + "/admin/v2/clusters/" + cluster + "/failureDomains", header);
            Map<String, Map<String, List<String>>> failureDomains = gson.fromJson(
                    failureDomainsResult, new TypeToken<Map<String, Map<String, List<String>>>>() {}.getType());
            String result = HttpUtil.doGet(directRequestHost + "/admin/v2/brokers/" + cluster, header);
            List<String> brokersList = gson.fromJson(result, new TypeToken<List<String>>() {}.getType());
            for (String broker: brokersList) {
                Map<String, Object> brokerEntity = Maps.newHashMap();
                List<String> failureDomain = this.getFailureDomain(broker, failureDomains);
                brokerEntity.put("broker", broker);
                brokerEntity.put("failureDomain", failureDomain);
                brokersArray.add(brokerEntity);
            }
            brokersMap.put("isPage", false);
            brokersMap.put("total", brokersArray.size());
            brokersMap.put("data", brokersArray);
            brokersMap.put("pageNum", 1);
            brokersMap.put("pageSize", brokersArray.size());
        }
        return brokersMap;
    }

    private List<String> getFailureDomain(String broker, Map<String, Map<String, List<String>>> failureDomains) {
        List<String> failureDomainsList = new ArrayList<>();
        Map<String, String> header = Maps.newHashMap();
        header.put("Content-Type", "application/json");
        for (String failureDomain: failureDomains.keySet()) {
            Map<String, List<String>> domains = failureDomains.get(failureDomain);
            for (String domain: domains.keySet()) {
                List<String> domainList = domains.get(domain);
                if (domainList.contains(broker)) {
                    failureDomainsList.add(failureDomain);
                }
            }
        }
        return failureDomainsList;
    }
}
