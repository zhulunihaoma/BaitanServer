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
     * 新订单通知  发送给商户
     * 线上支付成功 以及二维码支付订单
     * 小程序模板消息
     * <p>
     * 订单号     {{keyword1.DATA}}
     * 订单总价   {{keyword2.DATA}}
     * 支付时间   {{keyword3.DATA}}
     * 商品名称   {{keyword4.DATA}}
     * 数量       {{keyword5.DATA}}
     * 收货地址   {{keyword6.DATA}}
     * 支付状态   {{keyword7.DATA}}
     */
    public static final String NewOrderMessageId_APPLET = "ujyjqlbkEYqhA2GXaMEfYEMdczf8TAzF3NJ0kV7-Ciw";
    /**
     * 新订单通知  发送给商户
     * 线上支付成功 以及二维码支付订单
     * 公众号模板消息
     * <p>
     * {{first.DATA}}
     * 订单编号：{{keyword1.DATA}}
     * 客户昵称：{{keyword2.DATA}}
     * 订单价格：{{keyword3.DATA}}
     * 订单标题：{{keyword4.DATA}}
     * 订单截止时间：{{keyword5.DATA}}
     * {{remark.DATA}}
     */
    public static final String NewOrderMessageId_PIBLIC = "itFb7D_HBWK7DzAhPxVHX7VstFyYFD1xg-Jf2-EkyBw";
    //跳转小程序页面地址
    public static final String NewOrderGoPage = "pages/mycenter/ordermanager/ordermanager";


    /**
     * 订单（二维码支付）待支付提醒 发送给用户  二维码支付订单
     * 小程序模板消息
     * <p>
     * 订单号     {{keyword1.DATA}}
     * 订单总价   {{keyword2.DATA}}
     * 下单时间   {{keyword3.DATA}}
     * 商品详情   {{keyword4.DATA}}
     * 付款类型   {{keyword4.DATA}}
     * 待付金额   {{keyword6.DATA}}
     * 备注       {{keyword7.DATA}}
     * 收货地址   {{keyword8.DATA}}
     */
    public static final String PendingPaymentMessageId_APPLET = "czhVdoQz1X4lwfHETBg0mKyGPpnj8UBYDho12ojJYKA";
    /**
     * 订单（二维码支付）待支付提醒 发送给用户  二维码支付订单
     * 公众号模板消息
     * <p>
     * {{first.DATA}}
     * 商品名称：{{keyword1.DATA}}
     * 商品价格：{{keyword2.DATA}}
     * 商品数量：{{keyword3.DATA}}
     * {{remark.DATA}}
     */
    public static final String PendingPaymentMessageId_PIBLIC = "8xrqFUTi4E6O7qm7iBFEK7PSI82GQ1BTGg027-ORYOI";
    //跳转小程序页面地址
    public static final String PendingPaymentPage = "pages/order/orderlist/orderlist?orderstate=0";


    /**
     * 订单支付成功  发送给用户  线上支付订单
     * 小程序模板消息
     * <p>
     * 订单号     {{keyword1.DATA}}
     * 支付时间   {{keyword2.DATA}}
     * 订单金额   {{keyword3.DATA}}
     * 商品名称   {{keyword4.DATA}}
     * 数量       {{keyword5.DATA}
     * 支付方式   {{keyword6.DATA}}
     * 收货地址   {{keyword7.DATA}}
     *
     */
    public static final String PaySuccessfulMessageId_APPLET = "upiW0jHo9pfyrYKHAgzqpsfYvi1VFPg3nPWNMwHayl8";
    /**
     * 订单支付成功  发送给用户  线上支付订单
     * 公众号模板消息
     * <p>
     * {{first.DATA}}
     * 支付金额：{{orderMoneySum.DATA}}
     * 商品信息：{{orderProductName.DATA}}
     * {{Remark.DATA}}
     */
    public static final String PaySuccessfulMessageId_PIBLIC = "0OiBUsjbwVCY-pDBemZwJrAhpYCB0OUPjPfCAhwdoEo";
    //跳转小程序页面地址
    public static final String PaySuccessfulPage = "pages/order/orderlist/orderlist";


    /**
     * 提现申请通知 发送给商户 提现详情
     * 小程序模板消息
     * <p>
     * 提现金额   {{keyword1.DATA}}
     * 提现时间   {{keyword2.DATA}}
     * 姓名       {{keyword3.DATA}}
     * 帐号       {{keyword4.DATA}}
     * 到账类型   {{keyword5.DATA}}
     * 备注       {{keyword6.DATA}}
     */
    public static final String WithdrawMessageId_APPLET = "dSs2OuJBcrNWmHx09U-3hedr-SjZviB6KbJkgcbkSe8";
    /**
     * 提现申请通知 发送给商户 提现详情
     * 公众号模板消息
     * <p>
     * {{first.DATA}}
     * 昵称：{{keyword1.DATA}}
     * 时间：{{keyword2.DATA}}
     * 金额：{{keyword3.DATA}}
     * 方式：{{keyword4.DATA}}
     * {{remark.DATA}}
     */
    public static final String WithdrawMessageId_PIBLIC = "u5NFEDBNiH-dmoWf0MIujAMDccs8zhA40wcCjTwLJxI";
    //跳转小程序页面地址
    public static final String WithdrawGoPage = "pages/mycenter/wallet/wallet";


    /**
     * 活动结果通知  发送给用户
     * 小程序模板消息
     * 用户昵称  {{keyword1.DATA}}
     * 助力结果  {{keyword2.DATA}}
     */
    public static final String ActivityResultMessageId_APPLET = "wYk0pJrZ-zCaV_MyO8lXMUcB-LIvravSs1Rnur0PR10";
    //跳转小程序页面地址
    public static final String ActivityResultPage = "";


    /**
     * 订单发货提醒 发送给用户
     * 小程序模板消息
     */
    public static final String OrderDeliverMessageId_APPLET = "7AyFmVh0LnaiaFZ55HWtvg7UazkwrXYUC2CZUGp3TLY";
    /**
     * 订单发货提醒 发送给用户
     * 公众号模板消息
     * {{first.DATA}}
     * 商品名称：{{keyword1.DATA}}
     * 快递公司：{{keyword2.DATA}}
     * 快递单号：{{keyword3.DATA}}
     * 收货地址：{{keyword4.DATA}}
     * {{remark.DATA}}
     */
    public static final String OrderDeliverMessageId_PIBLIC = "tBfYfvkKHLt-KJEjYN5BlccvCV025SdKznqu41t-YYE";
    //跳转小程序页面地址
    public static final String OrderDeliverPage = "pages/order/orderlist/orderlist";

    /**
     * 新增formid 用于发送小程序模板消息
     * @param openId
     * @param formId
     * @return
     */
    public boolean addFormId(String openId, String formId);

    /**
     * 获取用户的fromId用于发送小程序模板消息
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
