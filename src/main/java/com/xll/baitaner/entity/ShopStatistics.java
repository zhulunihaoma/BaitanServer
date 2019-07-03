package com.xll.baitaner.entity;

import lombok.Data;

/**
 * 描述：店铺销售统计数据实体类
 * 创建者：xie
 * 日期：2017/10/12
 **/

@Data
public class ShopStatistics {

    /**
     * 店铺Id
     */
    private int shopId;

    /**
     * 今日已接订单数
     */
    private int todayReceivedOrder;
    /**
     * 昨日已接订单数
     */
    private int yesterdayReceivedOrder;

    /**
     * 今日营业额
     */
    private String todaySales;
    /**
     * 昨日营业额
     */
    private String yesterdaySales;

    /**
     * 本周营业额
     */
    private String thisweekSales;
    /**
     * 上周营业额
     */
    private String lastweekSales;

    /**
     * 本月营业额
     */
    private String thismonthSales;
    /**
     * 上个月营业额
     */
    private String lastmonthSales;

    /**
     * 总营业额
     */
    private String totalSales;


}
