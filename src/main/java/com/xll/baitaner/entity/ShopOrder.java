package com.xll.baitaner.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 订单表实体类
 *
 * @author denghuohuo 2019/6/28
 */
@Data
@ApiModel(value = "订单实体类", description = "订单信息实体类")
public class ShopOrder {

    private Integer id;

    /**
     * 订单编号 后台随机生成唯一标识
     */
    @ApiModelProperty(value = "订单编号 后台随机生成唯一标识", name = "orderId")
    private Long orderId;

    /**
     * 用户Id
     */
    @ApiModelProperty(value = "用户Id", name = "openId")
    private String openId;

    /**
     * 店铺Id
     */
    @ApiModelProperty(value = "店铺ID", name = "shopId")
    private Integer shopId;

    private Integer addressId;

    /**
     * 下单时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "下单时间", name = "createDate")
    private Date createDate;

    /**
     * 支付方式
     * 0：在线支付
     * 1：二维码支付
     */
    @ApiModelProperty(value = "支付方式  0：在线支付  1：二维码支付", name = "payType")
    private Integer payType;

    /**
     * 订单总金额
     */
    @ApiModelProperty(value = "订单总金额", name = "totalMoney")
    private String totalMoney;

    /**
     * 订单运费
     */
    @ApiModelProperty(value = "订单运费", name = "postage")
    private String postage;

    /**
     * 订单状态
     * 0：待支付
     * 1：已接单
     * 2：待完成
     * 3：已完成
     */
    @ApiModelProperty(value = "订单状态  0：待支付  1：已接单  2：待完成   3：已完成", name = "state")
    private Integer state;

    /**
     * 是否活动订单  默认false
     */
    @ApiModelProperty(value = "是否活动订单  默认false", name = "activityNot")
    private Integer activityNot;

    /**
     * 活动id  默认无
     */
    @ApiModelProperty(value = "活动id  默认无", name = "activityId")
    private String activityId;

    private Integer delFlag;

    /**
     * 订单备注
     */
    @ApiModelProperty(value = "订单备注", name = "remarks")
    private String remarks;

}
