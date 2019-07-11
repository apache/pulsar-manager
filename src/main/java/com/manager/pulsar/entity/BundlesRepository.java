package com.manager.pulsar.entity;

import com.github.pagehelper.Page;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interface of bundles
 */
@Repository
public interface BundlesRepository {

    int save(BundlesEntity bundlesEntity);

    Optional<BundlesEntity> findById(int bundleId);

    Optional<BundlesEntity> findByBrokerTenantNamespaceBundle(
            String broker, String tenant, String namespace, String bundle);

    Page<BundlesEntity> findByBrokerOrTenantOrNamespaceOrBundle(Integer pageNum, Integer pageSize, String btnt);

    Page<BundlesEntity> findByBundle(Integer pageNum, Integer pageSize, String bundle);

    Page<BundlesEntity> getBundlesList(Integer pageNum, Integer pageSize);

    void remove(String broker, String tenant, String namespace, String bundle);

    void update(BundlesEntity bundlesEntity);
}
