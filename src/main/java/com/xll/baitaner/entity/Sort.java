package com.xll.baitaner.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 店铺首页的查看分类
 */
@Data
@ApiModel(value = "分类实体类", description = "店铺首页的查看分类")
public class Sort {

    /**
     * 分类ID
     */
    private int id;

    /**
     * 分类的店铺Id
     */
    @ApiModelProperty(value = "分类的店铺Id", name = "shopId")
    private int shopId;

    /**
     * 分类名称
     */
    @ApiModelProperty(value = "分类名称", name = "sortName")
    private String sortName;

    /**
     * 分类的排列位次
     */
    @ApiModelProperty(value = "分类的排列位次", name = "sortOrder")
    private int sortOrder;

}
