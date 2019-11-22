package com.xll.baitaner.service;

import net.sf.json.JSONObject;

/**
 * 接口名：WeChatService
 * 描述：与微信API交互的相关业务
 * 创建者：xie
 * 日期：2019/7/9/009
 **/
public interface WeChatService {

    /**
     * 获取微信小程序access_token
     * @return
     */
    public String getAppletAccessToken();

    /**
     * 获取微信服务号access_token
     * @return
     */
    public String getPublicAccessToken();

    /**
     * 公众号、小程序发送消息
     * @param jsonObject
     * @param type 0：公众号发送模板消息 1：小程序发送模板消息 2：小程序发送客服消息
     * @return
     */
    public String sendTemplateMessage(JSONObject jsonObject, int type);

    /**
     * 获取小程序码 使用微信wxacode.getUnlimited接口
     * @param sceneStr
     * @param page
     * @return
     */
    public String creatWXacodeUnlimited(String sceneStr, String page);
}
