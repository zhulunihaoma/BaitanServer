package com.xll.baitaner.service;

import com.xll.baitaner.entity.VO.HistoryOrderVO;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * 接口名：HistoryOrderService
 * 描述：历史订单模块相关接口
 *      历史订单由 日期记录ShopOrderDate一对多订单HistoryOrder
 * 创建者：xie
 * 日期：2019/7/3/003
 **/
public interface HistoryOrderService {

    /**
     * 付款成功的订单 生成历史订单
     * @param orderId
     * @return
     */
    boolean creatHistoryOrder(String orderId);

    /**
     * 获取订单管理和经营数据中历史订单列表
     * @param shopId
     * @param type
     * @param pageable
     * @return
     */
    PageImpl<HistoryOrderVO> getHistoryOrderList(int shopId, int type, Pageable pageable);
}
