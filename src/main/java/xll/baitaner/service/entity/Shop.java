package xll.baitaner.service.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


/**
 * 描述：店铺实体类
 **/
@Getter
@Setter
@ApiModel(value = "店铺实体类", description = "店铺实体类，包括创建店铺需要的各类字段以及店铺首页的轮播图路径")
public class Shop {

    /**
     * 店铺ID
     */
    private int id;

    /**
     * 店铺拥有者openid
     */
    @ApiModelProperty(value="店铺拥有者openid",name="openId")
    private String openId;

    /**
     * 店铺名称
     */
    @ApiModelProperty(value="店铺名称",name="shopName")
    private String shopName;

    /**
     * 店铺简介
     */
    @ApiModelProperty(value="店铺简介",name="shopIntroduction")
    private String shopIntroduction;

    /**
     * 店主姓名
     */
    @ApiModelProperty(value="店主姓名",name="ownerName")
    private String ownerName;

    /**
     * 店主微信号
     */
    @ApiModelProperty(value="店主微信号",name="wxNumber")
    private String wxNumber;

    /**
     * 联系电话
     */
    @ApiModelProperty(value="联系电话",name="contactNumber")
    private String contactNumber;

    /**
     * 店铺地址
     */
    @ApiModelProperty(value="店铺地址",name="shopAddress")
    private String shopAddress;

    /**
     * 店铺logo（选填）
     */
    @ApiModelProperty(value="店铺logo（选填）",name="shopLogoUrl")
    private String shopLogoUrl;

    /**
     * 店铺轮播图1地址
     */
    @ApiModelProperty(value="店铺轮播图1地址",name="shopPicUrl1")
    private String shopPicUrl1;

    /**
     * 店铺轮播图2地址
     */
    @ApiModelProperty(value="店铺轮播图2地址",name="shopPicUrl2")
    private String shopPicUrl2;

    /**
     * 店铺轮播图3地址
     */
    @ApiModelProperty(value="店铺轮播图3地址",name="shopPicUrl3")
    private String shopPicUrl3;

    /**
     * 小程序内部支付开启状态  1:true   0:false
     */
    @ApiModelProperty(value="小程序内部支付开启状态，默认false",name="payPlatform")
    private boolean payPlatform;

    /**
     * 二维码支付开启状态  1:true   0:false
     */
    @ApiModelProperty(value="二维码支付开启状态,默认true ",name="payQrcode")
    private boolean payQrcode;

    /**
     * 店铺的支付二维码图片路径
     */
    @ApiModelProperty(value="店铺的支付二维码图片路径",name="payQrcodeUrl")
    private String payQrcodeUrl;

    /**
     *  店铺状态 1:营业  0;歇业
     */
    @ApiModelProperty(value="店铺名称",name="shopState")
    private boolean shopState;
}
