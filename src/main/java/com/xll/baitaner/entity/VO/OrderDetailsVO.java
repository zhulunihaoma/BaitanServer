package com.xll.baitaner.entity.VO;

import com.xll.baitaner.entity.OrderCommodity;
import com.xll.baitaner.entity.ReceiverAddress;
import com.xll.baitaner.entity.Shop;
import com.xll.baitaner.entity.ShopOrder;
import lombok.Data;

import java.util.List;

/**
 * 订单详情
 * @author dengyy
 * @date 19-7-1
 */
@Data
public class OrderDetailsVO {

    private Shop shop;

    private ReceiverAddress address;

    private ShopOrder shopOrder;

    private List<OrderCommodity> commoditys;
}
