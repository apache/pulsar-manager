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
package org.apache.pulsar.manager.service.impl;

import com.google.common.collect.Maps;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.common.policies.data.FailureDomain;
import org.apache.pulsar.manager.controller.exception.PulsarAdminOperationException;
import org.apache.pulsar.manager.service.BrokersService;
import org.apache.pulsar.manager.service.PulsarAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BrokersServiceImpl implements BrokersService {

    private static final Logger log = LoggerFactory.getLogger(BrokersServiceImpl.class);

    @Value("${backend.directRequestBroker}")
    private boolean directRequestBroker;

    private final PulsarAdminService pulsarAdminService;

    @Autowired
    public BrokersServiceImpl(PulsarAdminService pulsarAdminService) {
        this.pulsarAdminService = pulsarAdminService;
    }


    public Map<String, Object> getBrokersList(Integer pageNum, Integer pageSize, String cluster, String requestHost) {
        Map<String, Object> brokersMap = Maps.newHashMap();
        List<Map<String, Object>> brokersArray = new ArrayList<>();
        if (directRequestBroker) {
            Map<String, FailureDomain> failureDomains;
            try {
                failureDomains = pulsarAdminService.clusters(requestHost).getFailureDomains(cluster);
            } catch (PulsarAdminException e) {
                PulsarAdminOperationException pulsarAdminOperationException
                        = new PulsarAdminOperationException("Failed to get failureDomains list.");
                log.error(pulsarAdminOperationException.getMessage(), e);
                throw pulsarAdminOperationException;
            }

            List<String>  brokersList;
            try {
                brokersList = pulsarAdminService.brokers(requestHost).getActiveBrokers(cluster);
            } catch (PulsarAdminException e) {
                PulsarAdminOperationException pulsarAdminOperationException
                        = new PulsarAdminOperationException("Failed to get brokers list.");
                log.error(pulsarAdminOperationException.getMessage(), e);
                throw pulsarAdminOperationException;
            }

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

    private List<String> getFailureDomain(String broker, Map<String, FailureDomain> failureDomains) {
        List<String> failureDomainsList = new ArrayList<>();
        for (String failureDomain: failureDomains.keySet()) {
            FailureDomain domain = failureDomains.get(failureDomain);
            if (domain.getBrokers().contains(broker)) {
                failureDomainsList.add(failureDomain);
            }
        }
        return failureDomainsList;
    }
}
