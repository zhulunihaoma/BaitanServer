package com.xll.baitaner.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 描述：店铺实体类
 **/
@Data
@ApiModel(value = "店铺实体类", description = "店铺实体类，包括创建店铺需要的各类字段")
public class Shop {

    /**
     * 店铺ID
     */
    private int id;

    /**
     * 店铺拥有者openid
     */
    @ApiModelProperty(value = "店铺拥有者openid", name = "openId")
    private String openId;

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
     * 小程序内部支付开启状态  1:true   0:false
     */
    @ApiModelProperty(value = "小程序内部支付开启状态，默认false", name = "payPlatform")
    private boolean payPlatform;

    /**
     * 二维码支付开启状态  1:true   0:false
     */
    @ApiModelProperty(value = "二维码支付开启状态,默认true ", name = "payQrcode")
    private boolean payQrcode;

    /**
     * 店铺的支付二维码图片路径
     */
    @ApiModelProperty(value = "店铺的支付二维码图片路径", name = "payQrcodeUrl")
    private String payQrcodeUrl;

    @ApiModelProperty(value = "店铺的支付宝支付二维码图片路径", name = "aliPayQrcodeUrl")
    private String aliPayQrcodeUrl;

    /**
     * 店铺状态 1:营业  0;歇业
     */
    @ApiModelProperty(value = "店铺状态 (默认true，创建店铺时可不传值)", name = "shopState")
    private boolean shopState;

    /**
     * 店铺访问人次
     */
    @ApiModelProperty(value = "店铺访问人数，自动累加  可手动更新，也有", name = "number")
    private int number;

    /**
     * 店铺的小程序二维码
     */
    @ApiModelProperty(value = "店铺的小程序二维码", name = "shopWxacode")
    private String shopWxacode;

    /**
     * 店铺地址经度
     */
    @ApiModelProperty(value = "店铺地址经度", name = "longitude")
    private String longitude;

    /**
     * 店铺地址纬度
     */
    @ApiModelProperty(value = "店铺地址纬度", name = "latitude")
    private String latitude;
}
