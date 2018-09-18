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

    /**
     * 是否在正式环境
     */
    @Value("${baitaner.runtime}")
    private boolean runtimeOrDev;

    /**
     * 微信小程序登录流程 todo 登录状态储存，每次后台请求做校验
     * @param code
     * @return
     */
    @GetMapping("wxlogin")
    public ResponseResult wxlogin(String code){
        if(code == null || "".equals(code)){
            return ResponseResult.result(1, "code值为空",null);
        }

        try {
            config = WXPayConfigImpl.getInstance();

            String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + config.getAppID()
                    + "&secret=" + config.getAppSecret() + "&js_code=" + code + "&grant_type=authorization_code";
            System.out.print("微信登录，请求url：" + url);

            String res = HttpRequest.sendGetRequest(url);
            if (res == null || "".equals(res)) {
                System.out.print("微信登录，请求接口返回值为空或null");
                return ResponseResult.result(1, "请求微信失败：" + res + "--",null);
            }
            System.out.print("微信登录，请求接口返回值：" + res);

            JSONObject obj = JSONObject.fromObject(res);
            if (obj.containsKey("errcode")) {
                String errcode = obj.get("errcode").toString();
                System.out.print("微信登录，微信返回的错误码：" + errcode);
                return ResponseResult.result(1, "微信返回的错误码：" + errcode,null);
            } else if (obj.containsKey("session_key")) {
                String openId = obj.get("openid").toString();
                return ResponseResult.result(0, "success",openId);
            }
            return ResponseResult.result(0, "请求微信失败：" + res,null);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseResult.result(1, e.toString(),null);
        }
    }

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
            DecimalFormat decimalFormat = new DecimalFormat("0");
            String total_fee = decimalFormat.format((order.getTotalMoney() * 100));

            //商户订单号
            String out_trade_no = order.getOrderId();

            String notify_url = runtimeOrDev ? "https://www.eastzebra.cn/service/wxpay/notify" :
                                               "https://www.eastzebra.cn/service_test/wxpay/notify";

            HashMap<String, String> data = new HashMap<>();
            data.put("body","小仙女烘焙-支付" + total_fee);//商品描述
            data.put("out_trade_no",out_trade_no);//商户订单号
            data.put("fee_type", "CNY");//货币
            data.put("total_fee", total_fee);//标价金额 单位为分 不能有小数点 todo 测试用1分钱
            data.put("spbill_create_ip", "192.168.1.1");//终端IP
            data.put("notify_url", notify_url);//异步接收微信支付结果通知的回调地址,外网可访问地址
            data.put("trade_type", "JSAPI");//交易类型 JSAP:公众号支付
            data.put("openid", openId);//用户标识,微信用户ID 暂时使用我的openid

            System.out.print("WXPay unifiedOrder data: " + data.toString());

            //返回预支付订单信息，发送给前端
            Map<String, String> result = wxPay.unifiedOrder(data);
            System.out.print("WXPay unifiedOrder result: " + result.toString());
            if(result.get("return_code").equals("FAIL")){
                return ResponseResult.result(1, result.get("return_msg"),null);
            }

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


    /***************好近钱方支付测试******************/
    /**
     * 好近钱方支付测试
     * @param fee
     * @param openId
     * @return
     */
    @GetMapping("qfpay")
    public ResponseResult qfpay(String fee, String openId){

        String payUrl = "https://openapi.qfpay.com/trade/v1/payment";
        String out_trade_no = SerialUtils.getSerialId();
        String txdtm = DateUtils.getCurrentDate();

        String code = "382368FBDDE84259AA9029D38D2BC5CA";
        String key = "0910E30FD78648BE913F167F9594932E";
        String mchid = "M4V9GiYLdj";

        try{
            //发起支付数据
            HashMap<String, String> data = new HashMap<>();
            data.put("txamt",fee); //订单支付金额，单位分
            data.put("txcurrcd", "CNY");//货币
            data.put("pay_type", "800213");//支付类型    微信小程序支付:800213
            data.put("out_trade_no", out_trade_no);//外部订单号
            data.put("txdtm", txdtm);//请求交易时间  格式为：YYYY-MM-MM HH:MM:SS
            data.put("sub_openid", openId);//微信小程序的openid
            data.put("goods_name", "qfTest");//商品名称标示
            data.put("mchid", mchid);

            //MD5签名
            String sign = WXPayUtil.generateSignature(data, key);
            String sign2 = WXPayUtil.MD5("goods_name=qfTest&mchid=" + mchid + "&out_trade_no=" + out_trade_no +
                    "&pay_type=800213&sub_openid="+openId+"&txamt=1&txcurrcd=CNY&txdtm="+txdtm + key).toUpperCase();
            LogUtils.info(TAG, "发起支付的data: " + data + "\n 签名sign: " + sign + "\n sing2: " + sign2);

            String UTF8 = "UTF-8";
            URL httpUrl = new URL(payUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) httpUrl.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setUseCaches(false);

            //http头添加验证属性
            httpURLConnection.setRequestProperty("X-QF-APPCODE", code);
            httpURLConnection.setRequestProperty("X-QF-SIGN", sign2);

            httpURLConnection.connect();

            JSONObject jsonObject = JSONObject.fromObject(data);
            LogUtils.info(TAG, "qfpay reqBody：" + jsonObject.toString());

            OutputStream outputStream = httpURLConnection.getOutputStream();
            // 注意编码格式
            outputStream.write(jsonObject.toString().getBytes(UTF8));
            outputStream.close();

            //获取返回内容
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, UTF8));
            final StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            String resp = stringBuffer.toString();
            if (stringBuffer!=null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream!=null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //获取数据发送前端
            LogUtils.info(TAG, "支付返回 resp: " + resp);
            JSONObject payObj = (JSONObject)JSONObject.fromObject(resp).get("pay_params");
            LogUtils.info(TAG, "获取返回数据中的pay_params: " + payObj);

            if(payObj == null){
                return ResponseResult.result(1, "钱方返回数据出错",resp);
            }

            //生成信息签名
            HashMap<String, String> finalpackage = new HashMap<>();
            finalpackage.put("appId", payObj.get("appId").toString());
            finalpackage.put("timeStamp", payObj.get("timeStamp").toString());
            finalpackage.put("nonceStr", payObj.get("nonceStr").toString());
            finalpackage.put("package", payObj.get("package").toString());
            finalpackage.put("signType", payObj.get("signType").toString());
            String paySign = WXPayUtil.generateSignature(finalpackage, key);

            //直接打包JSON格式发送到前端
            payObj.put("paySign", paySign);

            LogUtils.info(TAG, "发送给前端小程序JSON: " + payObj);
            return ResponseResult.result(0, "成功",payObj);
        }
        catch (Exception e){
            e.printStackTrace();
            return ResponseResult.result(1, "支付接口出错",null);
        }
    }


    @GetMapping("qfwxpay")
    public ResponseResult qfwxpay(String fee, String openId){

        String out_trade_no = SerialUtils.getSerialId();
        String txdtm = DateUtils.getCurrentDate();

        JSONObject payObj = QfWxPay.QfPayMent(fee, out_trade_no, txdtm, openId);
        LogUtils.info(TAG, "qfwxpay 返回数据中的pay_params: " + payObj);
        return ResponseResult.result(0, "成功",payObj);
    }

}
