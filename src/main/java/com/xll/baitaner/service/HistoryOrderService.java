package com.xll.baitaner.service;

import com.xll.baitaner.entity.VO.HistoryOrderResultVO;
import com.xll.baitaner.entity.VO.HistoryOrderVO;

/**
 * 接口名：HistoryOrderService
 * 描述：历史订单模块相关接口
 * 历史订单由 日期记录ShopOrderDate一对多订单HistoryOrder
 * 创建者：xie
 * 日期：2019/7/3/003
 **/
public interface HistoryOrderService {

    /**
     * 付款成功的订单 生成历史订单
     *
     * @param orderId
     * @return
     */
    boolean creatHistoryOrder(String orderId);

    /**
     * 获取订单管理和经营数据中历史订单列表
     *
     * @param shopId
     * @param type  获取历史订单种类
     *               0： 经营数据中 全部已付款订单
     *               1： 经营数据中 在线付款订单
     *               2： 经营数据中 二维码付款订单
     *               3： 订单管理模块的历史订单  已完成订单
     * @return
     */
    HistoryOrderResultVO getHistoryOrderList(int shopId, int type, Integer offset, Integer size);

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
    HistoryOrderVO getHistoryOrderByDate(int shopId, int type, String date);
}
