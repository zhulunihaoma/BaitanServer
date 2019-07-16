package com.xll.baitaner.impl;

import com.xll.baitaner.service.WeChatService;
import com.xll.baitaner.utils.Constant;
import com.xll.baitaner.utils.HttpRequest;
import com.xll.baitaner.utils.LogUtils;
import com.xll.baitaner.utils.WXPayConfigImpl;
import net.sf.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 类名：WeChatServiceImpl
 * 描述：与微信API交互的相关业务impl
 * 创建者：xie
 * 日期：2019/7/9/009
 **/
@Service
public class WeChatServiceImpl implements WeChatService {

    private String TAG = "Baitaner-WeChatServiceImpl";

    private WXPayConfigImpl wxConfig;

    /**
     * 微信小程序access_token 后台接口调用凭证
     */
    private static String access_token_applet = "";

    /**
     * 亿优公众号access_token 后台接口调用凭证 TODO 后期单独做个微信公众号中控服务器 管理公众号相关业务
     */
    private static String access_token_public = "";


    /**
     * 获取微信小程序access_token
     * @return
     */
    public String getAppletAccessToken(){
        return access_token_applet;
    }

    /**
     * 每隔一个半小时获取access_token
     */
    @Scheduled(fixedRate = 5400 * 1000)
    private void getAccess_token() {
        LogUtils.info(TAG, "getAccess_token--------");
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + Constant.APPLET_APP_ID +
                "&secret=" + Constant.APPLET_APP_SECRET;
        LogUtils.info(TAG, "获取小程序access_token，请求url：" + url);

        try {
            String res = HttpRequest.sendRequest(url, "GET", null);
            LogUtils.info(TAG, "获取小程序access_token,返回res：" + res);

            if (res != null && !"".equals(res)) {
                JSONObject obj = JSONObject.fromObject(res);
                if(obj.containsKey("access_token")){
                    access_token_applet = obj.get("access_token").toString();
                    LogUtils.info(TAG, "小程序access_token生成：" + access_token_applet);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 公众号、小程序发送消息
     * @param jsonObject //模板消息data
     * @param type 0：公众号发送模板消息 1：小程序发送模板消息 2：小程序发送客服消息
     * @return
     */
    @Override
    public String sendTemplateMessage(JSONObject message, int type) {
        String strUrl = "";
        if(type == 0){
            strUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + access_token_public;
        }else if(type == 1){
            strUrl = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token_applet;
        }else if(type == 2){
            strUrl = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + access_token_applet;
        }

        try {
            //POST请求小程序二维码生成
            String UTF8 = "UTF-8";
            URL httpUrl = new URL(strUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) httpUrl.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.connect();

            LogUtils.info(TAG, "sendTemplateMessage reqBody：\n" + message.toString());

            OutputStream outputStream = httpURLConnection.getOutputStream();
            // 注意编码格式
            outputStream.write(message.toString().getBytes(UTF8));
            outputStream.close();

            //获取内容
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, UTF8));
            final StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

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
            String resp = stringBuffer.toString();
            LogUtils.info(TAG,"sendTemplateMessage resp--：\n" + resp);

            JSONObject resq_json = JSONObject.fromObject(resp);
            if(resq_json.getInt("errcode") == 0){
                LogUtils.info(TAG,"sendTemplateMessage success");
                return null;
            }else {
                LogUtils.warn(TAG,"sendTemplateMessage fail: " + resq_json.get("errcode") + "__" + resq_json.get("errmsg"));
                return "发送模板洗消息失败：" + resq_json.get("errcode") + "__" + resq_json.get("errmsg");
            }
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
