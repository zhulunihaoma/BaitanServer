package com.xll.baitaner.mapper;

import com.xll.baitaner.entity.WXPublicUserInfo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * 接口名：WXPublicUserMapper
 * 描述：微信公众号用户数据操作类
 * 数据库表：wx_public_user_info
 * 创建者：xie
 * 日期：2019/11/16/016
 **/
@Repository
public interface WXPublicUserMapper {

    /**
     * 插入公众号用户信息
     * @param userInfo
     * @return
     */
    @Insert("INSERT INTO wx_public_user_info (openId,subscribe,subscribeTime,nickname,headImgUrl,sex,city,province,country," +
            "language,unionid) VALUES (#{userInfo.openId},#{userInfo.subscribe},#{userInfo.subscribeTime}," +
            "#{userInfo.nickname},#{userInfo.headImgUrl},#{userInfo.sex},#{userInfo.country},#{userInfo.province}," +
            "#{userInfo.city},#{userInfo.language},#{userInfo.unionid})")
    int insertWXPublicUser(@Param("userInfo") WXPublicUserInfo userInfo);

    /**
     * 删除公众号用户
     * @param openId
     * @return
     */
    @Delete("DELETE FROM wxpublicuserinfo WHERE openId = #{openId}")
    int deleteWXPublicUser(@Param("openId") String openId);

    /**
     * 查询是否已存在公众号用户
     * @param openId
     * @return
     */
    @Select("SELECT COUNT(*) FROM wxpublicuserinfo WHERE openId = #{openId}")
    int slelctCountWXPublicUser(@Param("openId") String openId);

    /**
     * 根据unionid获取公众号用户信息
     * @param unionid
     * @return
     */
    @Select("SELECT * FROM wxpublicuserinfo WHERE unionid = #{unionid}")
    WXPublicUserInfo selectPublicUser(@Param("unionid") String unionid);
}
