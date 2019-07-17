package com.xll.baitaner.service;

import com.xll.baitaner.entity.VO.WithdrawResultVO;
import com.xll.baitaner.entity.VO.WithdrawVO;

import java.math.BigDecimal;

/**
 * 钱包相关接口
 *
 * @author denghuohuo 2019/6/29
 */
public interface WalletService {

    /**
     * 根据时间获取提现记录
     *
     * @param openId
     * @param date
     * @return
     */
    WithdrawResultVO queryWithdrawAmountList(String openId, String date, Integer offset, Integer size);

    /**
     * 查询所有提现记录
     *
     * @param openId
     * @return
     */
    WithdrawResultVO queryWithdrawAmountList(String openId, Integer offset, Integer size);

    /**
     * 获取店铺余额
     *
     * @param openId
     * @return
     */
    BigDecimal getShopAmounts(String openId);

    /**
     * 查询提现结果
     *
     * @param openId
     */
    void queryWithdrawResultRecords(String openId);

    /**
     * 提现
     *
     * @param openId
     * @param fee
     * @return
     */
    WithdrawVO withdrawFromWx(String openId, String fee, String desc);
}
