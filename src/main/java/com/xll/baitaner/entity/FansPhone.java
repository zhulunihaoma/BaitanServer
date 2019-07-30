package com.xll.baitaner.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhulu
 * @version 1.0
 * @description com.xll.baitaner.entity
 * @date 2019/7/28
 */
@Data
@ApiModel(value = "获取活动参加者信息实体类", description = "活动参加者信息实体类")
public class FansPhone {
    /**
     * 店铺ID
     */
    private int id;

    /**
     * 获取这个手机号的活动id
     */
    @ApiModelProperty(value = "获取这个手机号的活动id", name = "activityId")
    private int activityId;

    /**
     * 获取这个手机号的活动activityRecordId
     */
    @ApiModelProperty(value = "获取这个手机号的活动activityRecordId", name = "activityRecordId")
    private int activityRecordId;

    /**
     * 发起活动的店铺id
     */
    @ApiModelProperty(value = "发起活动的店铺id", name = "shopId")
    private int shopId;

    /**
     * 参加活动的openId
     */
    @ApiModelProperty(value = "参加者的openId", name = "openId")
    private String openId;

    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称", name = "nickName")
    private String nickName;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像", name = "avatarUrl")
    private String avatarUrl;

    /**
     * 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
     */
    @ApiModelProperty(value = "用户的性别，值为1时是男性，值为2时是女性，值为0时是未知", name = "gender")
    private String gender;

    /**
     * 用户的手机号
     */
    @ApiModelProperty(value = "用户的手机号", name = "phoneNum")
    private String phoneNum;

}
