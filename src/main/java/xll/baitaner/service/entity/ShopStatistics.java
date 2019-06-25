package xll.baitaner.service.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 描述：店铺销售统计数据实体类
 * 创建者：xie
 * 日期：2017/10/12
 **/

@Getter
@Setter
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
    private float todaySales;
    /**
     * 昨日营业额
     */
    private float yesterdaySales;

    /**
     * 本周营业额
     */
    private float thisweekSales;
    /**
     * 上周营业额
     */
    private float lastweekSales;

    /**
     * 本月营业额
     */
    private float thismonthSales;
    /**
     * 上个月营业额
     */
    private float lastmonthSales;

    /**
     * 总营业额
     */
    private float totalSales;


}
