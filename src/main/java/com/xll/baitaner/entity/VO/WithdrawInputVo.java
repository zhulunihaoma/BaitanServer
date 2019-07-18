package com.xll.baitaner.entity.VO;

import lombok.Data;

/**
 * @author dengyy
 * @date 19-7-17
 */
@Data
public class WithdrawInputVo {

    /**
     * 商户订单号
     */
    private String partnerTradeNo;

    private String openId;

    /**
     * 提现金额
     */
    private String fee;

    /**
     * 提现备注
     */
    private String desc;


}
