package com.zzx.jiachangcai.mapper;

import com.zzx.jiachangcai.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    List<User> selectAllUser();
}
