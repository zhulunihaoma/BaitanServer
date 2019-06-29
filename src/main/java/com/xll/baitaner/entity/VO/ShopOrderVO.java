package com.xll.baitaner.entity.VO;

import com.xll.baitaner.entity.OrderCommodity;
import com.xll.baitaner.entity.ShopOrder;
import lombok.Data;

import java.util.List;

/**
 *
 * @author denghuohuo 2019/6/28
 */
@Data
public class ShopOrderVO {

    private ShopOrder order;

    private List<OrderCommodity> commodityList;
}
