package xll.baitaner.service.service;

import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xll.baitaner.service.entity.Activity;
import xll.baitaner.service.entity.Shop;
import xll.baitaner.service.entity.WXUserInfo;
import xll.baitaner.service.mapper.ActivityMapper;

import java.util.List;

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
        Shop shopinfo = shopManageService.getShopByUser(activity.getOpenId());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("activity",activity);
        jsonObject.put("shopinfo",shopinfo);


        return jsonObject;

    }
}
