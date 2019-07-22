package com.xll.baitaner.entity.VO;

import lombok.Data;

import java.math.BigDecimal;

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
    private BigDecimal balance;

    /**
     * 不用余额
     */
    private BigDecimal unBalance;
}
