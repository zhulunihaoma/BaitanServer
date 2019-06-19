package xll.baitaner.service.controller;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xll.baitaner.service.entity.Order;
import xll.baitaner.service.service.OrderService;
import xll.baitaner.service.service.PayService;
import xll.baitaner.service.utils.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述：
 * 创建者：xie
 * 日期：2017/11/14
 **/
@RestController
public class WXPayController {

    @Autowired
    private OrderService orderService;

    private WXPayConfigImpl config;
    private WXPay wxPay;

    private String TAG = "Baitaner-WXPayController";

    @Autowired
    private PayService payService;

    /**
     * 发起微信支付
     * @param orderId
     * @param openId
     * @return
     */
    @GetMapping("wxpay")
    public ResponseResult pay(String orderId, String openId){
        return payService.payMent(orderId, openId, 0);
    }

    /**
     * 微信支付结果回调
     * @return
     */
    @RequestMapping("/wxpay/notify")
    public void  getwxpaynotify(HttpServletRequest request, HttpServletResponse response){
        String tt = "WXPay--Notify:  ";
        System.out.print(tt + "++++++++++++++++++++++++\n" +
                "------------------------\n" +
                "fuckWXfuckWXfuckWXfuckWX\n");
        String UTF8 = "UTF-8";

        try{
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
            System.out.print(tt + "回调xml:  \n"+ resp);
            resultMap = WXPayUtil.xmlToMap(resp);
            if(resultMap.get("return_code").equals("SUCCESS")){
                //签名对比 应答微信服务器
                if(wxPay.isPayResultNotifySignatureValid(resultMap)){
                    String responseXml = "<xml>" +
                            "<return_code><![CDATA[SUCCESS]]></return_code>" +
                            "<return_msg><![CDATA[OK]]></return_msg>" +
                            "</xml>";
                    System.out.print(tt + "签名对比: true");
                    response.getWriter().write(responseXml);
                }

                String out_trade_no = resultMap.get("out_trade_no");// 订单号
                String total_fee = resultMap.get("total_fee"); //支付金额

                Order order = orderService.getOrder(out_trade_no);

                DecimalFormat decimalFormat = new DecimalFormat("0");
                String money = decimalFormat.format((order.getTotalMoney() * 100));

                System.out.print("\nout_trade_no: " + out_trade_no + "\ntotal_fee : " + total_fee + "\nMoney: "
                        + money);

                if(total_fee.equals(money)){
                    boolean res = orderService.updateOrderState(out_trade_no, 1);
                    System.out.print("updateOrderState: " + res);
                }

            }
            else {
                System.out.print(tt + resultMap.get("return_msg"));
            }


        } catch (Exception e){
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
     * @param fee
     * @param openId
     * @return
     */
    @GetMapping("qfpay")
    public ResponseResult qfwxpay(String orderId, String openId){
        return payService.payMent(orderId, openId, 1);
    }


    /************************微信提现测试******************************/

    /**
     * 微信企业付款 提现到个人
     * @param openId
     * @param fee
     * @return
     */
    @GetMapping("wxpayforper")
    public ResponseResult wxpayforper(String appId, String openId, String fee){
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

            return ResponseResult.result(0, "返回数据",respXml);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.result(1, "企业付款接口出错",null);
        }
    }
}
