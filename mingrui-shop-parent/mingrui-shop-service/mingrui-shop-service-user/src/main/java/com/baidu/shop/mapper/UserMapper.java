package com.baidu.shop.mapper;

import com.baidu.shop.entity.UserEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface UserMapper extends Mapper<UserEntity> {

    @Select(value = "select * from tb_user where logicdel = 0")
    List<UserEntity> selectUser();
}
