package com.manager.pulsar.entity;

import com.github.pagehelper.Page;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interface of brokers
 */
@Repository
public interface BrokersRepository {

    int save(BrokersEntity brokersEntity);

    Optional<BrokersEntity> findById(int brokerId);

    Optional<BrokersEntity> findByBroker(String broker);

    Page<BrokersEntity> getBrokersList(Integer pageNum, Integer pageSize);

    void remove(String broker);

    void update(BrokersEntity brokersEntity);
}
