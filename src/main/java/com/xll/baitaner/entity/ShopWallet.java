package com.xll.baitaner.entity;

import lombok.Data;

import java.util.Date;

/**
 * 商户钱包
 * @author denghuohuo 2019/6/28
 */
@Data
public class ShopWallet {

    private Integer id;

    private Integer shopId;

    private Long orderId;

    private String openId;

    private String amount;

    private String operator;

    private Date createData;
}
