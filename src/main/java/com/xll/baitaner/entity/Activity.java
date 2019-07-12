package com.xll.baitaner.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author zhulu
 * @version 1.0
 * @description xll.baitaner.service.entity
 * @date 2019/1/13
 */
@Data
public class Activity {
    private int id;

    /**
     * 用户ID
     */
    private String openId;

    /**
     * 店铺ID
     */
    private int shopId;

    /**
     * 活动名称
     */
    private String activityName;


    /**
     * 活动描述
     */
    private String activityIntro;

    /**
     * 开始时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")

    private Date startTime;

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
     * 商品原价
     */
    @ApiModelProperty(value = "商品原价", name = "originPrice")
    private String originPrice;

    /**
     * 商品活动价格
     */
    @ApiModelProperty(value = "商品活动价格", name = "activityPrice")
    private String activityPrice;


    /**
     * 商品数量
     */
    @ApiModelProperty(value = "商品数量", name = "stock")
    private int stock;

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
     * 参与条件（none,wechat,codeword）
     */
    @ApiModelProperty(value = "参与条件（无none,微信号wechat,口令codeword）", name = "requirement")
    private String requirement;


    /**
     * 产品方法方式（邮寄post）
     */
    @ApiModelProperty(value = "产品方法方式（邮寄post）", name = "sendType")
    private String sendType;


    /**
     * 商品信息id
     */

    @ApiModelProperty(value = "绑定的商品Id", name = "CommodityId")
    private int commodityId;

    /**
     * 商品名
     */
    @ApiModelProperty(value = "商品名", name = "commodityName")
    private String commodityName;

    /**
     * 商品描述
     */
    @ApiModelProperty(value = "商品描述", name = "introduction")
    private String introduction;

    /**
     * 商品单价
     */
    @ApiModelProperty(value = "商品单价", name = "price")
    private String price;

    /**
     * 商品运费
     */
    @ApiModelProperty(value = "商品运费", name = "postage")
    private String postage;

}
