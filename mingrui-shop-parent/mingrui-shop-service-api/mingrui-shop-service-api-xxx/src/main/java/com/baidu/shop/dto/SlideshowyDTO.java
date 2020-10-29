package com.baidu.shop.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import java.util.Date;

/**
 * @ClassName slideshowyDTO
 * @Description: TODO
 * @Author gengzifang
 * @Date 2020/10/27
 * @Version V1.0
 **/
@Data
@ApiModel(value = "品牌DTO")
public class SlideshowyDTO {

    @Id
    @ApiModelProperty(value = "轮播图主键",example = "1")
    private Integer id;

    @ApiModelProperty(value = "轮播图地址")
    private String img;

    @ApiModelProperty(value = "spuid")
    private Integer spuId;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "逻辑删除标识")//1 : 删除  0 : 没有删除
    private Integer delFlag;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

}
