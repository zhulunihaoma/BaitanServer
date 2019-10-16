package com.xll.baitaner.impl;

import com.xll.baitaner.entity.OrderCommodity;
import com.xll.baitaner.entity.ShopWallet;
import com.xll.baitaner.entity.VO.ActivityRecordVO;
import com.xll.baitaner.entity.VO.OrderDetailsVO;
import com.xll.baitaner.mapper.TemplateMapper;
import com.xll.baitaner.mapper.WalletMapper;
import com.xll.baitaner.service.OrderService;
import com.xll.baitaner.service.ShopManageService;
import com.xll.baitaner.service.TemplateService;
import com.xll.baitaner.service.WXUserService;
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
public class TemplateServiceImpl implements TemplateService {

    private String TAG = "Baitaner-TemplateServiceImpl";

    @Resource
    private OrderService orderService;

    @Resource
    private WeChatService weChatService;

    @Resource
    private WXUserService wxUserService;

    @Resource
    private ShopManageService shopManageService;

    @Resource
    private TemplateMapper templateMapper;

    @Resource
    private WalletMapper walletMapper;

    /**
     * 新订单通知  发送给商户
     * 线上支付成功 以及二维码支付订单
     */
    public static final String NewOrderMessageId = "ujyjqlbkEYqhA2GXaMEfYEMdczf8TAzF3NJ0kV7-Ciw";
    //跳转小程序页面地址
    public static final String NewOrderGoPage = "pages/mycenter/ordermanager/ordermanager";

    /**
     * 提现申请通知 发送给商户 提现详情
     */
    public static final String WithdrawMessageId = "dSs2OuJBcrNWmHx09U-3hedr-SjZviB6KbJkgcbkSe8";
    //跳转小程序页面地址
    public static final String WithdrawGoPage = "pages/mycenter/wallet/wallet";

    /**
     * 订单发货提醒 发送给用户
     */
    public static final String OrderDeliverMessageId = "7AyFmVh0LnaiaFZ55HWtvg7UazkwrXYUC2CZUGp3TLY";
    //跳转小程序页面地址
    public static final String OrderDeliverPage = "pages/order/orderlist/orderlist";

    /**
     * 订单（二维码支付）待支付提醒 发送给用户  二维码支付订单
     */
    public static final String PendingPaymentMessageId = "czhVdoQz1X4lwfHETBg0mKyGPpnj8UBYDho12ojJYKA";
    //跳转小程序页面地址
    public static final String PendingPaymentPage = "pages/order/orderlist/orderlist?orderstate=0";

    /**
     * 订单支付成功  发送给用户  线上支付订单
     */
    public static final String PaySuccessfulMessageId = "upiW0jHo9pfyrYKHAgzqpsfYvi1VFPg3nPWNMwHayl8";
    //跳转小程序页面地址
    public static final String PaySuccessfulPage = "pages/order/orderlist/orderlist";

    /**
     * 活动结果通知  发送给用户
     */
    public static final String ActivityResultMessageId = "wYk0pJrZ-zCaV_MyO8lXMUcB-LIvravSs1Rnur0PR10";
    //跳转小程序页面地址
    public static final String ActivityResultPage = "";

    /**
     * 新增formid
     *
     * @param openId
     * @param formId
     * @return
     */
    @Override
    public boolean addFormId(String openId, String formId) {
        boolean isExisted = templateMapper.selectCountFormid(openId, formId) > 0;
        if (isExisted) {
            LogUtils.warn(TAG, "addFormId fail：" + formId + "formid已存在");
            return false;
        } else {
            return templateMapper.insertFormid(openId, formId) > 0;
        }
    }

    /**
     * 获取用户的fromId用于发送模板消息
     *
     * @param openId
     * @return
     */
    @Override
    public String getFormId(String openId) {
        String formId = templateMapper.selectFormId(openId);
        LogUtils.info(TAG, "用户" + openId + "的FormId：" + formId);
        if (formId == null) {
            String errorr = "用户" + openId + "无有效可用的FormId";
            LogUtils.info(TAG, errorr);
        }
        return formId;
    }

