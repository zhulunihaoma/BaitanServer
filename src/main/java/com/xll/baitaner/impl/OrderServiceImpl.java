package com.xll.baitaner.impl;

import com.xll.baitaner.entity.Commodity;
import com.xll.baitaner.entity.CommodityOrder;
import com.xll.baitaner.entity.HistoryOrder;
import com.xll.baitaner.entity.Order;
import com.xll.baitaner.entity.OrderCommodity;
import com.xll.baitaner.entity.Spec;
import com.xll.baitaner.mapper.CommodityMapper;
import com.xll.baitaner.mapper.OrderMapper;
import com.xll.baitaner.service.OrderService;
import com.xll.baitaner.service.SpecService;
import com.xll.baitaner.utils.LogUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述：订单管理service
 * 创建者：xie
 * 日期：2017/10/15
 **/

@Service
public class OrderServiceImpl implements OrderService {

    private final String TAG = "Baitaner-OrderService";

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private CommodityMapper commodityMapper;

    @Resource
    private SpecService specService;

    /**
     * 下单处理，新增订单及订单商品数据
     *
     * @param order
     * @param list
     * @return
     */
    @Transactional
    @Override
    public boolean addOrder(Order order, List<OrderCommodity> list) {
        int re = orderMapper.insertOrder(order);

        if (re > 0) {
            //插入订单商品
            String orderId = order.getOrderId();
            boolean result = true;
            for (OrderCommodity oc : list) {
                Commodity commodity = commodityMapper.selectCommodity(oc.getCommodityId());

                if (oc.getSpecId() >= 0) {
                    Spec spec = specService.getSpec(oc.getSpecId());
                    if (spec != null && spec.getCommodityId() == oc.getCommodityId()) {
                        result = orderMapper.insertOrderListSpec(commodity, oc.getCount(), orderId, spec) > 0;
                        continue;
                    }
                }
                result = orderMapper.insertOrderList(commodity, oc.getCount(), orderId) > 0;

                if (!result) {
                    LogUtils.error(TAG, "Order " + orderId + "--OrderCommodity: \n" + oc.toString() + "\n插入数据库失败");
                    throw new RuntimeException();
                }
            }

            return result;
        } else throw new RuntimeException();
    }

    /**
     * 获取单个订单数据,包含订单中商品详情
     *
     * @param orderId
     * @return
     */
    @Transactional
    @Override
    public Order getOrder(String orderId) {
        Order order = orderMapper.selectOrder(orderId);
        List<OrderCommodity> orderCoList = getOrderCoList(order.getOrderId());
        if (orderCoList.size() > 0) {
            order.setOrderCoList(orderCoList);
        }
        return order;
    }

    /**
     * 获取订单的商品详情
     *
     * @param orderId
     * @return
     */
    private List<OrderCommodity> getOrderCoList(String orderId) {
        return orderMapper.selectOrderCoListByOrderId(orderId);
    }

    /**
     * 获取用户的订单列表
     *
     * @param openId
     * @param pageable
     * @return
     */
    @Override
    public PageImpl<Order> getOrderListByUser(String openId, Pageable pageable) {
        List<Order> orderList = orderMapper.seleceOrdersByClientId(openId, pageable);

        for (Order order : orderList) {
            order.setOrderCoList(getOrderCoList(order.getOrderId()));
        }
        int count = orderMapper.countOrdersByClientId(openId);
        return new PageImpl<>(orderList, pageable, count);
    }

    /**
     * 获取店铺的未付款订单 （二维码支付的订单）
     *
     * @param shopId
     * @param pageable
     * @return
     */
    @Override
    public PageImpl<Order> getNoPayOrderListByShop(int shopId, Pageable pageable) {
        List<Order> orderList = orderMapper.selectNoPayOrdersByShop(shopId, pageable);

        for (Order order : orderList) {
            order.setOrderCoList(getOrderCoList(order.getOrderId()));
        }
        int count = orderMapper.countNoPayOrdersByShop(shopId);
        return new PageImpl<>(orderList, pageable, count);
    }

    /**
     * 获取店铺待完成订单
     *
     * @param shopId
     * @param pageable
     * @return
     */
    @Override
    public PageImpl<Order> getReadyOrderLisetShop(int shopId, Pageable pageable) {
        //state 0：待支付;  1：已接单;  2：待完成; 3：已完成
        List<Order> orderList = orderMapper.selectOrdersByShop(shopId, 2, pageable);

        for (Order order : orderList) {
            order.setOrderCoList(getOrderCoList(order.getOrderId()));
        }
        int count = orderMapper.countOrdersByShop(shopId, 2);
        return new PageImpl<>(orderList, pageable, count);
    }

    /**
     * 获取店铺的已接订单列表(按订单分类)
     *
     * @param shopId
     * @param pageable
     * @return
     */
    @Override
    public PageImpl<Order> getTakenOrderListByShop(int shopId, Pageable pageable) {
        //state 0：待支付;  1：已接单;  2：待完成; 3：已完成
        List<Order> orderList = orderMapper.selectOrdersByShop(shopId, 1, pageable);

        for (Order order : orderList) {
            order.setOrderCoList(getOrderCoList(order.getOrderId()));
        }
        int count = orderMapper.countOrdersByShop(shopId, 1);
        return new PageImpl<>(orderList, pageable, count);
    }


    /**
     * 获取店铺全部的商品订单关系数据列表，并插入订单实体类
     *
     * @return
     */
    private List<OrderCommodity> getAllOrderCoList(int shopId) {
        List<OrderCommodity> orderCommodityList = orderMapper.selectAllOrderCoList(shopId);
        for (OrderCommodity orderCommodity : orderCommodityList) {
            Order order = orderMapper.selectOrder(orderCommodity.getOrderId());
            orderCommodity.setOrder(order);
        }
        return orderCommodityList;
    }

