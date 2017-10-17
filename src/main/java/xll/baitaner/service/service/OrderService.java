package xll.baitaner.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xll.baitaner.service.entity.CommodityOrder;
import xll.baitaner.service.entity.Order;
import xll.baitaner.service.entity.OrderCommodity;
import xll.baitaner.service.mapper.CommodityMapper;
import xll.baitaner.service.mapper.OrderMapper;
import xll.baitaner.service.mapper.ProfileMapper;

import java.util.ArrayList;
import java.util.HashMap;
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

    @Autowired
    private ProfileMapper profileMapper;

    @Autowired
    private CommodityMapper commodityMapper;

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
     * 更新订单状态
     * @param orderId
     * @param state
     * @return
     */
    public boolean updateOrderState(String orderId, int state){
        return orderMapper.updateOrderState(orderId, state) > 0;
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
            order.setAddress(profileMapper.selectAddress(order.getReceiverAddressId()));
        }
        int count = orderMapper.countOrdersByClientId(clientId);
        return new PageImpl<Order>(orderList, pageable, count);
    }

    /**
     * 获取店铺的已接订单列表(按订单分类)
     * @param shopId
     * @param pageable
     * @return
     */
    public PageImpl<Order> getOrderListByShop(int shopId, Pageable pageable){
        List<Order> orderList = orderMapper.selectOrdersByShop(shopId, 1, pageable);

        for(Order order : orderList){
            order.setOrderCoList(getOrderCoList(order.getOrderId()));
            order.setAddress(profileMapper.selectAddress(order.getReceiverAddressId()));
        }
        int count = orderMapper.countOrdersByShop(shopId, 1);
        return new PageImpl<Order>(orderList, pageable, count);
    }

    /**
     * 获取店铺全部的商品订单关系数据列表，并插入订单实体类
     * @return
     */
    public List<OrderCommodity> getAllOrderCoList(int shopId){
        List<OrderCommodity> orderCommodityList = orderMapper.selectAllOrderCoList(shopId);
        for (OrderCommodity orderCommodity : orderCommodityList){
            Order order = orderMapper.selectOrder(orderCommodity.getOrderId());
            orderCommodity.setOrder(order);
            orderCommodity.setOrderRemarks(order.getRemarks());
            orderCommodity.setClientName(profileMapper.selectAddress(order.getReceiverAddressId()).getName());
        }
        return orderCommodityList;
    }

    /**
     * 获取店铺的已接订单列表(按商品分类)
     * @param shopId
     * @param pageable
     * @return
     */
    @Transactional
    public List<CommodityOrder> getCommdityListByShop(int shopId){
        List<OrderCommodity> orderCommodityList = getAllOrderCoList(shopId);
        if(orderCommodityList.size() > 0){
            Map<Integer, CommodityOrder> map = new HashMap<>();
            for (OrderCommodity orderCommodity : orderCommodityList){
                int coId = orderCommodity.getCommodityId();
                if(!map.containsKey(coId)){
                    CommodityOrder commodityOrder = new CommodityOrder();
                    commodityOrder.setCommodityId(coId);
                    commodityOrder.setCommodity(commodityMapper.selectCommodity(coId));
                    commodityOrder.setTotalNum(orderMapper.sumCoCount(coId));

                    List<OrderCommodity> list = new ArrayList<>();
                    list.add(orderCommodity);
                    commodityOrder.setOrderCommodityList(list);

                    map.put(coId, commodityOrder);
                }
                else {
                    List<OrderCommodity> list = map.get(coId).getOrderCommodityList();
                    list.add(orderCommodity);
                    map.get(coId).setOrderCommodityList(list);
                }
            }

            return new ArrayList<CommodityOrder>(map.values());
        }
        else {
            return null;
        }
    }
}