    /**
     * 将已用过的formid状态更新为不可用
     *
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
    private void deleteOverdueFormId() {
        boolean isDeleted = templateMapper.deleteFormid(7) > 0;
        if (isDeleted) {
            LogUtils.info(TAG, "deleteOverdueFormId success");
        } else {
            LogUtils.info(TAG, "deleteOverdueFormId fail");
        }
    }

    /**
     * 封装模板消息的请求json
     *
     * @param touser
     * @param template_id
     * @param page
     * @param form_id
     * @param values
     * @return
     */
    private JSONObject getTemplateMessage(String touser, String template_id, String page, String form_id, String[] values) {
        JSONObject message = new JSONObject();
        message.put("touser", touser);
        message.put("template_id", template_id);
        message.put("page", page);
        message.put("form_id", form_id);

        JSONObject data = new JSONObject();
        for (int i = 0; i < values.length; i++) {
            JSONObject value = new JSONObject();
            value.put("value", values[i]);

            String keyword = "keyword" + String.valueOf(i + 1);
            data.put(keyword, value);
        }
        message.put("data", data);

        return message;
    }

    /**
     * 封装订单相关模板消息 values数组数据
     * 防止重复实现
     *
     * @param orderId
     * @param type    0: 新订单通知 1: 订单（二维码支付）待支付 2: 订单支付成功
     * @return
     */
    private String[] getOrderDataValues(String orderId, int type) {
        String[] values = null;
        OrderDetailsVO orderDetailsVO = orderService.getOrderDetails(orderId);

        String totalMoney = orderDetailsVO.getShopOrder().getTotalMoney();
        String creatDate = DateUtils.dateTimetoString(orderDetailsVO.getShopOrder().getCreateDate());

        List<OrderCommodity> orderCommodityList = orderDetailsVO.getCommoditys();
        String commodityName = orderCommodityList.get(0).getName();
        int count = 0;
        for (OrderCommodity oc : orderCommodityList) {
            count += oc.getCount();
        }
        commodityName += orderCommodityList.size() > 1 ? "等" : "";
        String address = orderDetailsVO.getAddress().getAddress();

        if (type == 0) {         //新订单通知
            String state = orderDetailsVO.getShopOrder().getState() > 0 ? "已支付" : "待支付(二维码支付)";
            values = new String[]{
                    orderId,                        //订单号     {{keyword1.DATA}}
                    totalMoney,                     //订单总价   {{keyword2.DATA}}
                    creatDate,                      //支付时间   {{keyword3.DATA}}
                    commodityName,                  //商品名称   {{keyword4.DATA}}
                    String.valueOf(count),          //数量       {{keyword5.DATA}
                    address,                        //收货地址   {{keyword6.DATA}}
                    state                           //支付状态   {{keyword7.DATA}}
            };
        } else if (type == 1) {    //订单（二维码支付）待支付提醒
            String remark = "您选择的是二维码支付，请确认扫码付款或转账给店主！";
            values = new String[]{
                    orderId,                        //订单号     {{keyword1.DATA}}
                    totalMoney,                     //订单总价   {{keyword2.DATA}}
                    creatDate,                      //下单时间   {{keyword3.DATA}}
                    commodityName,                  //商品详情   {{keyword4.DATA}}
                    "扫描二维码支付",                //付款类型   {{keyword4.DATA}}
                    totalMoney,                     //待付金额   {{keyword6.DATA}}
                    remark,                         //备注       {{keyword7.DATA}}
                    address                         //收货地址   {{keyword8.DATA}}
            };
        } else if (type == 2) {    //订单支付成功
            String payType = orderDetailsVO.getShopOrder().getPayType() == 0 ? "微信支付" : "扫描二维码支付";
            values = new String[]{
                    orderId,                        //订单号     {{keyword1.DATA}}
                    creatDate,                      //支付时间   {{keyword2.DATA}}
                    totalMoney,                     //订单金额   {{keyword3.DATA}}
                    commodityName,                  //商品名称   {{keyword4.DATA}}
                    String.valueOf(count),          //数量       {{keyword5.DATA}
                    payType,                        //支付方式   {{keyword6.DATA}}
                    address                         //收货地址   {{keyword7.DATA}}
            };
        }
        return values;
    }

