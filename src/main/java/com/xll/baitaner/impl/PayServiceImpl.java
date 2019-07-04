package com.xll.baitaner.impl;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.xll.baitaner.entity.ShopOrder;
import com.xll.baitaner.entity.ShopWallet;
import com.xll.baitaner.mapper.OrderMapper;
import com.xll.baitaner.mapper.ShopMapper;
import com.xll.baitaner.mapper.WalletMapper;
import com.xll.baitaner.service.OrderService;
import com.xll.baitaner.service.PayService;
import com.xll.baitaner.utils.DateUtils;
import com.xll.baitaner.utils.LogUtils;
import com.xll.baitaner.utils.QfWxPay;
import com.xll.baitaner.utils.ResponseResult;
import com.xll.baitaner.utils.WXPayConfigImpl;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 类名：PayService //TODO 增加订单主动查询功能
 * 描述：支付类接口服务，包括微信支付、钱方支付，体现等
 * 创建者：xie
 * 日期：2019.6.19
 **/
@Service
public class PayServiceImpl implements PayService {

    @Resource
    private OrderService orderService;

    @Resource
    OrderMapper orderMapper;

    @Resource
    WalletMapper walletMapper;

    @Resource
    ShopMapper shopMapper;

    private final String TAG = "Baitaner-PayService";

    //异步接收微信支付结果通知的回调地址,外网可访问地址
    private final String notify_url = "https://www.eastzebra.cn/service/wxpay/notify";


    private WXPayConfigImpl config; //微信支付配置文件

    private WXPay wxPay;


    /**
     * 发起支付
     *
     * @param orderId
     * @param openId
     * @param payType 0：微信商户支付 1：钱方支付
     * @return
     */
    @Override
    public ResponseResult payMent(String orderId, String openId, int payType) {
        ShopOrder order = orderMapper.selectShopOrderByOrderId(Long.valueOf(orderId));
        if (order.getState() != 0) {
            return ResponseResult.result(1, "订单不是未支付订单", null);
        }
        if (!order.getOpenId().equals(openId)) {
            return ResponseResult.result(1, "订单用户和当前发起支付用户不匹配", null);
        }

        try {
            //支付金额
            String totalFee = order.getTotalMoney();
            //todo 测试用1分钱
            totalFee = "1";

            //商户订单号
            String outTradeNo = order.getOrderId().toString();

            if (payType == 0) {
                //微信支付
                return WXPayMent(totalFee, outTradeNo, openId);
            } else if (payType == 1) {
                //钱方支付
                String txdtm = DateUtils.getCurrentDate();
                JSONObject payObj = QfWxPay.QfPayMent(totalFee, outTradeNo, txdtm, openId);
                LogUtils.info(TAG, "qfwxpay 返回数据中的pay_params: \n" + payObj);
                return ResponseResult.result(0, "success", payObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 微信商户平台支付流程
     *
     * @param total_fee    订单支付金额，单位分
     * @param out_trade_no 订单号
     * @param openId
     * @return
     */
    private ResponseResult WXPayMent(String total_fee, String out_trade_no, String openId) {
        try {

            if (config == null || wxPay == null) {
                config = WXPayConfigImpl.getInstance();
                wxPay = new WXPay(config);
            }

            HashMap<String, String> data = new HashMap<>();
            data.put("body", "小仙女烘焙-支付" + total_fee);//商品描述
            data.put("out_trade_no", out_trade_no);//商户订单号
            data.put("fee_type", "CNY");//货币
            data.put("total_fee", total_fee);//标价金额 单位为分 不能有小数点
            data.put("spbill_create_ip", "192.168.1.1");//终端IP
            data.put("notify_url", notify_url);//异步接收微信支付结果通知的回调地址,外网可访问地址
            data.put("trade_type", "JSAPI");//交易类型 JSAP:公众号支付
            data.put("openid", openId);//用户标识,微信用户ID 暂时使用我的openid

            LogUtils.debug(TAG, "WXPay unifiedOrder data: " + data.toString());

            //返回预支付订单信息，发送给前端
            Map<String, String> result = wxPay.unifiedOrder(data);
            System.out.print("WXPay unifiedOrder result: " + result.toString());
            if (result.get("return_code").equals("FAIL")) {
                return ResponseResult.result(1, result.get("return_msg"), null);
            }

            String prepay_id = result.get("prepay_id");//预支付交易会话标识
            //出错跳转错误页面
            if (prepay_id.equals("")) {
                return ResponseResult.result(1, "预支付单生成错误,prepay_id为空", null);
            }

            String appId = result.get("appid");//小程序appid
            String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);//时间戳
            String nonceStr = WXPayUtil.generateNonceStr();//随机串
            String packageValue = "prepay_id=" + prepay_id;
            String signType = "MD5";

            //生成信息签名
            HashMap<String, String> finalpackage = new HashMap<>();
            finalpackage.put("appId", appId);
            finalpackage.put("timeStamp", timeStamp);
            finalpackage.put("nonceStr", nonceStr);
            finalpackage.put("package", packageValue);
            finalpackage.put("signType", signType);
            String paySign = WXPayUtil.generateSignature(finalpackage, config.getKey());

            //直接打包JSON格式发送到前端
            JSONObject json = new JSONObject();
            json.put("appId", appId);
            json.put("timeStamp", timeStamp);
            json.put("nonceStr", nonceStr);
            json.put("package", packageValue);
            json.put("signType", signType);
            json.put("paySign", paySign);

            return ResponseResult.result(0, "success", json);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.result(1, "统一支付接口获取预支付订单出错", null);
        }
    }

    /**
     * 根据支付异步通知结果验证支付是否成功，并更改订单状态 //TODO 增加流水记录
     *
     * @param orderId  订单号
     * @param totalFee 订单金额
     */
    @Override
    public void PayResuleCheck(String orderId, String totalFee) {
//        Order order = orderService.getOrder(orderId);
        ShopOrder shopOrder = orderMapper.selectShopOrderByOrderId(Long.valueOf(orderId));
        String money = shopOrder.getTotalMoney();
        LogUtils.debug(TAG, "\nout_trade_no: " + orderId + "\ntotal_fee : " + totalFee + "\nMoney: "
                + money);
        if (totalFee.equals(money)) {
            boolean res = false;
            try {
                res = orderService.updateOrderState(orderId, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //添加付款记录
            try {
                ShopWallet wallet = new ShopWallet();
                wallet.setShopId(shopOrder.getShopId());
                wallet.setOrderId(Long.valueOf(orderId));
                //存的是店铺拥有者openId，不是用户openId
                wallet.setOpenId(shopMapper.getOpenIdByShopId(shopOrder.getShopId()));
                wallet.setAmount(money);
                wallet.setOperator("ADD");
                walletMapper.insertWalletRecord(wallet);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            LogUtils.debug(TAG, "updateOrderState: " + res);
        }
    }
}
