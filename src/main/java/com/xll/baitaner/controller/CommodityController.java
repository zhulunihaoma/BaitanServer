package com.xll.baitaner.controller;

import com.xll.baitaner.entity.Commodity;
import com.xll.baitaner.service.CommodityService;
import com.xll.baitaner.utils.LogUtils;
import com.xll.baitaner.utils.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 描述：商品模块controller
 * 创建者：xie
 * 日期：2017/10/10
 **/
@Api(value = "商品模块controller")
@RestController
public class CommodityController {

    private final String TAG = "Baitaner-CommodityController";

    @Resource
    private CommodityService commodityService;

    /**
     * 新增商品接口
     *
     * @param commodity
     * @return
     */
    @ApiOperation(
            value = "新增商品接口",
            httpMethod = "POST",
            notes = "新增商品接口，turn字段不用填，后台自动生成；specs字段为规格数组 例：specs: [1, 2, 3]")
    @ApiImplicitParam(name = "commodity", value = "商品实体类", required = true, dataType = "Commodity")
    @RequestMapping("addcommodity")
    public ResponseResult addCommodity(Commodity commodity) {
        LogUtils.info(TAG, JSONObject.fromObject(commodity).toString());
        boolean result = commodityService.addCommodity(commodity);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 修改商品信息接口
     *
     * @param commodity
     * @return
     */
    @ApiOperation(
            value = "修改商品接口",
            httpMethod = "POST",
            notes = "修改商品信息接口 可改字段 name,price,postage,monthlySales,pictUrl,introduction,stock")
    @ApiImplicitParam(name = "commodity", value = "商品实体类", required = true, dataType = "Commodity")
    @RequestMapping("updatecommodity")
    public ResponseResult updateCommodity(Commodity commodity) {
        boolean result = commodityService.updateCommodity(commodity);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 分页查询店铺中对应分类的商品列表
     * 上下架均显示
     *
     * @param shopId
     * @return
     */
    @ApiOperation(
            value = "分页查询店铺中对应分类的商品列表",
            httpMethod = "GET",
            notes = "分页查询店铺中对应分类的商品列表  按分类内商品排序")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int"),
            @ApiImplicitParam(name = "storId", value = "分类storId", required = true, dataType = "int"),
            @ApiImplicitParam(name = "pageable", value = "分页", required = true, dataType = "Pageable")
    })
    @GetMapping("getstorcolist")
    public ResponseResult getStorColist(int shopId, int storId, Pageable pageable) {
        return ResponseResult.result(0, "success", commodityService.getStorColist(shopId, storId, pageable));
    }

    /**
     * 获取店铺中所有商品列表
     * 上下架均显示
     *
     * @param shopId
     * @return
     */
    @ApiOperation(
            value = "分页获取店铺中所有商品列表, 上下架均显示",
            httpMethod = "GET",
            notes = "分页获取店铺中所有商品列表, 上下架均显示，先按分类排序，再按分类内商品排序")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int"),
            @ApiImplicitParam(name = "pageable", value = "分页", required = true, dataType = "Pageable")
    })
    @GetMapping("getallcolist")
    public ResponseResult getAllCoList(int shopId, Pageable pageable) {
        return ResponseResult.result(0, "success", commodityService.getAllCoList(shopId, pageable));
    }

    /**
     * 获取店铺已上架商品列表
     *
     * @param shopId
     * @return
     */
    @ApiOperation(
            value = "分页获取店铺已上架商品列表",
            httpMethod = "GET",
            notes = "分页获取店铺已上架商品列表，先按分类排序，再按分类内商品排序（店铺首页）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int"),
            @ApiImplicitParam(name = "pageable", value = "分页", required = true, dataType = "Pageable")
    })
    @GetMapping("getcolist")
    public ResponseResult geCoList(int shopId, Pageable pageable) {
        return ResponseResult.result(0, "success", commodityService.getCoList(shopId, pageable));
    }

    /**
     * 获取店铺已上架商品列表
     *
     * @param shopId
     * @return
     */
    @ApiOperation(
            value = "查询店铺中的商品列表（上架）,分类包裹",
            httpMethod = "GET",
            notes = "查询店铺中的商品列表（上架）,分类包裹")
    @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int")
    @GetMapping("getsortco")
    public ResponseResult getSortCo(int shopId) {
        return ResponseResult.result(0, "success", commodityService.getSortCo(shopId));
    }

    /**
     * 获取单个商品的详情数据
     *
     * @param commodityId
     * @return
     */
    @ApiOperation(
            value = "获取单个商品的详情数据",
            httpMethod = "GET",
            notes = "获取单个商品的详情数据")
    @ApiImplicitParam(name = "commodityId", value = "商品id", required = true, dataType = "int")
    @GetMapping("getcommodity")
    public ResponseResult getCommodity(int commodityId) {
        return ResponseResult.result(0, "success", commodityService.getCommodity(commodityId));
    }

    /**
     * 删除商品接口
     *
     * @param commodityId
     * @return
     */
    @ApiOperation(
            value = "删除商品接口",
            httpMethod = "GET",
            notes = "删除商品接口")
    @ApiImplicitParam(name = "commodityId", value = "商品id", required = true, dataType = "int")
    @RequestMapping("deletecommodity")
    public ResponseResult deleteCommodity(int commodityId) {
        boolean result = commodityService.deleteCommodity(commodityId);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 修改商品上下架状态接口
     *
     * @param commodityId
     * @return
     */
    @ApiOperation(
            value = "修改商品上下架状态接口",
            httpMethod = "GET",
            notes = "修改商品上下架状态接口")
    @ApiImplicitParam(name = "commodityId", value = "商品id", required = true, dataType = "int")
    @RequestMapping("updatecostate")
    public ResponseResult updateCommodityState(int commodityId) {
        boolean result = commodityService.updateCoState(commodityId);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 分类下更新商品到指定位置顺序
     *
     * @param co
     * @param turn
     * @return
     */
    @ApiOperation(
            value = "分类下更新商品到指定位置顺序",
            httpMethod = "GET",
            notes = "分类下更新商品到指定位置顺序 自定义位置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "co", value = "商品实体类", required = true, dataType = "Commodity"),
            @ApiImplicitParam(name = "turn", value = "指定位置", required = true, dataType = "int")
    })
    @RequestMapping("updatecoturn")
    public ResponseResult updateCoTurn(Commodity co, int turn) {
        boolean result = commodityService.updateCoTurn(co, turn);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 分类下上移商品
     *
     * @param co
     * @return
     */
    @ApiOperation(
            value = "分类下上移商品",
            httpMethod = "GET",
            notes = "分类下上移商品")
    @ApiImplicitParam(name = "co", value = "商品实体类", required = true, dataType = "Commodity")
    @RequestMapping("moveupcoturn")
    public ResponseResult moveUpCoTurn(Commodity co) {
        boolean result = commodityService.updateCoTurn(co, co.getTurn() - 1);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 分类下置顶商品
     *
     * @param co
     * @return
     */
    @ApiOperation(
            value = "分类下置顶商品",
            httpMethod = "GET",
            notes = "分类下置顶商品")
    @ApiImplicitParam(name = "co", value = "商品实体类", required = true, dataType = "Commodity")
    @RequestMapping("toppingcoturn")
    public ResponseResult toppingCoTurn(Commodity co) {
        boolean result = commodityService.updateCoTurn(co, 0);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }
}
