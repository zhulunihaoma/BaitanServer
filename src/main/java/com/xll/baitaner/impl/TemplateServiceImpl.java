package com.xll.baitaner.impl;

import com.xll.baitaner.entity.OrderCommodity;
import com.xll.baitaner.entity.ShopWallet;
import com.xll.baitaner.entity.VO.ActivityRecordVO;
import com.xll.baitaner.entity.VO.OrderDetailsVO;
import com.xll.baitaner.entity.WXPublicUserInfo;
import com.xll.baitaner.mapper.TemplateMapper;
import com.xll.baitaner.mapper.WalletMapper;
import com.xll.baitaner.service.*;
import com.xll.baitaner.utils.Constant;
import com.xll.baitaner.utils.DateUtils;
import com.xll.baitaner.utils.LogUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
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

    @Resource
    private WXPublicUserService wxPublicUserService;

    /**
     * 新增formid
     * 用于发送小程序模板消息
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
     * 用于发送小程序模板消息
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
     * 用于发送小程序模板消息
     * @param formId
     * @return
     */
    @Override
    public boolean updateFormidUsed(String openId, String formId) {
        return templateMapper.updateFormidUsed(openId, formId) > 0;
    }

    /**
     * 定时任务，每天清除7天过期formid,过期fromid 无法用于发送小程序模板消息
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
     * 小程序模板消息
     * 封装模板消息的请求json
     * @param touser
     * @param template_id
     * @param page
     * @param form_id
     * @param values
     * @return
     */
    private JSONObject getAppletTemplateMessage(String touser, String template_id, String page, String form_id, String[] values) {
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
     * 公众号模板消息
     * 封装公众号模板消息的请求json
     * @param touser
     * @param template_id
     * @param pagepath
     * @param firstValue
     * @param remarkValue
     * @param values
     * @return
     */
    private JSONObject getPublicTemplateMessage(String touser, String template_id, String pagepath, String firstValue,
                                                String remarkValue, String[] values){
        JSONObject message = new JSONObject();
        message.put("touser", touser);
        message.put("template_id", template_id);

        JSONObject miniprogram = new JSONObject();
        miniprogram.put("appid", Constant.APPLET_APP_ID);
        miniprogram.put("pagepath", pagepath);
        message.put("miniprogram", miniprogram);

        JSONObject data = new JSONObject();

        JSONObject first_value = new JSONObject();
        first_value.put("value", firstValue);
        data.put("first", first_value);

        for (int i = 0; i < values.length; i++) {
            JSONObject value = new JSONObject();
            value.put("value", values[i]);

            String keyword = "keyword" + String.valueOf(i + 1);
            data.put(keyword, value);
        }

        JSONObject remark_value = new JSONObject();
        remark_value.put("value", firstValue);
        data.put("remark", remarkValue);

        message.put("data", data);
        return message;
    }

    /**
     * 小程序模板消息
     * 封装订单相关模板消息 values数组数据
     * 避免重复逻辑代码
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
     * 根据小程序openId用户公众号openId
     * 返回null 表示用户未关注公众号
     * @param appletOpenId
     * @return
     */
    private String getWXPublicOpenId(String appletOpenId){
        String unionid = wxUserService.getWXUserById(appletOpenId).getUnionId();
        if (StringUtils.isNotBlank(unionid)){
            WXPublicUserInfo wxPublicUserInfo = wxPublicUserService.getWXPublicUserInfoByUnionid(unionid);
            if ( wxPublicUserInfo != null){
                return wxPublicUserInfo.getOpenId();
            }else {
                LogUtils.info(TAG, "appletOpenId: " + appletOpenId + "\n 用户未关注公众号,无法发送公众号模板消息");
            }
        }else {
            LogUtils.info(TAG, "appletOpenId: " + appletOpenId + "\n unionid is null, 无法发送公众号模板消息");
        }
        return null;
    }

    /**
     * 新订单通知  发送给商户
     * 线上支付成功 以及二维码支付订单
     * @param orderId
     * @return
     */
    @Override
    public boolean sendNewOrderMessage(String orderId) {
        LogUtils.info(TAG, "sendNewOrderMessage 新订单通知 orderId: " + orderId);

        OrderDetailsVO orderDetailsVO = orderService.getOrderDetails(orderId);
        String sendOpenId = orderDetailsVO.getShop().getOpenId(); //商户

        //判断发送的用户是否关注公众号
        boolean isPublic = false;
        String publicOpenId = this.getWXPublicOpenId(sendOpenId);
        if (StringUtils.isNotBlank(publicOpenId)){
            isPublic = true;
            sendOpenId = publicOpenId;
        }

        // 0: 待支付(二维码支付)
        // 1: 已支付
        String orderstae = "?orderstate=" + orderDetailsVO.getShopOrder().getState().toString();

        String result = "";
        //发送模板消息
        if (isPublic){
            LogUtils.info(TAG, "sendNewOrderMessage 新订单通知 发送公众号模板消息");
            String creatDate = DateUtils.dateTimetoString(orderDetailsVO.getShopOrder().getCreateDate()); //下单时间
            String name = wxPublicUserService.getWXPublicUserInfoByOpenid(sendOpenId).getNickname(); //下单用户
            String address = orderDetailsVO.getAddress().getAddress(); //收货地址
            String[] values = new String[]{
                    orderId,
                    creatDate,
                    name,
                    address
            };
            String firstValue = "您收到了一个新订单，请尽快接单处理";
            String remarkValue = "点击查看订单详情";
            JSONObject message = getPublicTemplateMessage(sendOpenId, NewOrderMessageId_PIBLIC, NewOrderGoPage + orderstae,
                    firstValue, remarkValue, values);
            result = weChatService.sendTemplateMessage(message, 0);
        }
        else {
            LogUtils.info(TAG, "sendNewOrderMessage 新订单通知 发送小程序模板消息");
            String fromId = this.getFormId(sendOpenId);
            if (fromId == null) {
                LogUtils.info(TAG, "sendNewOrderMessage 新订单通知 fromId is null, 发送小程序模板消息fail");
                return false;
            }

            String[] values = this.getOrderDataValues(orderId, 0);
            JSONObject message = getAppletTemplateMessage(sendOpenId, NewOrderMessageId_APPLET, NewOrderGoPage + orderstae, fromId, values);
            result = weChatService.sendTemplateMessage(message, 1);

            if (result.equals("0")) {
                //发送成功后 更新fromid
                this.updateFormidUsed(sendOpenId, fromId);
            }
        }

        LogUtils.info(TAG, "sendNewOrderMessage 新订单通知 result code " + result);
        return result.equals("0");
    }

    /**
     * 订单（二维码支付）待支付提醒 发送给用户  二维码支付订单
     * 二维码支付订单
     * @param orderId
     * @return
     */
    @Override
    public boolean sendPendingPaymentMessage(String orderId) {
        LogUtils.info(TAG, "sendPendingPaymentMessage 订单待支付提醒 orderId: " + orderId);
        OrderDetailsVO orderDetailsVO = orderService.getOrderDetails(orderId);
        String sendOpenId = orderDetailsVO.getShopOrder().getOpenId(); //用户

        //判断发送的用户是否关注公众号
        boolean isPublic = false;
        String publicOpenId = this.getWXPublicOpenId(sendOpenId);
        if (StringUtils.isNotBlank(publicOpenId)){
            isPublic = true;
            sendOpenId = publicOpenId;
        }

        String result = "";
        //发送模板消息
        if (isPublic){
            LogUtils.info(TAG, "senPendingPaymentMessage 订单待支付提醒 发送公众号模板消息");
            String totalMoney = orderDetailsVO.getShopOrder().getTotalMoney(); //商品价格
            String creatDate = DateUtils.dateTimetoString(orderDetailsVO.getShopOrder().getCreateDate()); //购买时间
            List<OrderCommodity> orderCommodityList = orderDetailsVO.getCommoditys();
            String commodityName = orderCommodityList.get(0).getName() + (orderCommodityList.size() > 1 ? "等" : ""); //商品信息
            String[] values = new String[]{
                    commodityName,
                    totalMoney,
                    orderId,
                    creatDate
            };
            String firstValue = "您收到了一个新订单，支付方式为二维码支付";
            String remarkValue = "请确认扫码付款或转账给店主";
            JSONObject message = getPublicTemplateMessage(sendOpenId, PendingPaymentMessageId_PIBLIC, PendingPaymentPage,
                    firstValue, remarkValue, values);
            result = weChatService.sendTemplateMessage(message, 0);
        }
        else{
            LogUtils.info(TAG, "senPendingPaymentMessage 订单待支付提醒 发送小程序模板消息");
            String fromId = this.getFormId(sendOpenId);
            if (fromId == null) {
                LogUtils.info(TAG, "senPendingPaymentMessage 订单待支付提醒 fromId is null, 发送小程序模板消息fail");
                return false;
            }

            String[] values = this.getOrderDataValues(orderId, 1);
            JSONObject message = getAppletTemplateMessage(sendOpenId, PendingPaymentMessageId_APPLET, PendingPaymentPage, fromId, values);
            result = weChatService.sendTemplateMessage(message, 1);

            if (result.equals("0")) {
                //发送成功后 更新fromid
                this.updateFormidUsed(sendOpenId, fromId);
            }
        }

        LogUtils.info(TAG, "senPendingPaymentMessage 订单待支付提醒 result code " + result);
        return result.equals("0");
    }

    /**
     * 订单支付成功  发送给用户  线上支付订单
     * 线上支付订单
     * @param orderId
     * @return
     */
    @Override
    public boolean sendPaySuccessfulMessage(String orderId) {
        LogUtils.info(TAG, "sendPaySuccessfulMessage 订单支付成功提醒 orderId: " + orderId);
        OrderDetailsVO orderDetailsVO = orderService.getOrderDetails(orderId);
        String sendOpenId = orderDetailsVO.getShopOrder().getOpenId(); //用户

        //判断发送的用户是否关注公众号
        boolean isPublic = false;
        String publicOpenId = this.getWXPublicOpenId(sendOpenId);
        if (StringUtils.isNotBlank(publicOpenId)){
            isPublic = true;
            sendOpenId = publicOpenId;
        }

        String result = "";
        //发送模板消息
        if (isPublic){
            LogUtils.info(TAG, "sendPaySuccessfulMessage 订单支付成功提醒 发送公众号模板消息");
            String totalMoney = orderDetailsVO.getShopOrder().getTotalMoney(); //商品价格
            String creatDate = DateUtils.dateTimetoString(orderDetailsVO.getShopOrder().getCreateDate()); //购买时间
            List<OrderCommodity> orderCommodityList = orderDetailsVO.getCommoditys();
            String commodityName = orderCommodityList.get(0).getName() + (orderCommodityList.size() > 1 ? "等" : ""); //商品信息
            String[] values = new String[]{
                    orderId,
                    commodityName,
                    totalMoney,
                    "线上已支付",
                    creatDate
            };
            String firstValue = "您购买的商品已支付成功";
            String remarkValue = "点击查看订单详情";
            JSONObject message = getPublicTemplateMessage(sendOpenId, PaySuccessfulMessageId_PIBLIC, PaySuccessfulPage,
                    firstValue, remarkValue, values);
            result = weChatService.sendTemplateMessage(message, 0);
        }
        else {
            LogUtils.info(TAG, "sendPaySuccessfulMessage 订单支付成功提醒 发送小程序模板消息");
            String fromId = this.getFormId(sendOpenId);
            if (fromId == null) {
                LogUtils.info(TAG, "sendPaySuccessfulMessage 订单支付成功提醒 fromId is null, 发送小程序模板消息fail");
                return false;
            }

            String[] values = this.getOrderDataValues(orderId, 2);
            JSONObject message = getAppletTemplateMessage(sendOpenId, PaySuccessfulMessageId_APPLET, PaySuccessfulPage, fromId, values);
            result = weChatService.sendTemplateMessage(message, 1);

            if (result.equals("0")) {
                //发送成功后 更新fromid
                this.updateFormidUsed(sendOpenId, fromId);
            }
        }

        LogUtils.info(TAG, "sendPaySuccessfulMessage 订单支付成功提醒  result code " + result);
        return result.equals("0");
    }

    /**
     * 提现申请通知 发送给商户 提现详情
     * @param wallet
     * @return
     */
    @Override
    public boolean sendWithdrawMessage(ShopWallet wallet) {
        LogUtils.info(TAG, "sendWithdrawMessage 提现申请通知 walletId: " + wallet.getId() + " openId: " + wallet.getOpenId());
        String sendOpenId = wallet.getOpenId();

        //判断发送的用户是否关注公众号
        boolean isPublic = false;
        String publicOpenId = this.getWXPublicOpenId(sendOpenId);
        if (StringUtils.isNotBlank(publicOpenId)){
            isPublic = true;
            sendOpenId = publicOpenId;
        }

        String result = "";
        //发送模板消息
        String amount = wallet.getAmount();
        String date = DateUtils.dateTimetoString(wallet.getCreateDate());
        String name = wxUserService.getWXUserById(sendOpenId).getNickName();
        String type = "微信零钱";
        if (isPublic){
            LogUtils.info(TAG, "sendWithdrawMessage 提现申请通知 发送公众号模板消息");
            String[] values = new String[]{
                    name,
                    date,
                    amount,
                    type
            };
            String firstValue = "您的提现申请已收到 ";
            String remarkValue = "点击查看详情";
            JSONObject message = getPublicTemplateMessage(sendOpenId, WithdrawMessageId_PIBLIC, WithdrawGoPage,
                    firstValue, remarkValue, values);
            result = weChatService.sendTemplateMessage(message, 0);
        }
        else {
            LogUtils.info(TAG, "sendWithdrawMessage 提现申请通知 发送小程序模板消息");
            String fromId = this.getFormId(sendOpenId);
            if (fromId == null) {
                LogUtils.info(TAG, "sendWithdrawMessage 提现申请通知 fromId is null, 发送小程序模板消息fail");
                return false;
            }

            String shopName = shopManageService.getShopByUser(sendOpenId).getShopName();
            String remark = "如有疑问请查阅【更多】-【个人中心】-【使用帮助】";
            String[] values = new String[]{
                    amount,
                    date,
                    name,
                    shopName,
                    type,
                    remark,
            };
            JSONObject message = getAppletTemplateMessage(sendOpenId, WithdrawMessageId_APPLET, WithdrawGoPage, fromId, values);
            result = weChatService.sendTemplateMessage(message, 1);

            if (result.equals("0")) {
                //发送成功后 更新fromid
                this.updateFormidUsed(sendOpenId, fromId);
            }
        }

        LogUtils.info(TAG, "sendWithdrawMessage 提现申请通知  result code " + result);
        return result.equals("0");
    }

    /**
     * 订单发货提醒 发送给用户
     * @return
     */
    @Override
    public boolean sendOrderDeliverMessage() {
        //TODO 物流模块
        return false;
    }

    /**
     * 活动结果通知  发送给用户
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
        JSONObject message = getAppletTemplateMessage(openId, ActivityResultMessageId_APPLET, ActivityResultPage, fromId, values);
        String result = weChatService.sendTemplateMessage(message, 1);
        LogUtils.info(TAG, "sendActivityResultMessage openId: " + openId + "  result code " + result);

        if (result.equals("0")) {
            //发送成功后 更新fromid
            this.updateFormidUsed(openId, fromId);
        }
        return result.equals("0");
    }
}
