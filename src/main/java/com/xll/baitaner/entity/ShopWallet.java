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

    /**
     * 付款渠道 0:钱方支付 1:微信支付
     */
    private Integer payChannel;

    private Date createDate;

    /**
     * 转账状态
     * SUCCESS:转账成功，FAILED:转账失败，PROCESSING:处理中
     */
    private String status;

    /**
     * 失败原因
     */
    private String reason;

    /**
     * 提现备注
     */
    private String descRemarks;

    /**
     * 发起转账的时间
     */
    private String transferTime;

    /**
     * 企业付款成功时间
     */
    private String paymentTime;
}
