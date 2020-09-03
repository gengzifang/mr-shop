package com.baidu.shop.entity;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.Generated;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName BrandEntity
 * @Description: TODO
 * @Author gengzifang
 * @Date 2020/8/31
 * @Version V1.0
 **/
@Data
@Table(name = "tb_brand")
@ApiModel(value = "品牌管理实体类")
public class BrandEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String image;

    private Character letter;

}
