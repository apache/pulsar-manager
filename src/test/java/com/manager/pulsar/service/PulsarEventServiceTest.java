package com.manager.pulsar.service;

import com.manager.pulsar.entity.TenantsEntity;
import com.manager.pulsar.entity.TenantsRepository;
import org.apache.pulsar.ecosystem.io.function.ActionType;
import org.apache.pulsar.ecosystem.io.function.EventType;
import org.apache.pulsar.ecosystem.io.function.PulsarEvent;
import org.apache.pulsar.ecosystem.io.function.TenantEvent;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PulsarEventServiceTest {

    @Autowired
    private PulsarEventService pulsarEventService;

    @Autowired
    private TenantsRepository tenantsRepository;

    private void checkTenant(String tenant, TenantEvent tenantEvent) {
        Optional<TenantsEntity> tenantsEntity = tenantsRepository.findByName(tenant);
        TenantsEntity tenantEntity = tenantsEntity.get();
        Assert.assertEquals(tenantEntity.getTenant(), tenantEvent.getTenant());
        Assert.assertEquals(tenantEntity.getAdminRoles(), String.join(",", tenantEvent.getAdminRoles()));
        Assert.assertEquals(tenantEntity.getAllowedClusters(), String.join(",", tenantEvent.getAllowedClusters()));

    }

    @Test
    public void tenantEventServiceTest() {
        String tenant = "test-tenant";

        // test INSERT action
        PulsarEvent insertPulsarEvent = new PulsarEvent();
        TenantEvent insertTenantEvent = new TenantEvent();
        insertTenantEvent.setTenant(tenant);
        HashSet<String> insertAllowedClusters = new HashSet<>();
        insertAllowedClusters.add("insert-standalone");
        insertAllowedClusters.add("insert-pulsar-clusters");
        insertTenantEvent.setAllowedClusters(insertAllowedClusters);
        HashSet<String> insertAdminRoles = new HashSet<>();
        insertAdminRoles.add("insert-role1");
        insertAdminRoles.add("insert-role2");
        insertTenantEvent.setAdminRoles(insertAdminRoles);
        insertPulsarEvent.setTenantEvent(insertTenantEvent);
        insertPulsarEvent.setEventType(EventType.TENANT);
        insertPulsarEvent.setAction(ActionType.INSERT);

        pulsarEventService.convertPulsarEvent(insertPulsarEvent);
        checkTenant(tenant, insertTenantEvent);

        // test UPDATE action
        PulsarEvent updatePulsarEvent = new PulsarEvent();
        TenantEvent updateTenantEvent = new TenantEvent();
        updateTenantEvent.setTenant(tenant);
        HashSet<String> updateAllowedClusters = new HashSet<>();
        updateAllowedClusters.add("update-stadnalone");
        updateAllowedClusters.add("insert-pulsar-clusters");
        updateTenantEvent.setAllowedClusters(updateAllowedClusters);
        HashSet<String> updateAdminRoles = new HashSet<>();
        updateAdminRoles.add("update-role1");
        updateAdminRoles.add("update-role2");
        updateTenantEvent.setAdminRoles(updateAdminRoles);
        updatePulsarEvent.setTenantEvent(updateTenantEvent);
        updatePulsarEvent.setEventType(EventType.TENANT);
        updatePulsarEvent.setAction(ActionType.UPDATE);
        pulsarEventService.convertPulsarEvent(updatePulsarEvent);
        checkTenant(tenant, updateTenantEvent);

        // test DELETE action
        PulsarEvent deletePulsarEvent = new PulsarEvent();
        TenantEvent deleteTenantEvent = new TenantEvent();
        deleteTenantEvent.setTenant(tenant);
        deletePulsarEvent.setTenantEvent(deleteTenantEvent);
        deletePulsarEvent.setEventType(EventType.TENANT);
        deletePulsarEvent.setAction(ActionType.DELETE);
        pulsarEventService.convertPulsarEvent(deletePulsarEvent);
        Optional<TenantsEntity> tenantsEntity = tenantsRepository.findByName(tenant);
        Assert.assertFalse(tenantsEntity.isPresent());

    }
}
