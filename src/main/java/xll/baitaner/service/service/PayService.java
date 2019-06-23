package xll.baitaner.service.service;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xll.baitaner.service.entity.Order;
import xll.baitaner.service.utils.*;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 类名：PayService
 * 描述：支付类接口服务，包括微信支付、钱方支付，体现等
 * 创建者：xie
 * 日期：2019.6.19
 **/
@Service
public class PayService {

    private String TAG = "Baitaner-PayService";

    @Autowired
    private OrderService orderService;

    private WXPayConfigImpl config; //微信支付配置文件
    private WXPay wxPay;


    /**
     * 发起支付
     * @param orderId
     * @param openId
     * @param payType 0：微信商户支付 1：钱方支付
     * @return
     */
    public ResponseResult payMent(String orderId, String openId, int payType) {
        Order order = orderService.getOrder(orderId);

        if (order.getState() != 0) {
            return ResponseResult.result(1, "订单不是未支付订单", null);
        }
        if (!order.getOpenId().equals(openId)) {
            return ResponseResult.result(1, "订单用户和当前发起支付用户不匹配", null);
        }

        try {
            //支付金额
            DecimalFormat decimalFormat = new DecimalFormat("0");
            String total_fee = decimalFormat.format((order.getTotalMoney() * 100));
            //todo 测试用1分钱
            total_fee = "1";

            //商户订单号
            String out_trade_no = order.getOrderId();

            if (payType == 0) {
                //微信支付
                return WXPayMent(total_fee, out_trade_no, openId);
            } else if (payType == 1) {
                //钱方支付
                String txdtm = DateUtils.getCurrentDate();
                JSONObject payObj = QfWxPay.QfPayMent(total_fee, out_trade_no, txdtm, openId);
                LogUtils.info(TAG, "qfwxpay 返回数据中的pay_params: " + payObj);
                return ResponseResult.result(0, "success", payObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //异步接收微信支付结果通知的回调地址,外网可访问地址
    String notify_url = "https://www.eastzebra.cn/service/wxpay/notify";

    /**
     * 微信商户平台支付流程
     * @param total_fee     订单支付金额，单位分
     * @param out_trade_no  订单号
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
     * 根据支付异步通知结果验证支付是否成功，并更改订单状态
     * @param orderId       订单号
     * @param total_fee     订单金额
     */
    public void PayResuleCheck(String orderId, String total_fee)
    {
        Order order = orderService.getOrder(orderId);

        DecimalFormat decimalFormat = new DecimalFormat("0");
        String money = decimalFormat.format((order.getTotalMoney() * 100));

        LogUtils.debug(TAG,"\nout_trade_no: " + orderId + "\ntotal_fee : " + total_fee + "\nMoney: "
                + money);

        if(total_fee.equals(money)){
            boolean res = orderService.updateOrderState(orderId, 1);
            LogUtils.debug(TAG,"updateOrderState: " + res);
        }
    }
}
