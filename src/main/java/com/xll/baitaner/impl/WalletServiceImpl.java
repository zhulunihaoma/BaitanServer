package com.xll.baitaner.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.xll.baitaner.entity.ShopWallet;
import com.xll.baitaner.entity.VO.WithdrawResultVO;
import com.xll.baitaner.entity.VO.WithdrawVO;
import com.xll.baitaner.mapper.WalletMapper;
import com.xll.baitaner.service.WalletService;
import com.xll.baitaner.utils.Constant;
import com.xll.baitaner.utils.WXPayConfigImpl;
import org.apache.commons.collections.CollectionUtils;
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

    private WXPay wxPay;

    /**
     * 根据日期查询提现记录
     *
     * @param openId
     * @param date
     * @return
     */
    @Override
    public WithdrawResultVO queryWithdrawAmountList(String openId, String date, Integer offset, Integer size) {
        WithdrawResultVO resultVO = new WithdrawResultVO();
        List<WithdrawVO> result = new ArrayList<>();
        Page<ShopWallet> page =
                PageHelper.startPage(offset, size).doSelectPage(() -> walletMapper.getWalletWithAmountByDate(openId, date));
        List<ShopWallet> wallets = page.getResult();
        if (CollectionUtils.isEmpty(wallets)) {
            resultVO.setData(result);
            resultVO.setCount(0);
            return resultVO;
        }
        for (ShopWallet wallet : wallets) {
            WithdrawVO vo = new WithdrawVO();
            vo.setAmount(wallet.getAmount());
            vo.setDate(wallet.getCreateDate());
            vo.setReason(wallet.getReason());
            vo.setStatus(wallet.getStatus());
            vo.setRemarks(wallet.getDescRemarks());
            result.add(vo);
        }
        resultVO.setData(result);
        resultVO.setCount(page.getTotal());
        return resultVO;
    }

    /**
     * 查询所有体现记录
     *
     * @param openId
     * @return
     */
    @Override
    public WithdrawResultVO queryWithdrawAmountList(String openId, Integer offset, Integer size) {
        WithdrawResultVO resultVO = new WithdrawResultVO();
        List<WithdrawVO> result = new ArrayList<>();
        Page<ShopWallet> page =
                PageHelper.startPage(offset, size).doSelectPage(() -> walletMapper.getWalletAllAmount(openId));
        List<ShopWallet> wallets = page.getResult();
        if (CollectionUtils.isEmpty(wallets)) {
            resultVO.setData(result);
            resultVO.setCount(0);
            return resultVO;
        }
        for (ShopWallet wallet : wallets) {
            WithdrawVO vo = new WithdrawVO();
            vo.setAmount(wallet.getAmount());
            vo.setDate(wallet.getCreateDate());
            vo.setReason(wallet.getReason());
            vo.setStatus(wallet.getStatus());
            vo.setRemarks(wallet.getDescRemarks());
            result.add(vo);
        }
        resultVO.setData(result);
        resultVO.setCount(page.getTotal());
        return resultVO;
    }

    /**
     * 根据shopId查询余额
     *
     * @param openId
     * @return
     */
    @Override
    public BigDecimal getShopAmounts(String openId) {
        List<ShopWallet> shopWallets = walletMapper.selectAllByOpenId(openId);
        BigDecimal amount = new BigDecimal("0.00");
        if (CollectionUtils.isEmpty(shopWallets)) {
            return amount;
        }
        for (ShopWallet wallet : shopWallets) {
            if ("ADD".equals(wallet.getOperator())) {
                amount = amount.add(new BigDecimal(wallet.getAmount()));
            } else {
                if ("SUCCESS".equals(wallet.getStatus()) || "PROCESSING".equals(wallet.getStatus())) {
                    amount = amount.subtract(new BigDecimal(wallet.getAmount()));
                }
            }
        }
        return amount;
    }

    /**
     * 查询提现记录
     *
     * @param openId
     */
    @Override
    public void queryWithdrawResultRecords(String openId) {
        List<ShopWallet> shopWallets = walletMapper.queryWithdrawRecords(openId);
        queryWithdrawByOpenId(shopWallets);
    }

    private void queryWithdrawByOpenId(List<ShopWallet> shopWallets) {
        try {
            if (config == null) {
                config = WXPayConfigImpl.getInstance();
            }
            if (wxPay == null) {
                wxPay = new WXPay(config);
            }
            for (ShopWallet shopWallet : shopWallets) {
                //提现成功的不做查询
                if ("SUCCESS".equals(shopWallet.getStatus())) {
                    continue;
                }
                Map<String, String> inputMap = new HashMap<>();
                inputMap.put("nonce_str", WXPayUtil.generateNonceStr());
                inputMap.put("partner_trade_no", String.valueOf(shopWallet.getOrderId()));
                inputMap.put("mch_id", config.getMchID());
                inputMap.put("appid", config.getAppID());
                inputMap.put("sign", WXPayUtil.generateSignature(inputMap, config.getKey()));
                String respXml = wxPay.requestWithCert(Constant.TRANSFERINFO_URL, inputMap, config.getHttpConnectTimeoutMs(), config.getHttpReadTimeoutMs());
                Map<String, String> respMap = WXPayUtil.xmlToMap(respXml);
                if ("SUCCESS".equals(respMap.get("return_code"))) {
                    ShopWallet sw = new ShopWallet();
                    sw.setId(shopWallet.getId());
                    BigDecimal amount = new BigDecimal(shopWallet.getAmount()).multiply(new BigDecimal(100));
                    if (amount.toString().equals(respMap.get("payment_amount"))) {
                        sw.setStatus(respMap.get("status"));
                        if ("FAILED".equals(respMap.get("status"))) {
                            sw.setReason(respMap.get("reason"));
                        }
                        sw.setTransferTime(respMap.get("transfer_time"));
                        sw.setPaymentTime(respMap.get("payment_time"));
                        sw.setDescRemarks(respMap.get("desc"));
                        walletMapper.updateShopWalletWithdraw(sw);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
