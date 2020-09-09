package com.baidu.shop.mapper;

import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CategoryMapper extends Mapper<CategoryEntity> , SelectByIdListMapper<CategoryEntity,Integer> {

    @Select(value = "select c.id,c.name from tb_category c where c.id in (select cb.category_id from tb_category_brand cb where cb.brand_id=#{brandId})")
    List<CategoryEntity> getByBrand(Integer brandId);

    @Select(value = "select group_concat(name separator '/') as categoryName from tb_category c where c.id in (cid1,cid2,cid3)")
    List<CategoryEntity> getcCategory(Integer cid1, Integer cid2, Integer cid3);
}