    /**
     * 新订单通知  发送给商户
     * 线上支付成功 以及二维码支付订单
     * <p>
     * 订单号     {{keyword1.DATA}}
     * 订单总价   {{keyword2.DATA}}
     * 支付时间   {{keyword3.DATA}}
     * 商品名称   {{keyword4.DATA}}
     * 数量       {{keyword5.DATA}}
     * 收货地址   {{keyword6.DATA}}
     * 支付状态   {{keyword7.DATA}}
     *
     * @param orderId
     * @return
     */
    @Override
    public boolean sendNewOrderMessage(String orderId) {
        OrderDetailsVO orderDetailsVO = orderService.getOrderDetails(orderId);

        String sendOpenId = orderDetailsVO.getShop().getOpenId(); //商户
        String fromId = this.getFormId(sendOpenId);
        if (fromId == null) {
            LogUtils.info(TAG, "sendNewOrderMessage orderId: " + orderId + "  fromId is null");
            return false;
        }

        // 0: 待支付(二维码支付)
        // 1: 已支付
        String orderstae = "?orderstate=" + orderDetailsVO.getShopOrder().getState().toString();

        String[] values = this.getOrderDataValues(orderId, 0);
        JSONObject message = getTemplateMessage(sendOpenId, NewOrderMessageId, NewOrderGoPage + orderstae, fromId, values);
        String result = weChatService.sendTemplateMessage(message, 1);
        LogUtils.info(TAG, "sendNewOrderMessage orderId: " + orderId + "  result code " + result);

        if (result.equals("0")) {
            //发送成功后 更新fromid
            this.updateFormidUsed(sendOpenId, fromId);
        }
        return result.equals("0");
    }

    /**
     * 订单（二维码支付）待支付提醒 发送给用户  二维码支付订单
     * 二维码支付订单
     * <p>
     * 订单号     {{keyword1.DATA}}
     * 订单总价   {{keyword2.DATA}}
     * 下单时间   {{keyword3.DATA}}
     * 商品详情   {{keyword4.DATA}}
     * 付款类型   {{keyword4.DATA}}
     * 待付金额   {{keyword6.DATA}}
     * 备注       {{keyword7.DATA}}
     * 收货地址   {{keyword8.DATA}}
     *
     * @param orderId
     * @return
     */
    @Override
    public boolean sendPendingPaymentMessage(String orderId) {
        OrderDetailsVO orderDetailsVO = orderService.getOrderDetails(orderId);

        String sendOpenId = orderDetailsVO.getShopOrder().getOpenId(); //用户
        String fromId = this.getFormId(sendOpenId);
        if (fromId == null) {
            LogUtils.info(TAG, "senPendingPaymentMessage orderId: " + orderId + "  fromId is null");
            return false;
        }

        String[] values = this.getOrderDataValues(orderId, 1);
        JSONObject message = getTemplateMessage(sendOpenId, PendingPaymentMessageId, PendingPaymentPage, fromId, values);
        String result = weChatService.sendTemplateMessage(message, 1);
        LogUtils.info(TAG, "senPendingPaymentMessage orderId: " + orderId + "  result code " + result);

        if (result.equals("0")) {
            //发送成功后 更新fromid
            this.updateFormidUsed(sendOpenId, fromId);
        }
        return result.equals("0");
    }

