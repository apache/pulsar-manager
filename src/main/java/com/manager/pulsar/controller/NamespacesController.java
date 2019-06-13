package com.manager.pulsar.controller;

import com.github.pagehelper.Page;
import com.manager.pulsar.entity.NamespacesEntity;
import com.manager.pulsar.entity.NamespacesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NamespacesController {

    @Autowired
    private NamespacesRepository namespacesRepository;

    @RequestMapping(value = "/namespaces", method =  RequestMethod.GET)
    public Page<NamespacesEntity> getTenants(
            @RequestParam(name = "pageNum", defaultValue = "1")
                    Integer pageNum,
            @RequestParam(name="pageSize", defaultValue = "10")
                    Integer pageSize) {
        return namespacesRepository.getNamespacesList(pageNum, pageSize);
    }
}
