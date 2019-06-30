package com.xll.baitaner.controller;

import com.xll.baitaner.entity.VO.WithdrawVO;
import com.xll.baitaner.service.WalletService;
import com.xll.baitaner.utils.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * 钱包相关操作
 *
 * @author denghuohuo 2019/6/29
 */
@Api(value = "钱包模块controller")
@RestController
public class WalletController {

    @Resource
    WalletService walletService;

    /**
     * 根据日期查询提现记录
     *
     * @param shopId
     * @param date
     * @param page
     * @return
     */
    @ApiOperation(value = "分页查询店铺提现记录", httpMethod = "GET", notes = "分页查询店铺提现记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int"),
            @ApiImplicitParam(name = "date", value = "查询日期", required = true, dataType = "string"),
            @ApiImplicitParam(name = "pageable", value = "分页", required = true, dataType = "Pageable")
    })
    @GetMapping("wallet/withdrawamount")
    public ResponseResult getWithdrawList(Integer shopId, String date, Pageable page) {
        try {
            WithdrawVO amountVo = walletService.queryWithdrawAmountList(shopId, date, page);
            return ResponseResult.result(0, "success", amountVo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.result(1, "fail", null);
        }
    }

    /**
     * 获取店铺余额
     *
     * @param shopId
     * @return
     */
    @ApiOperation(value = "获取店铺余额", httpMethod = "GET", notes = "根据店铺ID获取店铺余额")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int"),
    })
    @GetMapping("wallet/shopamount")
    public ResponseResult getShopAmounts(Integer shopId) {
        if (shopId == null || shopId == 0) {
            return ResponseResult.result(1, "店铺id为空", null);
        }
        try {
            BigDecimal shopAmounts = walletService.getShopAmounts(shopId);
            return ResponseResult.result(0, "success", shopAmounts);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.result(1, "fail", null);
        }
    }
}
