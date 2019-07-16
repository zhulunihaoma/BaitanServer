package com.xll.baitaner.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 订单表实体类
 *
 * @author denghuohuo 2019/6/28
 */
@Data
public class ShopOrder {

    private Integer id;

    private Long orderId;

    private String openId;

    private Integer shopId;

    private Integer addressId;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    private Integer payType;

    private String totalMoney;

    private String postage;

    private Integer state;

    private Integer activityNot;

    private String activityId;

    private Integer delFlag;

    private String remarks;

}
