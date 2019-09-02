package com.xll.baitaner.service;

import com.xll.baitaner.entity.CommodityOrder;
import com.xll.baitaner.entity.OrderCommodity;
import com.xll.baitaner.entity.ShopOrder;
import com.xll.baitaner.entity.VO.OrderDetailsResultVO;
import com.xll.baitaner.entity.VO.OrderDetailsVO;
import com.xll.baitaner.entity.VO.ShopOrderVO;
import com.xll.baitaner.utils.ResponseResult;

import java.util.List;

/**
 * @author denghuohuo 2019/6/25
 */
public interface OrderService {

    /**
     * 提交订单
     *
     * @param input
     * @return
     */
    ResponseResult submitOrder(ShopOrderVO input);

    /**
     * 下单处理，新增订单及订单商品数据
     *
     * @param order
     * @param list
     * @return
     */
    boolean addOrder(ShopOrder order, List<OrderCommodity> list);

    /**
     * 获取单个订单数据,包含订单中商品详情
     *
     * @param orderId
     * @return
     */
    OrderDetailsVO getOrderDetails(String orderId);

    /**
     * 获取订单的商品详情
     *
     * @param orderId
     * @return
     */
    List<OrderCommodity> getOrderCoList(String orderId);

    /**
     * 获取用户的订单列表
     *
     * @param openId
     * @return
     */
    OrderDetailsResultVO getOrderListByUser(String openId, Integer offset, Integer size);

    /**
     * 获取店铺的未付款订单 （二维码支付的订单）
     *
     * @param shopId
     * @return
     */
    OrderDetailsResultVO getNoPayOrderListByShop(int shopId, Integer offset, Integer size);

    /**
     * 获取店铺的已取消订单 （二维码支付的订单）
     *
     * @param shopId
     * @return
     */
    OrderDetailsResultVO getCancelledOrderListByShop(int shopId, Integer offset, Integer size);

    /**
     * 获取店铺待完成订单
     *
     * @param shopId
     * @return
     */
    OrderDetailsResultVO getReadyOrderLisetShop(int shopId, Integer offset, Integer size);

    /**
     * 获取店铺的已接订单列表(按订单分类)
     *
     * @param shopId
     * @return
     */
    OrderDetailsResultVO getTakenOrderListByShop(int shopId, Integer offset, Integer size);

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
     * @param state   -1: 已取消;  0：待支付;  1：已接单;  2：待完成; 3：已完成
     * @return
     */
    boolean updateOrderState(String orderId, int state);

    /**
     * 删除订单（限制只能删除已取消订单）
     *
     * @param orderId
     * @return
     */
    boolean deleteOrder(String orderId);
}