    /**
     * 获取店铺的已接订单列表(按商品分类)
     *
     * @param shopId
     * @return
     */
    @Transactional
    @Override
    public List<CommodityOrder> getCommdityListByShop(int shopId) {
        List<OrderCommodity> orderCommodityList = getAllOrderCoList(shopId);
        if (orderCommodityList.size() > 0) {
            Map<Integer, CommodityOrder> map = new HashMap<>();
            for (OrderCommodity orderCommodity : orderCommodityList) {
                int coId = orderCommodity.getCommodityId();
                if (!map.containsKey(coId)) {
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
                } else {
                    List<OrderCommodity> list = map.get(coId).getOrderCommodityList();
                    list.add(orderCommodity);
                    map.get(coId).setOrderCommodityList(list);
                }
            }

            return new ArrayList<>(map.values());
        } else {
            return null;
        }
    }

    /**
     * 更新订单状态
     *
     * @param orderId
     * @param state   0：待支付;  1：已接单;  2：待完成; 3：已完成
     * @return
     */
    @Transactional
    @Override
    public boolean updateOrderState(String orderId, int state) {
        boolean result = orderMapper.updateOrderState(orderId, state) > 0;
        if (result) {
            if (state == 1) {
                //state更新为1：已接单 创建历史订单
                if (creatHistoryOrder(orderId)) {
                    return true;
                } else throw new RuntimeException();
            } else if (state > 1) {
                //更新历史订单状态
                return orderMapper.updateHistoryorderState(orderId, state) > 0;
            }
        }
        return false;
    }

    /**
     * 生成历史订单流程
     *
     * @param orderId
     * @return
     */
    @Transactional
    public boolean creatHistoryOrder(String orderId) {
        Order order = getOrder(orderId);
        int shopId = order.getShopId();
        Date historyDate = order.getDate();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String datetostr = formatter.format(historyDate);
        ParsePosition pos = new ParsePosition(0);
        Date date = formatter.parse(datetostr, pos);

        HistoryOrder ho = orderMapper.selectShopHistory(shopId, date);
        if (ho != null) {
            //已存在该日期,直接插订单

            //先判断是否有此历史订单，再插入
            if (orderMapper.selectCountHistoryOrder(ho.getId(), orderId) > 0) {
                return false;
            } else {
                return orderMapper.insertHistoryOrder(ho.getId(), orderId, order.getPayType(), order.getState()) > 0;
            }
        } else {
            HistoryOrder hon = new HistoryOrder();
            hon.setShopId(shopId);
            hon.setHistoryDate(date);
            int result = orderMapper.insertShopHistory(hon);
            if (result > 0) {
                int id = hon.getId();
                //先判断是否有此历史订单，再插入
                if (orderMapper.selectCountHistoryOrder(id, orderId) > 0) {
                    return false;
                } else {
                    return orderMapper.insertHistoryOrder(id, orderId, order.getPayType(), order.getState()) > 0;
                }
            } else {
                return false;
            }
        }
    }

    /**
     * 获取历史订单列表
     * <p>
     * 获取订单管理和经营数据中历史订单列表
     *
     * @param shopId
     * @param type     获取历史订单种类
     * @param pageable
     * @return
     */
    @Transactional
    @Override
    public PageImpl<HistoryOrder> getHistoryOrderList(int shopId, int type, Pageable pageable) {
        List<HistoryOrder> list = orderMapper.selectHistoryOrderList(shopId, pageable);
        if (list.size() > 0) {
            for (HistoryOrder ho : list) {
                List<Order> orderList;
                if (type == 0) { //经营数据中 全部已付款订单
                    orderList = orderMapper.selectDateOrderList(ho.getId());
                } else if (type == 1) { //经营数据中 在线付款订单
                    orderList = orderMapper.selectDateOrderListByPayType(ho.getId(), 0);
                } else if (type == 2) { //经营数据中 二维码付款订单
                    orderList = orderMapper.selectDateOrderListByPayType(ho.getId(), 1);
                } else { //订单管理模块的历史订单  已完成订单
                    orderList = orderMapper.selectDateOrderListByState(ho.getId(), 3);
                }

                for (Order order : orderList) {
                    order.setOrderCoList(getOrderCoList(order.getOrderId()));
                }
                ho.setOrderList(orderList);
            }
        }

        int count = orderMapper.countHistoryOrderList(shopId);
        return new PageImpl<>(list, pageable, count);
    }

    /**
     * 获取店铺除了未支付外的所有订单按照日期和支付方式查询(payType = -1查询所有支付方式订单)
     *
     * @param shopId
     * @param pageable
     * @return
     */
    private PageImpl<Order> selectOrdersByShopAndPayTypeAndDate(int shopId, int payType, Date date, Pageable pageable) {
        //state 0：待支付;  1：已接单;  2：待完成; 3：已完成
        List<Order> orderList;
        int count;
        if (payType == -1) {
            orderList = orderMapper.selectOrdersByShopAllAndDate(shopId, date, pageable);
            count = orderMapper.countOrdersByShopAllAndDate(shopId, date);

        } else {
            orderList = orderMapper.selectOrdersByShopAndPayTypeAndDate(shopId, payType, date, pageable);
            count = orderMapper.countOrdersByShopAndPayTypeAndDate(shopId, payType, date);

        }

        for (Order order : orderList) {
            order.setOrderCoList(getOrderCoList(order.getOrderId()));
        }

        return new PageImpl<>(orderList, pageable, count);
    }


    /**
     * 删除订单（二维码订单且未支付的）
     *
     * @param orderId
     * @return
     */
    @Override
    public boolean deleteOrder(String orderId) {
        return orderMapper.deleteOrder(orderId) > 0;
    }

}
