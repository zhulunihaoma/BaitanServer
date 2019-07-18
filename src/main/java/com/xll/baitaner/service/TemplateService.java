package com.xll.baitaner.service;

import com.xll.baitaner.entity.ShopOrder;
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
     *
     订单号     {{keyword1.DATA}}
     订单总价   {{keyword2.DATA}}
     支付时间   {{keyword3.DATA}}
     商品名称   {{keyword4.DATA}}
     数量       {{keyword5.DATA}}
     收货地址   {{keyword6.DATA}}
     支付状态   {{keyword7.DATA}}
     * @param orderId
     * @return
     */
    public boolean sendNewOrderMessage(String orderId);
}
