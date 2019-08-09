package com.xll.baitaner.entity.VO;

import lombok.Data;

import java.util.Date;

/**
 * 提现
 *
 * @author denghuohuo 2019/6/29
 */
@Data
public class WithdrawVO {

    private Date date;

    private String amount;

    /**
     * 提现状态
     */
    private String status;

    /**
     * 提现备注
     */
    private String remarks;

    /**
     * 失败原因
     */
    private String reason;

    /**
     * "ADD":支付，"DEC":提现
     */
    private String operator;

    /**
     * 手续费
     */
    private String fee;
}
