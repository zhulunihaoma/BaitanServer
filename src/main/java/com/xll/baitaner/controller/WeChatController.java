package com.xll.baitaner.controller;

import com.xll.baitaner.service.WXPublicMessageService;
import com.xll.baitaner.utils.LogUtils;
import com.xll.baitaner.utils.WeChatCheckoutUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * 类名：WeChatController
 * 描述：微信服务器通信
 * 微信公众号、微信小程序
 * 创建者：xie
 * 日期：2019/11/13/013
 **/
@RestController
public class WeChatController {

    private String TAG = "Baitan-WeChatController";

    /**
     * 微信服务器接入令牌
     */
    private static String token = "xie744428439";

    private static String UTF8 = "UTF-8";

    @Autowired
    private WXPublicMessageService wxPublicMessageService;

    /**
     * 接收微信服务器消息 (公众号)
     * @param request
     * @param response
     */
    @RequestMapping("wechatservice_public")
    public void WeChatService(HttpServletRequest request, HttpServletResponse response){
        try {
            request.setCharacterEncoding(UTF8);
            response.setCharacterEncoding(UTF8);

            boolean isGet = request.getMethod().toLowerCase().equals("get");
            PrintWriter out = response.getWriter();

            if(isGet){ //响应服务器接入
                String signature = request.getParameter("signature");// 微信加密签名
                String timestamp = request.getParameter("timestamp");// 时间戳
                String nonce = request.getParameter("nonce");// 随机数
                String echostr = request.getParameter("echostr");//随机字符串

                LogUtils.info(TAG,"公众号 Connect WeChatService---signature: " + signature +
                        "\ntimestamp: " + timestamp + "\nnonce: " + nonce + "\nechostr: " + echostr);

                boolean checkRes = WeChatCheckoutUtils.checkSignature(token, timestamp, nonce, signature);
                LogUtils.info(TAG, "WeChatPublic Check Signature---" + checkRes);
                if(checkRes){
                    out.write(echostr);
                    LogUtils.info(TAG, "公众号 微信服务器接入成功！");
                    //TODO 提交自定义菜单
                }
            }else { //响应消息推送
                InputStream inputStream = request.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, UTF8));
                final StringBuffer stringBuffer = new StringBuffer();
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                String req = stringBuffer.toString();

                //处理消息
                String resp = wxPublicMessageService.wxPublicMessageManager(req);
                out.write(resp);
            }

            out.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