    /**
     * 订单支付成功  发送给用户  线上支付订单
     * 线上支付订单
     * <p>
     * 订单号     {{keyword1.DATA}}
     * 支付时间   {{keyword2.DATA}}
     * 订单金额   {{keyword3.DATA}}
     * 商品名称   {{keyword4.DATA}}
     * 数量       {{keyword5.DATA}
     * 支付方式   {{keyword6.DATA}}
     * 收货地址   {{keyword7.DATA}}
     *
     * @param orderId
     * @return
     */
    @Override
    public boolean sendPaySuccessfulMessage(String orderId) {
        OrderDetailsVO orderDetailsVO = orderService.getOrderDetails(orderId);

        String sendOpenId = orderDetailsVO.getShopOrder().getOpenId(); //用户
        String fromId = this.getFormId(sendOpenId);
        if (fromId == null) {
            LogUtils.info(TAG, "sendPaySuccessfulMessage orderId: " + orderId + "  fromId is null");
            return false;
        }

        String[] values = this.getOrderDataValues(orderId, 2);
        JSONObject message = getTemplateMessage(sendOpenId, PaySuccessfulMessageId, PaySuccessfulPage, fromId, values);
        String result = weChatService.sendTemplateMessage(message, 1);
        LogUtils.info(TAG, "sendPaySuccessfulMessage orderId: " + orderId + "  result code " + result);

        if (result.equals("0")) {
            //发送成功后 更新fromid
            this.updateFormidUsed(sendOpenId, fromId);
        }
        return result.equals("0");
    }

    /**
     * 提现申请通知 发送给商户 提现详情
     * <p>
     * 提现金额   {{keyword1.DATA}}
     * 提现时间   {{keyword2.DATA}}
     * 姓名       {{keyword3.DATA}}
     * 帐号       {{keyword4.DATA}}
     * 到账类型   {{keyword5.DATA}}
     * 备注       {{keyword6.DATA}}
     *
     * @param wallet
     * @return
     */
    @Override
    public boolean sendWithdrawMessage(ShopWallet wallet) {
        String sendOpenId = wallet.getOpenId();
        String fromId = this.getFormId(sendOpenId);
        if (fromId == null) {
            LogUtils.info(TAG, "sendWithdrawMessage walletId: " + wallet.getOrderId() + "  fromId is null");
            return false;
        }

        String amount = wallet.getAmount();
        String date = DateUtils.dateTimetoString(wallet.getCreateDate());
        String name = wxUserService.getWXUserById(sendOpenId).getNickName();
        String shopName = shopManageService.getShopByUser(sendOpenId).getShopName();
        String type = "零钱";
        String remark = "如有疑问请查阅【更多】-【个人中心】-【使用帮助】";
        String[] values = new String[]{
                amount,
                date,
                name,
                shopName,
                type,
                remark,
        };

        JSONObject message = getTemplateMessage(sendOpenId, WithdrawMessageId, WithdrawGoPage, fromId, values);
        String result = weChatService.sendTemplateMessage(message, 1);
        LogUtils.info(TAG, "sendWithdrawMessage walletId: " + wallet.getOrderId() + "  result code " + result);

        if (result.equals("0")) {
            //发送成功后 更新fromid
            this.updateFormidUsed(sendOpenId, fromId);
        }
        return result.equals("0");
    }

    /**
     * 订单发货提醒 发送给用户
     *
     * @return
     */
    @Override
    public boolean sendOrderDeliverMessage() {
        //TODO 物流模块
        return false;
    }

    /**
     * 活动结果通知  发送给用户
     * 用户昵称  {{keyword1.DATA}}
     * 助力结果  {{keyword2.DATA}}
     * @return
     */
    @Override
    public boolean sendActivityResultMessage(ActivityRecordVO activityRecordVO) {
        String openId = activityRecordVO.getActivityRecord().getOpenId();
        String fromId = this.getFormId(openId);
        if (fromId == null) {
            LogUtils.info(TAG, "sendActivityResultMessage openId: " + openId + "  fromId is null");
            return false;
        }
        String shopName = activityRecordVO.getShop().getShopName();

        String name = activityRecordVO.getWxUserInfo().getNickName();
        String word = "您参加“" + shopName + "”的活动已被好友助力，点击查看详情。";

        String[] values = new String[]{
                name,
                word
        };
        JSONObject message = getTemplateMessage(openId, ActivityResultMessageId, ActivityResultPage, fromId, values);
        String result = weChatService.sendTemplateMessage(message, 1);
        LogUtils.info(TAG, "sendActivityResultMessage openId: " + openId + "  result code " + result);

        if (result.equals("0")) {
            //发送成功后 更新fromid
            this.updateFormidUsed(openId, fromId);
        }
        return result.equals("0");
    }
}
