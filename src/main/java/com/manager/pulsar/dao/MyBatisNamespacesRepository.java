package com.manager.pulsar.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.manager.pulsar.entity.NamespacesEntity;
import com.manager.pulsar.entity.NamespacesRepository;
import com.manager.pulsar.mapper.NamespacesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MyBatisNamespacesRepository implements NamespacesRepository {

    private final NamespacesMapper namespacesMapper;

    @Autowired
    public MyBatisNamespacesRepository(NamespacesMapper namespacesMapper) {
        this.namespacesMapper = namespacesMapper;
    }

    @Override
    public Optional<NamespacesEntity> findById(int namespaceId) {
        return Optional.ofNullable(namespacesMapper.findById(namespaceId));
    }

    @Override
    public Optional<NamespacesEntity> findByTenantNamespace(String tenant, String namespace) {
        return Optional.ofNullable(namespacesMapper.findByTenantNamespace(tenant, namespace));
    }

    @Override
    public Page<NamespacesEntity> findByTenant(Integer pageNum, Integer pageSize, String tenant) {
        PageHelper.startPage(pageNum, pageSize);
        Page<NamespacesEntity> namespacesEntities = namespacesMapper.findByTenant(tenant);
        return namespacesEntities;
    }

    @Override
    public Page<NamespacesEntity> findByNamespace(Integer pageNum, Integer pageSize, String namespace) {
        PageHelper.startPage(pageNum, pageSize);
        Page<NamespacesEntity> namespacesEntities = namespacesMapper.findByNamespace(namespace);
        return namespacesEntities;
    }

    @Override
    public Page<NamespacesEntity> getNamespacesList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<NamespacesEntity> namespacesEntities = namespacesMapper.getNamespacesList();
        return namespacesEntities;
    }

    @Override
    public void remove(NamespacesEntity namespacesEntity) {
        namespacesMapper.delete(namespacesEntity);
    }

    @Override
    public void update(NamespacesEntity namespacesEntity) {
        namespacesMapper.updateByTenantNamespace(namespacesEntity);
    }

    @Override
    public int save(NamespacesEntity namespacesEntity) {
        namespacesMapper.insert(namespacesEntity);
        return namespacesEntity.getNamespaceId();
    }
}
