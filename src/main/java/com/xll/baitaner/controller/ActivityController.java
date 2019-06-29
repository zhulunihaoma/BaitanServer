package com.xll.baitaner.controller;

import com.xll.baitaner.entity.Activity;
import com.xll.baitaner.entity.ActivityRecord;
import com.xll.baitaner.service.ActivityService;
import com.xll.baitaner.utils.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * 获取用户所有创建的活动列表
     *
     * @param openId
     * @return
     */
    @GetMapping("getactivitylist")
    public ResponseResult getActivitylist(String openId) {

        return ResponseResult.result(0, "success", activityService.getActivitylist(openId));
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
     * @param nickName
     * @param avatarUrl
     * @param gender
     * @return
     */
    @PostMapping("insertActivityrecord")
    public ResponseResult insertActivityrecord(int activityId, String openId, String nickName, String avatarUrl, String gender, String shopName,
                                               String shopLogoUrl, String goodname, float activityPrice, Date endTime) {
        ActivityRecord activityRecord = new ActivityRecord();
        activityRecord.setActivityId(activityId);
        activityRecord.setOpenId(openId);
        activityRecord.setNickName(nickName);
        activityRecord.setAvatarUrl(avatarUrl);
        activityRecord.setGender(gender);
        activityRecord.setShopName(shopName);
        activityRecord.setShopLogoUrl(shopLogoUrl);
        activityRecord.setGoodname(goodname);
        activityRecord.setActivityPrice(activityPrice);
        activityRecord.setEndTime(endTime);

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
        return ResponseResult.result(result, result == 0 ? "success" : "fail", "价格已砍到最低价格");


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
     * 查询活动record根据排名
     *
     * @param activityId
     * @return
     */
    @GetMapping("selectActivityRecordByOrder")
    public ResponseResult selectActivityRecordByOrder(int activityId, Pageable pageable) {

        return ResponseResult.result(0, "success", activityService.selectActivityRecordByOrder(activityId, pageable));

    }

    /**
     * 查询一个openId参加过所有活动
     *
     * @param openId
     * @return
     */
    @GetMapping("selectActivityRecordByOpenId")
    public ResponseResult selectActivityRecordByOpenId_ActivityId(String openId, Pageable pageable) {
        return ResponseResult.result(0, "success", activityService.selectActivityRecordByOpenId(openId, pageable));

    }
}