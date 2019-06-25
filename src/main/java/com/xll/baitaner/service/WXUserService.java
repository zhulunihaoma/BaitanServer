package com.xll.baitaner.service;

import com.xll.baitaner.entity.WXUserInfo;

/**
 * @author denghuohuo 2019/6/25
 */
public interface WXUserService {

    /**
     * 微信用户数据操作
     *
     * @param userInfo
     * @return
     */
    String wxuserinfo(WXUserInfo userInfo);

    /**
     * 获取微信用户信息
     *
     * @param openId
     * @return
     */
    WXUserInfo getWXUserById(String openId);
}
