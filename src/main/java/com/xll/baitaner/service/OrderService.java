package com.xll.baitaner.service;

import com.xll.baitaner.entity.*;
import com.xll.baitaner.entity.VO.OrderDetailsVO;
import com.xll.baitaner.entity.VO.ShopOrderVO;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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
    Long submitOrder(ShopOrderVO input);

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
     * 获取用户的订单列表
     *
     * @param openId
     * @param pageable
     * @return
     */
    PageImpl<OrderDetailsVO> getOrderListByUser(String openId, Pageable pageable);

    /**
     * 获取店铺的未付款订单 （二维码支付的订单）
     *
     * @param shopId
     * @param pageable
     * @return
     */
    PageImpl<OrderDetailsVO> getNoPayOrderListByShop(int shopId, Pageable pageable);

    /**
     * 获取店铺待完成订单
     *
     * @param shopId
     * @param pageable
     * @return
     */
    PageImpl<OrderDetailsVO> getReadyOrderLisetShop(int shopId, Pageable pageable);

    /**
     * 获取店铺的已接订单列表(按订单分类)
     *
     * @param shopId
     * @param pageable
     * @return
     */
    PageImpl<OrderDetailsVO> getTakenOrderListByShop(int shopId, Pageable pageable);

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
     * 删除订单（二维码订单且未支付的）
     *
     * @param orderId
     * @return
     */
    boolean deleteOrder(String orderId);

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
    PageImpl<HistoryOrder> getHistoryOrderList(int shopId, int type, Pageable pageable);
}
