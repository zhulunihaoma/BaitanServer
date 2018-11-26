package xll.baitaner.service.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import xll.baitaner.service.entity.WXUserInfo;

import java.util.List;

/**
 * 微信用户数据操作类
 */
@Repository
public interface WXUserMapper {

    /**
     * 查询是否已存在微信用户
     * @param openId
     * @return
     */
    @Select("SELECT COUNT(*) FROM wxuserinfo WHERE openId = #{openId}")
    int selectCountWXUser(@Param("openId") String openId);

    /**
     * 查询微信用户信息
     * @param openId
     * @return
     */
    @Select("SELECT * FROM wxuserinfo WHERE openId = #{openId}")
    WXUserInfo selectWXUser(@Param("openId") String openId);


    /**
     * 查询用户列表
     * @param isFake
     * @return
     */
    @Select("SELECT * FROM wxuserinfo WHERE isFake = #{isFake}")
    List<WXUserInfo> selectWXUserList(@Param("isFake") boolean isFake);

    /**
     * 新增微信用户数据
     * @param userInfo
     * @return
     */
    @Insert("INSERT INTO wxuserinfo (openId,nickName,avatarUrl,gender,city,province,country,language,unionId) " +
            "VALUES (#{userInfo.openId},#{userInfo.nickName},#{userInfo.avatarUrl},#{userInfo.gender},#{userInfo.country}," +
            "#{userInfo.province},#{userInfo.city},#{userInfo.language},#{userInfo.unionId})")
    int insertWXUser(@Param("userInfo") WXUserInfo userInfo);

    /**
     * 更新微信用户信息
     * @param userInfo
     * @return
     */
    @Update("UPDATE wxuserinfo SET nickName=#{userInfo.nickName}, avatarUrl=#{userInfo.avatarUrl}, " +
            "gender=#{userInfo.gender}, city=#{userInfo.city}, province=#{userInfo.province}, " +
            "country=#{userInfo.country}, language=#{userInfo.language} WHERE openId = #{userInfo.openId}")
    int updateWXUser(@Param("userInfo") WXUserInfo userInfo);

    /**
     * 设置微信用户的unionid
     * @param openId
     * @param unionid
     * @return
     */
    @Update("UPDATE wxuserinfo SET unionid = #{unionid} WHERE openId = #{openId}")
    int updateWXUserUnionid(@Param("openId") String openId, @Param("unionid") String unionid);
}
