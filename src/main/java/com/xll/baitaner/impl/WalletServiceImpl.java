package com.xll.baitaner.impl;

import com.xll.baitaner.entity.ShopWallet;
import com.xll.baitaner.entity.VO.WithdrawVO;
import com.xll.baitaner.mapper.WalletMapper;
import com.xll.baitaner.service.WalletService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
    public WithdrawVO queryWithdrawAmountList(Integer shopId, String date, Pageable page) {
        WithdrawVO result = new WithdrawVO();
        List<String> amounts = walletMapper.getWalletWithAmountByDate(shopId, date, page);
        result.setAmounts(amounts);
        return result;
    }

    /**
     * 根据shopId查询余额
     * @param shopId
     * @return
     */
    @Override
    public BigDecimal getShopAmounts(Integer shopId) {
        List<ShopWallet> shopWallets = walletMapper.selectAllByShopId(shopId);
        BigDecimal amount = new BigDecimal("0.00");
        if (CollectionUtils.isEmpty(shopWallets)) {
            return amount;
        }
        for (ShopWallet wallet : shopWallets) {
            if ("ADD".equals(wallet.getOperator())) {
                amount = amount.add(new BigDecimal(wallet.getAmount()));
            } else {
                amount = amount.subtract(new BigDecimal(wallet.getAmount()));
            }
        }
        return amount;
    }
}
