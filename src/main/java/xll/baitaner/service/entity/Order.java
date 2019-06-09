package xll.baitaner.service.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 描述：订单实体类
 * 创建者：xie
 * 日期：2019
 **/

@Getter
@Setter
@ApiModel(value = "订单实体类", description = "订单信息实体类")
public class Order {

    /**
     * 订单编号 后台随机生成唯一标识
     */
    @ApiModelProperty(value="订单编号 后台随机生成唯一标识",name="orderId")
    private String orderId;

    /**
     * 用户Id
     */
    @ApiModelProperty(value="用户Id",name="openId")
    private String openId;

    /**
     * 支付方式
     * 0：在线支付
     * 1：二维码支付
     */
    @ApiModelProperty(value="支付方式  0：在线支付  1：二维码支付",name="payType")
    private int payType;

    /**
     * 店铺Id
     */
    @ApiModelProperty(value="店铺ID",name="shopId")
    private int shopId;

    /**
     * 店铺名称
     */
    @ApiModelProperty(value="店铺名称",name="shopName")
    private String shopName;

    /**
     * 店铺logo（选填）
     */
    @ApiModelProperty(value="店铺logo（选填）",name="shopLogoUrl")
    private String shopLogoUrl;

    /**
     * 下单时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value="下单时间",name="date")
    private Date date;

    /**
     * 订单备注
     */
    @ApiModelProperty(value="订单备注",name="remarks")
    private String remarks;

    /**
     * 订单总金额
     */
    @ApiModelProperty(value="订单总金额",name="totalMoney")
    private float totalMoney;

    /**
     * 订单运费
     */
    @ApiModelProperty(value="订单运费",name="postage")
    private float postage;

    /**
     * 订单状态
     * 0：待支付
     * 1：已接单
     * 2：待完成
     * 3：已完成
     */
    @ApiModelProperty(value="订单状态  0：待支付  1：待送达  2：已完成",name="state")
    private int state;

    /**
     * 订单中商品详情
     */
    @ApiModelProperty(value="订单中商品详情",name="OrderCoList")
    private List<OrderCommodity> OrderCoList;

    /**
     * 收货人姓名
     */
    @ApiModelProperty(value="收货人姓名",name="name")
    private String name;

    /**
     * 收货人性别 0:男士 1:女士
     */
    @ApiModelProperty(value="收货人性别 0:男士 1:女士",name="sex")
    private int sex;

    /**
     * 收货地址
     */
    @ApiModelProperty(value="收货地址",name="address")
    private String address;

    /**
     * 收货人电话
     */
    @ApiModelProperty(value="收货人电话",name="phone")
    private String phone;


    /**活动订单**/

    /**
     * 是否活动订单  默认false
     */
    @ApiModelProperty(value="是否活动订单  默认false",name="isActivity")
    private boolean isActivity;

    /**
     * 活动id  默认无
     */
    @ApiModelProperty(value="活动id  默认无",name="activityId")
    private int activityId;
}
