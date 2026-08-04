package com.jcx.jiachangcai.module.user.adress.service.impl;

import com.jcx.jiachangcai.module.user.adress.entity.Address;
import com.jcx.jiachangcai.module.user.adress.mapper.AddressMapper;
import com.jcx.jiachangcai.module.user.adress.service.IAddressService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements IAddressService {

    @Autowired
    private AddressMapper mapper;

    @Override
    public String addAddress(Address address) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, address.getUserId()).eq(Address::getIsDeleted, 0);
        address.setIsDefault(mapper.selectCount(wrapper) == 0 ? 1 : 0);
        address.setIsDeleted(0);
        address.setCreateTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());
        mapper.insert(address);
        return "ok";
    }

    @Override
    public List<Address> getAddressList(Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId).eq(Address::getIsDeleted, 0)
               .orderByDesc(Address::getIsDefault).orderByDesc(Address::getCreateTime);
        return mapper.selectList(wrapper);
    }

    @Override
    public String removeAddress(Address address) {
         Long address_id = address.getAddressId();
         mapper.deleteById(address_id);
         return "ok" ;
    }


}
