package com.manager.pulsar.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.manager.pulsar.entity.BrokersEntity;
import com.manager.pulsar.entity.BrokersRepository;
import com.manager.pulsar.mapper.BrokersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MyBatisBrokersRepository implements BrokersRepository {

    private final BrokersMapper brokersMapper;

    @Autowired
    public MyBatisBrokersRepository(BrokersMapper brokersMapper) {
        this.brokersMapper = brokersMapper;
    }

    @Override
    public Optional<BrokersEntity> findById(int brokerId) {
        return Optional.ofNullable(brokersMapper.findById(brokerId));
    }

    @Override
    public Optional<BrokersEntity> findByBroker(String broker) {
        return Optional.ofNullable(brokersMapper.findByBroker(broker));
    }

    @Override
    public Page<BrokersEntity> getBrokersList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<BrokersEntity> brokersEntities = brokersMapper.getBrokersList();
        return brokersEntities;
    }

    @Override
    public int save(BrokersEntity brokersEntity) {
        brokersMapper.insert(brokersEntity);
        return brokersEntity.getBrokerId();
    }

    @Override
    public void remove(String broker) {
        brokersMapper.deleteByBroker(broker);
    }

    @Override
    public void update(BrokersEntity brokersEntity) {
        brokersMapper.update(brokersEntity);
    }

}
