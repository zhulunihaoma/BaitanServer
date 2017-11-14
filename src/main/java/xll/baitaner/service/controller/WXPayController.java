package xll.baitaner.service.controller;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xll.baitaner.service.entity.Order;
import xll.baitaner.service.service.OrderService;
import xll.baitaner.service.utils.ResponseResult;
import xll.baitaner.service.utils.WXPayConfigImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    /**
     * 发起微信支付
     * @param orderId
     * @param openId
     * @return
     */
    @GetMapping("order/pay")
    public ResponseResult pay(String orderId, String openId){
        Order order = orderService.getOrder(orderId);
        if(order.getState() != 0){
            return ResponseResult.result(1, "订单并不是未支付订单",null);
        }

        if(!order.getClientId().equals(openId)){
            return ResponseResult.result(1, "订单用户和当前发起支付用户不匹配",null);
        }

        try{
            config = WXPayConfigImpl.getInstance();
            wxPay = new WXPay(config);

            //支付金额
            String total_fee = String.valueOf(order.getTotalMoney() * 100);

            //商户订单号
            String out_trade_no = order.getOrderId();

            HashMap<String, String> data = new HashMap<>();
            data.put("body","小仙女烘焙-支付" + total_fee);//商品描述
            data.put("out_trade_no",out_trade_no);//商户订单号
            data.put("fee_type", "CNY");//货币
            data.put("total_fee", "1");//标价金额 单位为分 不能有小数点 todo 测试用1分钱
            data.put("spbill_create_ip", "192.168.1.1");//终端IP
            data.put("notify_url", "http://111.231.114.159:8080/service/notify");//异步接收微信支付结果通知的回调地址,外网可访问地址
            data.put("trade_type", "JSAPI");//交易类型 JSAP:公众号支付
            data.put("openid", openId);//用户标识,微信用户ID 暂时使用我的openid

            //返回预支付订单信息，发送给前端
            Map<String, String> result = wxPay.unifiedOrder(data);

            String prepay_id = result.get("prepay_id");//预支付交易会话标识
            //出错跳转错误页面
            if(prepay_id.equals("")){
                return ResponseResult.result(1, "预支付单生成错误",null);
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

            return ResponseResult.result(0, "成功",json);

        } catch (Exception e){
            e.printStackTrace();
            return ResponseResult.result(1, "统一支付接口获取预支付订单出错",null);
        }
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
                if(total_fee.equals(String.valueOf(order.getTotalMoney() * 100))){
                    orderService.updateOrderState(out_trade_no, 1);
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
}
