package com.xll.baitaner.service;

import com.xll.baitaner.entity.ShopOrder;
import com.xll.baitaner.entity.ShopWallet;
import com.xll.baitaner.entity.VO.ActivityRecordVO;
import com.xll.baitaner.entity.VO.OrderDetailsVO;

/**
 * 接口名：TemplateService
 * 描述：微信模板消息业务
 * 创建者：xie
 * 日期：2019/7/9/009
 **/
public interface TemplateService {

    /**
     * 新增formid 用于发送模板消息
     * @param openId
     * @param formId
     * @return
     */
    public boolean addFormId(String openId, String formId);

    /**
     * 获取用户的fromId用于发送模板消息
     * @param openId
     * @return
     */
    public String getFormId(String openId);

    /**
     * 将已用过的formid状态更新为不可用
     * @param formId
     * @return
     */
    public boolean updateFormidUsed(String openId, String formId);

    /**
     * 新订单通知  发送给商户
     * 线上支付成功 以及二维码支付订单
     * @param orderId
     * @return
     */
    public boolean sendNewOrderMessage(String orderId);

    /**
     * 订单（二维码支付）待支付提醒 发送给用户  二维码支付订单
     * @param orderId
     * @return
     */
    public boolean sendPendingPaymentMessage(String orderId);

    /**
     * 订单支付成功  发送给用户  线上支付订单
     * @param orderId
     * @return
     */
    public boolean sendPaySuccessfulMessage(String orderId);

    /**
     * 提现申请通知 发送给商户 提现详情
     * @param wallet
     * @return
     */
    public boolean sendWithdrawMessage(ShopWallet wallet);

    /**
     * 订单发货提醒 发送给用户
     * @return
     */
    public boolean sendOrderDeliverMessage();

    /**
     * 活动结果通知  发送给用户
     * @return
     */
    public boolean sendActivityResultMessage(ActivityRecordVO activityRecordVO);
}
