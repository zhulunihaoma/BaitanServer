package com.xll.baitaner.controller;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.xll.baitaner.service.PayService;
import com.xll.baitaner.utils.LogUtils;
import com.xll.baitaner.utils.QfWxPay;
import com.xll.baitaner.utils.ResponseResult;
import com.xll.baitaner.utils.WXPayConfigImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
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
@Api(value = "支付模块controller")
@RestController
public class PayController {

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
        LogUtils.info(TAG, tt + "++++++++++++++++++++++++接收到钱微信支付结果通知回调");
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
            LogUtils.info(TAG, tt + "回调xml:  \n" + resp);

            resultMap = WXPayUtil.xmlToMap(resp);
            if (resultMap.get("return_code").equals("SUCCESS")) {
                //签名对比 应答微信服务器
                if (wxPay.isPayResultNotifySignatureValid(resultMap)) {
                    String responseXml = "<xml>" +
                            "<return_code><![CDATA[SUCCESS]]></return_code>" +
                            "<return_msg><![CDATA[OK]]></return_msg>" +
                            "</xml>";
                    LogUtils.info(TAG, tt + "签名对比: true");
                    response.getWriter().write(responseXml);

                    if (resultMap.get("result_code").equals("SUCCESS")) {
                        LogUtils.info(TAG, tt + "支付结果: SUCCESS");
                        String out_trade_no = resultMap.get("out_trade_no");// 订单号
                        String total_fee = resultMap.get("total_fee"); //支付金额
                        payService.PayResuleCheck(out_trade_no, total_fee, 1);
                    } else {
                        LogUtils.info(TAG, tt + "支付结果: FAIL");
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


    /**
     * 钱方平台支付结果通知回调
     *
     * @param request
     * @param response
     */
    @PostMapping("/qfpay/notify")
    public void getqfpaynotify(HttpServletRequest request, HttpServletResponse response) {
        String tt = "QFPay--Notify:  ";
        LogUtils.info(TAG, tt + "++++++++++++++++++++++++接收到钱方平台支付结果通知回调");
        String UTF8 = "UTF-8";

        try {
            String sign = request.getHeader("X-QF-SIGN");

            //获取body
            InputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, UTF8));
            final StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            String resp = stringBuffer.toString();
            //签名对比 应答钱方服务器
            if (QfWxPay.isPayResultNotifySignatureValid(resp, sign)) {
                String responseStr = "SUCCESS";
                LogUtils.info(TAG, tt + "签名对比: true");
                response.getWriter().write(responseStr);

                JSONObject res = JSONObject.fromObject(resp);
                if (res.get("status").equals("1")) {
                    LogUtils.info(TAG, tt + "支付结果: SUCCESS");
                    String out_trade_no = res.getString("out_trade_no");// 订单号
                    String total_fee = res.getString("txamt"); //支付金额
                    payService.PayResuleCheck(out_trade_no, total_fee, 0);
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
}
