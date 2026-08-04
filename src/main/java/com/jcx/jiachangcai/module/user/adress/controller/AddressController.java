package com.jcx.jiachangcai.module.user.adress.controller;

import com.jcx.jiachangcai.module.user.adress.entity.Address;
import com.jcx.jiachangcai.module.user.adress.service.IAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private IAddressService service;

    @PostMapping("/addAddress")
    public String addAddress(Address address) {
        return service.addAddress(address);
    }

    @GetMapping("/getAddress")
    public List<Address> getAddress(Long userId) {
        return service.getAddressList(userId);
    }

    @DeleteMapping("/deleAddress")
    public String deleAddress(Address address) {
        return service.removeAddress(address);
    }
}
