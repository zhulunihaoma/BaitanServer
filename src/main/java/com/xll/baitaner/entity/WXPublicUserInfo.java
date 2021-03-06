package com.xll.baitaner.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 类名：WXPublicUserInfo
 * 描述：微信用户信息实体类 （公众号）
 * 创建者：xie
 * 日期：2019/11/16/016
 **/
@Getter
@Setter
public class WXPublicUserInfo {

    /**
     *  用户的标识
     */
    private String openId;

    /**
     * 关注状态（1是关注，0是未关注），未关注时获取不到其余信息
     */
    private int subscribe;

    /**
     * 用户关注时间，为时间戳。如果用户曾多次关注，则取最后关注时间
     */
    private String subscribeTime;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 用户的性别（1是男性，2是女性，0是未知）
     */
    private int sex;

    /**
     * 用户所在国家
     */
    private String country;

    /**
     * 用户所在省份
     */
    private String province;

    /**
     * 用户所在城市
     */
    private String city;

    /**
     * 用户的语言，简体中文为zh_CN
     */
    private String language;

    /**
     * 用户头像
     */
    private String headImgUrl;

    /**
     * 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。
     */
    private String unionid;

}
