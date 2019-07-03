package com.xll.baitaner.entity;

/**
 * 类名：HistoryOrder
 * 描述：历史订单实体类
 * 创建者：xie
 * 日期：2019/7/3/003
 **/
public class HistoryOrder {

    private Integer id;

    /**
     * ShopOrderDate记录的日期实体类 Id
     */
    private Integer dateId;

    /**
     * 支付方式
     * 0：在线支付
     * 1：二维码支付
     */
    private Integer payType;

    /**
     * 订单状态
     * 0：待支付
     * 1：已接单
     * 2：待完成
     * 3：已完成
     */
    private Integer state;

    private Long orderId;
}
