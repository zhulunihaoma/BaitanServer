package com.xll.baitaner.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.xll.baitaner.entity.ShopWallet;
import com.xll.baitaner.entity.VO.AccountBalanceVO;
import com.xll.baitaner.entity.VO.WithdrawInputVo;
import com.xll.baitaner.entity.VO.WithdrawResultVO;
import com.xll.baitaner.entity.VO.WithdrawVO;
import com.xll.baitaner.mapper.WalletMapper;
import com.xll.baitaner.service.TemplateService;
import com.xll.baitaner.service.WalletService;
import com.xll.baitaner.utils.Constant;
import com.xll.baitaner.utils.WXPayConfigImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
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
        try {
            //再次查询状态，更新数据
            queryWithdrawByOrder(wallets);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (ShopWallet wallet : wallets) {
            WithdrawVO vo = new WithdrawVO();
            vo.setAmount(wallet.getAmount());
            vo.setDate(wallet.getCreateDate());
            vo.setReason(wallet.getReason());
            vo.setStatus(wallet.getStatus());
            vo.setOperator("DEC");
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
    public AccountBalanceVO getShopAmounts(String openId) {
        AccountBalanceVO balance = new AccountBalanceVO();
        List<ShopWallet> shopWallets = walletMapper.selectAllByOpenId(openId);
        if (CollectionUtils.isEmpty(shopWallets)) {
            return balance;
        }
        //FIXME 暂时只有钱方到账金额
        balance.setBalance(calculateQfAmounts(shopWallets).subtract(calculateWithdrawAmounts(shopWallets)));
        //FIXME 暂时只有钱方未到账金额
        balance.setUnBalance(calculateQfWithoutAmounts(openId));
        return balance;
    }

    /**
     * 计算钱方到账总金额
     *
     * @param shopWallets
     * @return
     */
    private BigDecimal calculateQfAmounts(List<ShopWallet> shopWallets) {
        BigDecimal amount = new BigDecimal("0.00");
        if (CollectionUtils.isEmpty(shopWallets)) {
            return amount;
        }
        ZoneId zoneId = ZoneId.systemDefault();
        //当前时间的24小时前
        ZonedDateTime zonedDateTime = LocalDateTime.now().minusHours(24).atZone(zoneId);
        //转成Date
        Date before24Hours = Date.from(zonedDateTime.toInstant());
        for (ShopWallet qf : shopWallets) {
            if ("ADD".equals(qf.getOperator()) && (qf.getPayChannel() == 0) &&
                    qf.getCreateDate().before(before24Hours)) {
                amount = amount.add(new BigDecimal(qf.getAmount()));
            }
        }
        return amount;
    }

    /**
     * 计算所有提现总金额
     *
     * @param shopWallets
     * @return
     */
    private BigDecimal calculateWithdrawAmounts(List<ShopWallet> shopWallets) {
        BigDecimal amount = new BigDecimal("0.00");
        if (CollectionUtils.isEmpty(shopWallets)) {
            return amount;
        }
        for (ShopWallet wallet : shopWallets) {
            if ("DEC".equals(wallet.getOperator()) &&
                    ("SUCCESS".equals(wallet.getStatus()) || "PROCESSING".equals(wallet.getStatus()))) {
                amount = amount.add(new BigDecimal(wallet.getAmount()));
            }
        }
        return amount;
    }

    /**
     * 计算钱方未到账金额
     *
     * @param openId
     * @return
     */
    private BigDecimal calculateQfWithoutAmounts(String openId) {
        List<ShopWallet> shopWallets = walletMapper.selectBetween24HoursByOpenIdToQF(openId);
        BigDecimal amount = new BigDecimal("0.00");
        if (CollectionUtils.isEmpty(shopWallets)) {
            return amount;
        }
        for (ShopWallet sw : shopWallets) {
            amount = amount.add(new BigDecimal(sw.getAmount()));
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
            //换算成分
            String amount =
                    new BigDecimal(input.getFee()).multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString();
            data.put("amount", amount);
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
            String respXml = wxPay.requestWithCert(Constant.TRANSFERS_URL, data, config.getHttpConnectTimeoutMs(),
                    config.getHttpReadTimeoutMs());
            Map<String, String> respMap = WXPayUtil.xmlToMap(respXml);
            if ("SUCCESS".equals(respMap.get("return_code"))) {
                if ("SUCCESS".equals(respMap.get("result_code"))) {
                    ShopWallet sw = new ShopWallet();
                    sw.setOrderId(Long.valueOf(input.getPartnerTradeNo()));
                    sw.setAmount(input.getFee());
                    sw.setOpenId(input.getOpenId());
                    sw.setDescRemarks(desc);
                    sw.setOperator("DEC");
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
            Map<String, String> respMap = WXPayUtil.xmlToMap(respXml);
            return respMap;
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
                String payment_amount = new BigDecimal(respMap.get("payment_amount")).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).toPlainString();
                shopWallet.setAmount(payment_amount);
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
}
