package xll.baitaner.service.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 描述：订单中商品及个数详情
 *  新增 商品详情字段 订单详情个别字段
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
     * 商品个数
     */
    private int count;

    /**
     * 订单编号
     */
    private String orderId;


    //商品详情字段

    /**
     * 商品名
     */
    private String name;

    /**
     * 商品单价
     */
    private float price;

    /**
     * 月销售数目
     */
    private int monthlySales;

    /**
     * 好评率
     */
    private String praise;

    /**
     * 商品图片
     */
    private String pictUrl;

    /**
     * 商品描述
     */
    private String introduction;



    //订单详情字段, 不在数据库中

    /**
     * 订单中收货人名字
     */
    private String clientName;

    /**
     * 订单备注
     */
    private String orderRemarks;


    /**
     * 订单实体类
     */
    private Order order;

}
