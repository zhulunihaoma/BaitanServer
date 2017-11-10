package xll.baitaner.service.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 描述：订单实体类  新增收货地址数据字段
 * 创建者：xie
 * 日期：2017/10/14
 **/

@Getter
@Setter
public class Order {

    /**
     * 订单编号 后台随机生成唯一标识
     */
    private String orderId;

    /**
     * 用户Id
     */
    private String clientId;

    /**
     * 收货地址Id
     */
    private int receiverAddressId;

    /**
     * 店铺Id
     */
    private int shopId;

    /**
     * 下单时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    /**
     * 用户要求送达时间
     */
    private String arriveDate;

    /**
     * 订单备注
     */
    private String remarks;

    /**
     * 订单总金额
     */
    private float totalMoney;

    /**
     * 订单状态
     * 0：待支付
     * 1：待送达
     * 2：已完成
     */
    private int state;

    /**
     * 订单中商品详情
     */
    private List<OrderCommodity> OrderCoList;

    //新增订单收货地址详情

    /**
     * 收货人姓名
     */
    private String name;

    /**
     * 收货人性别 0:男士 1:女士
     */
    private int sex;

    /**
     * 收货地址
     */
    private String address;

    /**
     * 收货人电话
     */
    private String phone;

}
