package com.xll.baitaner.controller;

import com.xll.baitaner.entity.ReceiverAddress;
import com.xll.baitaner.service.ProfileService;
import com.xll.baitaner.utils.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 描述：个人功能类（收货地址）Controller
 * 创建者：xie
 * 日期：2017/10/11
 **/
@Api(value = "个人功能类（收货地址）Controller", description = "个人功能  目前主要是收货地址相关接口")
@RestController
public class ProfileController {

    @Resource
    private ProfileService profileService;

    /**
     * 获取用户所有收货地址列表
     *
     * @param openId
     * @return
     */
    @ApiOperation(
            value = "获取用户所有收货地址列表",
            httpMethod = "GET",
            notes = "获取用户所有收货地址列表")
    @ApiImplicitParam(name = "openId", value = "用户openId", required = true, dataType = "String")
    @GetMapping("getaddresslist")
    public ResponseResult getAddressList(String openId) {
        return ResponseResult.result(0, "success", profileService.getAddressList(openId));
    }

    /**
     * 获取单个收货地址数据
     *
     * @param addressId
     * @return
     */
    @ApiOperation(
            value = "获取单个收货地址数据",
            httpMethod = "GET",
            notes = "获取单个收货地址数据")
    @ApiImplicitParam(name = "addressId", value = "收货地址Id", required = true, dataType = "int")
    @GetMapping("getaddressbyid")
    public ResponseResult getAddress(int addressId) {
        return ResponseResult.result(0, "success", profileService.getAddress(addressId));
    }

    /**
     * 新增收货地址接口
     *
     * @param address
     * @return
     */
    @ApiOperation(
            value = "新增收货地址接口",
            httpMethod = "POST",
            notes = "获取单个收货地址数据")
    @ApiImplicitParam(name = "address", value = "收货地址", required = true, dataType = "ReceiverAddress")
    @PostMapping("addaddress")
    public ResponseResult addAddress(ReceiverAddress address) {
        boolean result = profileService.addAddress(address);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 修改收货地址接口
     *
     * @param address
     * @return
     */
    @ApiOperation(
            value = "修改收货地址接口",
            httpMethod = "POST",
            notes = "修改收货地址接口")
    @ApiImplicitParam(name = "address", value = "收货地址", required = true, dataType = "ReceiverAddress")
    @PostMapping("editaddress")
    public ResponseResult editAddress(ReceiverAddress address) {
        boolean result = profileService.updateAddress(address);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 删除收货地址
     *
     * @param addressId
     * @return
     */
    @ApiOperation(
            value = "删除收货地址",
            httpMethod = "GET",
            notes = "删除收货地址")
    @ApiImplicitParam(name = "addressId", value = "收货地址Id", required = true, dataType = "int")
    @GetMapping("deleteaddress")
    public ResponseResult deleteAddress(int addressId) {
        boolean result = profileService.deleteAddress(addressId);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 修改收货地址是否默认状态接口
     *
     * @param addressId
     * @param openId
     * @return
     */
    @ApiOperation(
            value = "修改收货地址是否默认状态接口",
            httpMethod = "POST",
            notes = "修改收货地址是否默认状态接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addressId", value = "收货地址Id", required = true, dataType = "int"),
            @ApiImplicitParam(name = "openId", value = "用户openId", required = true, dataType = "String")
    })
    @PostMapping("updatadstate")
    public ResponseResult updateAddressState(int addressId, String openId) {
        boolean result = profileService.updateAddressState(addressId, openId);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 获取用户默认收货地址接口
     *
     * @param openId
     * @return
     */
    @ApiOperation(
            value = "获取用户默认收货地址接口",
            httpMethod = "GET",
            notes = "获取用户默认收货地址接口")
    @ApiImplicitParam(name = "openId", value = "用户openId", required = true, dataType = "String")
    @GetMapping("getdefaultaddress")
    public ResponseResult getDefaultAddress(String openId) {
        return ResponseResult.result(0, "success", profileService.getDefaultAddress(openId));
    }
}
