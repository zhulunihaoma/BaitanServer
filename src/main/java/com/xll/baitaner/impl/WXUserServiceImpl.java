package com.xll.baitaner.impl;

import com.xll.baitaner.entity.WXUserInfo;
import com.xll.baitaner.mapper.WXUserMapper;
import com.xll.baitaner.service.WXUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 微信用户数据服务类
 */
@Service
public class WXUserServiceImpl implements WXUserService {

    private final String TAG = "BAITAN-WXUserService";

    @Resource
    public WXUserMapper wxUserMapper;


    /**
     * 微信用户数据操作
     *
     * @param userInfo
     * @return
     */
    @Override
    public String wxuserinfo(WXUserInfo userInfo) {
        boolean isExisted = isWXUser(userInfo.getOpenId());
        if (isExisted) {
            return wxUserMapper.updateWXUser(userInfo) > 0 ? null : "更新用户信息失败";
        } else {
            return wxUserMapper.insertWXUser(userInfo) > 0 ? null : "新增用户信息失败";
        }
    }

    /**
     * 查询是否已存在微信用户
     *
     * @param openId
     * @return
     */
    @Override
    public boolean isWXUser(String openId) {
        return wxUserMapper.selectCountWXUser(openId) > 0;
    }

    /**
     * 获取微信用户信息
     *
     * @param openId
     * @return
     */
    @Override
    public WXUserInfo getWXUserById(String openId) {
        return wxUserMapper.selectWXUser(openId);
    }

    /**
     * 设置微信用户的unionid
     *
     * @param openId
     * @param unionid
     * @return
     */
    @Override
    public String UpdateWXUserUnionid(String openId, String unionid){
        if (wxUserMapper.updateWXUserUnionid(openId, unionid) > 0){
            return "设置微信用户的unionid成功";
        }
        else {
            return "设置微信用户的unionid失败";
        }
    }
}
