package xll.baitaner.service.mapper;

import net.sf.json.JSONObject;
import org.apache.ibatis.annotations.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import xll.baitaner.service.entity.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhulu
 * @version 1.0
 * @description 关于活动相关接口
 * @date 2019/1/13
 */
@Repository
public interface ActivityMapper {
    /**
     * 新增活动
     * @param activity
     * @return
     */
    @Insert("INSERT INTO activity (openId,shopId,activityType,activityName,activityIntro,startTime,endTime,originPrice,activityPrice,stock,operateType,operateContent,requirement,sendType,commodityId) " +
            "VALUES (#{activity.openId},#{activity.shopId},#{activity.activityType},#{activity.activityName},#{activity.activityIntro},#{activity.startTime},#{activity.endTime},#{activity.originPrice},#{activity.activityPrice},#{activity.stock},#{activity.operateType},#{activity.operateContent},#{activity.requirement},#{activity.sendType},#{activity.commodityId})")
    @Options(useGeneratedKeys = true, keyProperty = "activity.id")
    int insertActivity(@Param("activity") Activity activity);

    /**
     * 删除活动
     * @param id
     * @return
     */
    @Delete("DELETE FROM activity WHERE Id = #{id}")
    int deleteActivity(@Param("id") int id);


    /**
     * 修改活动状态
     * @param id
     * @return
     */

    @Update("UPDATE activity SET status = #{status} WHERE id = #{id}")
    int switchActivityStatus(@Param("id") int id , @Param("status") int status);
    /**
     * 查询用户所有参加活动的列表
     * @param clientId
     * @return
     */
    @Select("SELECT * FROM activity WHERE openId = #{openId}")
    List<Activity> selectActivityList(@Param("openId") String clientId);

    /**
     * 根据活动id查询活动详情
     * @param id
     * @return
     */

    @Select("SELECT activity.*, commodity.* FROM activity JOIN commodity ON activity.commodityId = commodity.id WHERE activity.id = #{id}")
    Activity selectActivityById(@Param("id") int id);


    /**
     * 根据活动id查询活动详情 （多表查询activity,commodity,shopinfo 单独请求）
     * @param id
     * @return
     */

    @Select("SELECT activity.endTime, activity.status, activity.activityType, activity.nickName, activity.avatarUrl, activity.activityPrice,commodity.name As commodityName ,commodity.introduction, commodity.pictUrl, commodity.price, " +
            "shop.shopName ,shop.shopIntroduction ,shop.ownerName ,shop.wxNumber ,shop.contactNumber ,shop.shopAddress ,shop.shopLogoUrl ,shop.id As shopId FROM activity JOIN commodity ON activity.commodityId = commodity.id JOIN shop ON activity.shopId = shop.id   WHERE activity.id = #{id}")
    Activity_Shop_Commodity selectActivityById2(@Param("id") int id);

    /**
     * 新增activityrecord
     * @param activityRecord
     * @return
     */
    @Insert("INSERT INTO activityrecord (activityId,openId,nickName,avatarUrl,gender,commodityId,status,activityPrice,endTime,shopName,shopLogoUrl,goodname) " +
            "VALUES (#{activityRecord.activityId},#{activityRecord.openId},#{activityRecord.nickName},#{activityRecord.avatarUrl},#{activityRecord.gender},#{activityRecord.commodityId},#{activityRecord.status},#{activityRecord.activityPrice},#{activityRecord.endTime},#{activityRecord.shopName},#{activityRecord.shopLogoUrl},#{activityRecord.goodname})")
    @Options(useGeneratedKeys = true, keyProperty = "activityRecord.id")
    int insertActivityRecord(@Param("activityRecord") ActivityRecord activityRecord );

    /**
     * 根据activityrecord查询activityrecord详情
     * @param activityRecordId
     * @return
     */
    @Select("SELECT * FROM activityrecord WHERE id = #{activityRecordId}")
    ActivityRecord selectActivityrecordById(@Param("activityRecordId") int activityRecordId);

    /**
     * 新增supportrecord
     * @param recordId
     * @param openId
     * @return
     */
    @Insert("INSERT INTO supportrecord (activityId,recordId,openId,nickName,avatarUrl,gender) " +
            "VALUES (#{activityId},#{recordId},#{openId},#{nickName},#{avatarUrl},#{gender})")
    int insertSupportrecord(@Param("activityId") int activityId ,@Param("recordId") int recordId ,@Param("openId") String openId,@Param("nickName")String nickName, @Param("avatarUrl") String avatarUrl, @Param("gender") String gender);

    /**
     * 更新 activityrecord supportCount+1
     * @param recordId
     * @return
     */
    @Insert("UPDATE activityrecord SET supportCount = supportCount+1 WHERE id = #{recordId}")
    int addsupportCount(@Param("recordId") int recordId);

    /**
     * 查询一个record对应的support列表
     * @param recordId
     * @return
     */
    @Select("SELECT * FROM supportrecord WHERE recordId = #{recordId}")
    List<SupportRecord> selecSupportRecordList(@Param("recordId") int recordId);

    /**
     *查询一个openId有没有参加过一个活动
     * @param openId
     * @param activityId
     * @return
     */
    @Select("SELECT * FROM activityrecord WHERE openId  = #{openId} AND activityId = #{activityId}")
    List<ActivityRecord> selectActivityRecordByOpenId_ActivityId(@Param("openId") String openId, @Param("activityId") int activityId);


    /**
     *查询活动record根据排名
     * @param activityId
     * @return
     */
    @Select("SELECT * FROM activityrecord WHERE activityId  = #{activityId} ORDER BY supportCount DESC  LIMIT #{page.offset},#{page.size}")
    List <ActivityRecord> selectActivityRecordByOrder(@Param("activityId") int activityId,@Param("page") Pageable page);

    /**
     *查询一个openId参加过的所有活动
     * @param openId
     * @return
    */
    @Select("SELECT * FROM activityrecord WHERE openId  = #{openId} LIMIT #{page.offset},#{page.size}")
    List <ActivityRecord> selectActivityRecordByOpenId(@Param("openId") String openId,@Param("page") Pageable page);


    /**
     *更改一个活动record的状态
     * @param recordStatus
     * @return
     */
    @Update("UPDATE activityrecord SET recordStatus = #{recordStatus} WHERE id = #{recordId}")
    int  changeRecordStatus(@Param("recordStatus") int recordStatus, @Param("recordId") int recordId);


    /**
     * 定时任务方法 每小时执行一次 查询数据库中结束时间和当前时间一致的则改变状态值为0
     * @param status
     * @param endTime
     * @return
     */
    @Update("UPDATE activity SET status = #{status} WHERE endTime = #{endTime}")
    int  CheckActivityEndtime(@Param("status") int status, @Param("endTime") Date endTime);

    /**
     * 设置新的currentPrice
     * @param currentPrice
     * @param recordId
     * @return
     */
    @Update("UPDATE activityrecord SET currentPrice = #{currentPrice} WHERE recordId = #{recordId}")
    int  UpdateCurrentPrice(@Param("currentPrice") float currentPrice, @Param("recordId") int recordId);



}


