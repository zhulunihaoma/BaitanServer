package com.xll.baitaner.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 描述：历史订单实体类
 * 创建者：xie
 * 日期：2017/10/18
 **/
@Data
public class HistoryOrder {

    private int id;

    /**
     * 店铺Id
     */
    private int shopId;

    /**
     * 支付方式
     * 0：在线支付
     * 1：二维码支付
     */
    @ApiModelProperty(value = "支付方式  0：在线支付  1：二维码支付", name = "payType")
    private int payType;

    /**
     * 订单状态
     * 0：待支付
     * 1：已接单
     * 2：待完成
     * 3：已完成
     */
    @ApiModelProperty(value = "订单状态  0：待支付  1：已接单  2：待完成   3：已完成", name = "state")
    private int state;

    /**
     * 历史日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date historyDate;

    /**
     * 历史订单列表
     */
    private List<Order> orderList;
}
