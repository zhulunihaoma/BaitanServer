package com.xll.baitaner.controller;


import com.xll.baitaner.entity.VO.HistoryOrderVO;
import com.xll.baitaner.entity.VO.ShopOrderVO;
import com.xll.baitaner.service.HistoryOrderService;
import com.xll.baitaner.service.OrderService;
import com.xll.baitaner.utils.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 描述：订单模块controller
 * 创建者：xie
 * 日期：2019
 **/
@Api(value = "订单模块controller", description = "订单模块接口 包括下单 订单管理等")
@RestController
public class OrderController {

    private final String TAG = "Baitaner-OrderController";

    @Resource
    private OrderService orderService;

    @Resource
    private HistoryOrderService historyOrderService;

    /**
     * 提交订单接口
     *
     * @param input
     * @return
     */
//    @ApiOperation(
//            value = "提交订单接口",
//            httpMethod = "POST",
//            notes = "提交订单接口，" +
//                    "orderCoListStr字段说明实例：\n" +
//                    "订单中商品详情，数组，已string方式传递\n" +
//                    "orderCoList:[\n" +
//                    "      { commodityId: 1, count: 2, specId: 1},\n" +
//                    "      { commodityId: 3, count: 1, specId: 2 },\n" +
//                    "      { commodityId: 4, count: 4, specId: 3 },\n" +
//                    "]\n" +
//                    "orderCoListStr = JSON.stringify(that.data.orderCoList); //需要转化成字符串提交服务器")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "order", value = "订单实体类", required = true, dataType = "Commodity"),
//            @ApiImplicitParam(name = "orderCoListStr", value = "订单中商品详情 数组", required = true, dataType = "OrderCommodity")
//    })
//    @PostMapping("ordersubmit")
//    public ResponseResult submitOrder(Order order, String orderCoListStr) {
//        List<OrderCommodity> orderCoList = (List<OrderCommodity>) JSONArray.toCollection(JSONArray.fromObject(orderCoListStr), OrderCommodity.class);
//
//        String id = SerialUtils.getSerialId();
//        //生成订单编号
//        order.setOrderId(id);
//
//        //生成下单时间
//        order.setDate(new Date(System.currentTimeMillis()));
//
//        //计算订单金额
//        float total = 0;
//        for (OrderCommodity orderCommodity : orderCoList) {
//            float money = 0;
//            Commodity commodity = commodityService.getCommodity(orderCommodity.getCommodityId());
//            if (orderCommodity.getSpecId() >= 0) {
//                Spec spec = specService.getSpec(orderCommodity.getSpecId());
//                if (spec != null && spec.getCommodityId() != orderCommodity.getCommodityId()) {
//                    money = spec.getPrice() * orderCommodity.getCount();
//                    total += money;
//                    continue;
//                } else {
//                    LogUtils.error(TAG, "Order " + id + " OrderCommodity list commodityId: " +
//                            orderCommodity.getCommodityId() + " specId " + orderCommodity.getSpecId() + " is unavailable_1");
//                }
//            } else {
//                LogUtils.error(TAG, "Order " + id + " OrderCommodity list commodityId: " +
//                        orderCommodity.getCommodityId() + " specId " + orderCommodity.getSpecId() + " < 0");
//            }
//
//            money = commodity.getPrice() * orderCommodity.getCount();
//            total += money;
//        }
//        order.setTotalMoney(total);
//
//        //订单状态
//        order.setState(0);
//
//        boolean result = orderService.addOrder(order, orderCoList);
//        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", result ? id : null);
//    }
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
            @ApiImplicitParam(name = "input", value = "订单实体类", required = true, dataType = "ShopOrderVO"),
    })
    @PostMapping("ordersubmit")
    public ResponseResult submitOrder(@RequestBody ShopOrderVO input) {
            return orderService.submitOrder(input);
    }

    /**
     * 获取单个订单详情接口
     *
     * @param orderId
     * @return
     */
    @ApiOperation(
            value = "获取单个订单详情接口",
            httpMethod = "GET",
            notes = "获取单个订单详情接口, OrderCoList包含订单中的商品信息")
    @ApiImplicitParam(name = "orderId", value = "订单编号", required = true, dataType = "String")
    @GetMapping("getorder")
    public ResponseResult getOrder(String orderId) {
        return ResponseResult.result(0, "success", orderService.getOrderDetails(orderId));
    }

    /**
     * 获取用户订单列表接口
     *
     * @param openId
     * @return
     */
    @ApiOperation(
            value = "获取用户订单列表接口",
            httpMethod = "GET",
            notes = "获取用户订单列表接口, 分页显示")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "openId", value = "用户openId", required = true, dataType = "String"),
            @ApiImplicitParam(name = "offset", value = "请求页码，从0开始计数", required = true, dataType = "int"),
            @ApiImplicitParam(name = "size", value = "请求每页数据的个数", required = true, dataType = "int")
    })
    @GetMapping("getuserorders")
    public ResponseResult getOrderListClient(String openId, Integer offset, Integer size) {
        return ResponseResult.result(0, "success", orderService.getOrderListByUser(openId, offset, size));
    }

    /**
     * 获取店铺的未付款订单 （二维码支付的订单）
     *
     * @param shopId
     * @return
     */
    @ApiOperation(
            value = "获取店铺的未付款订单",
            httpMethod = "GET",
            notes = "获取店铺的未付款订单 （二维码支付的订单）, 分页显示")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int"),
            @ApiImplicitParam(name = "offset", value = "请求页码，从0开始计数", required = true, dataType = "int"),
            @ApiImplicitParam(name = "size", value = "请求每页数据的个数", required = true, dataType = "int")
    })
    @GetMapping("getshopnopayorders")
    public ResponseResult getNoPayOrderLisetShop(int shopId, Integer offset, Integer size) {
        return ResponseResult.result(0, "success", orderService.getNoPayOrderListByShop(shopId, offset, size));
    }

    /**
     * 获取店铺的已取消订单 （二维码支付的订单）
     *
     * @param shopId
     * @return
     */
    @ApiOperation(
            value = "获取店铺的已取消订单",
            httpMethod = "GET",
            notes = "获取店铺的已取消订单 （二维码支付的订单）, 分页显示")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int"),
            @ApiImplicitParam(name = "offset", value = "请求页码，从0开始计数", required = true, dataType = "int"),
            @ApiImplicitParam(name = "size", value = "请求每页数据的个数", required = true, dataType = "int")
    })
    @GetMapping("getshopcancelledorders")
    public ResponseResult getCancelledOrderListByShop(int shopId, Integer offset, Integer size) {
        return ResponseResult.result(0, "success", orderService.getCancelledOrderListByShop(shopId, offset, size));
    }

    /**
     * 更新订单状态
     *
     * @param orderId
     * @param state
     * @return
     */
    @ApiOperation(
            value = "更新订单状态",
            httpMethod = "GET",
            notes = "更新订单状态, 订单状态  -1: 已取消;  0：待支付;  1：已接单;  2：待完成; 3：已完成, 只有待支付订单可以取消")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单编号", required = true, dataType = "String"),
            @ApiImplicitParam(name = "state", value = "订单状态  -1: 已取消;  0：待支付;  1：已接单;  2：待完成; 3：已完成", required = true, dataType = "int")
    })
    @GetMapping("updateorderstate")
    public ResponseResult updateOrderState(String orderId, int state) {
        boolean result = orderService.updateOrderState(orderId, state);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 获取店铺的已接单列表（按订单分类）
     *
     * @param shopId
     * @return
     */
    @ApiOperation(
            value = "获取店铺的已接单列表（按订单分类）",
            httpMethod = "GET",
            notes = "获取店铺的已接单列表（按订单分类）, 分页显示")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int"),
            @ApiImplicitParam(name = "offset", value = "请求页码，从0开始计数", required = true, dataType = "int"),
            @ApiImplicitParam(name = "size", value = "请求每页数据的个数", required = true, dataType = "int")
    })
    @GetMapping("getshoptakenorders")
    public ResponseResult getTakenOrderListShop(int shopId, Integer offset, Integer size) {
        return ResponseResult.result(0, "success", orderService.getTakenOrderListByShop(shopId, offset, size));
    }

    /**
     * 获取店铺的已接单列表(按商品分类)
     *
     * @param shopId
     * @return
     */
    @ApiOperation(
            value = "获取店铺的已接单列表(按商品分类)",
            httpMethod = "GET",
            notes = "获取店铺的已接单列表(按商品分类), 分页显示")
    @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int")
    @GetMapping("getshoptakenordersbyco")
    public ResponseResult getCoListShop(int shopId) {
        return ResponseResult.result(0, "success", orderService.getCommdityListByShop(shopId));
    }

    /**
     * 获取店铺待完成订单
     *
     * @param shopId
     * @return
     */
    @ApiOperation(
            value = "获取店铺待完成订单",
            httpMethod = "GET",
            notes = "获取店铺待完成订单, 分页显示")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int"),
            @ApiImplicitParam(name = "offset", value = "请求页码，从0开始计数", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "size", value = "请求每页数据的个数", required = true, dataType = "Integer")
    })
    @GetMapping("getshopreadyorders")
    public ResponseResult getReadyOrderLisetShop(int shopId, Integer offset, Integer size) {
        return ResponseResult.result(0, "success", orderService.getReadyOrderLisetShop(shopId, offset, size));
    }

    /**
     * 删除订单（限制只能删除已取消订单）
     *
     * @param orderId
     * @return
     */
    @ApiOperation(
            value = "删除订单（限制只能删除已取消订单）",
            httpMethod = "GET",
            notes = "除已取消订单接口 买家卖家通用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单编号", required = true, dataType = "String")})
    @GetMapping("deleteorder")
    public ResponseResult deleteOrder(String orderId) {
        boolean result = orderService.deleteOrder(orderId);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", result);
    }

    /*******************历史订单相关********************?

    /**
     * 获取店铺订单管理 历史订单列表
     *
     * @param shopId
     * @return
     */
    @ApiOperation(
            value = "获取店铺历史订单列表",
            httpMethod = "GET",
            notes = "获取店铺历史订单列表 (已完成), 分页显示")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int"),
            @ApiImplicitParam(name = "offset", value = "请求页码，从0开始计数", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "size", value = "请求每页数据的个数", required = true, dataType = "Integer")
    })
    @GetMapping("gethistoryorders")
    public ResponseResult getHistoryOrderList(int shopId, Integer offset, Integer size) {
        return ResponseResult.result(0, "success", historyOrderService.getHistoryOrderList(shopId, 3, offset, size));
    }

    /**
     * 获取店铺经营数据中各类型的历史订单列表
     *
     * @param shopId
     * @param type
     * @return
     */
    @ApiOperation(
            value = "获取店铺经营数据 中各类型的历史订单列表 (分页显示)",
            httpMethod = "GET",
            notes = "type类型定义 0: 全部已付款订单, 1: 在线付款订单, 2: 二维码付款订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int"),
            @ApiImplicitParam(name = "type", value = "经营数据下订单的type", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "offset", value = "请求页码，从0开始计数", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "size", value = "请求每页数据的个数", required = true, dataType = "Integer")
    })
    @GetMapping("getstatisticsorders")
    public ResponseResult getStatisticsHistoryOrderList(int shopId, int type, Integer offset, Integer size) {
        return ResponseResult.result(0, "success", historyOrderService.getHistoryOrderList(shopId, type, offset, size));
    }

    /**
     * 根据日期查找店铺订单管理中历史订单
     *
     * @param shopId
     * @return
     */
    @ApiOperation(
            value = "根据日期查找店铺订单管理中历史订单",
            httpMethod = "GET",
            notes = "根据日期查找店铺订单管理中历史订单 date日期参数格式：yyyy-MM-dd")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int"),
            @ApiImplicitParam(name = "date", value = "日期 yyyy-MM-dd", required = true, dataType = "String")
    })
    @GetMapping("gethistoryorderbydate")
    public ResponseResult getHistoryOrderByDate(int shopId, String date) {
        return ResponseResult.result(0, "success", historyOrderService.getHistoryOrderByDate(shopId, 3, date));
    }

    /**
     * 根据日期查找店铺经营数据中各类型的历史订单
     *
     * @param shopId
     * @param type
     * @return
     */
    @ApiOperation(
            value = "根据日期查找店铺经营数据中各类型的历史订单",
            httpMethod = "GET",
            notes = "date日期参数格式：yyyy-MM-dd;  type类型定义 0: 全部已付款订单, 1: 在线付款订单, 2: 二维码付款订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺shopId", required = true, dataType = "int"),
            @ApiImplicitParam(name = "type", value = "经营数据下订单的type", required = true, dataType = "int"),
            @ApiImplicitParam(name = "date", value = "日期 yyyy-MM-dd", required = true, dataType = "String")
    })
    @GetMapping("getstatisticsorderbydate")
    public ResponseResult getStatisticsHistoryOrderByDate(int shopId, int type, String date) {
        return ResponseResult.result(0, "success", historyOrderService.getHistoryOrderByDate(shopId, type, date));
    }

}
