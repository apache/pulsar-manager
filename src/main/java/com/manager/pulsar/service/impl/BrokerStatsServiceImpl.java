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

import com.manager.pulsar.service.BrokerStatsService;
import com.manager.pulsar.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BrokerStatsServiceImpl implements BrokerStatsService {

    @Value("${backend.directRequestHost}")
    private String directRequestHost;

    private static final Map<String, String> header = new HashMap<String, String>(){{
        put("Content-Type","application/json");
    }};

    public String forwarBrokerStatsMetrics(String broker) {

        broker = checkBroker(broker);
        return HttpUtil.doGet(broker + "/admin/v2/broker-stats/metrics", header);
    }

    public String forwardBrokerStatsTopics(String broker) {

        broker = checkBroker(broker);
        return HttpUtil.doGet(broker + "/admin/v2/broker-stats/topics", header);
    }

    private String checkBroker(String broker) {
        if (broker == null || broker.length() <= 0) {
            broker = directRequestHost;
        }

        if (!broker.startsWith("http")) {
            broker = "http://" + broker;
        }
        return broker;
    }
}
