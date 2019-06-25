package com.xll.baitaner.service;

import com.xll.baitaner.entity.CommodityOrder;
import com.xll.baitaner.entity.HistoryOrder;
import com.xll.baitaner.entity.Order;
import com.xll.baitaner.entity.OrderCommodity;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author denghuohuo 2019/6/25
 */
public interface OrderService {

    /**
     * 下单处理，新增订单及订单商品数据
     *
     * @param order
     * @param list
     * @return
     */
    boolean addOrder(Order order, List<OrderCommodity> list);

    /**
     * 获取单个订单数据,包含订单中商品详情
     *
     * @param orderId
     * @return
     */
    Order getOrder(String orderId);

    /**
     * 获取用户的订单列表
     *
     * @param openId
     * @param pageable
     * @return
     */
    PageImpl<Order> getOrderListByUser(String openId, Pageable pageable);

    /**
     * 获取店铺的未付款订单 （二维码支付的订单）
     *
     * @param shopId
     * @param pageable
     * @return
     */
    PageImpl<Order> getNoPayOrderListByShop(int shopId, Pageable pageable);

    /**
     * 获取店铺待完成订单
     *
     * @param shopId
     * @param pageable
     * @return
     */
    PageImpl<Order> getReadyOrderLisetShop(int shopId, Pageable pageable);

    /**
     * 获取店铺的已接订单列表(按订单分类)
     *
     * @param shopId
     * @param pageable
     * @return
     */
    PageImpl<Order> getTakenOrderListByShop(int shopId, Pageable pageable);

    /**
     * 获取店铺的已接订单列表(按商品分类)
     *
     * @param shopId
     * @return
     */
    List<CommodityOrder> getCommdityListByShop(int shopId);

    /**
     * 更新订单状态
     *
     * @param orderId
     * @param state   0：待支付;  1：已接单;  2：待完成; 3：已完成
     * @return
     */
    boolean updateOrderState(String orderId, int state);

    /**
     * 获取历史订单列表
     *
     * @param shopId
     * @param pageable
     * @return
     */
    PageImpl<HistoryOrder> getHistoryOrderList(int shopId, int payType, int state, Pageable pageable);

    /**
     * 删除订单（二维码订单且未支付的）
     *
     * @param orderId
     * @return
     */
    boolean deleteOrder(String orderId);

    /**
     * 获取历史订单列表
     *
     * 获取订单管理和经营数据中历史订单列表
     * @param shopId
     * @param type  获取历史订单种类
     * @param pageable
     * @return
     */
    public PageImpl<HistoryOrder> getHistoryOrderList(int shopId, int type, Pageable pageable);
}
