package com.xll.baitaner.impl;

import com.xll.baitaner.entity.WXPublicUserInfo;
import com.xll.baitaner.service.WXMessageService;
import com.xll.baitaner.service.WXPublicUserService;
import com.xll.baitaner.service.WeChatService;
import com.xll.baitaner.utils.HttpRequest;
import com.xll.baitaner.utils.LogUtils;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 类名：WXMessageServiceImpl
 * 描述：微信公众号消息处理类
 * 创建者：xie
 * 日期：2019/11/16/016
 **/
@Service
public class WXMessageServiceImpl implements WXMessageService {

    private String TAG = "Baitaner-WXMessageService";

    @Autowired
    private WXPublicUserService wxPublicUserService;

    @Autowired
    private WeChatService weChatService;

    /**
     * 响应微信服务器 公众号推送消息
     * @param requestMsg 返回响应消息
     * @return
     */
    @Override
    public String wxPublicMessageManager(String requestMsg) {
        String responseMsg = "";
        try
        {
            LogUtils.info(TAG, "接收到微信公众号发送的消息：\n" + requestMsg);

            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap = HttpRequest.xmlToMap(requestMsg);

            /**消息类型**/
            String msgType = resultMap.get("MsgType");

            if(msgType.equals(MESSAGE_TYPE_TEXT)){ //文本消息
                responseMsg = MessageText(resultMap, 0);
            }
            else if(msgType.equals(MESSAGE_TYPE_EVENT)){ //事件推送
                responseMsg = MessageEvent(resultMap);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return responseMsg;
    }

    /**
     * 响应小程序客服消息
     * @param requestMsg 返回响应消息
     * @return
     */
    @Override
    public String wxAppletMessageManager(String requestMsg) {
        String responseMsg = "";
        try
        {
            LogUtils.info(TAG, "接收到小程序客服消息：\n" + requestMsg);

            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap = HttpRequest.xmlToMap(requestMsg);

            /**消息类型**/
            String msgType = resultMap.get("MsgType");

            if(msgType.equals(MESSAGE_TYPE_TEXT)){ //文本消息
                responseMsg = MessageText(resultMap, 1);
            }
            else if(msgType.equals(MESSAGE_TYPE_EVENT)){ //事件推送
                responseMsg = MessageEvent(resultMap);
            }

            //目前直接回复二维码链接
            sendLinkMsg(resultMap.get("FromUserName"));
        }catch (Exception e){
            e.printStackTrace();
        }

        return responseMsg;
    }

    /**
     * 接收文本消息
     * @param resultMap
     * @param type  0: 公众号  1: 小程序
     * @return
     */
    public String MessageText(Map<String, String> resultMap, int type) throws Exception{
        LogUtils.info(TAG, "接收到文本消息--------");
        String res = "";
        /**发送方帐号（open_id）**/
        String fromUserName = resultMap.get("FromUserName");
        /**公众帐号**/
        String toUserName = resultMap.get("ToUserName");
        /**消息内容**/
        String content = resultMap.get("Content");

        if (type == 0){
            String re_content = "亿优微平台有各种神奇的小程序等着你体验\n " +
                    "点击下方“一键开店”按钮，创建属于自己的微信小店吧！";
            Map<String, String> message = new HashMap<String, String>();
            message.put("ToUserName", fromUserName);
            message.put("FromUserName", toUserName);
            message.put("CreateTime", new Date().getTime() + "");
            message.put("MsgType", MESSAGE_TYPE_TEXT);
            message.put("Content",re_content );
            res = HttpRequest.mapToXml(message);
        }
        else if (type == 1){
            //sendLinkMsg(fromUserName);
        }

        LogUtils.info(TAG, "接收文本消息后自动回复：" + res);
        return res;
    }

    /**
     * 接收事件推送
     * @param resultMap
     * @return
     */
    public String MessageEvent(Map<String, String> resultMap)throws Exception{
        LogUtils.info(TAG, "接收到事件推送--------");
        String res = "";
        /**发送方帐号（open_id）**/
        String fromUserName = resultMap.get("FromUserName");
        /**公众帐号**/
        String toUserName = resultMap.get("ToUserName");
        /**事件类型**/
        String eventType = resultMap.get("Event");

        Map<String, String> message = new HashMap<String, String>();
        message.put("ToUserName", fromUserName);
        message.put("FromUserName", toUserName);
        message.put("CreateTime", new Date().getTime() + "");
        message.put("MsgType", MESSAGE_TYPE_TEXT);

        if(eventType.equals(EVENT_TYPE_SUBSCRIBE)){
            //公众号—新关注的用户
            WXPublicUserInfo userInfo = wxPublicUserService.getWXPublicUserInfoByOpenid(fromUserName);
            if (!wxPublicUserService.existWXPublicUserInfo(fromUserName)){
                boolean insert = wxPublicUserService.insertWXPublicUser(userInfo);
                LogUtils.info(TAG, "新关注用户openId: " + fromUserName + " 插入数据库：" + insert);
            }

            String content = "终于等到你的关注啦！\n" + userInfo.getNickname() + "\n" +
                    "这里有各种神奇的小程序在等着你探索! \n点击下方“一键开店”按钮，创建属于自己的微信小店吧！";
            message.put("Content",content );

            res = HttpRequest.mapToXml(message);
        }
        else if(eventType.equals(EVENT_TYPE_UNSUBSCRIBE)){
            //公众号—取消关注的用户
            boolean delete = wxPublicUserService.deleteWXPublicUser(fromUserName);
            LogUtils.info(TAG, "删除取消关注的用户openId：" + fromUserName + " " + delete);
        }
        else if(eventType.equals(EVENT_TYPE_TEMPLATESENDJOBFINISH)){
            //公众号发送模板消息返回结果
            String status = resultMap.get("Status");
            LogUtils.info(TAG, "向用户" + fromUserName + " 发送模板消息的状态Status：" + status);
        }
        else if(eventType.equals("user_enter_tempsession")){
            //小程序“客服会话按钮”进入客服会话
            //sendLinkMsg(fromUserName);
        }

        LogUtils.info(TAG, "接收事件推送后自动回复：" + res);
        return res;
    }

    /**
     * 小程序客服会话发送公众号二维码
     * TODO
     * @param fromUserName
     */
    private void sendQrcodeMsg(String fromUserName) {
        JSONObject data = new JSONObject();

        JSONObject link = new JSONObject();

        data.put("touser", fromUserName);
        data.put("msgtype", "link");
        data.put("link", link);

        LogUtils.info(TAG, "小程序客服会话发送公众号二维码");
        weChatService.sendTemplateMessage(data, 2);
    }

    /**
     * 小程序客服会话发送公众号二维码图文消息链接
     * @param fromUserName
     */
    public void sendLinkMsg(String fromUserName){
        JSONObject data = new JSONObject();

        JSONObject link = new JSONObject();
        link.put("title", "公众号二维码");
        link.put("description", "长按二维码关注公众号");
        link.put("url", "https://mp.weixin.qq.com/s/tcS33jQ-IApKcBSL8aeO3g");
        link.put("thumb_url", "https://mmbiz.qpic.cn/mmbiz_jpg/J630qvlj7vzR0L0pqYicRqMib21An6HykJWiapRBp5cOWl0kytOicOdNLaLbDlC26clODB4WN2IAVvicYQLQLTITgww/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1");

        data.put("touser",fromUserName);
        data.put("msgtype","link");
        data.put("link",link);

        LogUtils.info(TAG, "小程序客服会话发送公众号二维码链接");
        weChatService.sendTemplateMessage(data, 2);
    }
}
