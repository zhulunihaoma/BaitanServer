package xll.baitaner.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xll.baitaner.service.entity.Commodity;
import xll.baitaner.service.service.CommodityService;
import xll.baitaner.service.utils.ResponseResult;

/**
 * 描述：商品管理controller
 * 创建者：xie
 * 日期：2017/10/10
 **/
@RestController
public class CommodityController {

    @Autowired
    private CommodityService commodityService;

    /**
     * 获取店铺中所有商品列表
     * 上下架均显示
     * @param shopId
     * @return
     */
    @GetMapping("commoditymanage/getallcolist")
    public ResponseResult getAllCoList(int shopId){
        return ResponseResult.result(0, "success", commodityService.getAllCoList(shopId));
    }

    /**
     * 获取店铺已上架商品列表
     * @param shopId
     * @return
     */
    @GetMapping("commoditymanage/getcolist")
    public ResponseResult geCoList(int shopId){
        return ResponseResult.result(0, "success", commodityService.getCoList(shopId));
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
     * 新增商品接口
     * @param commodity
     * @return
     */
    @RequestMapping("commoditymanage/addcommodity")
    public ResponseResult addCommodity(Commodity commodity){
        boolean result = commodityService.addCommodity(commodity);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 修改商品信息接口
     * @param commodity
     * @return
     */
    @RequestMapping("commoditymanage/editcommodity")
    public ResponseResult editCommodity(Commodity commodity){
        boolean result = commodityService.updateCommodity(commodity);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 删除商品接口
     * @param commodity
     * @return
     */
    @RequestMapping("commoditymanage/deletecommodity")
    public ResponseResult deleteCommodity(int commodityId){
        boolean result = commodityService.deleteCommodity(commodityId);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 修改商品上下架状态接口
     * @param commodity
     * @return
     */
    @RequestMapping("commoditymanage/updatecostate")
    public ResponseResult updateCommodityState(int commodityId){
        boolean result = commodityService.updateCoState(commodityId);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }
}
