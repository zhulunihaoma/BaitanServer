package com.xll.baitaner.service;

import com.xll.baitaner.entity.VO.WithdrawVO;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * 钱包相关接口
 *
 * @author denghuohuo 2019/6/29
 */
public interface WalletService {

    /**
     * 根据时间获取提现记录
     *
     * @param shopId
     * @param date
     * @param page
     * @return
     */
    List<WithdrawVO> queryWithdrawAmountList(Integer shopId, String date, Pageable page);

    /**
     * 查询所有提现记录
     *
     * @param shopId
     * @param page
     * @return
     */
    List<WithdrawVO> queryWithdrawAmountList(Integer shopId, Pageable page);

    /**
     * 获取店铺余额
     *
     * @param shopId
     * @return
     */
    BigDecimal getShopAmounts(Integer shopId);

    /**
     * 查询提现结果
     * @param openId
     */
    void queryWithdrawResultRecords(String openId);
}
