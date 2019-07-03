package com.xll.baitaner.entity.VO;

import com.xll.baitaner.entity.Order;
import com.xll.baitaner.entity.ShopOrderDate;
import lombok.Data;

import java.util.List;

/**
 * 描述：历史订单实体类
 * 创建者：xie
 * 日期：2017/10/18
 **/
@Data
public class HistoryOrderVO {

    private ShopOrderDate shopOrderDate;

    /**
     * 订单列表
     */
    private List<Order> orderList;
}
