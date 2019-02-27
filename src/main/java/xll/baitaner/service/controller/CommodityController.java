package xll.baitaner.service.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import xll.baitaner.service.entity.Commodity;
import xll.baitaner.service.service.CommodityService;
import xll.baitaner.service.utils.LogUtils;
import xll.baitaner.service.utils.ResponseResult;

import java.sql.Array;
import java.util.Arrays;
import java.util.Map;

/**
 * 描述：商品模块controller
 * 创建者：xie
 * 日期：2017/10/10
 **/
@Api(value = "商品模块controller", description = "商品规格模块接口")
@RestController
public class CommodityController {

    private String TAG = "Baitaner-CommodityController";

    @Autowired
    private CommodityService commodityService;

    /**
     * 新增商品接口
     * @param commodity
     * @return
     */
    @ApiOperation(
            value = "新增商品接口",
            httpMethod = "POST",
            notes = "新增商品接口，turn字段不用填，后台自动生成；specs字段为规格数组 例：specs: [1, 2, 3]")
    @ApiImplicitParam(name = "commodity", value = "商品实体类", required = true, dataType = "Commodity")
    @RequestMapping("addcommodity")
    public ResponseResult addCommodity(Commodity commodity){
        LogUtils.info(TAG, JSONObject.fromObject(commodity).toString());
        boolean result = commodityService.addCommodity(commodity);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 修改商品信息接口
     * @param commodity
     * @return
     */
    @ApiOperation(
            value = "修改商品接口",
            httpMethod = "POST",
            notes = "修改商品信息接口 可改字段 name,price,postage,monthlySales,pictUrl,introduction,stock")
    @ApiImplicitParam(name = "commodity", value = "商品实体类", required = true, dataType = "Commodity")
    @RequestMapping("updatecommodity")
    public ResponseResult updateCommodity(Commodity commodity){
        boolean result = commodityService.updateCommodity(commodity);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }


    /**
     * 获取店铺中所有商品列表
     * 上下架均显示
     * @param shopId
     * @return
     */
    @ApiOperation(
            value = "获取店铺中所有商品列表, 上下架均显示",
            httpMethod = "GET",
            notes = "获取店铺中所有商品列表, 上下架均显示")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int"),
            @ApiImplicitParam(name = "pageable", value = "分页", required = true, dataType = "Pageable")
    })
    @GetMapping("getallcolist")
    public ResponseResult getAllCoList(int shopId, Pageable pageable){
        return ResponseResult.result(0, "success", commodityService.getAllCoList(shopId, pageable));
    }

    /**
     * 获取店铺已上架商品列表
     * @param shopId
     * @return
     */
    @GetMapping("commoditymanage/getcolist")
    public ResponseResult geCoList(int shopId, Pageable pageable){
        return ResponseResult.result(0, "success", commodityService.getCoList(shopId, pageable));
    }

    /**
     * 获取单个商品的详情数据
     * @param commodityId
     * @return
     */
    @GetMapping("commoditymanage/getcommodity")
    public ResponseResult getCommodity(int commodityId){
        return ResponseResult.result(0, "success", commodityService.getCommodity(commodityId));
    }

    /**
     * 删除商品接口
     * @param commodityId
     * @return
     */
    @RequestMapping("commoditymanage/deletecommodity")
    public ResponseResult deleteCommodity(int commodityId){
        boolean result = commodityService.deleteCommodity(commodityId);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 修改商品上下架状态接口
     * @param commodityId
     * @return
     */
    @RequestMapping("commoditymanage/updatecostate")
    public ResponseResult updateCommodityState(int commodityId){
        boolean result = commodityService.updateCoState(commodityId);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }
}
