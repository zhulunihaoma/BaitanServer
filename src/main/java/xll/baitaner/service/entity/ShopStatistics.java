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
     * 今日营业额
     */
    private float todaySales;

    /**
     * 总营业额
     */
    private float totalSales;

    /**
     * 已接订单数
     */
    private int receivedOrder;
}
