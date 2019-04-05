package xll.baitaner.service.service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xll.baitaner.service.entity.*;
import xll.baitaner.service.mapper.ActivityMapper;
import xll.baitaner.service.utils.DateUtils;

import java.util.List;
import java.util.Map;

/**
 * @author zhulu
 * @version 1.0
 * @description xll.baitaner.service.service
 * @date 2019/1/13
 */
@Service
public class ActivityService {
    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ShopManageService shopManageService;

    @Autowired
    private  CommodityService commodityService;

    /**
     * 查询用户参加所有活动列表
     * @param openId
     * @return
     */
    public List<Activity> getActivitylist(String openId){
        return activityMapper.selectActivityList(openId);
    }

    /**
     * 删除活动
     * @param activityId
     * @return
     */
    public boolean deleteActivity(int activityId){
        return  activityMapper.deleteActivity(activityId) > 0;
    }

    /**
     * 修改活动状态
     * @param activityId
     * @param status
     * @return
     */
    public boolean switchActivityStatus(int activityId,int status){
        return activityMapper.switchActivityStatus(activityId,status) >0;
    }
    /**
     * 新增活动信息数据操作
     * @param activity
     * @return
     */
    public int insertActivity(Activity activity){

        boolean result = activityMapper.insertActivity(activity) > 0;
        if (result){
            return activity.getId();
        }
        else {
            return -1;
        }
    }
    /**
     * 根据活动id获取活动详情
     * @param activityId
     * @return
     */

    public JSONObject  getActivityById(int activityId){
        Activity activity = activityMapper.selectActivityById(activityId);
        Shop shopinfo = shopManageService.getShopById(activity.getShopId());//后面改成根据shopId获取，活动和店铺绑定而不是个人
        Commodity commodity = commodityService.getCommodity(activity.getCommodityId());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("activity",activity);
        jsonObject.put("shopinfo",shopinfo);
        jsonObject.put("commodity",commodity);
        return jsonObject;

    }
    /**
     * 新增activityrecord
     * @param activityRecord
     * @return
     */
    public  int insertActivityRecord(ActivityRecord activityRecord){

        boolean result =  activityMapper.insertActivityRecord(activityRecord) > 0;
        if (result){
            return activityRecord.getId();
        }
        else {
            return -1;
        }

    }
    /**
     * 根据activityrecord查询activityrecord详情
     * @param activityRecordId
     * @return
     */
    public JSONObject getActivityrecordById(int activityRecordId){
        ActivityRecord activityRecord = activityMapper.selectActivityrecordById(activityRecordId);
        Activity activity = activityMapper.selectActivityById(activityRecord.getActivityId());
        Shop shopinfo = shopManageService.getShopById(activity.getShopId());
        Commodity commodity = commodityService.getCommodity(activity.getCommodityId());
        List<SupportRecord>  supportRecordList = activityMapper.selecSupportRecordList(activityRecordId);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("shopinfo",shopinfo);
        activity.setEndTimeString(DateUtils.toStringtime(activity.getEndTime()));
        jsonObject.put("activityRecord",activityRecord);
        jsonObject.put("activity",activity);
        jsonObject.put("commodity",commodity);
        jsonObject.put("supportRecordList",supportRecordList);
        return jsonObject;

    }

    /**
     * 新增supportrecord
     * @param recordId
     * @param openId
     * @return
     */
    public  boolean insertSupportrecord(int recordId,String openId,String nickName,String avatarUrl,String gender){
        return  activityMapper.insertSupportrecord(recordId,openId,nickName,avatarUrl,gender) > 0;
    }
    /**
     *查询一个openId有没有参加过一个活动
     * @param openId
     * @param activityId
     * @return
     */
    public ActivityRecord selectActivityRecordByOpenId_ActivityId(String openId,int activityId){

        return activityMapper.selectActivityRecordByOpenId_ActivityId(openId,activityId);
    }
}

