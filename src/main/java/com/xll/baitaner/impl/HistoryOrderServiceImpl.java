package com.xll.baitaner.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xll.baitaner.entity.ShopOrder;
import com.xll.baitaner.entity.ShopOrderDate;
import com.xll.baitaner.entity.VO.HistoryOrderResultVO;
import com.xll.baitaner.entity.VO.HistoryOrderVO;
import com.xll.baitaner.entity.VO.OrderDetailsVO;
import com.xll.baitaner.mapper.HistoryOrderMapper;
import com.xll.baitaner.mapper.OrderMapper;
import com.xll.baitaner.service.HistoryOrderService;
import com.xll.baitaner.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
     *
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
        String date = formatter.format(historyDate);

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
     * @param type   获取历史订单种类
     *               0： 经营数据中 全部已付款订单
     *               1： 经营数据中 在线付款订单
     *               2： 经营数据中 二维码付款订单
     *               3： 订单管理模块的历史订单  已完成订单
     * @return
     */
    @Transactional
    @Override
    public HistoryOrderResultVO getHistoryOrderList(int shopId, int type, Integer offset, Integer size) {
        Page<ShopOrderDate> page = PageHelper.startPage(offset, size).doSelectPage(() -> historyOrderMapper.selectShopOrderDateList(shopId));
        List<ShopOrderDate> shopOrderDateList = page.getResult();
        List<HistoryOrderVO> historyOrderVOList = new ArrayList<>();

        if (shopOrderDateList.size() > 0) {
            for (ShopOrderDate shopOrderDate : shopOrderDateList) {
                List<String> orderIdList;
                if (type == 0) { //经营数据中 全部已付款订单
                    orderIdList = historyOrderMapper.selectDateOrderList(shopOrderDate.getId());
                } else if (type == 1) { //经营数据中 在线付款订单
                    orderIdList = historyOrderMapper.selectDateOrderListByPayType(shopOrderDate.getId(), 0);
                } else if (type == 2) { //经营数据中 二维码付款订单
                    orderIdList = historyOrderMapper.selectDateOrderListByPayType(shopOrderDate.getId(), 1);
                } else { //订单管理模块的历史订单  已完成订单
                    orderIdList = historyOrderMapper.selectDateOrderListByState(shopOrderDate.getId(), 3);
                }

                if (orderIdList.size() <= 0)
                    continue;

                List<OrderDetailsVO> orderList = new ArrayList<>();
                for (String orderId : orderIdList) {
                    OrderDetailsVO orderDetailsVO = orderService.getOrderDetails(orderId);
                    orderList.add(orderDetailsVO);
                }

                HistoryOrderVO historyOrderVO = new HistoryOrderVO();
                historyOrderVO.setShopOrderDate(shopOrderDate);
                historyOrderVO.setOrderList(orderList);

                historyOrderVOList.add(historyOrderVO);
            }
        }
        HistoryOrderResultVO resultVO = new HistoryOrderResultVO();
        resultVO.setData(historyOrderVOList);
        resultVO.setCount(page.getTotal());
        return resultVO;
    }

    /**
     * 根据日期查找历史订单
     * @param shopId
     * @param type  获取历史订单种类
     *               0： 经营数据中 全部已付款订单
     *               1： 经营数据中 在线付款订单
     *               2： 经营数据中 二维码付款订单
     *               3： 订单管理模块的历史订单  已完成订单
     * @param date
     * @return
     */
    @Override
    public HistoryOrderVO getHistoryOrderByDate(int shopId, int type, String date) {
        //判断shop_order_date是否包含对应的记录
        ShopOrderDate shopOrderDate = historyOrderMapper.selectShopOrderDate(shopId, date);
        if (shopOrderDate == null){
            return null;
        }

        List<String> orderIdList;
        if (type == 0) { //经营数据中 全部已付款订单
            orderIdList = historyOrderMapper.selectDateOrderList(shopOrderDate.getId());
        } else if (type == 1) { //经营数据中 在线付款订单
            orderIdList = historyOrderMapper.selectDateOrderListByPayType(shopOrderDate.getId(), 0);
        } else if (type == 2) { //经营数据中 二维码付款订单
            orderIdList = historyOrderMapper.selectDateOrderListByPayType(shopOrderDate.getId(), 1);
        } else { //订单管理模块的历史订单  已完成订单
            orderIdList = historyOrderMapper.selectDateOrderListByState(shopOrderDate.getId(), 3);
        }

        List<OrderDetailsVO> orderList = new ArrayList<>();
        for (String orderId : orderIdList) {
            OrderDetailsVO orderDetailsVO = orderService.getOrderDetails(orderId);
            orderList.add(orderDetailsVO);
        }

        HistoryOrderVO historyOrderVO = new HistoryOrderVO();
        historyOrderVO.setShopOrderDate(shopOrderDate);
        historyOrderVO.setOrderList(orderList);

        return historyOrderVO;
    }
}
