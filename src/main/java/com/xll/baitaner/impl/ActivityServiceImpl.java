package com.xll.baitaner.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xll.baitaner.entity.*;
import com.xll.baitaner.entity.VO.ActivityRecordVO;
import com.xll.baitaner.entity.VO.ActivityResultVO;
import com.xll.baitaner.entity.VO.ActivityVO;
import com.xll.baitaner.mapper.ActivityMapper;
import com.xll.baitaner.mapper.WXUserMapper;
import com.xll.baitaner.service.ActivityService;
import com.xll.baitaner.service.CommodityService;
import com.xll.baitaner.service.ShopManageService;
import com.xll.baitaner.service.WXUserService;
import com.xll.baitaner.utils.DateUtils;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
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

    @Resource
    private WXUserService wxUserService;

    @Resource
    private WXUserMapper wxUserMapper;
    /**
     * 查询店铺创建的所有活动列表
     *
     * @param shopId
     * @return
     */
    @Override
    public ActivityResultVO getActivitylist(int shopId) {
        List<Activity> activityList = activityMapper.selectActivityList(shopId);
        List<ActivityVO> activityVOList = new ArrayList<>();
        ActivityResultVO activityResultVO = new ActivityResultVO();


        for (Activity activity : activityList) {
            ActivityVO activityVO = new ActivityVO();
            Commodity commodity = commodityService.getCommodity(activity.getCommodityId());
            Shop shop = shopManageService.getShopById(activity.getShopId());
            activityVO.setActivity(activity);
            activityVO.setCommodity(commodity);
            activityVO.setShop(shop);
            activityVOList.add(activityVO);
        }
        activityResultVO.setData(activityVOList);
        activityResultVO.setCount(activityVOList.size());


        return activityResultVO;
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
    public ActivityVO getActivityById(int activityId) {
        Activity activity = activityMapper.selectActivityById(activityId);

        Shop shopinfo = shopManageService.getShopById(activity.getShopId());
        Commodity commodity = commodityService.getCommodity(activity.getCommodityId());
//        activity.setEndTimeString(DateUtils.dateTimetoString(activity.getEndTime()));

        ActivityVO activityVO = new ActivityVO();
        activityVO.setShop(shopinfo);
        activityVO.setActivity(activity);
        activityVO.setCommodity(commodity);

        return activityVO;

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
    public ActivityRecordVO getActivityrecordById(int activityRecordId) {
        ActivityRecord activityRecord = activityMapper.selectActivityrecordById(activityRecordId);
        ActivityRecordVO activityRecordVO = new ActivityRecordVO();

        activityRecordVO.setActivityRecord(activityRecord);

        Activity activity = activityMapper.selectActivityById(activityRecord.getActivityId());
        activityRecordVO.setActivity(activity);

        Shop shop = shopManageService.getShopById(activity.getShopId());
        activityRecordVO.setShop(shop);

        Commodity commodity = commodityService.getCommodity(activity.getCommodityId());
        activityRecordVO.setCommodity(commodity);
        String openId = activityRecord.getOpenId();
        WXUserInfo wxUserInfo = wxUserService.getWXUserById(openId);
        activityRecordVO.setWxUserInfo(wxUserInfo);


        return activityRecordVO;

    }

    /**
     * 根据recordId更改record状态
     *
     * @param recordId
     * @param recordStatus
     * @return
     */

    public boolean changeRecordstatus(int recordStatus, int recordId) {
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

        if (operateType.equals("maxnum")) {//点赞数量类型  点赞的数量超过也没事
            int result = activityMapper.insertSupportrecord(activityId, recordId, "0.00", openId, nickName, avatarUrl,
                    gender);
            List<SupportRecord> SupportRecordList = activityMapper.selecSupportRecordList(recordId);
            if (SupportRecordList.size() >= Integer.parseInt(operateContent) && result > 0) {
                // 改变recordstattus的值为1（可购买）
                return activityMapper.changeRecordStatus(1, recordId) > 0 ? 0 : 1;

            } else {

                return 0;
            }
        } else if (operateType.equals("kanprice")) {//如果是砍价
            //如果为砍价的话，获取当前record里面的currentprice 然后减去一定的价格然后再判断是否到最后了，是则改变状态
            ActivityVO activityVO = this.getActivityById(activityId);

            ActivityRecord activityRecord = activityMapper.selectActivityrecordById(recordId);
            //活动价格和当前价格一致的时候 这时候需要提出提示，而不能再砍价了
            BigDecimal currentPrice = new BigDecimal(activityRecord.getCurrentPrice());//activityRecord.getCurrentPrice();
            BigDecimal ativityPrice = new BigDecimal(activityVO.getActivity().getActivityPrice());

            if (currentPrice.compareTo(ativityPrice) == 0) {
                //给出提示已经砍完价格了
                return 2;
            } else {

                //  砍价计算
                BigDecimal originprice =  new BigDecimal(activityVO.getCommodity().getPrice());
                BigDecimal activityprice = new BigDecimal(activityVO.getActivity().getActivityPrice());

                BigDecimal newCurrentPrice;
                BigDecimal cutPrice;
                List<SupportRecord> SupportRecordList = activityMapper.selecSupportRecordList(recordId);


                if(SupportRecordList.size() == Integer.parseInt(operateContent) +1){//如果是最后一个砍价者

                    newCurrentPrice = activityprice;
                    cutPrice = new BigDecimal(activityRecord.getCurrentPrice()).subtract(activityprice);
                }else {
                    cutPrice = (originprice.subtract(activityprice)).divide(new BigDecimal(operateContent));
                    newCurrentPrice = new BigDecimal(activityRecord.getCurrentPrice()).subtract(cutPrice);


                }

                int UpdateResult = activityMapper.UpdateCurrentPrice(newCurrentPrice.toString(), recordId) > 0 ? 0 : 1;


                int result = activityMapper.insertSupportrecord(activityId, recordId, cutPrice.toString(), openId, nickName,
                        avatarUrl, gender);//可以直接返回价格
                return result > 0 ? 0 : 1;

            }


        } else {//点赞排名
            return activityMapper.insertSupportrecord(activityId, recordId, "0.00", openId, nickName, avatarUrl,
                    gender) > 0 ? 0 : 1;

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
     * 更改/插入活动recordWxcode
     *
     * @param recordWxcode
     * @param recordId
     * @return
     */

    @Override
    public boolean updateRecordWxcode(int recordId, String recordWxcode) {
        return activityMapper.updateRecordWxcode(recordId, recordWxcode) > 0;
    }


    /**
     * 查询活动record根据排名
     *
     * @param activityId
     * @return
     */
    @Override
    public List<ActivityRecord> selectActivityRecordByOrder(int activityId, Integer offset, Integer size) {
        Page<ActivityRecord> page =
                PageHelper.startPage(offset, size).doSelectPage(() -> activityMapper.selectActivityRecordByOrder(activityId));
        List<ActivityRecord> result = page.getResult();
        if (result == null) {
            return new ArrayList<>();
        }
        return result;
    }

    /**
     * 查询一个openId参加过所有活动
     *
     * @param openId
     * @return
     */
    @Override
    public List<ActivityRecord> selectActivityRecordByOpenId(String openId, Integer offset, Integer size) {
        Page<ActivityRecord> page =
                PageHelper.startPage(offset, size).doSelectPage(() -> activityMapper.selectActivityRecordByOpenId(openId));
        List<ActivityRecord> result = page.getResult();
        if (result == null) {
            return new ArrayList<>();
        }
        //塞入对应的商品信息
        return result;

    }


    /**
     * 获客粉丝的插入
     *
     * @param fansPhone
     * @return
     */
    @Override
    public int insertFans_phone(FansPhone fansPhone){

        return activityMapper.insertFans_phone(fansPhone);

    }


    /**
     * 查询一个shopId所拥有的获客粉丝
     *
     * @param shopId
     * @return
     */
    @Override
    public List<FansPhone> selectFansPhoneByshopId(int shopId, Integer offset, Integer size){
        Page<FansPhone> page =
                PageHelper.startPage(offset, size).doSelectPage(() -> activityMapper.selectFansPhoneByshopId(shopId));
        List<FansPhone> result = page.getResult();
        if (result == null) {
            return new ArrayList<>();
        }
        //塞入对应的商品信息
        return result;
    }

}

