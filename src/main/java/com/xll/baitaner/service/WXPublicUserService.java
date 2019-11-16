package com.xll.baitaner.service;

import com.xll.baitaner.entity.WXPublicUserInfo;

import java.util.List;

/**
 * 接口名：WXPublicUserService
 * 描述：微信公众号用户管理服务接口类
 * 创建者：xie
 * 日期：2019/11/16/016
 **/
public interface WXPublicUserService {

    /**
     * 微信公众号用户管理接口——获取公众号用户列表
     * 一次拉取调用最多拉取10000个关注者的OpenID
     * @param nextOpenid 第一个拉取的OPENID，不填默认从头开始拉取
     * @return openId列表
     */
    public List<String> getWXPublicUserList(String nextOpenid);


    /**
     * 微信公众号用户管理接口——获取公众号用户信息
     * @param openId
     * @return
     */
    public WXPublicUserInfo getWXPublicUserInfoByOpenid(String openId);


    /**
     * 本地接口——判断数据库是否存在公众号用户
     * @param openId
     * @return
     */
    public boolean existWXPublicUserInfo(String openId);

    /**
     * 本地接口——数据库插入公众号用户信息
     * 已存在则不插入
     * @param userInfo
     * @return
     */
    public boolean insertWXPublicUser(WXPublicUserInfo userInfo);

    /**
     * 本地接口——数据库删除公众号用户信息
     * @param openId
     * @return
     */
    public boolean deleteWXPublicUser(String openId);

    /**
     * 本地接口——根据unionid获取公众号用户信息
     * 如用户未关注公众号则返回null
     * @param unionid
     * @return
     */
    public WXPublicUserInfo getWXPublicUserInfoByUnionid(String unionid);
}
