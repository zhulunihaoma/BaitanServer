package xll.baitaner.service.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import xll.baitaner.service.entity.Activity;
import xll.baitaner.service.entity.ActivityRecord;
import xll.baitaner.service.entity.SupportRecord;
import xll.baitaner.service.entity.WXUserInfo;

import java.util.List;

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
     * 新增activityrecord
     * @param activityRecord
     * @return
     */
    @Insert("INSERT INTO activityrecord (activityId,openId,nickName,avatarUrl,gender) " +
            "VALUES (#{activityRecord.activityId},#{activityRecord.openId},#{activityRecord.nickName},#{activityRecord.avatarUrl},#{activityRecord.gender})")
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
    @Insert("INSERT INTO supportrecord (recordId,openId,nickName,avatarUrl,gender) " +
            "VALUES (#{recordId},#{openId},#{nickName},#{avatarUrl},#{gender})")
    int insertSupportrecord(@Param("recordId") int recordId ,@Param("openId") String openId,@Param("nickName")String nickName, @Param("avatarUrl") String avatarUrl, @Param("gender") String gender);

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
    ActivityRecord selectActivityRecordByOpenId_ActivityId(@Param("openId") String openId,@Param("activityId") int activityId);


}


