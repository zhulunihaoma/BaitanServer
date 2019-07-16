package com.xll.baitaner.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhulu
 * @version 1.0
 * @description xll.baitaner.service.entity
 * @date 2019/5/19
 */
@Data
public class ActivityShopCommodity {
    /**
     * 店铺信息
     */

    /**
     * 店铺ID
     */
    private int shopId;

    /**
     * 店铺名称
     */
    @ApiModelProperty(value = "店铺名称", name = "shopName")
    private String shopName;

    /**
     * 店铺简介
     */
    @ApiModelProperty(value = "店铺简介", name = "shopIntroduction")
    private String shopIntroduction;

    /**
     * 店主姓名
     */
    @ApiModelProperty(value = "店主姓名", name = "ownerName")
    private String ownerName;

    /**
     * 店主微信号
     */
    @ApiModelProperty(value = "店主微信号", name = "wxNumber")
    private String wxNumber;

    /**
     * 联系电话
     */
    @ApiModelProperty(value = "联系电话", name = "contactNumber")
    private String contactNumber;

    /**
     * 店铺地址
     */
    @ApiModelProperty(value = "店铺地址", name = "shopAddress")
    private String shopAddress;

    /**
     * 店铺logo（选填）
     */
    @ApiModelProperty(value = "店铺logo（选填）", name = "shopLogoUrl")
    private String shopLogoUrl;

    /**
     * 活动详情
     */
    /**
     * 商品活动价格
     */
    @ApiModelProperty(value = "商品活动价格", name = "activityPrice")
    private String activityPrice;

    /**
     * 集赞方式(rank,maxnum)/砍价方式(rank,maxnum)
     */
    @ApiModelProperty(value = "集赞方式/砍价方式", name = "operateType")
    private String operateType;

    /**
     * 集赞方式对应的内容如 前多少名可以领取
     */
    @ApiModelProperty(value = "集赞/砍价方式对应的内容", name = "operateContent")
    private String operateContent;

    /**
     * 结束时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")

    private Date endTime;

    /**
     * 结束时间string
     */

    private String endTimeString;


    /**
     * 活动状态1：启用 0：终止
     */

    private int status;

    /**
     * 活动状态zan：赞 kan：砍价
     */

    private String activityType;

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
     * 商品信息
     */

    /**
     * 商品名
     */
    @ApiModelProperty(value = "商品名", name = "name")
    private String commodityName;

    /**
     * 商品描述
     */
    @ApiModelProperty(value = "商品描述", name = "introduction")
    private String introduction;

    /**
     * 商品图片
     */
    @ApiModelProperty(value = "商品图片", name = "pictUrl")
    private String pictUrl;

    /**
     * 商品单价
     */
    @ApiModelProperty(value = "商品单价", name = "price")
    private String price;

}
