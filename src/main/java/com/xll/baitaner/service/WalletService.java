package com.xll.baitaner.service;

import com.xll.baitaner.entity.VO.WithdrawVO;
import org.springframework.data.domain.Pageable;

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
    WithdrawVO getWithdrawAmountList(Integer shopId, String date, Pageable page);
}
