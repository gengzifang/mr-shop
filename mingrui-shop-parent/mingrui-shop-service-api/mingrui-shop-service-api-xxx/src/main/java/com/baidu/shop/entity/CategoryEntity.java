package com.baidu.shop.entity;

import com.baidu.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @ClassName CategoryEntity
 * @Description: TODO
 * @Author gengzifang
 * @Date 2020/8/27
 * @Version V1.0
 **/
@Data
@Table(name = "tb_category")
@ApiModel(value = "分类实体类")
public class CategoryEntity {

    @Id
    @ApiModelProperty(value = "主键",example = "1")
    @NotNull(message = "主键不能为空")
    //此处要引出来一个分组的概念,就是当前参数校验属于哪个组
    //有的操作不需要验证次参数就比如说新增就不需要校验id,但是修改需要
    @NotNull(message = "ID不能空",groups = {MingruiOperation.Update.class})
    private Integer id;

    @ApiModelProperty(value = "类目名称")
    @NotEmpty(message = "类目名称不能为空")
    //新增和修改都需要校验此参数
    @NotEmpty(message = "分类名称不能为空",groups = {MingruiOperation.Add.class, MingruiOperation.Update.class})
    private String name;

    @ApiModelProperty(value = "父类目id",example = "1")
    @NotNull(message = "父类目id不能为空")
    @NotNull(message = "父级分类不能为null",groups = {MingruiOperation.Add.class})
    private Integer parentId;

    @ApiModelProperty(value = "是否为父节点,0为否,1为是",example = "1")
    @NotNull(message = "是否为父节点不能为空")
    @NotNull(message = "是否为父级节点不能为null",groups = {MingruiOperation.Add.class})
    private Integer isParent;

    @ApiModelProperty(value = "排序指数,越小越靠前",example = "1")
    @NotNull(message = "排序指数不能为空")
    @NotNull(message = "排序字段不能为null",groups = {MingruiOperation.Add.class})
    private Integer sort;


}
