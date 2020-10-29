package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Id;

/**
 * @ClassName slideshowyEntity
 * @Description: TODO
 * @Author gengzifang
 * @Date 2020/10/27
 * @Version V1.0
 **/
@Data
@Table(name = "tb_slideshowy")
public class SlideshowyEntity {

    @Id
    private Integer id;

    private String img;

    private Integer spuId;

    private String title;

    private Integer delFlag;

    private Date createTime;

}
