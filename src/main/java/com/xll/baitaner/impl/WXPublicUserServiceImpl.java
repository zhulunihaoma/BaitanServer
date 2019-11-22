package com.xll.baitaner.impl;

import com.xll.baitaner.entity.WXPublicUserInfo;
import com.xll.baitaner.mapper.WXPublicUserMapper;
import com.xll.baitaner.service.WXPublicUserService;
import com.xll.baitaner.service.WeChatService;
import com.xll.baitaner.utils.HttpRequest;
import com.xll.baitaner.utils.LogUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名：WXPublicUserServiceImpl
 * 描述：微信公众号用户管理服务类
 * 创建者：xie
 * 日期：2019/11/16/016
 **/
@Service
public class WXPublicUserServiceImpl implements WXPublicUserService {

    private String TAG = "Baitaner-WXPublicUserService";

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private WXPublicUserMapper wxPublicUserMapper;


    /**
     * 微信公众号用户管理接口——获取公众号用户列表
     * 一次拉取调用最多拉取10000个关注者的OpenID
     * @param nextOpenid 第一个拉取的OPENID，不填默认从头开始拉取
     * @return openId列表
     */
    @Override
    public List<String> getWXPublicUserList(String nextOpenid) {
        List<String> result = new ArrayList<>();
        String url = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=" + weChatService.getPublicAccessToken();
        if(nextOpenid != null){
            url = url + "&next_openid=" + nextOpenid;
        }
        LogUtils.info(TAG, "获取公众号用户列表 url: \n" + url);
        try {
            String res = HttpRequest.sendRequest(url, "GET", null);

            if (StringUtils.isNotBlank(res)) {
                JSONObject obj = JSONObject.fromObject(res);
                if(obj.containsKey("errcode")){
                    LogUtils.warn(TAG, "获取公众号用户列表错误,返回res：" + res);
                    return null;
                }else {
                    JSONObject data = JSONObject.fromObject(obj.get("data"));
                    result = (List<String>) data.get("openid");
                }
            }else {
                LogUtils.warn(TAG, "获取公众号用户列表错误,返回res：" + res);
                return null;
            }
        }catch (Exception e){
            result = null;
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 微信公众号用户管理接口——获取公众号用户信息
     * @param openId
     * @return
     */
    @Override
    public WXPublicUserInfo getWXPublicUserInfoByOpenid(String openId) {
        WXPublicUserInfo userInfo = new WXPublicUserInfo();
        String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + weChatService.getPublicAccessToken() +
                "&openid="+ openId + "&lang=zh_CN";
        try{
            String res = HttpRequest.sendRequest(url, "GET", null);
            if (StringUtils.isNotBlank(res)) {
                JSONObject obj = JSONObject.fromObject(res);
                if(obj.containsKey("errcode")){
                    userInfo = null;
                    LogUtils.warn(TAG, "获取公众号用户信息失败,返回res：" + res);
                }else {
                    LogUtils.warn(TAG, "获取公众号用户信息成功,返回res：" + res);
                    userInfo.setOpenId(obj.getString("openid"));
                    userInfo.setSubscribe(obj.getInt("subscribe"));
                    userInfo.setSubscribeTime(obj.getString("subscribe_time"));
                    userInfo.setNickname(obj.getString("nickname"));
                    userInfo.setSex(obj.getInt("sex"));
                    userInfo.setCountry(obj.getString("country"));
                    userInfo.setProvince(obj.getString("province"));
                    userInfo.setCity(obj.getString("city"));
                    userInfo.setLanguage(obj.getString("language"));
                    userInfo.setHeadImgUrl(obj.getString("headimgurl"));
                    userInfo.setUnionid(obj.getString("unionid"));
                }
            }else {
                userInfo = null;
                LogUtils.warn(TAG, "获取公众号用户信息失败,返回res：" + res);
            }
        }catch (Exception e){
            userInfo = null;
            e.printStackTrace();
        }

        return userInfo;
    }

    /**
     * 本地接口——判断数据库是否存在公众号用户
     * @param openId
     * @return
     */
    @Override
    public boolean existWXPublicUserInfo(String openId) {
        return wxPublicUserMapper.slelctCountWXPublicUser(openId) > 0;
    }

    /**
     * 本地接口——数据库插入公众号用户信息
     * 已存在则不插入
     * @param userInfo
     * @return
     */
    @Override
    public boolean insertWXPublicUser(WXPublicUserInfo userInfo) {
        if (existWXPublicUserInfo(userInfo.getOpenId())){
            LogUtils.info(TAG, "插入公众号用户fail，openid " + userInfo.getOpenId() + " 数据库已存在");
            return false;
        }
        boolean res = wxPublicUserMapper.insertWXPublicUser(userInfo) > 0;
        if (!res)
            LogUtils.info(TAG, "插入公众号用户fail，openid " + userInfo.getOpenId());
        return res;
    }

    /**
     * 本地接口——数据库删除公众号用户信息
     * @param openId
     * @return
     */
    @Override
    public boolean deleteWXPublicUser(String openId) {
        boolean res = wxPublicUserMapper.deleteWXPublicUser(openId) > 0;
        if (!res)
            LogUtils.info(TAG, "删除公众号用户fail，openid " + openId);
        return res;
    }

    /**
     * 本地接口——根据unionid获取公众号用户信息
     * 如用户未关注公众号则返回null
     * @param unionid
     * @return
     */
    @Override
    public WXPublicUserInfo getWXPublicUserInfoByUnionid(String unionid) {
        //先获取数据库中公众号用户信息
        WXPublicUserInfo userInfo = wxPublicUserMapper.selectPublicUser(unionid);

        if(userInfo != null){
            //通过微信接口获取公众号用户信息
            userInfo = this.getWXPublicUserInfoByOpenid(userInfo.getOpenId());
            //判断订阅状态
            if(userInfo.getSubscribe() == 1){
                //已订阅
                return userInfo;
            }else if(userInfo.getSubscribe() == 0){
                //如用户已取消订阅，则删除数据库中数据
                deleteWXPublicUser(userInfo.getOpenId());
            }
        }
        return null;
    }

    /**
     * 获取公众号用户信息并插入数据库 定时任务
     */
    //@Scheduled(cron = "0 0 2 * * ?")
    @Scheduled(fixedRate = 10800 * 1000)
    private void SetWXPublicUserToDataBase(){
        //获取公众号关注着列表
        List<String> userList = getWXPublicUserList(null);
        if(userList != null && userList.size() > 0){
            LogUtils.info(TAG, "WXPublicUserToDataBase--获取公众号用户信息并插入数据库");

            //超过10000条 再次拉取
            if(userList.size() >= 10000){
                userList.addAll(getWXPublicUserList(userList.get(userList.size() - 1)));
            }

            try {
                for(int i = 0; i < userList.size(); i++){
                    String openId = userList.get(i);
                    //已存在 不插入
                    if (this.existWXPublicUserInfo(openId)){
                        continue;
                    }

                    WXPublicUserInfo userInfo = this.getWXPublicUserInfoByOpenid(openId);
                    if(userInfo != null){
                        boolean res = this.insertWXPublicUser(userInfo);
                        LogUtils.warn(TAG, "WXPublicUserToDataBase-用户信息 " + openId + " 插入数据库: " + res);
                    }else {
                        LogUtils.warn(TAG, "WXPublicUserToDataBase--用户信息获取getWXPublicUserInfoByOpenid失败: " + openId);
                        break;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
