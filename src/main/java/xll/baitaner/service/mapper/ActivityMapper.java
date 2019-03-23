package xll.baitaner.service.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import xll.baitaner.service.entity.Activity;
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
    @Insert("INSERT INTO activity (openId,activityType,activityName,activityIntro,startTime,endTime,originPrice,activityPrice,stock,operateType,operateContent,requirement,sendType)" +
            "VALUES (#{activity.openId},#{activity.activityType},#{activity.activityName},#{activity.activityIntro},#{activity.startTime},#{activity.endTime},#{activity.originPrice},#{activity.activityPrice},#{activity.stock},#{activity.operateType},#{activity.operateContent},#{activity.requirement},#{activity.sendType})")
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
    @Select("SELECT * FROM activity WHERE id = #{id}")
    Activity selectActivityById(@Param("id") int id);

    /*参加活动的内容*/


    /**
     * 新增activityrecord
     * @param activityId
     * @param openId
     * @return
     */
    @Insert("INSERT INTO activityrecord (activityId,openId)" +
            "VALUES (#{activity.openId},#{activity.activityType},#{activity.activityName},#{activity.activityIntro},#{activity.startTime},#{activity.endTime},#{activity.originPrice},#{activity.activityPrice},#{activity.stock},#{activity.operateType},#{activity.operateContent},#{activity.requirement},#{activity.sendType})")
    @Options(useGeneratedKeys = true, keyProperty = "activity.id")
    int insertActivity(@Param("activityId") int activityId , @Param("openId") String openId);
}



