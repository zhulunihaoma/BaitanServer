package com.xll.baitaner.service;

/**
 * 接口名：WXPublicMessageService
 * 描述：微信公众号消息处理接口类
 * 创建者：xie
 * 日期：2019/11/16/016
 **/
public interface WXPublicMessageService {

    /**
     * 消息类型：文本消息
     */
    public static final String MESSAGE_TYPE_TEXT = "text";

    /**
     * 消息类型：图片消息
     */
    public static final String MESSAGE_TYPE_IMAGE = "image";

    /**
     * 消息类型：语音消息
     */
    public static final String MESSAGE_TYPE_VOICE = "voice";

    /**
     * 消息类型：视频消息
     */
    public static final String MESSAGE_TYPE_VIDEO = "video";

    /**
     * 消息类型：视频消息
     */
    public static final String MESSAGE_TYPE_SHORTVIDEO = "shortvideo";

    /**
     * 消息类型：链接
     */
    public static final String MESSAGE_TYPE_LINK = "link";

    /**
     * 消息类型：地理位置
     */
    public static final String MESSAGE_TYPE_LOCATION = "location";


    /**
     * 消息类型：推送事件
     */
    public static final String MESSAGE_TYPE_EVENT = "event";

    /**
     * 事件类型：subscribe(订阅)
     */
    public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";

    /**
     * 事件类型：unsubscribe(取消订阅)
     */
    public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";

    /**
     * 事件类型：上报地理位置
     */
    public static final String EVENT_TYPE_LOCATION = "LOCATION";

    /**
     * 事件类型：CLICK(自定义菜单点击事件)
     */
    public static final String EVENT_TYPE_CLICK = "CLICK";

    /**
     * 事件类型：模版消息送达后通知
     */
    public static final String EVENT_TYPE_TEMPLATESENDJOBFINISH = "TEMPLATESENDJOBFINISH";


    /**
     * 响应微信公众号消息
     * @param requestMsg 返回响应消息
     * @return
     */
    public String wxPublicMessageManager(String requestMsg);
}
