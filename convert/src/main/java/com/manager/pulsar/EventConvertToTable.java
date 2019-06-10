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
package com.manager.pulsar;

import com.google.common.collect.Maps;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.TypedMessageBuilder;
import org.apache.pulsar.client.impl.schema.AvroSchema;
import org.apache.pulsar.ecosystem.io.connector.ZooKeeperAction;
import org.apache.pulsar.ecosystem.io.function.EventType;
import org.apache.pulsar.ecosystem.io.function.PulsarEvent;
import org.apache.pulsar.ecosystem.io.function.TenantEvent;
import org.apache.pulsar.functions.api.Context;
import org.apache.pulsar.functions.api.Function;
import org.slf4j.Logger;

import java.util.Map;

public class EventConvertToTable implements Function<PulsarEvent, Void> {

    private final Map<String, String> properties = Maps.newHashMap();

    private Logger logger;

    private boolean isInitialized = false;

    private static final String PULSAR_MANAGER_TENANT = "pulsar/manager/__tenant";

    private static final AvroSchema<Tenant> tenantSchema = AvroSchema.of(Tenant.class);


    @Override
    public Void process(PulsarEvent input, Context context) throws Exception {

        initUserConfig(context);
        eventConvert(input, context);

        return null;
    }

    private void initUserConfig(Context context) {
        if (!isInitialized) {
            logger = context.getLogger();
            logger.info("Start Init User Config");
            isInitialized = true;
        }
    }

    private void eventConvert(PulsarEvent pulsarEvent, Context context) throws Exception {
        EventType eventType = pulsarEvent.getEventType();
        try {
            switch (eventType) {
                case TENANT:
                    Tenant tenant = convertTenant(pulsarEvent);
                    setAction(pulsarEvent.getRawEvent().getAction());
                    TypedMessageBuilder messageBuilder = context.newOutputMessage(PULSAR_MANAGER_TENANT, tenantSchema)
                            .value(tenant).properties(properties);
                    messageBuilder.eventTime(pulsarEvent.getRawEvent().getTime()).sendAsync();
                    break;
                case CLUSTER:
                    break;
                case DEFAULT:
                    break;
            }
        } catch (PulsarClientException e) {
            logger.error(e.getMessage());
        }
    }

    private void setAction(ZooKeeperAction action) {
        switch(action) {
            case CREATE:
                properties.put("ACTION", "INSERT");
                break;
            case CREATE2:
                properties.put("ACTION", "INSERT");
                break;
            case DELETE:
                properties.put("ACTION", "DELETE");
                break;
            case SET_DATA:
                properties.put("ACTION", "UPDATE");
                break;
            case UNKNOWN:
                break;
        }
    }

    private Tenant convertTenant(PulsarEvent pulsarEvent) {
        Tenant tenant = new Tenant();
        TenantEvent tenantEvent = pulsarEvent.getTenantEvent();
        tenant.setTenantId(pulsarEvent.getRawEvent().getZxid());
        tenant.setTenant(tenantEvent.getTenant());
        if (pulsarEvent.getRawEvent().getAction() != ZooKeeperAction.DELETE) {
            tenant.setAdminRoles(String.join(",", tenantEvent.getAdminRoles()));
            tenant.setAllowedClusters(String.join(",", tenantEvent.getAllowedClusters()));
        }
        return tenant;
    }
}
