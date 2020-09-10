package com.baidu.shop.mapper;


import com.baidu.shop.entity.SpuEntity;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;



public interface SpuMapper extends Mapper<SpuEntity> {

    @Update(value = "UPDATE tb_spu SET saleable = #{saleable} WHERE id = #{spuId}")
    void editGoodsBySaleable(Integer spuId, Integer saleable);
}
