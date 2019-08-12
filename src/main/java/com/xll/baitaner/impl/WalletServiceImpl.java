package com.xll.baitaner.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.xll.baitaner.entity.ShopWallet;
import com.xll.baitaner.entity.VO.AccountBalanceVO;
import com.xll.baitaner.entity.VO.WithdrawAllResultVO;
import com.xll.baitaner.entity.VO.WithdrawDateVO;
import com.xll.baitaner.entity.VO.WithdrawInputVo;
import com.xll.baitaner.entity.VO.WithdrawResultVO;
import com.xll.baitaner.entity.VO.WithdrawVO;
import com.xll.baitaner.mapper.WalletMapper;
import com.xll.baitaner.service.TemplateService;
import com.xll.baitaner.service.WalletService;
import com.xll.baitaner.utils.Constant;
import com.xll.baitaner.utils.DateUtils;
import com.xll.baitaner.utils.LogUtils;
import com.xll.baitaner.utils.MoneyUtil;
import com.xll.baitaner.utils.WXPayConfigImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
@Slf4j
@Service
public class WalletServiceImpl implements WalletService {

    @Resource
    WalletMapper walletMapper;

    @Resource
    private TemplateService templateService;

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
        try {
            //再次查询状态，更新数据
            queryWithdrawByOrder(wallets);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (ShopWallet wallet : wallets) {
            WithdrawVO vo = new WithdrawVO();
            vo.setAmount(wallet.getAmount());
            vo.setFee(wallet.getFee());
            vo.setDate(wallet.getCreateDate());
            vo.setReason(wallet.getReason());
            vo.setStatus(wallet.getStatus());
            vo.setRemarks(wallet.getDescRemarks());
            vo.setOperator("DEC");
            result.add(vo);
        }
        resultVO.setData(result);
        resultVO.setCount(page.getTotal());
        return resultVO;
    }

