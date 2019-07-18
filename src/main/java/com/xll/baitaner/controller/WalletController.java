package com.xll.baitaner.controller;

import com.xll.baitaner.entity.VO.WithdrawInputVo;
import com.xll.baitaner.entity.VO.WithdrawResultVO;
import com.xll.baitaner.entity.VO.WithdrawVO;
import com.xll.baitaner.service.WalletService;
import com.xll.baitaner.utils.ResponseResult;
import com.xll.baitaner.utils.SerialUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
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
     * @param openId
     * @param date
     * @return
     */
    @ApiOperation(value = "分页查询店铺提现记录", httpMethod = "GET", notes = "分页查询店铺提现记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "openId", value = "提现用户openId", required = true, dataType = "string"),
            @ApiImplicitParam(name = "date", value = "查询日期", required = true, dataType = "string"),
            @ApiImplicitParam(name = "offset", value = "开始页", required = true, dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页条数", required = true, dataType = "int")
    })
    @GetMapping("wallet/withdrawamount")
    public ResponseResult getWithdrawList(String openId, String date, Integer offset, Integer size) {
        try {
            WithdrawResultVO resultVO = walletService.queryWithdrawAmountList(openId, date, offset, size);
            return ResponseResult.result(0, "success", resultVO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.result(1, "fail", null);
        }
    }

    @ApiOperation(value = "分页查询店铺所有提现记录", httpMethod = "GET", notes = "分页查询店铺提现记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "openId", value = "提现用户openId", required = true, dataType = "string"),
            @ApiImplicitParam(name = "offset", value = "开始页", required = true, dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页条数", required = true, dataType = "int")
    })
    @GetMapping("wallet/withdrawallamount")
    public ResponseResult getWithdrawList(String openId, Integer offset, Integer size) {
        try {
            WithdrawResultVO resultVO = walletService.queryWithdrawAmountList(openId, offset, size);
            return ResponseResult.result(0, "success", resultVO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.result(1, "fail", null);
        }
    }

    /**
     * 获取店铺余额
     *
     * @param openId
     * @return
     */
    @ApiOperation(value = "获取店铺余额", httpMethod = "GET", notes = "根据店铺ID获取店铺余额")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "openId", value = "店铺openId", required = true, dataType = "string"),
    })
    @GetMapping("wallet/shopamount")
    public ResponseResult getShopAmounts(String openId) {
        if (StringUtils.isBlank(openId)) {
            return ResponseResult.result(1, "店铺openId为空", null);
        }
        try {
            BigDecimal shopAmounts = walletService.getShopAmounts(openId);
            return ResponseResult.result(0, "success", shopAmounts);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.result(1, "fail", null);
        }
    }

    /**
     * 提现
     *
     * @param input
     * @return
     */
    @ApiOperation(
            value = "微信企业付款 提现到个人",
            httpMethod = "GET",
            notes = "微信企业付款 提现到个人")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "openId", value = "openId", required = true, dataType = "String"),
            @ApiImplicitParam(name = "desc", value = "提现备注", required = true, dataType = "String"),
            @ApiImplicitParam(name = "fee", value = "金额", required = true, dataType = "String")
    })
    @GetMapping("wxpayforper")
    public ResponseResult wxpayforper(WithdrawInputVo input) {
        try {
            //提现订单号
            input.setPartnerTradeNo(SerialUtils.getSerialId());
            WithdrawVO withdrawVO = walletService.withdrawTransfer(input);
            return ResponseResult.result(0, "查询提现状态成功", withdrawVO);
        } catch (Exception e) {
            return ResponseResult.result(1, "查询提现状态出错", null);
        }
    }
}
