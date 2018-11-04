package xll.baitaner.service.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import xll.baitaner.service.entity.Shop;
import xll.baitaner.service.entity.ShopBanner;
import xll.baitaner.service.service.ShopManageService;
import xll.baitaner.service.utils.ResponseResult;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * 描述：店铺模块管理controller
 * 创建者：xie
 **/
@Api(value = "店铺模块管理controller", description = "店铺模块管理接口，包括店铺创建更新")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class ShopManageController {

    @Autowired
    private ShopManageService shopManageService;

    /**
     * 创建店铺数据提交接口
     * @param shop
     * @return
     */
    @ApiOperation(
            value = "创建店铺数据提交接口",
            httpMethod = "POST",
            notes = "创建店铺数据提交接口")
    @ApiImplicitParam(name = "shop", value = "店铺信息实体类", required = true, dataType = "Shop")
    @RequestMapping("addshop")
    public ResponseResult addShop(Shop shop){
        String result = shopManageService.addShop(shop);
        if(result == null){
            return ResponseResult.result(0, "success" , result);
        }
        else {
            return ResponseResult.result(1, "fail" , result);
        }
    }

    /**
     * 根据Id获取店铺信息
     * @return
     */
    @ApiOperation(
            value = "根据Id获取店铺信息",
            notes = "根据Id获取店铺信息")
    @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int")
    @GetMapping("getshopbyid")
    public ResponseResult getShopById(int shopId){
        return ResponseResult.result(0, "success" , shopManageService.getShopById(shopId));
    }

    /**
     * 获取用户拥有的店铺信息
     * @return
     */
    @ApiOperation(
            value = "获取用户拥有的店铺信息",
            notes = "获取用户拥有的店铺信息")
    @ApiImplicitParam(name = "openId", value = "用户openId", required = true, dataType = "String")
    @GetMapping("getshopbyuser")
    public ResponseResult getShopByUser(String openId){
        return ResponseResult.result(0, "success" , shopManageService.getShopByUser(openId));
    }

    /**
     * 获取店铺banner图
     * @return
     */
    @ApiOperation(
            value = "获取店铺banner图",
            notes = "返回新建店铺时自动创建的空的banner图数据类,可调用修改接口上传图片")
    @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int")
    @GetMapping("getshopbanner")
    public ResponseResult getShopBanner(int shopId){
        return ResponseResult.result(0, "success" , shopManageService.getShopBanner(shopId));
    }

    /**
     * 修改店铺banner图
     * @return
     */
    @ApiOperation(
            value = "修改店铺banner图",
            httpMethod = "POST",
            notes = "修改店铺banner图")
    @ApiImplicitParam(name = "shopBanner", value = "banner图数据类", required = true, dataType = "ShopBanner")
    @RequestMapping("updateshopbanner")
    public ResponseResult updateShopBanner(ShopBanner shopBanner){
        boolean result = shopManageService.updateShopBanner(shopBanner);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }


    /**
     * 修改店铺信息,店铺名、店铺简介、店主微信名、店主微信、联系电话、店铺地址、店铺logo、访问人次
     * @return
     */
    @ApiOperation(
            value = "修改店铺信息",
            httpMethod = "POST",
            notes = "只修改字段：店铺名、店铺简介、店主微信名、店主微信、联系电话、店铺地址、店铺logo、访问人次")
    @ApiImplicitParam(name = "shop", value = "店铺数据类", required = true, dataType = "Shop")
    @RequestMapping("updateshopinfo")
    public ResponseResult updateShopInfo(Shop shop){
        boolean result = shopManageService.updateShopInfo(shop);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 修改小程序内部支付开启状态
     * @return
     */
    @ApiOperation(
            value = "修改小程序内部支付开启状态",
            notes = "修改小程序内部支付开启状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int"),
            @ApiImplicitParam(name = "state", value = "状态 0:false  1:true", required = true, dataType = "int")
    })
    @GetMapping("updatepayplatform")
    public ResponseResult updateShopPayPlatform(int shopId, int state){
        boolean result = shopManageService.updateShopPayPlatform(shopId, state);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }


    /**
     * 上传支付二维码图片路径
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
    @RequestMapping("updatepayqrcodeurl")
    public ResponseResult updateShopPayQrcodeUrl(int shopId,String openId, String url){
        boolean result = shopManageService.updateShopPayQrcodeUrl(shopId, openId, url) == null;
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", result);
    }

    /**
     * 获取店铺支付二维码
     * @return
     */
    @ApiOperation(
            value = "获取店铺支付二维码",
            notes = "获取店铺支付二维码")
    @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int")
    @GetMapping("getpayqrcode")
    public ResponseResult getShopPayQrcodeUrl(int shopId){
        return ResponseResult.result(0, "success" , shopManageService.getShopPayQrcodeUrl(shopId));
    }

    /**
     * 修改店铺二维码支付开启状态
     * @return
     */
    @ApiOperation(
            value = "修改店铺二维码支付开启状态",
            notes = "修改店铺二维码支付开启状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int"),
            @ApiImplicitParam(name = "state", value = "状态 0:false  1:true", required = true, dataType = "int")
    })
    @GetMapping("updatepayqrcode")
    public ResponseResult updateShopPayQrcode(int shopId, int state){
        boolean result = shopManageService.updateShopPayQrcode(shopId, state) == null;
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", result);
    }

    /**
     * 更新店铺状态
     * @param shopId
     * @param state
     * @return
     */
    @ApiOperation(
            value = "更新店铺状态",
            notes = "更新店铺状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int"),
            @ApiImplicitParam(name = "state", value = "状态 0:false  1:true", required = true, dataType = "int")
    })
    @GetMapping("updateshopstate")
    public ResponseResult updateShopState(int shopId, int state){
        boolean result = shopManageService.updateShopState(shopId, state);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 增加店铺访问人数
     * @param id
     * @return
     */
    @ApiOperation(
            value = "增加店铺访问人数",
            notes = "增加店铺访问人数，每次进入店铺时调用此接口，人数+1")
    @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int")
    @GetMapping("addshopvisits")
    public ResponseResult addNumber(int shopId){
        boolean res = shopManageService.addNumber(shopId);
        return ResponseResult.result(res ? 0 : 1, res ? "success" : "fail", null);
    }

    /**
     * 删除店铺
     * @param shopId
     * @return
     */
    @ApiOperation(
            value = "删除店铺",
            notes = "删除店铺")
    @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int")
    @GetMapping("deleteshop")
    public ResponseResult deleteShop(int shopId){
        boolean result = shopManageService.deleteShop(shopId);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }
}
