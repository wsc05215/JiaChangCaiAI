package com.jcx.jiachangcai.module.user.adress.service;

import com.jcx.jiachangcai.module.user.adress.entity.Address;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IAddressService extends IService<Address> {
    String addAddress(Address address);
    List<Address> getAddressList(Long userId);
     String removeAddress(Address address);
}
