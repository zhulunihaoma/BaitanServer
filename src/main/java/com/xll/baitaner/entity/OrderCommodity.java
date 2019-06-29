package com.xll.baitaner.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 描述：订单中商品详情实体类
 * 创建者：xie
 * 日期：2017/10/15
 **/

@Data
@ApiModel(value = "订单中商品详情实体类", description = "订单中商品详情实体类，下单时只需填写commodityId, count, specId三个字段")
public class OrderCommodity {

    /**
     * 商品Id
     */
    @ApiModelProperty(value = "商品Id,下单需填写", name = "commodityId")
    private int commodityId;

    /**
     * 商品个数
     */
    @ApiModelProperty(value = "商品个数,下单需填写", name = "count")
    private int count;

    /**
     * 规格id
     */
    @ApiModelProperty(value = "规格id,下单需填写", name = "specId")
    private int specId;

    /**
     * 订单编号
     */
    @ApiModelProperty(value = "订单编号", name = "orderId")
    private Long orderId;


    //规格详情
    /**
     * 规格名称
     */
    @ApiModelProperty(value = "规格名称", name = "specName")
    private String specName;

    /**
     * 规格价格
     */
//    @ApiModelProperty(value = "规格价格", name = "specPrice")
//    private float specPrice;

    @ApiModelProperty(value = "规格价格", name = "specPrice")
    private String specPrice;

    //商品详情字段

    /**
     * 商品名
     */
    @ApiModelProperty(value = "商品名", name = "name")
    private String name;

    /**
     * 商品单价
     */
    @ApiModelProperty(value = "商品单价", name = "price")
    private float price;

    /**
     * 商品单价
     */
    @ApiModelProperty(value = "商品单价", name = "price")
    private String unitPrice;

    /**
     * 月销售数目
     */
    @ApiModelProperty(value = "月销售数目", name = "monthlySales")
    private int monthlySales;

    /**
     * 商品图片
     */
    @ApiModelProperty(value = "商品图片", name = "pictUrl")
    private String pictUrl;

    /**
     * 商品描述
     */
    @ApiModelProperty(value = "商品描述", name = "introduction")
    private String introduction;

    //订单详情字段, 不在数据库中

    /**
     * 订单中收货人名字
     */
    @ApiModelProperty(value = "订单中收货人名字", name = "clientName")
    private String clientName;

    /**
     * 订单备注
     */
    @ApiModelProperty(value = "订单备注", name = "orderRemarks")
    private String orderRemarks;


    /**
     * 订单实体类
     */
    private Order order;

}
