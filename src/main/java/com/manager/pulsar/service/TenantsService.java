package com.manager.pulsar.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface TenantsService {

    Map<String, Object> getTenantsList(Integer pageNum, Integer pageSize);
}
