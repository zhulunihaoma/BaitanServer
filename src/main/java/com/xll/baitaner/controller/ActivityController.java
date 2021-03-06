package com.xll.baitaner.controller;

import com.xll.baitaner.entity.Activity;
import com.xll.baitaner.entity.ActivityRecord;
import com.xll.baitaner.entity.FansPhone;
import com.xll.baitaner.service.ActivityService;
import com.xll.baitaner.utils.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;

/**
 * @author zhulu
 * @version 1.0
 * @description xll.baitaner.service.controller
 * @date 2019/1/13
 */
@Api(value = "活动模块controller")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController

public class ActivityController {

    @Resource
    private ActivityService activityService;


    /**
     * 新增活动数据操作
     *
     * @param activity
     * @return
     */
    @ApiOperation(
            value = "用户创建活动",
            notes = "用户创建活动"
    )
    @ApiImplicitParam(name = "activity", value = "用户活动", required = true, dataType = "Activity")
    @PostMapping("createActivity")
    public ResponseResult insertActivity(@RequestBody Activity activity) throws ParseException {
        //生成下单时间
        activity.setStartTime(new Date(System.currentTimeMillis()));
        int res = activityService.insertActivity(activity);
        return ResponseResult.result(res != -1 ? 0 : 1, res != -1 ? "success" : "fail", res);
    }

