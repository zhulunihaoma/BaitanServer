package xll.baitaner.service.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xll.baitaner.service.entity.Activity;
import xll.baitaner.service.entity.ActivityRecord;
import xll.baitaner.service.service.ActivityService;
import xll.baitaner.service.utils.DateUtils;
import xll.baitaner.service.utils.ResponseResult;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zhulu
 * @version 1.0
 * @description xll.baitaner.service.controller
 * @date 2019/1/13
 */
@Api(value = "活动模块controller",
        description = "活动模块")
@RestController
public class ActivityController {


    @Autowired
    private ActivityService activityService;


    /**
     * 新增活动数据操作
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

        return ResponseResult.result(res == -1 ? 0 : 1, res == -1 ? "success" : "fail", res);
    }

    /**
     * 删除活动
     * @param activityId
     * @return
     */
    @RequestMapping("deleteActivity")
    public ResponseResult deleteActivity(int activityId){
        boolean result = activityService.deleteActivity(activityId);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 更改活动状态
     * @param activityId
     * @return
     */
    @RequestMapping("switchActivityStatus")
    public ResponseResult switchActivityStatus(int activityId,int status){
        boolean result = activityService.switchActivityStatus(activityId, status);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 获取用户所有创建的活动列表
     * @param openId
     * @return
     */
    @GetMapping("getactivitylist")
    public ResponseResult getActivitylist(String openId){

        return ResponseResult.result(0, "success", activityService.getActivitylist(openId));
    }
    /**
     * 根据活动id获取活动详情
     * @param activityId
     * @return
     */
    @GetMapping("getactivityinfo")
    public ResponseResult getActivityinfo(int activityId){

        return ResponseResult.result(0, "success", activityService.getActivityById(activityId));

    }

    /**
     * 新增activityrecord
     * @param activityId
     * @param openId
     * @param nickName
     * @param avatarUrl
     * @param gender
     * @return
     */
    @PostMapping("insertActivityrecord")
    public ResponseResult insertActivityrecord(int activityId,String openId,String nickName,String avatarUrl,String gender){

        ActivityRecord activityRecord = new ActivityRecord();
        activityRecord.setActivityId(activityId);
        activityRecord.setOpenId(openId);
        activityRecord.setNickName(nickName);
        activityRecord.setAvatarUrl(avatarUrl);
        activityRecord.setGender(gender);
        int res = activityService.insertActivityRecord(activityRecord);

        return ResponseResult.result(res > 0 ? 0 : 1, res > 0 ? "success" : "fail", res);

    }
    /**
     * 根据activityrecord查询activityrecord详情
     * @param activityRecordId
     * @return
     */
    @GetMapping("getActivityrecordById")
    public  ResponseResult getActivityrecordById(int activityRecordId){

        return ResponseResult.result(0, "success", activityService.getActivityrecordById(activityRecordId));

    }

    /**
     * 新增supportrecord
     * @param recordId
     * @param openId
     * @param nickName
     * @param avatarUrl
     * @param gender
     * @return
     */
    @PostMapping("insertSupportrecord")
    public ResponseResult insertSupportrecord(int recordId,String openId,String nickName,String avatarUrl,String gender){
        boolean result = activityService.insertSupportrecord(recordId,openId,nickName,avatarUrl,gender);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);

    }

    /**
     *查询一个openId有没有参加过一个活动
     * @param openId
     * @param activityId
     * @return
     */
    @GetMapping("selectActivityRecordByOpenId_ActivityId")
    public ResponseResult selectActivityRecordByOpenId_ActivityId(String openId,int activityId) {
        return ResponseResult.result(0, "success" , activityService.selectActivityRecordByOpenId_ActivityId(openId,activityId));

        }
    }
