package xll.baitaner.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xll.baitaner.service.entity.Order;
import xll.baitaner.service.entity.OrderCommodity;
import xll.baitaner.service.mapper.OrderMapper;

import java.util.List;
import java.util.Map;

/**
 * 描述：订单管理service
 * 创建者：xie
 * 日期：2017/10/15
 **/

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 下单处理，新增订单及订单商品数据
     * @param order
     * @param list
     * @return
     */
    @Transactional
    public boolean addOrder(Order order, List<OrderCommodity> list){
        int re = orderMapper.insertOrder(order);

        if(re > 0 ){
            String orderId = order.getOrderId();
            boolean result = true;
            for (OrderCommodity oc : list){
                result = orderMapper.insertOrderList(oc.getCommodityId(), oc.getCount(), orderId) > 0;
            }

            return result;
        }else throw new RuntimeException();
    }

    /**
     * 获取单个订单数据,包含订单中商品详情
     * @param clientId
     * @return
     */
    @Transactional
    public Order getOrder(String orderId){
        Order order = orderMapper.selectOrder(orderId);
        List<OrderCommodity> orderCoList = getOrderCoList(order.getOrderId());
        if(orderCoList.size() > 0){
            order.setOrderCoList(orderCoList);
        }
        return order;
    }

    /**
     * 获取订单的商品详情
     * @param orderId
     * @return
     */
    public List<OrderCommodity> getOrderCoList(String orderId){
        return orderMapper.selectOrderCoListByOrderId(orderId);
    }

    /**
     * 获取用户的订单列表
     * @param clientId
     * @return
     */
    public PageImpl<Order> getOrderListByClient(String clientId, Pageable pageable){
        List<Order> orderList = orderMapper.seleceOrdersByClientId(clientId, pageable);

        for(Order order : orderList){
            order.setOrderCoList(getOrderCoList(order.getOrderId()));
        }
        int count = orderMapper.countOrdersByClientId(clientId);
        return new PageImpl<Order>(orderList, pageable, count);
    }

    /**
     * 获取店铺的订单列表
     * @param shopId
     * @param state 1:待送达  2：已完成
     * @param pageable
     * @return
     */
    public PageImpl<Order> getOrderListByShop(int shopId, int state, Pageable pageable){
        List<Order> orderList = orderMapper.selectOrdersByShop(shopId, state, pageable);

        for(Order order : orderList){
            order.setOrderCoList(getOrderCoList(order.getOrderId()));
        }
        int count = orderMapper.countOrdersByShop(shopId, state);
        return new PageImpl<Order>(orderList, pageable, count);
    }
}
