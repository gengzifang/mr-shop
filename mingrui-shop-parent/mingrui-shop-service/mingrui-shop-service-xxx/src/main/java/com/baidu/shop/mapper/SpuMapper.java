package com.baidu.shop.mapper;

import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.entity.SpuEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpuMapper extends Mapper<SpuEntity> {

//    @Select(value = "select group_concat(name separator '/') as categoryName from tb_category c,tb_spu s where s.cid1 = c.id and s.cid2 = c.id and s.cid3 = c.id and c.id in (s.cid1,s.cid2,s.cid3)")
//    List<CategoryEntity> getcCategory(Integer cid1, Integer cid2, Integer cid3);

//    @Select(value = "select b.name as brandEntity from tb_spu s,tb_brand b where s.brand_id = b.id")
//    List<BrandEntity> getbrandEntity(Integer brandId);
}
