package com.xll.baitaner.impl;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.xll.baitaner.entity.ShopWallet;
import com.xll.baitaner.entity.VO.WithdrawVO;
import com.xll.baitaner.mapper.WalletMapper;
import com.xll.baitaner.service.WalletService;
import com.xll.baitaner.utils.WXPayConfigImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 钱包相关服务
 *
 * @author denghuohuo 2019/6/29
 */
@Service
public class WalletServiceImpl implements WalletService {

    @Resource
    WalletMapper walletMapper;

    private WXPayConfigImpl config; //微信支付配置文件

    /**
     * 根据日期查询提现记录
     *
     * @param shopId
     * @param date
     * @param page
     * @return
     */
    @Override
    public List<WithdrawVO> queryWithdrawAmountList(Integer shopId, String date, Pageable page) {
        List<WithdrawVO> result = new ArrayList<>();
        List<ShopWallet> wallets = walletMapper.getWalletWithAmountByDate(shopId, date, page);
        if (CollectionUtils.isEmpty(wallets)) {
            return result;
        }
        for (ShopWallet wallet : wallets) {
            WithdrawVO vo = new WithdrawVO();
            vo.setAmount(wallet.getAmount());
            vo.setDate(wallet.getCreateDate());
            result.add(vo);
        }
        return result;
    }

    /**
     * 查询所有体现记录
     *
     * @param shopId
     * @param page
     * @return
     */
    @Override
    public List<WithdrawVO> queryWithdrawAmountList(Integer shopId, Pageable page) {
        List<WithdrawVO> result = new ArrayList<>();
        List<ShopWallet> wallets = walletMapper.getWalletAllAmount(shopId, page);
        if (CollectionUtils.isEmpty(wallets)) {
            return result;
        }
        for (ShopWallet wallet : wallets) {
            WithdrawVO vo = new WithdrawVO();
            vo.setAmount(wallet.getAmount());
            vo.setDate(wallet.getCreateDate());
            result.add(vo);
        }
        return result;
    }

    /**
     * 根据shopId查询余额
     *
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

    @Override
    public void queryWithdrawResultRecords(String openId) {
        List<ShopWallet> shopWallets = walletMapper.queryWithdrawRecords(openId);
        String nonceStr = WXPayUtil.generateNonceStr();//随机串
        try {
            if (config == null) {
                config = WXPayConfigImpl.getInstance();
            }
            Map<String, String> inputMap = new HashMap<>();
            inputMap.put("nonce_str", WXPayUtil.generateNonceStr());
            inputMap.put("partner_trade_no", openId);
            inputMap.put("mch_id", config.getMchID());
            inputMap.put("appid", config.getAppID());
            WXPayUtil.generateSignature(inputMap, config.getKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
