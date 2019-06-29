package com.xll.baitaner.impl;

import com.xll.baitaner.entity.VO.WithdrawVO;
import com.xll.baitaner.mapper.WalletMapper;
import com.xll.baitaner.service.WalletService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 钱包相关服务
 *
 * @author denghuohuo 2019/6/29
 */
@Service
public class WalletServiceImpl implements WalletService {

    @Resource
    WalletMapper walletMapper;

    /**
     * 根据日期查询提现记录
     *
     * @param shopId
     * @param date
     * @param page
     * @return
     */
    @Override
    public WithdrawVO getWithdrawAmountList(Integer shopId, String date, Pageable page) {
        WithdrawVO result = new WithdrawVO();
        List<String> amounts = walletMapper.getWalletWithAmountByDate(shopId, date, page);
        result.setAmountList(amounts);
        return result;
    }
}