    /**
     * 查询所有提现记录
     *
     * @param openId
     * @return
     */
    @Override
    public WithdrawAllResultVO queryWithdrawAmountList(String openId, Integer offset, Integer size) {
        WithdrawAllResultVO resultVO = new WithdrawAllResultVO();
        List<WithdrawDateVO> result = new ArrayList<>();
        Page<ShopWallet> page =
                PageHelper.startPage(offset, size).doSelectPage(() -> walletMapper.getWalletAllAmount(openId));
        List<ShopWallet> wallets = page.getResult();
        if (CollectionUtils.isEmpty(wallets)) {
            resultVO.setData(result);
            resultVO.setCount(0);
            return resultVO;
        }
        try {
            //再次查询状态，更新数据
            queryWithdrawByOrder(wallets);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //放入map,按照日期归类
        Map<String, List<WithdrawVO>> map = new HashMap<>();
        for (ShopWallet wallet : wallets) {
            WithdrawVO vo = new WithdrawVO();
            vo.setAmount(wallet.getAmount());
            vo.setDate(wallet.getCreateDate());
            vo.setReason(wallet.getReason());
            vo.setStatus(wallet.getStatus());
            vo.setOperator("DEC");
            vo.setFee(wallet.getFee());
            map.computeIfAbsent(DateUtils.dateToString(wallet.getCreateDate()), k -> new ArrayList<>());
            map.get(DateUtils.dateToString(wallet.getCreateDate())).add(vo);
        }
        //整合成大日期下，按小日期时间
        for (Map.Entry<String, List<WithdrawVO>> entry : map.entrySet()) {
            WithdrawDateVO dateVO = new WithdrawDateVO();
            dateVO.setDate(entry.getKey());
            dateVO.setList(entry.getValue());
            result.add(dateVO);
        }
        resultVO.setData(result);
        resultVO.setCount(page.getTotal());
        return resultVO;
    }

    /**
     * 根据openId查询余额
     *
     * @param openId
     * @return
     */
    @Override
    public AccountBalanceVO getShopAmounts(String openId) {
        AccountBalanceVO balance = new AccountBalanceVO();
        List<ShopWallet> shopWallets = walletMapper.selectAllByOpenId(openId);
        if (CollectionUtils.isEmpty(shopWallets)) {
            return balance;
        }
        balance = calculateAmounts(shopWallets);
        //FIXME 暂时只有钱方未到账金额
        balance.setUnBalance(calculateWithoutAccountAmounts(shopWallets).toPlainString());
        return balance;
    }

    /**
     * 计算到账总金额
     *
     * @param shopWallets
     * @return
     */
    private AccountBalanceVO calculateAmounts(List<ShopWallet> shopWallets) {
        BigDecimal qfAmount = new BigDecimal("0.00");
        BigDecimal wxAmount = new BigDecimal("0.00");
        BigDecimal withdrawamount = new BigDecimal("0.00");
        BigDecimal fee = new BigDecimal("0.00");
        if (CollectionUtils.isEmpty(shopWallets)) {
            return new AccountBalanceVO();
        }
        AccountBalanceVO account = new AccountBalanceVO();
        for (ShopWallet wallet : shopWallets) {
            //钱方到账
            if (("ADD".equals(wallet.getOperator())) && (wallet.getPayChannel() == 0) &&
                    (wallet.getCreateDate().compareTo(DateUtils.dayBeforeHoursFromNow(48)) < 0)) {
                qfAmount = qfAmount.add(new BigDecimal(wallet.getAmount()));
            }
            //微信到账
            if (("ADD".equals(wallet.getOperator())) && (wallet.getPayChannel() == 1) &&
                    (wallet.getCreateDate().compareTo(DateUtils.dayBeforeWorkDaysFromNow(7)) < 0)) {
                wxAmount = wxAmount.add(new BigDecimal(wallet.getAmount()));
            }
            //提现总金额
            if ("DEC".equals(wallet.getOperator()) &&
                    ("SUCCESS".equals(wallet.getStatus()) || "PROCESSING".equals(wallet.getStatus()))) {
                withdrawamount = withdrawamount.add(new BigDecimal(wallet.getAmount()));
                //手续费
                fee = fee.add(new BigDecimal(wallet.getFee()));
            }
        }
        //可用余额
        account.setBalance((qfAmount.add(wxAmount).subtract(withdrawamount).subtract(fee)).toPlainString());
        account.setAllFee(fee.toPlainString());
        return account;
    }

    /**
     * 计算未到账余额
     *
     * @param shopWallets
     * @return
     */
    private BigDecimal calculateWithoutAccountAmounts(List<ShopWallet> shopWallets) {
        BigDecimal amount = new BigDecimal("0.00");
        if (CollectionUtils.isEmpty(shopWallets)) {
            return amount;
        }
        for (ShopWallet wallet : shopWallets) {
            //钱方未到账
            if (("ADD".equals(wallet.getOperator())) && (wallet.getPayChannel() == 0) &&
                    (wallet.getCreateDate().compareTo(DateUtils.dayBeforeHoursFromNow(48)) >= 0)) {
                amount = amount.add(new BigDecimal(wallet.getAmount()));
            }
            //微信未到账
            if (("ADD".equals(wallet.getOperator())) && (wallet.getPayChannel() == 1) &&
                    (wallet.getCreateDate().compareTo(DateUtils.dayBeforeWorkDaysFromNow(7)) >= 0)) {
                amount = amount.add(new BigDecimal(wallet.getAmount()));
            }
        }
        return amount;
    }

    /**
     * 提现
     *
     * @param input
     * @return
     */
    @Override
    public WithdrawVO withdrawTransfer(WithdrawInputVo input) {
        WithdrawVO withVo = new WithdrawVO();
        //格式化金额数据
        input.setFee(MoneyUtil.formatMoney(input.getFee()));
        //因为有手续费，所以判断一下是否可提现
//        if (!checkAmountByWithdraw(input.getOpenId(), input.getFee())) {
//            withVo.setReason("提现金额不足");
//            return withVo;
//        }
        try {
            if (config == null) {
                config = WXPayConfigImpl.getInstance();
            }
            if (wxPay == null) {
                wxPay = new WXPay(config);
            }
            Map<String, String> data = new HashMap<>();
            data.put("mch_appid", config.getAppID());
            data.put("mchid", config.getMchID());
            data.put("nonce_str", config.getAppID());
            data.put("partner_trade_no", input.getPartnerTradeNo());
            data.put("openid", input.getOpenId());
            data.put("check_name", "NO_CHECK");
            //除去手续费
            String withdraw = new BigDecimal(input.getFee()).subtract(new BigDecimal(calculateFee(input.getFee()))).toPlainString();
            //换算成分
            data.put("amount", MoneyUtil.changeY2F(withdraw));
            //付款备注
            String desc = input.getDesc();
            if (StringUtils.isBlank(desc)) {
                desc = "提现";
            }
            data.put("desc", desc);
            data.put("spbill_create_ip", "111.231.114.159");
            //MD5签名
            String sign = WXPayUtil.generateSignature(data, config.getKey());
            data.put("sign", sign);
            LogUtils.info("提现参数data: ", data.toString());
            String respXml = wxPay.requestWithCert(Constant.TRANSFERS_URL, data, config.getHttpConnectTimeoutMs(),
                    config.getHttpReadTimeoutMs());
            Map<String, String> respMap = WXPayUtil.xmlToMap(respXml);
            LogUtils.info("提现结果respMap: ", respMap.toString());
            if ("SUCCESS".equals(respMap.get("return_code"))) {
                if ("SUCCESS".equals(respMap.get("result_code"))) {
                    ShopWallet sw = new ShopWallet();
                    sw.setOrderId(Long.valueOf(input.getPartnerTradeNo()));
                    sw.setAmount(MoneyUtil.formatMoney(withdraw));
                    sw.setOpenId(input.getOpenId());
                    sw.setDescRemarks(desc);
                    sw.setOperator("DEC");
                    //手续费
                    sw.setFee(calculateFee(input.getFee()));
                    sw.setStatus("SUCCESS");
                    sw.setPaymentTime(respMap.get("payment_time"));
                    upInsertWalletRecord(input.getPartnerTradeNo(), sw);
                    withVo.setStatus("SUCCESS");
                    withVo.setOperator("DEC");
                    withVo.setReason("成功");
                    return withVo;
                } else {
                    //SYSTEMERROR时，需要调查询付款接口，查询付款状态
                    if ("SYSTEMERROR".equals(respMap.get("err_code"))) {
                        return dealWithResultByTransferInfo(this.withdrawTransferInfo(input.getPartnerTradeNo()), true);
                    }
                    withVo.setStatus("FAIL");
                    withVo.setOperator("DEC");
                    withVo.setReason(respMap.get("err_code_des"));
                    log.error("查询提现错误，错误码:{}, 错误信息:{}", respMap.get("err_code"), respMap.get("err_code_des"));
                    return withVo;
                }
            } else {
                withVo.setStatus("FAIL");
                withVo.setOperator("DEC");
                String returnMsg = respMap.get("return_msg");
                if (StringUtils.isBlank(returnMsg)) {
                    returnMsg = "系统繁忙，请稍后再试";
                }
                withVo.setReason(returnMsg);
                return withVo;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return withVo;
        }
    }

    /**
     * 提现查询
     *
     * @param orderId
     * @return
     */
    @Override
    public Map<String, String> withdrawTransferInfo(String orderId) {
        try {
            if (config == null) {
                config = WXPayConfigImpl.getInstance();
            }
            if (wxPay == null) {
                wxPay = new WXPay(config);
            }
            Map<String, String> inputMap = new HashMap<>();
            inputMap.put("nonce_str", WXPayUtil.generateNonceStr());
            //需要提现时的orderId
            inputMap.put("partner_trade_no", orderId);
            inputMap.put("mch_id", config.getMchID());
            inputMap.put("appid", config.getAppID());
            inputMap.put("sign", WXPayUtil.generateSignature(inputMap, config.getKey()));
            String respXml = wxPay.requestWithCert(Constant.TRANSFERINFO_URL, inputMap, config.getHttpConnectTimeoutMs(), config.getHttpReadTimeoutMs());
            return WXPayUtil.xmlToMap(respXml);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    /**
     * 提现查询后的处理
     *
     * @param respMap
     * @param isPayQuery true:是提现支付情景， false:查询提现记录及余额情景
     * @return
     */
    private WithdrawVO dealWithResultByTransferInfo(Map<String, String> respMap, boolean isPayQuery) {
        WithdrawVO withdraw = new WithdrawVO();
        if ("SUCCESS".equals(respMap.get("return_code"))) {
            if ("SUCCESS".equals(respMap.get("result_code"))) {
                String partnerTradeNo = respMap.get("partner_trade_no");
                withdraw.setOperator("DEC");
                withdraw.setStatus(respMap.get("status"));
                withdraw.setReason(respMap.get("reason") == null ? "" : respMap.get("reason"));
                //确定失败，不存数据库
                if ("FAILED".equals(respMap.get("status")) && isPayQuery) {
                    return withdraw;
                }
                ShopWallet shopWallet = new ShopWallet();
                shopWallet.setOrderId(Long.valueOf(partnerTradeNo));
                shopWallet.setStatus(respMap.get("status"));
                shopWallet.setOperator("DEC");
                shopWallet.setOpenId(respMap.get("openid"));
                //分转元
                shopWallet.setAmount(MoneyUtil.changeF2Y(respMap.get("payment_amount")));
                shopWallet.setDescRemarks(respMap.get("desc"));
                shopWallet.setPaymentTime(respMap.get("payment_time"));
                shopWallet.setTransferTime(respMap.get("transfer_time"));
                shopWallet.setReason(respMap.get("reason") == null ? "" : respMap.get("reason"));
                upInsertWalletRecord(partnerTradeNo, shopWallet);
                return withdraw;
            } else {
                withdraw.setOperator("DEC");
                withdraw.setStatus("PROCESSING");
                withdraw.setReason(respMap.get("err_code_des"));
                log.error("查询提现错误，错误码:{}, 错误信息:{}", respMap.get("err_code"), respMap.get("err_code_des"));
                return withdraw;
            }
        } else {
            //通信标识失败
            withdraw.setOperator("DEC");
            withdraw.setStatus("FAIL");
            withdraw.setReason(respMap.get("return_msg"));
            return withdraw;
        }
    }

    private void upInsertWalletRecord(String orderId, ShopWallet shopWallet) {
        try {
            ShopWallet wallet = walletMapper.selectOneByOrderId(Long.valueOf(orderId), "DEC");
            if (wallet == null) {
                walletMapper.insertWalletRecord(shopWallet);
            } else {
                shopWallet.setId(wallet.getId());
                walletMapper.updateShopWalletWithdraw(shopWallet);
            }

            //提现申请通知 发送给商户
            templateService.sendWithdrawMessage(shopWallet);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void queryWithdrawByOrder(List<ShopWallet> shopWallets) {
        try {
            for (ShopWallet shopWallet : shopWallets) {
                //处理中的再次查询状态
                if ("PROCESSING".equals(shopWallet.getStatus())) {
                    dealWithResultByTransferInfo(this.withdrawTransferInfo(String.valueOf(shopWallet.getOrderId())), false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 计算手续费
     *
     * @param amount
     * @return
     */
    private String calculateFee(String amount) {
        if (StringUtils.isBlank(amount)) {
            return "0.00";
        }
        //手续费去尾
        BigDecimal fee = new BigDecimal(amount).multiply(new BigDecimal(Constant.WITHDRAW_FEE)).setScale(2,
                BigDecimal.ROUND_DOWN);
        return fee.toPlainString();
    }

    /**
     * 判断是否可提现
     *
     * @param openId
     * @param amount
     * @return
     */
    private boolean checkAmountByWithdraw(String openId, String amount) {
        if (StringUtils.isBlank(amount)) {
            return false;
        }
        List<ShopWallet> shopWallets = walletMapper.selectAllByOpenId(openId);
        if (CollectionUtils.isEmpty(shopWallets)) {
            return false;
        }
        //计算余额和总手续费
        AccountBalanceVO accountVO = calculateAmounts(shopWallets);
        return (new BigDecimal(amount).add(new BigDecimal(calculateFee(amount)))).compareTo(new BigDecimal(accountVO.getBalance())) <= 0;
    }
}
