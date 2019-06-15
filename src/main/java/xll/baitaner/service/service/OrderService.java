package xll.baitaner.service.service;

import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xll.baitaner.service.entity.*;
import xll.baitaner.service.mapper.CommodityMapper;
import xll.baitaner.service.mapper.OrderMapper;
import xll.baitaner.service.mapper.ProfileMapper;
import xll.baitaner.service.utils.LogUtils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 描述：订单管理service
 * 创建者：xie
 * 日期：2017/10/15
 **/

@Service
public class OrderService {

    private String TAG = "Baitaner-OrderService";

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProfileMapper profileMapper;

    @Autowired
    private CommodityMapper commodityMapper;

    @Autowired
    private SpecService specService;

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
            //插入订单商品
            String orderId = order.getOrderId();
            boolean result = true;
            for (OrderCommodity oc : list){
                Commodity commodity = commodityMapper.selectCommodity(oc.getCommodityId());

                if (oc.getSpecId() >= 0){
                    Spec spec = specService.getSpec(oc.getSpecId());
                    if (spec != null && spec.getCommodityId() == oc.getCommodityId()){
                        result = orderMapper.insertOrderListSpec(commodity, oc.getCount(), orderId, spec) > 0;
                        continue;
                    }
                }
                result = orderMapper.insertOrderList(commodity, oc.getCount(), orderId) > 0;

                if (!result){
                    LogUtils.error(TAG, "Order " + orderId + "--OrderCommodity: \n" + oc.toString() + "\n插入数据库失败");
                    throw new RuntimeException();
                }
            }

            return result;
        }else throw new RuntimeException();
    }

    /**
     * 获取单个订单数据,包含订单中商品详情
     * @param orderId
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
    public PageImpl<Order> getOrderListByUser(String openId, Pageable pageable){
        List<Order> orderList = orderMapper.seleceOrdersByClientId(openId, pageable);

        for(Order order : orderList){
            order.setOrderCoList(getOrderCoList(order.getOrderId()));
        }
        int count = orderMapper.countOrdersByClientId(openId);
        return new PageImpl<Order>(orderList, pageable, count);
    }

    /**
     * 获取店铺的未付款订单 （二维码支付的订单）
     * @param clientId
     * @return
     */
    public PageImpl<Order> getNoPayOrderListByShop(int shopId, Pageable pageable){
        List<Order> orderList = orderMapper.selectNoPayOrdersByShop(shopId, pageable);

        for(Order order : orderList){
            order.setOrderCoList(getOrderCoList(order.getOrderId()));
        }
        int count = orderMapper.countNoPayOrdersByShop(shopId);
        return new PageImpl<Order>(orderList, pageable, count);
    }

    /**
     * 获取店铺待完成订单
     * @param shopId
     * @param pageable
     * @return
     */
    public PageImpl<Order> getReadyOrderLisetShop(int shopId, Pageable pageable){
        //state 0：待支付;  1：已接单;  2：待完成; 3：已完成
        List<Order> orderList = orderMapper.selectOrdersByShop(shopId, 2, pageable);

        for(Order order : orderList){
            order.setOrderCoList(getOrderCoList(order.getOrderId()));
        }
        int count = orderMapper.countOrdersByShop(shopId, 2);
        return new PageImpl<Order>(orderList, pageable, count);
    }

    /**
     * 获取店铺的已接订单列表(按订单分类)
     * @param shopId
     * @param pageable
     * @return
     */
    public PageImpl<Order> getTakenOrderListByShop(int shopId, Pageable pageable){
        //state 0：待支付;  1：已接单;  2：待完成; 3：已完成
        List<Order> orderList = orderMapper.selectOrdersByShop(shopId, 1, pageable);

        for(Order order : orderList){
            order.setOrderCoList(getOrderCoList(order.getOrderId()));
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
        }
        return orderCommodityList;
    }

    /**
     * 获取店铺的已接订单列表(按商品分类)
     * @param shopId
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

                    //商品详情数据直接从OrderCommodity中获取，防止商品被删除后无数据
                    Commodity commodity = new Commodity();
                    commodity.setId(coId);
                    commodity.setName(orderCommodity.getName());
                    commodity.setPrice(orderCommodity.getPrice());
                    commodity.setMonthlySales(orderCommodity.getMonthlySales());
                    //commodity.setPraise(orderCommodity.getPraise());
                    commodity.setPictUrl(orderCommodity.getPictUrl());
                    commodity.setIntroduction(orderCommodity.getIntroduction());
                    commodityOrder.setCommodity(commodity);

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

            return new ArrayList<>(map.values());
        }
        else {
            return null;
        }
    }

    /**
     * 更新订单状态
     * @param orderId
     * @param state 0：待支付;  1：已接单;  2：待完成; 3：已完成
     * @return
     */
    @Transactional
    public boolean updateOrderState(String orderId, int state){
        boolean result = orderMapper.updateOrderState(orderId, state) > 0;
        if(state == 3){
            if(result){
                if(creatHistoryOrder(orderId)){
                    return true;
                }
                else throw new RuntimeException();
            }
            else throw new RuntimeException();
        }
        return result;
    }

    /**
     * 生成历史订单流程
     * @param orderId
     * @return
     */
    @Transactional
    public boolean creatHistoryOrder(String orderId){
        Order order = getOrder(orderId);
        int shopId = order.getShopId();
        Date historyDate = order.getDate();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String datetostr = formatter.format(historyDate);
        ParsePosition pos = new ParsePosition(0);
        Date date = formatter.parse(datetostr, pos);

        HistoryOrder ho = orderMapper.selectShopHistory(shopId, date);
        if(ho != null){
            //已存在该日期,直接插订单

            //先判断是否有此历史订单，再插入
            if(orderMapper.selectCountHistoryOrder(ho.getId(), orderId) > 0){
                return false;
            }else {
                return orderMapper.insertHistoryOrder(ho.getId(), orderId) > 0;
            }
        }
        else {
            HistoryOrder hon = new HistoryOrder();
            hon.setShopId(shopId);
            hon.setHistoryDate(date);
            int result = orderMapper.insertShopHistory(hon);
            if(result > 0){
                int id = hon.getId();
                //先判断是否有此历史订单，再插入
                if(orderMapper.selectCountHistoryOrder(id, orderId) > 0){
                    return false;
                }else {
                    return orderMapper.insertHistoryOrder(id, orderId) > 0;
                }
            }
            else {
                return false;
            }
        }
    }

    /**
     * 获取历史订单列表
     * @param shopId
     * @param pageable
     * @return
     */
    @Transactional
    public PageImpl<HistoryOrder> getHistoryOrderList(int shopId, Pageable pageable){
        List<HistoryOrder> list = orderMapper.selectHistoryOrderList(shopId, pageable);
        if(list.size() > 0){
            for (HistoryOrder ho : list){
                List<Order> orderList = orderMapper.selectDateOrderList(ho.getId());
                for(Order order : orderList){
                    order.setOrderCoList(getOrderCoList(order.getOrderId()));
                }
                ho.setOrderList(orderList);
            }
        }

        int count = orderMapper.countHistoryOrderList(shopId);
        return new PageImpl<HistoryOrder>(list, pageable, count);
    }

    /**
     * 删除订单（二维码订单且未支付的）
     *
     * @param orderId
     * @return
     */
    public boolean deleteOrder(String orderId) {
        return orderMapper.deleteOrder(orderId) > 0;
    }

}
