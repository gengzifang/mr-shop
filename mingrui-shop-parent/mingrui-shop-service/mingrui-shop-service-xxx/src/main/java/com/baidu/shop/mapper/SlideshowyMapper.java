package com.baidu.shop.mapper;

import com.baidu.base.Result;
import com.baidu.shop.entity.SlideshowyEntity;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.json.JSONObject;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SlideshowyMapper extends Mapper<SlideshowyEntity> {

    @Select(value = "select * from tb_slideshowy where del_flag = 0")
    List<SlideshowyEntity> selectdelFlag();

}
