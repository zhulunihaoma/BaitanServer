package xll.baitaner.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xll.baitaner.service.entity.WXUserInfo;
import xll.baitaner.service.mapper.WXUserMapper;

/**
 * 微信用户数据服务类
 */
@Service
public class WXUserService {

    private String TAG = "BAITAN-WXUserService";


    /**
     * 小程序保存的access_token
     */
    public static String access_token = "";

    @Autowired
    public WXUserMapper wxUserMapper;


    /**
     * 微信用户数据操作
     * @param userInfo
     * @return
     */
    public String wxuserinfo(WXUserInfo userInfo){
        boolean isExisted = isWXUser(userInfo.getOpenId());
        if(isExisted){
            return wxUserMapper.updateWXUser(userInfo) > 0 ? null : "更新用户信息失败";
        }else {
            return wxUserMapper.insertWXUser(userInfo) > 0 ? null : "新增用户信息失败";
        }
    }

    /**
     * 查询是否已存在微信用户
     * @param openId
     * @return
     */
    public boolean isWXUser(String openId){
        return wxUserMapper.selectCountWXUser(openId) > 0;
    }

    /**
     * 获取微信用户信息
     * @param openId
     * @return
     */
    public WXUserInfo getWXUserById(String openId){
        return wxUserMapper.selectWXUser(openId);
    }
}
