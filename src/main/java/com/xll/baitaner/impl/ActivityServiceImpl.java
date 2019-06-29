package com.xll.baitaner.impl;

import com.xll.baitaner.entity.*;
import com.xll.baitaner.mapper.ActivityMapper;
import com.xll.baitaner.service.ActivityService;
import com.xll.baitaner.service.CommodityService;
import com.xll.baitaner.service.ShopManageService;
import com.xll.baitaner.utils.DateUtils;
import net.sf.json.JSONObject;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhulu
 * @version 1.0
 * @description xll.baitaner.service.service
 * @date 2019/1/13
 */
@Service
public class ActivityServiceImpl implements ActivityService {

    @Resource
    private ActivityMapper activityMapper;

    @Resource
    private ShopManageService shopManageService;

    @Resource
    private CommodityService commodityService;

    /**
     * 查询用户参加所有活动列表
     *
     * @param openId
     * @return
     */
    @Override
    public List<Activity> getActivitylist(String openId) {
        return activityMapper.selectActivityList(openId);
    }

    /**
     * 删除活动
     *
     * @param activityId
     * @return
     */
    @Override
    public boolean deleteActivity(int activityId) {
        return activityMapper.deleteActivity(activityId) > 0;
    }

    /**
     * 修改活动状态
     *
     * @param activityId
     * @param status
     * @return
     */
    @Override
    public boolean switchActivityStatus(int activityId, int status) {
        return activityMapper.switchActivityStatus(activityId, status) > 0;
    }

    /**
     * 新增活动信息数据操作
     *
     * @param activity
     * @return
     */
    @Override
    public int insertActivity(Activity activity) {
        boolean result = activityMapper.insertActivity(activity) > 0;
        if (result) {
            return activity.getId();
        } else {
            return -1;
        }
    }

    /**
     * 根据活动id获取活动详情
     *
     * @param activityId
     * @return
     */
    @Override
    public JSONObject getActivityById(int activityId) {
        Activity activity = activityMapper.selectActivityById(activityId);
        Shop shopinfo = shopManageService.getShopById(activity.getShopId());//后面改成根据shopId获取，活动和店铺绑定而不是个人
        Commodity commodity = commodityService.getCommodity(activity.getCommodityId());
        activity.setEndTimeString(DateUtils.toStringtime(activity.getEndTime()));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("activity", activity);
        jsonObject.put("shopinfo", shopinfo);
        jsonObject.put("commodity", commodity);
        return jsonObject;

    }

    /**
     * 根据活动id获取活动详情
     *
     * @param activityId
     * @return
     */
    @Override
    public ActivityShopCommodity getActivityById2(int activityId) {
        ActivityShopCommodity activity_shop_commodity = activityMapper.selectActivityById2(activityId);

//        jsonObject.put(DateUtils.toStringtime());

        return activity_shop_commodity;

    }

    /**
     * 新增activityrecord
     *
     * @param activityRecord
     * @return
     */
    @Override
    public int insertActivityRecord(ActivityRecord activityRecord) {
        activityRecord.setCurrentPrice(activityRecord.getActivityPrice());
        boolean result = activityMapper.insertActivityRecord(activityRecord) > 0;
        if (result) {
            return activityRecord.getId();
        } else {
            return -1;
        }

    }

    /**
     * 根据activityrecord查询activityrecord详情
     *
     * @param activityRecordId
     * @return
     */
    @Override
    public ActivityRecord getActivityrecordById(int activityRecordId) {
        ActivityRecord activityRecord = activityMapper.selectActivityrecordById(activityRecordId);

        return activityRecord;

    }

    /**
     * 根据recordId更改record状态
     *
     * @param recordId
     * @param recordStatus
     * @return
     */

    private boolean changeRecordstatus(int recordStatus, int recordId) {
        return activityMapper.changeRecordStatus(recordStatus, recordId) > 0;


    }

