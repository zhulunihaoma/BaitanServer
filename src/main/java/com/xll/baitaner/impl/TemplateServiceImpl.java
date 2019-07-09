package com.xll.baitaner.impl;

import com.xll.baitaner.service.TemplateService;
import org.springframework.stereotype.Service;

/**
 * 类名：TemplateServiceImpl
 * 描述：微信模板消息业务Impl
 * 创建者：xie
 * 日期：2019/7/4/004
 **/
@Service
public class TemplateServiceImpl implements TemplateService{

    private String TAG = "Baitaner-TemplateServiceImpl";

    /**
     * 新订单通知
     */
    public static final String NewOrderMessageId = "ujyjqlbkEYqhA2GXaMEfYEMdczf8TAzF3NJ0kV7-Ciw";

    /**
     * 提现申请通知
     */
    public static final String WithdrawMessageId = "dSs2OuJBcrNWmHx09U-3hedr-SjZviB6KbJkgcbkSe8";

    /**
     * 订单发货提醒
     */
    public static final String OrderDeliverMessageId = "7AyFmVh0LnaiaFZ55HWtvg7UazkwrXYUC2CZUGp3TLY";

    /**
     * 待付款提醒
     */
    public static final String PendingPaymentMessageId = "czhVdoQz1X4lwfHETBg0mKyGPpnj8UBYDho12ojJYKA";

    /**
     * 待付款提醒
     */
    public static final String PaySuccessfulMessageId = "upiW0jHo9pfyrYKHAgzqpsfYvi1VFPg3nPWNMwHayl8";

    @Override
    public boolean addFormId(String openId, String formId) {
        return false;
    }
}
