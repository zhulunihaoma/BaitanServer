package com.xll.baitaner.impl;

import com.xll.baitaner.entity.Order;
import com.xll.baitaner.entity.ShopOrder;
import com.xll.baitaner.entity.ShopOrderDate;
import com.xll.baitaner.entity.VO.HistoryOrderVO;
import com.xll.baitaner.mapper.HistoryOrderMapper;
import com.xll.baitaner.mapper.OrderMapper;
import com.xll.baitaner.service.HistoryOrderService;
import com.xll.baitaner.service.OrderService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 类名：HistoryOrderServiceImpl
 * 描述：历史订单模块   历史订单由日期记录ShopOrderDate一对多订单HistoryOrder
 * 创建者：xie
 * 日期：2019/7/3/003
 **/
@Service
public class HistoryOrderServiceImpl implements HistoryOrderService {

    @Resource
    private OrderService orderService;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private HistoryOrderMapper historyOrderMapper;

    /**
     * 订单状态state为1时，进入生成历史订单流程
     * 付款成功的订单，先根据日期记录shop_order_date
     * 再根据shop_order_date插入history_order
     * @param orderId
     * @return
     */
    @Transactional
    @Override
    public boolean creatHistoryOrder(String orderId) {
        ShopOrder order = orderMapper.selectShopOrderByOrderId(Long.valueOf(orderId));
        int shopId = order.getShopId();
        Date historyDate = order.getCreateDate();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String datetostr = formatter.format(historyDate);
        ParsePosition pos = new ParsePosition(0);
        Date date = formatter.parse(datetostr, pos);

        //判断shop_order_date是否包含对应的记录
        ShopOrderDate shopOrderDate = historyOrderMapper.selectShopOrderDate(shopId, date);

        if (shopOrderDate != null) {
            //已存在该日期,直接插订单

            //先判断是否有此历史订单，再插入
            if (historyOrderMapper.selectCountHistoryOrder(shopOrderDate.getId(), orderId) > 0) {
                //已存在历史订单  不合理  订单付款成功state为1时  理论上历史订单中不应该存在
                return false;
            } else {
                return historyOrderMapper.insertHistoryOrder(shopOrderDate.getId(), Long.valueOf(orderId), order.getPayType(),
                        order.getState()) > 0;
            }
        } else {
            //不存在该日期,先插入shop_order_date记录
            ShopOrderDate newShopOrderDate = new ShopOrderDate();
            newShopOrderDate.setShopId(shopId);
            newShopOrderDate.setOrderDate(date);
            int result = historyOrderMapper.insertOrderDate(newShopOrderDate);
            if (result > 0) {
                int id = newShopOrderDate.getId();//shop_order_date ID
                //先判断是否有此历史订单，再插入
                if (historyOrderMapper.selectCountHistoryOrder(id, orderId) > 0) {
                    return false;
                } else {
                    return historyOrderMapper.insertHistoryOrder(id, Long.valueOf(orderId), order.getPayType(),
                            order.getState()) > 0;
                }
            } else {
                return false;
            }
        }
    }

    /**
     * 获取订单管理和经营数据中历史订单列表
     *
     * @param shopId
     * @param type     获取历史订单种类
     * @param pageable
     * @return
     */
    @Transactional
    @Override
    public PageImpl<HistoryOrderVO> getHistoryOrderList(int shopId, int type, Pageable pageable) {
        List<ShopOrderDate> shopOrderDateList = historyOrderMapper.selectShopOrderDateList(shopId, pageable);
        List<HistoryOrderVO> historyOrderVOList = new ArrayList<>();
        if (shopOrderDateList.size() > 0) {
            for (ShopOrderDate shopOrderDate : shopOrderDateList) {
                List<Order> orderList;
                if (type == 0) { //经营数据中 全部已付款订单
                    orderList = historyOrderMapper.selectDateOrderList(shopOrderDate.getId());
                } else if (type == 1) { //经营数据中 在线付款订单
                    orderList = historyOrderMapper.selectDateOrderListByPayType(shopOrderDate.getId(), 0);
                } else if (type == 2) { //经营数据中 二维码付款订单
                    orderList = historyOrderMapper.selectDateOrderListByPayType(shopOrderDate.getId(), 1);
                } else { //订单管理模块的历史订单  已完成订单
                    orderList = historyOrderMapper.selectDateOrderListByState(shopOrderDate.getId(), 3);
                }
                for (Order order : orderList) {
                    order.setOrderCoList(orderService.getOrderCoList(order.getOrderId().toString()));
                }

                HistoryOrderVO historyOrderVO = new HistoryOrderVO();
                historyOrderVO.setShopOrderDate(shopOrderDate);
                historyOrderVO.setOrderList(orderList);

                historyOrderVOList.add(historyOrderVO);
            }
        }

        int count = historyOrderMapper.countShopOrderDateList(shopId);
        return new PageImpl<>(historyOrderVOList, pageable, count);
    }
}
