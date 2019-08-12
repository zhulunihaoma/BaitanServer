package com.xll.baitaner.entity.VO;

import lombok.Data;

/**
 * 账户余额
 *
 * @author dengyy
 * @date 19-7-22
 */
@Data
public class AccountBalanceVO {

    /**
     * 可用余额
     */
    private String balance;

    /**
     * 不用余额
     */
    private String unBalance;

    /**
     * 总手续费
     */
    private String allFee;
}
