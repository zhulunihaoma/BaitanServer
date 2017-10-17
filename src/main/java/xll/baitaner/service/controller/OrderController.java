package xll.baitaner.service.controller;


import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import xll.baitaner.service.entity.Commodity;
import xll.baitaner.service.entity.Order;
import xll.baitaner.service.entity.OrderCommodity;
import xll.baitaner.service.service.CommodityService;
import xll.baitaner.service.service.OrderService;
import xll.baitaner.service.utils.ResponseResult;
import xll.baitaner.service.utils.SerialUtils;

import java.util.Date;
import java.util.List;

/**
 * 描述：
 * 创建者：xie
 * 日期：2017/10/15
 **/

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CommodityService commodityService;

    /**
     * 提交订单接口
     * @param order
     * @param orderCoListStr
     * @return
     */
    @RequestMapping("ordermanage/submit")
    public ResponseResult submitOrder(Order order, String orderCoListStr){
        List<OrderCommodity> orderCoList = (List<OrderCommodity>) JSONArray.toList(JSONArray.fromObject(orderCoListStr), OrderCommodity.class);

        String id = SerialUtils.getSerialId();
        //生成订单编号
        order.setOrderId(id);

        //生成下单时间
        order.setDate(new Date(System.currentTimeMillis()));

        //计算订单金额
        float total = 0;
        for (OrderCommodity orderCommodity : orderCoList){
            Commodity commodity = commodityService.getCommodity(orderCommodity.getCommodityId());
            float money = commodity.getPrice() * orderCommodity.getCount();
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
    @GetMapping("ordermanage/getorder")
    public ResponseResult getOrder(String orderId){
        return ResponseResult.result(0, "success", orderService.getOrder(orderId));
    }

    /**
     * 获取用户订单列表接口
     * @param clientId
     * @return
     */
    @GetMapping("ordermanage/getclientorders")
    public ResponseResult getOrderListClient(String clientId, Pageable pageable){
        return ResponseResult.result(0, "success", orderService.getOrderListByClient(clientId, pageable));
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
}
