package com.xll.baitaner.controller;

import com.xll.baitaner.entity.VO.WithdrawVO;
import com.xll.baitaner.service.WalletService;
import com.xll.baitaner.utils.ResponseResult;
import io.swagger.annotations.Api;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 钱包相关操作
 * @author denghuohuo 2019/6/29
 */
@Api(value = "钱包模块controller")
@RestController("wallet")
public class WalletController {

    @Resource
    WalletService walletService;

    /**
     * 根据日期查询提现记录
     * @param shopId
     * @param date
     * @param page
     * @return
     */
    @GetMapping("withdrawamount")
    public ResponseResult getWithdrawList(Integer shopId, String date, Pageable page) {
        try {
            WithdrawVO amountVo = walletService.getWithdrawAmountList(shopId, date, page);
            return ResponseResult.result(0,"success", amountVo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.result(1,"fail", null);
        }
    }
}
