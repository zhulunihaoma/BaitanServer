package com.xll.baitaner.impl;

import com.xll.baitaner.service.WeChatService;
import com.xll.baitaner.utils.*;
import net.sf.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
    @Override
    public String getAppletAccessToken(){
        return access_token_applet;
    }

    /**
     * 获取微信服务号access_token
     * @return
     */
    @Override
    public String getPublicAccessToken(){
        return access_token_public;
    }

    /**
     * 每隔一个半小时获取access_token
     */
    @Scheduled(fixedRate = 3600 * 1000)
    private void getAccess_token() {
        LogUtils.info(TAG, "getAccess_token--------");
        String url_applet = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + Constant.APPLET_APP_ID +
                "&secret=" + Constant.APPLET_APP_SECRET;
        String url_public = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + Constant.PUBLIC_APP_ID +
                "&secret=" + Constant.PUBLIC_APP_SECRET;
        LogUtils.info(TAG, "\n获取小程序access_token------------" + "\n获取服务号access_token------------");

        try {
            String res_applet = HttpRequest.sendRequest(url_applet, "GET", null);
            String res_public = HttpRequest.sendRequest(url_public, "GET", null);
            LogUtils.info(TAG, "\n获取小程序access_token,返回res：" + res_applet + "\n获取服务号access_token,返回res：" + res_public);

            if (res_applet != null && !"".equals(res_applet)) {
                JSONObject obj = JSONObject.fromObject(res_applet);
                if(obj.containsKey("access_token")){
                    access_token_applet = obj.get("access_token").toString();
                    LogUtils.info(TAG, "小程序access_token生成：" + access_token_applet);
                }
            }

            if (res_public != null && !"".equals(res_public)) {
                JSONObject obj = JSONObject.fromObject(res_public);
                if(obj.containsKey("access_token")){
                    access_token_public = obj.get("access_token").toString();
                    LogUtils.info(TAG, "服务号access_token生成：" + access_token_public);
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
            //POST请求
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
                return "0";
            }else {
                LogUtils.warn(TAG,"sendTemplateMessage fail: " + resq_json.get("errcode") + "__" + resq_json.get("errmsg"));
                return resq_json.get("errcode").toString();
            }
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * 生成小程序码并储存到服务器中，返回路径地址 使用微信wxacode.getUnlimited接口
     * @param sceneStr
     * @param page
     * @return
     */
    @Override
    public String creatWXacodeUnlimited(String sceneStr, String page) {
        String strUrl = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + access_token_applet;
        Map<String, String> reqData =new HashMap<>();
        reqData.put("scene", sceneStr);
        reqData.put("page", page);
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

            JSONObject jsonObject = JSONObject.fromObject(reqData);
            LogUtils.info(TAG, "getQRCode reqBody：" + jsonObject.toString());

            OutputStream outputStream = httpURLConnection.getOutputStream();
            // 注意编码格式
            outputStream.write(jsonObject.toString().getBytes(UTF8));
            outputStream.close();

            //获取返回内容
            InputStream inputStream = httpURLConnection.getInputStream();

            LogUtils.info(TAG,"creatWXacodeUnlimited fail --- inputStream.available()：" + inputStream.available());

            //判断返回的文件流大小，太小说明不是图片文件，是错误信息
            //打印错误信息并返回
            if (inputStream.available() < 5120){
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
                String resp = stringBuffer.toString();
                LogUtils.info(TAG,"creatWXacodeUnlimited fail --- resp：\n" + resp);
                return "";
            }

            //返回数据流生成二维码图片并保存
            String fileName = java.util.UUID.randomUUID().toString();
            FileOutputStream fileOut = new FileOutputStream(PathUtil.getUploadPath(fileName,true));
            byte[] buffer = new byte[1024];
            int read;
            while(true){
                read = inputStream.read(buffer,0,1024);
                if(read<1)
                    break;
                fileOut.write(buffer,0,read);
            }
            fileOut.flush();
            fileOut.close();

            File file = new File(PathUtil.getUploadPath(fileName,true));
            if (file.exists() && file.isFile()){
                if (file.length() < 5120){ //文件过小 说明保存失败
                    return "";
                }
            }else{
                return "";
            }

            return "https://www.eastzebra.cn/servicepicture/" + fileName;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
