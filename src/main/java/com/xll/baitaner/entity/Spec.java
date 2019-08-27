package com.xll.baitaner.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商品规格
 */
@Data
@ApiModel(value = "商品规格", description = "商品规格信息")
public class Spec {

    private int id;

    /**
     * 商品id
     */
    @ApiModelProperty(value = "商品id", name = "commodityId")
    private int commodityId;

    /**
     * 规格名称
     */
    @ApiModelProperty(value = "规格名称", name = "name")
    private String name;

    /**
     * 规格价格
     */
    @ApiModelProperty(value = "规格价格", name = "price")
    private String price;

    /**
     * 零售价
     */
    private String retailPrice;

    /**
     * 规格库存
     */
    @ApiModelProperty(value = "规格库存", name = "stock")
    private int stock;
}
