package com.manager.pulsar.entity;

import org.springframework.stereotype.Repository;
import com.github.pagehelper.Page;

import java.util.Optional;

@Repository
public interface NamespacesRepository {

    int save(NamespacesEntity namespacesEntity);

    Optional<NamespacesEntity> findById(int namespaceId);

    Optional<NamespacesEntity> findByTenantNamespace(String tenant, String namespace);

    Page<NamespacesEntity> findByTenantOrNamespace(Integer pageNum, Integer pageSize, String tenantOrNamespace);

    Page<NamespacesEntity> findByNamespace(Integer pageNum, Integer pageSize, String namespace);

    Page<NamespacesEntity> getNamespacesList(Integer pageNum, Integer pageSize);

    void remove(NamespacesEntity namespacesEntity);

    void update(NamespacesEntity namespacesEntity);
}
