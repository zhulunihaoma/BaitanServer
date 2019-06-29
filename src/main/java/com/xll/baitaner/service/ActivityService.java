package com.xll.baitaner.service;

import com.xll.baitaner.entity.Activity;
import com.xll.baitaner.entity.ActivityRecord;
import com.xll.baitaner.entity.ActivityShopCommodity;
import com.xll.baitaner.entity.SupportRecord;
import net.sf.json.JSONObject;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 活动接口
 *
 * @author denghuohuo 2019/6/25
 */
public interface ActivityService {

    /**
     * 查询用户参加所有活动列表
     *
     * @param openId
     * @return
     */
    List<Activity> getActivitylist(String openId);

    /**
     * 删除活动
     *
     * @param activityId
     * @return
     */
    boolean deleteActivity(int activityId);

    /**
     * 修改活动状态
     *
     * @param activityId
     * @param status
     * @return
     */
    boolean switchActivityStatus(int activityId, int status);

    /**
     * 新增活动信息数据操作
     *
     * @param activity
     * @return
     */
    int insertActivity(Activity activity);

    /**
     * 根据活动id获取活动详情
     *
     * @param activityId
     * @return
     */
    JSONObject getActivityById(int activityId);

    /**
     * 根据活动id获取活动详情
     *
     * @param activityId
     * @return
     */
    ActivityShopCommodity getActivityById2(int activityId);

    /**
     * 新增activityrecord
     *
     * @param activityRecord
     * @return
     */
    int insertActivityRecord(ActivityRecord activityRecord);

    /**
     * 根据activityrecord查询activityrecord详情
     *
     * @param activityRecordId
     * @return
     */
    ActivityRecord getActivityrecordById(int activityRecordId);

    /**
     * 新增supportrecord
     * 提示码  1 请求失败 0 请求成功 2 请求成功，处理失败
     *
     * @param activityId
     * @param operateType
     * @param operateContent
     * @param recordId
     * @param openId
     * @param nickName
     * @param avatarUrl
     * @param gender
     * @return
     */
    int insertSupportrecord(int activityId, String operateType, String operateContent, int recordId, String openId, String nickName, String avatarUrl, String gender);

    /**
     * 查询一个recordId所有的点赞者
     *
     * @param recordId
     * @return
     */
    List<SupportRecord> selecSupportRecordList(int recordId);

    /**
     * 更新 activityrecord supportCount+1
     *
     * @param recordId
     * @return
     */
    boolean addsupportCount(int recordId);

    /**
     * 查询一个openId有没有参加过一个活动
     *
     * @param openId
     * @param activityId
     * @return
     */
    List<ActivityRecord> selectActivityRecordByOpenId_ActivityId(String openId, int activityId);

    /**
     * 查询活动record根据排名
     *
     * @param activityId
     * @param pageable
     * @return
     */
    List<ActivityRecord> selectActivityRecordByOrder(int activityId, Pageable pageable);

    /**
     * 查询一个openId参加过所有活动
     *
     * @param openId
     * @param pageable
     * @return
     */
    List<ActivityRecord> selectActivityRecordByOpenId(String openId, Pageable pageable);
}