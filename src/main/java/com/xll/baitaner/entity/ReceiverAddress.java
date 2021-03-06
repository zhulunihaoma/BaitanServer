package com.xll.baitaner.entity;

import lombok.Data;

/**
 * 描述：收货地址实体类
 * 创建者：xie
 * 日期：2017/10/11
 **/

@Data
public class ReceiverAddress {

    private int id;

    /**
     * 用户ID
     */
    private String openId;

    /**
     * 联系人姓名
     */
    private String name;

    /**
     * 联系人性别 0:男士 1:女士
     */
    private int sex;

    /**
     * 送餐地址
     */
    private String address;

    /**
     * 联系人电话
     */
    private String phone;

    /**
     * 是否默认地址（1：true   0：false）
     */
    private Integer defaultNot;

    /**
     * 地址启用状态（1：true   0：false）
     */
    private Integer disable;
}
