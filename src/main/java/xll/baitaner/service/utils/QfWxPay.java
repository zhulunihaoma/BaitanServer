package xll.baitaner.service.utils;

import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.*;



/**
 * 钱台微信公众号支付
 * @author
 * @version 1.0
 */
public class QfWxPay {

    private static String TAG = "Baitaner-QfWxPay";
	
	//public static void main(String[] args) {QfpayMent();}
    /**
     * 钱台微信支付预下单，返回支付参数
     * @return
     */
    public static JSONObject QfPayMent(String fee, String out_trade_no, String txdtm, String openId){
        // 1 拼接请求参数
        SortedMap<String, String> sortedMap = packetRequestParameters(fee, out_trade_no, txdtm, openId);
        // 2 发送预下单请求
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("X-QF-SIGN", sortedMap.get("sign"));
        headerMap.put("X-QF-APPCODE", "382368FBDDE84259AA9029D38D2BC5CA");
        sortedMap.remove("sign");
        try{
            CloseableHttpResponse response;
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("https://openapi.qfpay.com/trade/v1/payment"); //生产环境
            List<NameValuePair> params = new ArrayList<NameValuePair>(sortedMap.size());
            if (!sortedMap.isEmpty()) {
                for (Map.Entry<String, String> parameter : sortedMap.entrySet()) {
                    params.add(new BasicNameValuePair(parameter.getKey(), parameter
                            .getValue()));
                }
            }
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            if (!headerMap.isEmpty()) {
                for (Map.Entry<String, String> vo : headerMap.entrySet()) {
                    httpPost.setHeader(vo.getKey(), vo.getValue());
                }
            }
            response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 3 请求成功，处理请求结果
                if (response != null && response.getEntity() != null) {
                    JSONObject resultObject = JSONObject.fromObject(StringEscapeUtils
                            .unescapeJava(EntityUtils.toString(response.getEntity(),
                                    "utf-8")));
                    //System.out.println(resultObject.toString());
                    LogUtils.info(TAG, "QfPayMent result: " + resultObject.toString());
                    if (resultObject.getString("respcd").equals("0000")) {
                        JSONObject payInfo = (JSONObject) resultObject
                                .get("pay_params");

                        SortedMap<Object, Object> sortedParams = new TreeMap<Object, Object>();
                        sortedParams.put("appId", payInfo.getString("appId"));
                        sortedParams.put("timeStamp", payInfo.getString("timeStamp"));
                        sortedParams.put("nonceStr", payInfo.getString("nonceStr"));
                        sortedParams.put("package", payInfo.getString("package"));
                        sortedParams.put("signType", payInfo.getString("signType"));
                        sortedParams.put("paySign", payInfo.getString("paySign"));

                        return payInfo;
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 拼接请求参数
     * @return
     */
    private static SortedMap<String,String> packetRequestParameters(String fee, String out_trade_no, String txdtm, String openId) {
        SortedMap<String, String> sortedMap = new TreeMap<>();
        sortedMap.put("txamt",fee);
        sortedMap.put("txcurrcd", "CNY");
        sortedMap.put("pay_type","800213");
        sortedMap.put("mchid", "M4V9GiYLdj");
        sortedMap.put("out_trade_no", out_trade_no);
        sortedMap.put("txdtm", txdtm);
        sortedMap.put("sub_openid", openId);
        sortedMap.put("goods_name", "qfTest");
        Map<String, String> params = SignUtils.paraFilter(sortedMap);
        StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
        SignUtils.buildPayParams(buf, params, false);
        String preStr = buf.toString();
        String sign = DigestUtils.md5Hex(preStr+"0910E30FD78648BE913F167F9594932E").toUpperCase();
        sortedMap.put("sign", sign);
        return sortedMap;
    }
}
