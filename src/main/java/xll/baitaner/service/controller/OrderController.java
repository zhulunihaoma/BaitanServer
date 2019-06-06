package xll.baitaner.service.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import xll.baitaner.service.entity.Commodity;
import xll.baitaner.service.entity.Order;
import xll.baitaner.service.entity.OrderCommodity;
import xll.baitaner.service.entity.Spec;
import xll.baitaner.service.service.CommodityService;
import xll.baitaner.service.service.OrderService;
import xll.baitaner.service.service.SpecService;
import xll.baitaner.service.utils.LogUtils;
import xll.baitaner.service.utils.ResponseResult;
import xll.baitaner.service.utils.SerialUtils;

import java.util.Date;
import java.util.List;

/**
 * 描述：订单模块controller
 * 创建者：xie
 * 日期：2019
 **/
@Api(value = "订单模块controller", description = "订单模块接口 包括下单 订单管理等")
@RestController
public class OrderController {

    private String TAG = "Baitaner-OrderController";

    @Autowired
    private OrderService orderService;

    @Autowired
    private CommodityService commodityService;

    @Autowired
    private SpecService specService;

    /**
     * 提交订单接口
     * @param order
     * @param orderCoListStr
     * @return
     */
    @ApiOperation(
            value = "提交订单接口",
            httpMethod = "POST",
            notes = "提交订单接口，" +
                    "orderCoListStr字段说明实例：\n" +
                    "订单中商品详情，数组，已string方式传递\n" +
                    "orderCoList:[\n" +
                    "      { commodityId: 1, count: 2, specId: 1},\n" +
                    "      { commodityId: 3, count: 1, specId: 2 },\n" +
                    "      { commodityId: 4, count: 4, specId: 3 },\n" +
                    "]\n" +
                    "orderCoListStr = JSON.stringify(that.data.orderCoList); //需要转化成字符串提交服务器")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "order", value = "订单实体类", required = true, dataType = "Commodity"),
            @ApiImplicitParam(name = "orderCoListStr", value = "订单中商品详情 数组", required = true, dataType = "OrderCommodity")
    })
    @PostMapping("ordersubmit")
    public ResponseResult submitOrder(Order order, String orderCoListStr){
        List<OrderCommodity> orderCoList = (List<OrderCommodity>) JSONArray.toCollection(JSONArray.fromObject(orderCoListStr), OrderCommodity.class);

        String id = SerialUtils.getSerialId();
        //生成订单编号
        order.setOrderId(id);

        //生成下单时间
        order.setDate(new Date(System.currentTimeMillis()));

        //计算订单金额
        float total = 0;
        for (OrderCommodity orderCommodity : orderCoList){
            float money = 0;
            Commodity commodity = commodityService.getCommodity(orderCommodity.getCommodityId());
            if (orderCommodity.getSpecId() >= 0){
                Spec spec = specService.getSpec(orderCommodity.getSpecId());
                if (spec != null && spec.getCommodityId() == orderCommodity.getCommodityId()){
                    money = spec.getPrice() * orderCommodity.getCount();
                    total += money;
                    continue;
                }
                else {
                    LogUtils.error(TAG, "Order " + id + " OrderCommodity list commodityId: " +
                            orderCommodity.getCommodityId() + " specId "+ orderCommodity.getSpecId() + " is unavailable_1");
                }
            }else {
                LogUtils.error(TAG, "Order " + id + " OrderCommodity list commodityId: " +
                        orderCommodity.getCommodityId() + " specId "+ "orderCommodity.getSpecId()" + " is unavailable_2");
            }

            money = commodity.getPrice() * orderCommodity.getCount();
            total += money;
        }
        order.setTotalMoney(total);

        //订单状态
        order.setState(0);

        boolean result = orderService.addOrder(order, orderCoList);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", result ? id : null);
    }

    /**
     * 获取单个订单详情接口
     * @param orderId
     * @return
     */
    @ApiOperation(
            value = "获取单个订单详情接口",
            httpMethod = "GET",
            notes = "获取单个订单详情接口, OrderCoList包含订单中的商品信息")
    @ApiImplicitParam(name = "orderId", value = "订单编号", required = true, dataType = "String")
    @GetMapping("getorder")
    public ResponseResult getOrder(String orderId){
        return ResponseResult.result(0, "success", orderService.getOrder(orderId));
    }

    /**
     * 获取用户订单列表接口
     * @param clientId
     * @return
     */
    @ApiOperation(
            value = "获取用户订单列表接口",
            httpMethod = "GET",
            notes = "获取用户订单列表接口, 分页显示")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "openId", value = "用户openId", required = true, dataType = "String"),
            @ApiImplicitParam(name = "page", value = "请求页码，从0开始计数", required = true, dataType = "int"),
            @ApiImplicitParam(name = "size", value = "请求每页数据的个数", required = true, dataType = "int")
    })
    @GetMapping("getuserorders")
    public ResponseResult getOrderListClient(String openId, Pageable pageable){
        return ResponseResult.result(0, "success", orderService.getOrderListByUser(openId, pageable));
    }

    /**
     * 更新订单状态
     * @param orderId
     * @param state
     * @return
     */
    @GetMapping("ordermanage/updateorderstate")
    public ResponseResult updateOrderState(String orderId, int state){
        boolean result = orderService.updateOrderState(orderId,state);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail",null);
    }

    /**
     * 获取店铺的已结订单列表（按订单分类）
     * @param shopId
     * @param pageable
     * @return
     */
    @GetMapping("ordermanage/getshoporders")
    public ResponseResult getOrderListShop(int shopId, Pageable pageable){
        return ResponseResult.result(0, "success", orderService.getOrderListByShop(shopId, pageable));
    }

    /**
     * 获取店铺的已接订单列表(按商品分类)
     * @param shopId
     * @return
     */
    @GetMapping("ordermanage/getshoporders2")
    public ResponseResult getCoListShop(int shopId){
        return ResponseResult.result(0, "success", orderService.getCommdityListByShop(shopId));
    }

    /**
     * 获取店铺历史订单列表
     * @param shopId
     * @param pageable
     * @return
     */
    @GetMapping("ordermanage/gethistoryorders")
    public ResponseResult getHistoryOrderList(int shopId, Pageable pageable){
        return ResponseResult.result(0, "success", orderService.getHistoryOrderList(shopId, pageable));
    }
}
