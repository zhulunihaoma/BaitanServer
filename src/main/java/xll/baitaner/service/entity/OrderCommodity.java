package xll.baitaner.service.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 描述：订单中商品及个数详情
 * 创建者：xie
 * 日期：2017/10/15
 **/

@Getter
@Setter
public class OrderCommodity {

    /**
     * 商品Id
     */
    private int commodityId;

    /**
     * 商品名
     */
    private String commodityName;

    /**
     * 商品个数
     */
    private int count;

    /**
     * 订单编号
     */
    private String orderId;

    //新增字段，出参时使用

    /**
     * 订单实体类
     */
    private Order order;

    /**
     * 订单中收货人名字
     */
    private String ClientName;

    /**
     * 订单备注
     */
    private String orderRemarks;
}
