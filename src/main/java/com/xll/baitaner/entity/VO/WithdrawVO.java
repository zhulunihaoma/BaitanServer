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

    private String status;

    private String remarks;

    private String reason;
}
