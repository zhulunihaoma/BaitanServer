package com.xll.baitaner.controller;

import com.xll.baitaner.entity.Shop;
import com.xll.baitaner.entity.ShopBanner;
import com.xll.baitaner.service.ShopManageService;
import com.xll.baitaner.utils.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 描述：店铺模块controller
 * 创建者：xie
 **/
@Api(value = "店铺模块controller", description = "店铺模块接口，包括店铺创建更新以及店铺首页数据")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class ShopManageController {

    @Resource
    private ShopManageService shopManageService;

    /**
     * 创建店铺数据提交接口
     *
     * @param shop
     * @return
     */
    @ApiOperation(
            value = "创建店铺数据提交接口",
            httpMethod = "POST",
            notes = "创建店铺数据提交接口")
    @ApiImplicitParam(name = "shop", value = "店铺信息实体类", required = true, dataType = "Shop")
    @PostMapping("addshop")
    public ResponseResult addShop(Shop shop) {
        String result = shopManageService.addShop(shop);
        if (result == null) {
            return ResponseResult.result(0, "success", result);
        } else {
            return ResponseResult.result(1, "fail", result);
        }
    }

    /**
     * 根据Id获取店铺信息
     *
     * @return
     */
    @ApiOperation(
            value = "根据Id获取店铺信息",
            httpMethod = "GET",
            notes = "根据Id获取店铺信息")
    @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int")
    @GetMapping("getshopbyid")
    public ResponseResult getShopById(int shopId) {
        Shop shop = shopManageService.getShopById(shopId);
        return ResponseResult.result(shop != null ? 0 : 1, shop != null ? "success" : "fail", shop);
    }

    /**
     * 获取用户拥有的店铺信息
     *
     * @return
     */
    @ApiOperation(
            value = "获取用户拥有的店铺信息",
            httpMethod = "GET",
            notes = "获取用户拥有的店铺信息")
    @ApiImplicitParam(name = "openId", value = "用户openId", required = true, dataType = "String")
    @GetMapping("getshopbyuser")
    public ResponseResult getShopByUser(String openId) {
        return ResponseResult.result(0, "success", shopManageService.getShopByUser(openId));
    }

    /**
     * 获取店铺banner图
     *
     * @return
     */
    @ApiOperation(
            value = "获取店铺banner图",
            httpMethod = "GET",
            notes = "返回新建店铺时自动创建的空的banner图数据类,可调用修改接口上传图片")
    @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int")
    @GetMapping("getshopbanner")
    public ResponseResult getShopBanner(int shopId) {
        return ResponseResult.result(0, "success", shopManageService.getShopBanner(shopId));
    }

    /**
     * 修改店铺banner图
     *
     * @return
     */
    @ApiOperation(
            value = "修改店铺banner图",
            httpMethod = "POST",
            notes = "修改店铺banner图")
    @ApiImplicitParam(name = "shopBanner", value = "banner图数据类", required = true, dataType = "ShopBanner")
    @PostMapping("updateshopbanner")
    public ResponseResult updateShopBanner(ShopBanner shopBanner) {
        boolean result = shopManageService.updateShopBanner(shopBanner);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }


    /**
     * 修改店铺信息,店铺名、店铺简介、店主微信名、店主微信、联系电话、店铺地址、店铺logo、店铺平台支付开启状态、
     * 店铺二维码支付开启状态、店铺支付二维码路径、访问人次
     *
     * @return
     */
    @ApiOperation(
            value = "修改店铺信息",
            httpMethod = "POST",
            notes = "只修改字段：店铺名、店铺简介、店主微信名、店主微信、联系电话、店铺地址、店铺logo、店铺平台支付开启状态、\n" +
                    " 店铺二维码支付开启状态、店铺支付二维码路径、访问人次")
    @ApiImplicitParam(name = "shop", value = "店铺数据类", required = true, dataType = "Shop")
    @PostMapping("updateshopinfo")
    public ResponseResult updateShopInfo(Shop shop) {
        boolean result = shopManageService.updateShopInfo(shop);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 修改小程序内部支付开启状态
     *
     * @return
     */
    @ApiOperation(
            value = "修改小程序内部支付开启状态",
            httpMethod = "GET",
            notes = "修改小程序内部支付开启状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int"),
            @ApiImplicitParam(name = "state", value = "状态 0:false  1:true", required = true, dataType = "int")
    })
    @GetMapping("updatepayplatform")
    public ResponseResult updateShopPayPlatform(int shopId, int state) {
        boolean result = shopManageService.updateShopPayPlatform(shopId, state);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }


    /**
     * 上传支付二维码图片路径
     *
     * @return
     */
    @ApiOperation(
            value = "上传支付二维码图片路径",
            httpMethod = "POST",
            notes = "上传支付二维码图片路径")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int"),
            @ApiImplicitParam(name = "openId", value = "用户openId", required = true, dataType = "String"),
            @ApiImplicitParam(name = "url", value = "支付二维码图片路径", required = true, dataType = "String")
    })
    @PostMapping("updatepayqrcodeurl")
    public ResponseResult updateShopPayQrcodeUrl(int shopId, String openId, String url) {
        String result = shopManageService.updateShopPayQrcodeUrl(shopId, openId, url);
        return ResponseResult.result(result == null ? 0 : 1, result == null ? "success" : "fail", result);
    }

    /**
     * 获取店铺支付二维码
     *
     * @return
     */
    @ApiOperation(
            value = "获取店铺支付二维码",
            httpMethod = "GET",
            notes = "获取店铺支付二维码")
    @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int")
    @GetMapping("getpayqrcode")
    public ResponseResult getShopPayQrcodeUrl(int shopId) {
        return ResponseResult.result(0, "success", shopManageService.getShopPayQrcodeUrl(shopId));
    }

    /**
     * 修改店铺二维码支付开启状态
     *
     * @return
     */
    @ApiOperation(
            value = "修改店铺二维码支付开启状态",
            httpMethod = "GET",
            notes = "修改店铺二维码支付开启状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int"),
            @ApiImplicitParam(name = "state", value = "状态 0:false  1:true", required = true, dataType = "int")
    })
    @GetMapping("updatepayqrcode")
    public ResponseResult updateShopPayQrcode(int shopId, int state) {
        String result = shopManageService.updateShopPayQrcode(shopId, state);
        return ResponseResult.result(result == null ? 0 : 1, result == null ? "success" : "fail", result);
    }

    /**
     * 更新店铺状态
     *
     * @param shopId
     * @param state
     * @return
     */
    @ApiOperation(
            value = "更新店铺状态",
            httpMethod = "GET",
            notes = "更新店铺状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int"),
            @ApiImplicitParam(name = "state", value = "状态 0:false  1:true", required = true, dataType = "int")
    })
    @GetMapping("updateshopstate")
    public ResponseResult updateShopState(int shopId, int state) {
        boolean result = shopManageService.updateShopState(shopId, state);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 增加店铺访问人数
     *
     * @param shopId
     * @return
     */
    @ApiOperation(
            value = "增加店铺访问人数",
            httpMethod = "GET",
            notes = "增加店铺访问人数，每次进入店铺时调用此接口，人数+1")
    @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int")
    @GetMapping("addshopvisits")
    public ResponseResult addNumber(int shopId) {
        boolean res = shopManageService.addNumber(shopId);
        return ResponseResult.result(res ? 0 : 1, res ? "success" : "fail", null);
    }

    /**
     * 删除店铺
     *
     * @param shopId
     * @return
     */
    @ApiOperation(
            value = "删除店铺",
            httpMethod = "GET",
            notes = "删除店铺")
    @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int")
    @GetMapping("deleteshop")
    public ResponseResult deleteShop(int shopId) {
        boolean result = shopManageService.deleteShop(shopId);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 用户浏览店铺记录接口
     *
     * @param shopId
     * @return
     */
    @ApiOperation(
            value = "用户浏览店铺记录接口",
            httpMethod = "GET",
            notes = "用户浏览店铺记录接口，用户访问店铺时调用，记录到用户浏览过店铺数据中")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "openId", value = "用户openId", required = true, dataType = "String"),
            @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int")
    })
    @GetMapping("useraccessshop")
    public ResponseResult addShopForUser(String openId, int shopId) {
        String result = shopManageService.addShopUser(openId, shopId);
        return ResponseResult.result(result == null ? 0 : 1, result == null ? "success" : "fail", result);
    }

    /**
     * 获取用户浏览过的店铺
     *
     * @return
     */
    @ApiOperation(
            value = "获取用户浏览过的店铺",
            httpMethod = "GET",
            notes = "获取用户浏览过的店铺，用于首页展示")
    @ApiImplicitParam(name = "openId", value = "用户openId", required = true, dataType = "String")
    @GetMapping("getshoplistforuser")
    public ResponseResult getShopListForUser(String openId) {
        return ResponseResult.result(0, "success", shopManageService.getShopListForUser(openId));
    }

    /**
     * 获取用户店铺首页的数据
     *
     * @return
     */
    @ApiOperation(
            value = "获取用户店铺首页的数据",
            httpMethod = "GET",
            notes = "获取用户店铺首页的数据用于首页展示,包括用户浏览过的店铺，拥有的店铺，如没有则为空")
    @ApiImplicitParam(name = "openId", value = "用户openId", required = true, dataType = "String")
    @GetMapping("geshophome")
    public ResponseResult getShopHomeData(String openId) {
        return ResponseResult.result(0, "success", shopManageService.getShopHomeData(openId));
    }


    /**
     * 获取店铺下各类二维码存储路径 根据传参返回二维码地址
     *
     * @param shopId
     * @return
     */
    @ApiOperation(
            value = "获取店铺下各类二维码存储路径",
            httpMethod = "GET",
            notes = "获取店铺下各类二维码存储路径 根据传参返回二维码地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "scene", value = "二维码携带的参数", required = true, dataType = "String"),
            @ApiImplicitParam(name = "page", value = "二维码跳转的页面", required = true, dataType = "String")
    })
    @GetMapping("getwxacode")
    public ResponseResult getWXacodePath(Integer shopId, String scene, String page) {
        ResponseResult result = shopManageService.getWXacodePath(shopId, scene, page);
        return result;
    }
}
