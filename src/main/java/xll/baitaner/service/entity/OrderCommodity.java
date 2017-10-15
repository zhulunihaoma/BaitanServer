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
     * 商品个数
     */
    private int count;
}
