package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Table;

/**
 * @ClassName CategoryBrandEntity
 * @Description: TODO
 * @Author gengzifang
 * @Date 2020/9/1
 * @Version V1.0
 **/
@Data
@Table(name = "tb_category_brand")
public class CategoryBrandEntity {

    private Integer categoryId;

    private Integer brandId;

}