    /**
     * 新增supportrecord
     * 提示码  1 请求失败 0 请求成功 2 请求成功，处理失败
     *
     * @param recordId
     * @param operateContent;
     * @param openId
     * @return
     */
    @Override
    public int insertSupportrecord(int activityId, String operateType, String operateContent, int recordId, String openId, String nickName, String avatarUrl, String gender) {
        //如果supportrecord的数量已经达到要求了这时候要更改状态

        if (operateType.equals("maxnum")) {//点赞的数量超过也没事
            int result = activityMapper.insertSupportrecord(activityId, recordId, 0, openId, nickName, avatarUrl, gender);
            List<SupportRecord> SupportRecordList = activityMapper.selecSupportRecordList(recordId);
            if (SupportRecordList.size() >= Integer.parseInt(operateContent) && result > 0) {
                // 改变recordstattus的值为1（可购买）
                return activityMapper.changeRecordStatus(1, recordId) > 0 ? 0 : 1;

            } else {

                return 0;
            }
        } else if (operateType.equals("kanprice")) {
            //如果为砍价的话，获取当前record里面的currentprice 然后减去一定的价格然后再判断是否到最后了，是则改变状态
            ActivityShopCommodity activity_shop_commodity = activityMapper.selectActivityById2(activityId);
            ActivityRecord activityRecord = activityMapper.selectActivityrecordById(recordId);
            //活动价格和当前价格一致的时候 这时候需要提出提示，而不能再砍价了
            if (activityRecord.getCurrentPrice() == activity_shop_commodity.getActivityPrice()) {
                //给出提示已经砍完价格了
                return 2;
            } else {

                //  砍价计算
                float perPrice = (activity_shop_commodity.getPrice() - activity_shop_commodity.getActivityPrice()) / Integer.parseInt(operateContent);
                float newCurrentPrice = activityRecord.getCurrentPrice() - perPrice;
                int UpdateResult = activityMapper.UpdateCurrentPrice(newCurrentPrice, recordId) > 0 ? 0 : 1;


                int result = activityMapper.insertSupportrecord(activityId, recordId, perPrice, openId, nickName, avatarUrl, gender);//可以直接返回价格
                return result > 0 ? 0 : 1;

            }


        } else {
            return activityMapper.insertSupportrecord(activityId, recordId, 0, openId, nickName, avatarUrl, gender) > 0 ? 0 : 1;

        }


    }


    /**
     * 查询一个recordId所有的点赞者
     *
     * @param recordId
     * @return
     */
    @Override
    public List<SupportRecord> selecSupportRecordList(int recordId) {
        List<SupportRecord> SupportRecordList = activityMapper.selecSupportRecordList(recordId);
        return SupportRecordList;
    }

    /**
     * 更新 activityrecord supportCount+1
     *
     * @param recordId
     * @return
     */
    @Override
    public boolean addsupportCount(int recordId) {
        return activityMapper.addsupportCount(recordId) > 0;
    }

    /**
     * 查询一个openId有没有参加过一个活动
     *
     * @param openId
     * @param activityId
     * @return
     */
    @Override
    public List<ActivityRecord> selectActivityRecordByOpenId_ActivityId(String openId, int activityId) {

        return activityMapper.selectActivityRecordByOpenId_ActivityId(openId, activityId);
    }

    /**
     * 查询活动record根据排名
     *
     * @param activityId
     * @return
     */
    @Override
    public List<ActivityRecord> selectActivityRecordByOrder(int activityId, Pageable pageable) {
        return activityMapper.selectActivityRecordByOrder(activityId, pageable);
    }

    /**
     * 查询一个openId参加过所有活动
     *
     * @param openId
     * @return
     */
    @Override
    public List<ActivityRecord> selectActivityRecordByOpenId(String openId, Pageable pageable) {
        List<ActivityRecord> ActivityRecordList = activityMapper.selectActivityRecordByOpenId(openId, pageable);
//        for (ActivityRecord activityRecord : ActivityRecordList){
//
//            activityRecord.setActivity(this.getActivityById2(activityRecord.getActivityId()));
//        }
        //塞入对应的商品信息
        return ActivityRecordList;

    }
}
