package com.xll.baitaner.controller;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.xll.baitaner.service.OrderService;
import com.xll.baitaner.service.PayService;
import com.xll.baitaner.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 类名：PayController
 * 描述：支付模块接口 包括微信支付、钱方支付，体现等
 * 创建者：xie
 * 日期：2019.6.20
 **/
@Api(value = "支付模块controller", description = "微信支付、钱方支付，体现等")
@RestController
public class PayController {

    @Resource
    private OrderService orderService;

    @Resource
    private PayService payService;

    private WXPayConfigImpl config;

    private WXPay wxPay;

    private final String TAG = "Baitaner-WXPayController";


    /**
     * 发起微信支付
     *
     * @param orderId
     * @param openId
     * @return
     */
    @ApiOperation(
            value = "发起微信支付",
            httpMethod = "POST",
            notes = "发起微信支付,走微信商户平台")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单号", required = true, dataType = "int"),
            @ApiImplicitParam(name = "openId", value = "用户openId", required = true, dataType = "String")
    })
    @PostMapping("wxpay")
    public ResponseResult pay(String orderId, String openId) {
        return payService.payMent(orderId, openId, 0);
    }

    /**
     * 微信支付结果异步通知地址
     *
     * @return
     */
    @ApiOperation(
            value = "微信支付结果异步通知地址，内部不要调用",
            httpMethod = "POST",
            notes = "微信支付结果异步通知地址,接收微信商户平台返回的支付结果，内部不要调用")
    @PostMapping("/wxpay/notify")
    public void getwxpaynotify(HttpServletRequest request, HttpServletResponse response) {
        String tt = "WXPay--Notify:  ";
        LogUtils.debug(TAG, tt + "++++++++++++++++++++++++\n" +
                "------------------------\n" +
                "fuckWXfuckWXfuckWXfuckWX\n");
        String UTF8 = "UTF-8";

        try {
            config = WXPayConfigImpl.getInstance();
            wxPay = new WXPay(config);

            Map<String, String> resultMap = new HashMap<String, String>();
            //获取回调xml,转化Map
            InputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, UTF8));
            final StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            String resp = stringBuffer.toString();
            LogUtils.debug(TAG, tt + "回调xml:  \n" + resp);

            resultMap = WXPayUtil.xmlToMap(resp);
            if (resultMap.get("return_code").equals("SUCCESS")) {
                //签名对比 应答微信服务器
                if (wxPay.isPayResultNotifySignatureValid(resultMap)) {
                    String responseXml = "<xml>" +
                            "<return_code><![CDATA[SUCCESS]]></return_code>" +
                            "<return_msg><![CDATA[OK]]></return_msg>" +
                            "</xml>";
                    LogUtils.debug(TAG, tt + "签名对比: true");
                    response.getWriter().write(responseXml);

                    if (resultMap.get("result_code").equals("SUCCESS")) {
                        LogUtils.debug(TAG, tt + "支付结果: SUCCESS");
                        String out_trade_no = resultMap.get("out_trade_no");// 订单号
                        String total_fee = resultMap.get("total_fee"); //支付金额
                        payService.PayResuleCheck(out_trade_no, total_fee);
                    } else {
                        LogUtils.debug(TAG, tt + "支付结果: FAIL");
                    }
                }
            } else {
                LogUtils.error(TAG, tt + resultMap.get("return_msg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                String responseXml = "<xml>" +
                        "<return_code><![CDATA[FAIL]]></return_code>" +
                        "<return_msg><![CDATA[error]]></return_msg>" +
                        "</xml>";
                response.getWriter().write(responseXml);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }


    @PostMapping("/qfpay/notify")
    public void getqfpaynotify(HttpServletRequest request, HttpServletResponse response) {
        String tt = "QFPay--Notify:  ";
        LogUtils.debug(TAG, tt + "++++++++++++++++++++++++\n" +
                "------------------------\n" +
                "fuckWXfuckWXfuckWXfuckWX\n");
        String UTF8 = "UTF-8";

        try {
            String sign = request.getHeader("X-QF-SIGN");

            //获取body
            InputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, UTF8));
            final StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            String resp = stringBuffer.toString();
            //签名对比 应答钱方服务器
            if (QfWxPay.isPayResultNotifySignatureValid(resp, sign)) {
                String responseStr = "SUCCESS";
                LogUtils.debug(TAG, tt + "签名对比: true");
                response.getWriter().write(responseStr);

                JSONObject res = JSONObject.fromObject(resp);
                if (res.get("status").equals("1")) {
                    LogUtils.debug(TAG, tt + "支付结果: SUCCESS");
                    String out_trade_no = res.getString("out_trade_no");// 订单号
                    String total_fee = res.getString("txamt"); //支付金额
                    payService.PayResuleCheck(out_trade_no, total_fee);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                String responseXml = "<xml>" +
                        "<return_code><![CDATA[FAIL]]></return_code>" +
                        "<return_msg><![CDATA[error]]></return_msg>" +
                        "</xml>";
                response.getWriter().write(responseXml);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }


    /***************好近钱方支付******************/
    /**
     * 好近钱方支付
     *
     * @param orderId
     * @param openId
     * @return
     */
    @ApiOperation(
            value = "发起好近钱方支付",
            httpMethod = "POST",
            notes = "发起好近钱方支付,走钱方平台")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单号", required = true, dataType = "int"),
            @ApiImplicitParam(name = "openId", value = "用户openId", required = true, dataType = "String")
    })
    @PostMapping("qfpay")
    public ResponseResult qfwxpay(String orderId, String openId) {
        return payService.payMent(orderId, openId, 1);
    }


    /************************微信提现测试******************************/

    /**
     * 微信企业付款 提现到个人
     *
     * @param openId
     * @param fee
     * @return
     */
    @GetMapping("wxpayforper")
    public ResponseResult wxpayforper(String appId, String openId, String fee) {
        String url = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
        try {
            config = WXPayConfigImpl.getInstance();
            wxPay = new WXPay(config);

//            String mch_appid = config.getAppID();
            String mch_appid = appId;
            String mchid = config.getMchID();
            String nonce_str = WXPayUtil.generateNonceStr();//随机串
            String partner_trade_no = SerialUtils.getSerialId();
            String check_name = "NO_CHECK";
            String amount = fee;
            String desc = "提现测试";
            String spbill_create_ip = "111.231.114.159";

            HashMap<String, String> data = new HashMap<>();
            data.put("mch_appid", mch_appid);
            data.put("mchid", mchid);
            data.put("nonce_str", mch_appid);
            data.put("partner_trade_no", partner_trade_no);
            data.put("openid", openId);
            data.put("check_name", check_name);
            data.put("amount", amount);
            data.put("desc", desc);
            data.put("spbill_create_ip", spbill_create_ip);

            //MD5签名
            String sign = WXPayUtil.generateSignature(data, config.getKey());
            data.put("sign", sign);

            String respXml = wxPay.requestWithCert(url, data, config.getHttpConnectTimeoutMs(), config.getHttpReadTimeoutMs());

            LogUtils.info(TAG, "wxpayforper 返回XML: " + respXml);

            return ResponseResult.result(0, "返回数据", respXml);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.result(1, "企业付款接口出错", null);
        }
    }
}
