package com.manager.pulsar.controller;

import com.github.pagehelper.Page;
import com.manager.pulsar.entity.NamespacesEntity;
import com.manager.pulsar.entity.NamespacesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class NamespacesController {

    @Autowired
    private NamespacesRepository namespacesRepository;

    @RequestMapping(value = "/namespaces", method =  RequestMethod.GET)
    public Page<NamespacesEntity> getNamespaces(
            @RequestParam(name = "pageNum", defaultValue = "1")
                    Integer pageNum,
            @RequestParam(name="pageSize", defaultValue = "10")
                    Integer pageSize) {
        return namespacesRepository.getNamespacesList(pageNum, pageSize);
    }

    @RequestMapping(value = "/namespaces/{tenantOrNamespace}", method = RequestMethod.GET)
    public Page<NamespacesEntity> getNamespacesByTenant(
            @PathVariable String tenantOrNamespace,
            @RequestParam(name = "pageNum", defaultValue = "1")
            Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10")
            Integer pageSize) {
        return namespacesRepository.findByTenantOrNamespace(pageNum, pageSize, tenantOrNamespace);
    }

    @RequestMapping(value = "/namespaces/{tenant}/{namespace}", method = RequestMethod.GET)
    public Optional<NamespacesEntity> getNamespacesByTenantNamespace(
            @PathVariable String tenant,
            @PathVariable String namespace) {
        return namespacesRepository.findByTenantNamespace(tenant, namespace);
    }

}
