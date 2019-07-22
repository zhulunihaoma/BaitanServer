package com.xll.baitaner.service;

import com.xll.baitaner.utils.ResponseResult;

/**
 * @author denghuohuo 2019/6/25
 */
public interface PayService {

    /**
     * 发起支付
     *
     * @param orderId
     * @param openId
     * @param payType 0：微信商户支付 1：钱方支付
     * @return
     */
    ResponseResult payMent(String orderId, String openId, int payType);

    /**
     * 根据支付异步通知结果验证支付是否成功，并更改订单状态 //TODO 增加流水记录
     *
     * @param orderId   订单号
     * @param total_fee 订单金额
     * @param payChannel 0:钱方支付，1：微信支付
     */
    void PayResuleCheck(String orderId, String total_fee, Integer payChannel);
}