    /**
     * 删除活动
     *
     * @param activityId
     * @return
     */
    @RequestMapping("deleteActivity")
    public ResponseResult deleteActivity(int activityId) {
        boolean result = activityService.deleteActivity(activityId);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 更改活动状态
     *
     * @param activityId
     * @return
     */
    @RequestMapping("switchActivityStatus")
    public ResponseResult switchActivityStatus(int activityId, int status) {
        boolean result = activityService.switchActivityStatus(activityId, status);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 获取店铺所有创建的活动列表
     *
     * @param shopId
     * @return
     */
    @GetMapping("getactivitylist")
    public ResponseResult getActivitylist(int shopId) {

        return ResponseResult.result(0, "success", activityService.getActivitylist(shopId));
    }

    /**
     * 根据活动id获取活动详情
     *
     * @param activityId
     * @return
     */
    @GetMapping("getactivityinfo")
    public ResponseResult getActivityinfo(int activityId) {

        return ResponseResult.result(0, "success", activityService.getActivityById(activityId));

    }

    /**
     * 根据活动id获取活动详情
     *
     * @param activityId
     * @return
     */
    @GetMapping("getactivityinfo2")
    public ResponseResult getActivityinfo2(int activityId) {

        return ResponseResult.result(0, "success", activityService.getActivityById2(activityId));

    }

    /**
     * 新增activityrecord
     *
     * @param activityId
     * @param openId
     * @return
     */
    @PostMapping("insertActivityrecord")
    public ResponseResult insertActivityrecord(int activityId, String openId,  String currentPrice) {
        ActivityRecord activityRecord = new ActivityRecord();
        activityRecord.setActivityId(activityId);
        activityRecord.setOpenId(openId);
        activityRecord.setRecordStatus(0);
        activityRecord.setCurrentPrice(currentPrice);


        int res = activityService.insertActivityRecord(activityRecord);
        return ResponseResult.result(res > 0 ? 0 : 1, res > 0 ? "success" : "fail", res);

    }

    /**
     * 查询一个activityId所有点赞的人
     *
     * @param recordId
     * @return
     */
    @GetMapping("selecSupportRecordList")
    public ResponseResult selecSupportRecordList(int recordId) {
        return ResponseResult.result(0, "success", activityService.selecSupportRecordList(recordId));

    }

    /**
     * 根据activityrecord查询activityrecord详情
     *
     * @param activityRecordId
     * @return
     */
    @GetMapping("getActivityrecordById")
    public ResponseResult getActivityrecordById(int activityRecordId) {

        return ResponseResult.result(0, "success", activityService.getActivityrecordById(activityRecordId));

    }



    /**
     * 根据recordId更改record状态
     *
     * @param recordId
     * @param recordStatus 0：进行中 1：可购买 2：已下单，未支付 3：已支付
     * @return
     */
    @RequestMapping("changeRecordstatus")
    public ResponseResult changeRecordstatus(int recordId, int recordStatus) {
        boolean result = activityService.changeRecordstatus(recordStatus, recordId);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }


    /**
     * 新增supportrecord
     *
     * @param recordId
     * @param openId
     * @param nickName
     * @param avatarUrl
     * @param gender
     * @return
     */
    @PostMapping("insertSupportrecord")
    public ResponseResult insertSupportrecord(int activityId, String operateType, String operateContent, int recordId, String openId, String nickName, String avatarUrl, String gender) {
        int result = activityService.insertSupportrecord(activityId, operateType, operateContent, recordId, openId, nickName, avatarUrl, gender);
        return ResponseResult.result(result, result == 0 ? "success" : "fail", "成功");


    }

    /**
     * 更新 activityrecord supportCount+1
     *
     * @param recordId
     * @return
     */
    @PostMapping("addsupportCount")
    public ResponseResult addsupportCount(int recordId) {
        boolean result = activityService.addsupportCount(recordId);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 查询一个openId有没有参加过一个活动
     *
     * @param openId
     * @param activityId
     * @return
     */
    @GetMapping("selectActivityRecordByOpenId_ActivityId")
    public ResponseResult selectActivityRecordByOpenId_ActivityId(String openId, int activityId) {
        return ResponseResult.result(0, "success", activityService.selectActivityRecordByOpenId_ActivityId(openId, activityId));

    }

    /**
     * 更改/插入活动recordWxcode
     *
     * @param recordWxcode
     * @param recordId
     * @return
     */
    @RequestMapping("updateRecordWxcode")
    public ResponseResult updateRecordWxcode(int recordId, String recordWxcode) {
        boolean result = activityService.updateRecordWxcode(recordId, recordWxcode);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 查询活动record根据排名
     *
     * @param activityId
     * @return
     */
    @GetMapping("selectActivityRecordByOrder")
    public ResponseResult selectActivityRecordByOrder(int activityId, Integer offset, Integer size) {

        return ResponseResult.result(0, "success", activityService.selectActivityRecordByOrder(activityId, offset, size));

    }

    /**
     * 查询一个openId参加过所有活动
     *
     * @param openId
     * @return
     */
    @GetMapping("selectActivityRecordByOpenId")
    public ResponseResult selectActivityRecordByOpenId_ActivityId(String openId, Integer offset, Integer size) {
        return ResponseResult.result(0, "success", activityService.selectActivityRecordByOpenId(openId, offset, size));

    }


    /**
     * 获客粉丝的插入
     *
     * @param fansPhone
     * @return
     */

    @PostMapping("insertFans_phone")
    public ResponseResult insertFans_phone(int activityId, int activityRecordId, int shopId, String openId,String nickName,String avatarUrl,String gender,String phoneNum) {

        FansPhone fansPhone  = new FansPhone();
        fansPhone.setActivityId(activityId);
        fansPhone.setActivityRecordId(activityRecordId);
        fansPhone.setShopId(shopId);
        fansPhone.setOpenId(openId);
        fansPhone.setNickName(nickName);
        fansPhone.setAvatarUrl(avatarUrl);
        fansPhone.setGender(gender);
        fansPhone.setPhoneNum(phoneNum);
        int res = activityService.insertFans_phone(fansPhone);

        return ResponseResult.result(res > 0 ? 0 : 1, res > 0 ? "success" : "fail", res);

    }


    /**
     * 查询一个shopId所拥有的获客粉丝
     *
     * @param shopId
     * @return
     */
    @ApiOperation(
            value = "根据shopId获取所拥有的获客粉丝",
            httpMethod = "GET",
            notes = "根据shopId获取所拥有的获客粉丝")
    @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int")
    @GetMapping("selectFansPhoneByshopId")
    public ResponseResult selectFansPhoneByshopId(int shopId, Integer offset, Integer size) {
        return ResponseResult.result(0, "success", activityService.selectFansPhoneByshopId(shopId, offset, size));

    }


}
