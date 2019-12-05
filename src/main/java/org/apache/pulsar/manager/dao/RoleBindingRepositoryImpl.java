package org.apache.pulsar.manager.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.pulsar.manager.entity.RoleBindingEntity;
import org.apache.pulsar.manager.entity.RoleBindingRepository;
import org.apache.pulsar.manager.mapper.RoleBindingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RoleBindingRepositoryImpl implements RoleBindingRepository {

    private RoleBindingMapper roleBindingMapper;

    @Autowired
    public RoleBindingRepositoryImpl(RoleBindingMapper roleBindingMapper) {
        this.roleBindingMapper = roleBindingMapper;
    }

    @Override
    public long save(RoleBindingEntity roleBindingEntity) {
        this.roleBindingMapper.insert(roleBindingEntity);
        return roleBindingEntity.getRoleBindingId();
    }

    @Override
    public Page<RoleBindingEntity> findByUserId(Integer pageNum, Integer pageSize, long userId) {
        PageHelper.startPage(pageNum, pageSize);
        return this.roleBindingMapper.findByUserId(userId);
    }

    @Override
    public void update(RoleBindingEntity roleBindingEntity) {
        this.roleBindingMapper.update(roleBindingEntity);
    }

    @Override
    public void delete(long roleId, long userId) {
        this.roleBindingMapper.delete(roleId, userId);
    }
}
