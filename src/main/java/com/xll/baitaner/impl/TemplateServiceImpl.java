package com.xll.baitaner.impl;

import com.xll.baitaner.entity.OrderCommodity;
import com.xll.baitaner.entity.VO.OrderDetailsVO;
import com.xll.baitaner.mapper.TemplateMapper;
import com.xll.baitaner.service.OrderService;
import com.xll.baitaner.service.TemplateService;
import com.xll.baitaner.service.WeChatService;
import com.xll.baitaner.utils.DateUtils;
import com.xll.baitaner.utils.LogUtils;
import net.sf.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 类名：TemplateServiceImpl
 * 描述：微信模板消息业务Impl
 * 创建者：xie
 * 日期：2019/7/4/004
 **/
@Service
public class TemplateServiceImpl implements TemplateService{

    private String TAG = "Baitaner-TemplateServiceImpl";

    @Resource
    private OrderService orderService;

    @Resource
    private WeChatService weChatService;

    @Resource
    private TemplateMapper templateMapper;

    /**
     * 新订单通知  发送给商户
     * 线上支付成功 以及二维码支付订单
     */
    public static final String NewOrderMessageId = "ujyjqlbkEYqhA2GXaMEfYEMdczf8TAzF3NJ0kV7-Ciw";
    //跳转小程序页面地址
    public static final  String NewOrderGoPage = "pages/mycenter/ordermanager/ordermanager";

    /**
     * 提现申请通知 发送给商户 提现详情
     */
    public static final String WithdrawMessageId = "dSs2OuJBcrNWmHx09U-3hedr-SjZviB6KbJkgcbkSe8";
    //跳转小程序页面地址
    public static final  String WithdrawGoPage = "pages/mycenter/wallet/wallet";

    /**
     * 订单发货提醒 发送给用户
     */
    public static final String OrderDeliverMessageId = "7AyFmVh0LnaiaFZ55HWtvg7UazkwrXYUC2CZUGp3TLY";
    //跳转小程序页面地址
    public static final  String OrderDeliverPage = "pages/order/orderlist/orderlist";

    /**
     * 订单待支付提醒（二维码支付） 发送给用户  二维码支付订单
     */
    public static final String PendingPaymentMessageId = "czhVdoQz1X4lwfHETBg0mKyGPpnj8UBYDho12ojJYKA";
    //跳转小程序页面地址
    public static final  String PendingPaymentPage = "pages/order/orderlist/orderlist";

    /**
     * 订单支付成功  发送给用户  线上支付订单
     */
    public static final String PaySuccessfulMessageId = "upiW0jHo9pfyrYKHAgzqpsfYvi1VFPg3nPWNMwHayl8";
    //跳转小程序页面地址
    public static final  String PaySuccessfulPage = "pages/order/orderlist/orderlist";


    /**
     * 新增formid
     * @param openId
     * @param formId
     * @return
     */
    @Override
    public boolean addFormId(String openId, String formId) {
        boolean isExisted = templateMapper.selectCountFormid(openId, formId) > 0;
        if(isExisted){
            LogUtils.warn(TAG,"addFormId fail："+ formId + "formid已存在");
            return false;
        }else {
            return templateMapper.insertFormid(openId, formId) > 0;
        }
    }

    /**
     * 获取用户的fromId用于发送模板消息
     * @param openId
     * @return
     */
    @Override
    public String getFormId(String openId) {
        String formId = templateMapper.selectFormId(openId);
        LogUtils.info(TAG, "用户" + openId + "的FormId：" + formId);
        if (formId == null){
            String errorr = "用户" + openId + "无有效可用的FormId";
            LogUtils.info(TAG, errorr);
        }
        return formId;
    }

    /**
     * 将已用过的formid状态更新为不可用
     * @param formId
     * @return
     */
    @Override
    public boolean updateFormidUsed(String openId, String formId) {
        return templateMapper.updateFormidUsed(openId, formId) > 0;
    }

    /**
     * 定时任务，每天清除7天过期formid,过期fromid 无法用于发送模板消息
     */
    @Scheduled(cron = "0 0 2 * * ?")
    private void deleteOverdueFormId(){
        boolean isDeleted = templateMapper.deleteFormid(7) > 0;
        if(isDeleted){
            LogUtils.info(TAG, "deleteOverdueFormId success");
        }else {
            LogUtils.info(TAG, "deleteOverdueFormId fail");
        }
    }

    /**
     * 封装模板消息的请求json
     * @param touser
     * @param template_id
     * @param page
     * @param form_id
     * @param values
     * @return
     */
    private JSONObject getTemplateMessage(String touser, String template_id, String page, String form_id, String[] values){
        JSONObject message = new JSONObject();
        message.put("touser", touser);
        message.put("template_id", template_id);
        message.put("page", page);
        message.put("form_id", form_id);

        JSONObject data = new JSONObject();
        for (int i = 0; i < values.length; i++){
            JSONObject value = new JSONObject();
            value.put("value", values[i]);

            String keyword = "keyword" + String.valueOf(i +1);
            data.put(keyword, value);
        }
        message.put("data", data);

        return message;
    }

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
    @Override
    public boolean sendNewOrderMessage(String orderId) {
        OrderDetailsVO orderDetailsVO = orderService.getOrderDetails(orderId);

        String shopOwerOpenId = orderDetailsVO.getShop().getOpenId();
        String fromId = this.getFormId(shopOwerOpenId);
        if (fromId == null){
            LogUtils.info(TAG, "sendNewOrderMessage orderId: " + orderId + "  fromId is null");
            return false;
        }

        String totalMoney = orderDetailsVO.getShopOrder().getTotalMoney();
        String creatDate = DateUtils.toStringtime(orderDetailsVO.getShopOrder().getCreateDate());

        List<OrderCommodity> orderCommodityList = orderDetailsVO.getCommoditys();
        String commodityName = orderCommodityList.get(0).getName();
        int count = 0;
        for (OrderCommodity oc : orderCommodityList ){
            count += oc.getCount();
        }
        commodityName += orderCommodityList.size() > 1 ?  "等" : "";
        String address = orderDetailsVO.getAddress().getAddress();
        String state = orderDetailsVO.getShopOrder().getState() > 0 ? "已支付" : "待支付";

        String[] values = new String[]{
                orderId,
                totalMoney,
                creatDate,
                commodityName,
                String.valueOf(count),
                address,
                state
        };
        JSONObject message = getTemplateMessage(shopOwerOpenId, NewOrderMessageId, NewOrderGoPage, fromId, values);
        String result = weChatService.sendTemplateMessage(message, 1);
        LogUtils.info(TAG, "sendNewOrderMessage orderId: " + orderId + "  result code " +result);

        if (result.equals("0")){
            //发送成功后 更新fromid
            this.updateFormidUsed(shopOwerOpenId, fromId);
        }
        return result.equals("0");
    }
}
