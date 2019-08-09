package com.xll.baitaner.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author zhulu
 * @version 1.0
 * @description xll.baitaner.service.entity
 * @date 2019/3/1
 */
@Data
public class ActivityRecord {
    private int id;

    /**
     * activityID
     */
    private int activityId;

    /**
     * 用户ID
     */
    private String openId;


    /**
     * 记录状态 recordStatus 0：进行中 1：可购买 2：已下单，未支付 3：已支付
     */

    @ApiModelProperty(value = "记录状态", name = "recordStatus")
    private int recordStatus;

    /**
     * 砍价活动有的当前砍到的价格
     */
    @ApiModelProperty(value = "当前砍到的价格", name = "currentPrice")
    private String currentPrice;


    /**
     * 活动record二维码
     */
    @ApiModelProperty(value = "活动record二维码", name = "recordWxcode")
    private String recordWxcode;

    /**
     * 活动record点赞/砍价人数
     */
    @ApiModelProperty(value = "活动record点赞/砍价人数", name = "supportCount")
    private int supportCount;



}
