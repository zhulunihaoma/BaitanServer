package com.xll.baitaner.entity;

import lombok.Data;

import java.util.List;

/**
 * 描述：订单按商品分类结构的实体类
 * 创建者：xie
 * 日期：2017/10/17
 **/
@Data
public class CommodityOrder {

    /**
     * 商品Id
     */
    private int commodityId;

    /**
     * 商品实体类
     */
    private Commodity commodity;

    /**
     * 商品总个数
     */
    private int totalNum;

    /**
     * 商品订单列表
     */
    private List<OrderCommodity> orderCommodityList;
}
