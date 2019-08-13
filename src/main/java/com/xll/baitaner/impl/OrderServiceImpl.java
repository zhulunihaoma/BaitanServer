package com.xll.baitaner.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xll.baitaner.entity.*;
import com.xll.baitaner.entity.VO.ActivityRecordVO;
import com.xll.baitaner.entity.VO.OrderDetailsResultVO;
import com.xll.baitaner.entity.VO.OrderDetailsVO;
import com.xll.baitaner.entity.VO.ShopOrderVO;
import com.xll.baitaner.mapper.CommodityMapper;
import com.xll.baitaner.mapper.HistoryOrderMapper;
import com.xll.baitaner.mapper.OrderCommodityMapper;
import com.xll.baitaner.mapper.OrderMapper;
import com.xll.baitaner.mapper.ProfileMapper;
import com.xll.baitaner.mapper.ShopMapper;
import com.xll.baitaner.service.*;
import com.xll.baitaner.utils.LogUtils;
import com.xll.baitaner.utils.SerialUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述：订单管理service
 * 创建者：xie
 * 日期：2017/10/15
 **/

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final String TAG = "Baitaner-OrderService";

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private HistoryOrderMapper historyOrderMapper;

    @Resource
    private CommodityMapper commodityMapper;

    @Resource
    private SpecService specService;

    @Resource
    OrderCommodityMapper orderCommodityMapper;

    @Resource
    private CommodityService commodityService;

    @Resource
    private HistoryOrderService historyOrderService;

    @Resource
    private TemplateService templateService;

    @Resource
    private ActivityService activityService;

    @Resource
    ProfileMapper profileMapper;

    @Resource
    ShopMapper shopMapper;

    /**
     * 提交订单
     *
     * @param input
     * @return
     */
    @Override
    public Long submitOrder(ShopOrderVO input) {
        ShopOrder order = input.getOrder();
        List<OrderCommodity> commodityList = input.getCommodityList();
        Long orderId = SerialUtils.getSerialOrderId(order.getShopId());
        //生成订单编号
        order.setOrderId(orderId);
        //计算订单金额
        BigDecimal total = new BigDecimal("0.00");

        if (order.getActivityNot() == 1){
            //ActivityRecord活动订单
            ActivityRecordVO activityRecordVO = activityService.getActivityrecordById(Integer.parseInt(order.getActivityRecordId()));
            if (activityRecordVO == null)   return 0L;

            ActivityRecord activityRecord = activityRecordVO.getActivityRecord();
            if (activityRecord == null){
                LogUtils.error(TAG, "Activity order id " + orderId +" activityRecord id " + order.getActivityRecordId() + " is null!");
                return 0L;
            }
            Activity activity = activityRecordVO.getActivity();
            if (activity == null){
                LogUtils.error(TAG, "Activity order id " + orderId +" activity id " + activityRecord.getActivityId() + " is null!");
                return 0L;
            }
            // 判断ActivityRecord状态、Activity商品个数充足
            if (activityRecord.getRecordStatus() != 1 || activity.getStock() <= 0){ //判断活动是否达标以及活动商品数量是否大于0 可购买
                LogUtils.error(TAG, "Activity order id " + orderId + " activityRecord id " + activityRecord.getActivityId() +
                                " status is " + activityRecord.getRecordStatus()  + ", stock is " + activity.getStock() + " return!");
                return 0L;
            }

            BigDecimal money = new BigDecimal("0.00");
            money = new BigDecimal(activity.getActivityPrice());
            total = total.add(money);
        }else {
            for (OrderCommodity orderCommodity : commodityList) {
                //获取商品信息
                Commodity commodity = commodityService.getCommodity(orderCommodity.getCommodityId());
                BigDecimal money = new BigDecimal("0.00");
                //有规格
                if (orderCommodity.getSpecId() > 0) {
                    //获取商品规格
                    Spec spec = specService.getSpec(orderCommodity.getSpecId());
                    if (spec != null && spec.getCommodityId() == orderCommodity.getCommodityId()) {
                        money = new BigDecimal(spec.getPrice()).multiply(new BigDecimal(orderCommodity.getCount()));
                    }
                    else {
                        money = new BigDecimal(commodity.getPrice()).multiply(new BigDecimal(orderCommodity.getCount()));
                    }
                } else {
                    money = new BigDecimal(commodity.getPrice()).multiply(new BigDecimal(orderCommodity.getCount()));
                }
                total = total.add(money);
            }
        }

        //运费
        if (StringUtils.isNotBlank(order.getPostage())) {
            total = total.add(new BigDecimal(order.getPostage()));
        }
        order.setTotalMoney(total.toString());
        //订单状态
        order.setState(0);
        //下单
        boolean result = this.addOrder(order, commodityList);
        if (result) {
            //二维码支付订单下单成功后
            if (order.getPayType() == 1){
                //向商户发送 新订单通知模板消息
                templateService.sendNewOrderMessage(String.valueOf(orderId));
                //向用户发送 订单（二维码支付）待支付模板消息
                templateService.sendPendingPaymentMessage(String.valueOf(orderId));
            }

            //活动订单 修改活动记录为已下单
            if (order.getActivityNot() == 1){
                activityService.changeRecordstatus(2, Integer.parseInt(order.getActivityRecordId()));
            }

            return orderId;
        }
        return 0L;
    }

    /**
     * 下单处理，新增订单及订单商品数据
     *
     * @param order
     * @param list
     * @return
     */
    @Transactional
    @Override
    public boolean addOrder(ShopOrder order, List<OrderCommodity> list) {
        //插入订单数据
        int re = orderMapper.insertShopOrder(order);
        boolean result = false;
        if (re > 0) {
            //插入订单商品
            Long orderId = order.getOrderId();
            for (OrderCommodity oc : list) {
                Commodity commodity = commodityMapper.selectCommodity(oc.getCommodityId());
                oc.setOrderId(orderId);
                oc.setUnitPrice(commodity.getPrice());
                oc.setName(commodity.getName());
                oc.setPictUrl(commodity.getPictUrl());
                oc.setIntroduction(commodity.getIntroduction());
                oc.setSpecName("");
                oc.setSpecPrice("0.00");
                //oc.setSpecId(0); //SpecId设成0??  下面逻辑永远不走了
                //插入商品规格
                if (oc.getSpecId() > 0) {
                    Spec spec = specService.getSpec(oc.getSpecId());
                    if (spec != null && spec.getCommodityId() == oc.getCommodityId()) {
                        oc.setSpecId(spec.getId());
                        oc.setSpecName(spec.getName());
                        oc.setSpecPrice(spec.getPrice());
                    }
                }
                result = orderCommodityMapper.insertOrderList(oc) > 0;
                if (!result) {
                    log.error("下单存入商品失败");
                    throw new RuntimeException();
                }
            }
        } else throw new RuntimeException();
        return result;
    }

    /**
     * 获取单个订单数据,包含订单中商品详情
     *
     * @param orderId
     * @return
     */
    @Transactional
    @Override
    public OrderDetailsVO getOrderDetails(String orderId) {
        OrderDetailsVO details = new OrderDetailsVO();
        if (StringUtils.isBlank(orderId)) {
            return details;
        }
        //订单信息
        ShopOrder order = orderMapper.selectShopOrderByOrderId(Long.valueOf(orderId));
        if (order == null) {
            return details;
        }
        details.setShopOrder(order);
        //获取收货地址
        if (order.getAddressId() > 0) {
            ReceiverAddress receiverAddress = profileMapper.selectAddress(order.getAddressId());
            details.setAddress(receiverAddress);
        }
        //店铺信息
        if (order.getShopId() > 0) {
            Shop shop = shopMapper.selectShopById(order.getShopId());
            details.setShop(shop);
        }
        //查询商品信息
        List<OrderCommodity> orderCoList = getOrderCoList(orderId);
        if (CollectionUtils.isNotEmpty(orderCoList)) {
            details.setCommoditys(orderCoList);
        }
        return details;
    }

    /**
     * 获取订单的商品详情
     *
     * @param orderId
     * @return
     */
    @Override
    public List<OrderCommodity> getOrderCoList(String orderId) {
        return orderCommodityMapper.selectOrderCommodityByOrderId(Long.valueOf(orderId));
    }

    /**
     * 获取用户的订单列表
     *
     * @param openId
     * @return
     */
    @Override
    public OrderDetailsResultVO getOrderListByUser(String openId, Integer offset, Integer size) {
        OrderDetailsResultVO resultVO = new OrderDetailsResultVO();
        Page<ShopOrder> page =
                PageHelper.startPage(offset,size).doSelectPage(()->orderMapper.selectOrdersByOpenId(openId));
        List<ShopOrder> orderList = page.getResult();
        List<OrderDetailsVO> details = new ArrayList<>();
        for (ShopOrder order : orderList) {
            OrderDetailsVO orderDetails = this.getOrderDetails(order.getOrderId().toString());
            details.add(orderDetails);
        }
        resultVO.setData(details);
        resultVO.setCount(page.getTotal());
        return resultVO;
    }

    /**
     * 获取店铺的未付款订单 （二维码支付的订单）
     *
     * @param shopId
     * @return
     */
    @Override
    public OrderDetailsResultVO getNoPayOrderListByShop(int shopId, Integer offset, Integer size) {
        OrderDetailsResultVO resultVO = new OrderDetailsResultVO();
        Page<ShopOrder> page = PageHelper.startPage(offset,size).doSelectPage(()->orderMapper.selectNoPayOrdersByShopId(shopId));
        List<ShopOrder> orderList = page.getResult();
        List<OrderDetailsVO> details = new ArrayList<>();
        for (ShopOrder order : orderList) {
            OrderDetailsVO orderDetails = this.getOrderDetails(order.getOrderId().toString());
            details.add(orderDetails);
        }
        resultVO.setData(details);
        resultVO.setCount(page.getTotal());
        return resultVO;
    }

    /**
     * 获取店铺待完成订单
     *
     * @param shopId
     * @return
     */
    @Override
    public OrderDetailsResultVO getReadyOrderLisetShop(int shopId, Integer offset, Integer size) {
        //state 0：待支付;  1：已接单;  2：待完成; 3：已完成
        Page<ShopOrder> page =
                PageHelper.startPage(offset,size).doSelectPage(()->orderMapper.selectShopOrdersByShop(shopId, 2));
        List<ShopOrder> orderList = page.getResult();
        List<OrderDetailsVO> details = new ArrayList<>();
        for (ShopOrder order : orderList) {
            OrderDetailsVO orderDetails = this.getOrderDetails(order.getOrderId().toString());
            details.add(orderDetails);
        }
        OrderDetailsResultVO resultVO = new OrderDetailsResultVO();
        resultVO.setData(details);
        resultVO.setCount(page.getTotal());
        return resultVO;
    }

    /**
     * 获取店铺的已接订单列表(按订单分类)
     *
     * @param shopId
     * @return
     */
    @Override
    public OrderDetailsResultVO getTakenOrderListByShop(int shopId, Integer offset, Integer size) {
        //state 0：待支付;  1：已接单;  2：待完成; 3：已完成
        Page<ShopOrder> page =
                PageHelper.startPage(offset,size).doSelectPage(()->orderMapper.selectShopOrdersByShop(shopId, 1));
        List<ShopOrder> orderList = page.getResult();
        List<OrderDetailsVO> details = new ArrayList<>();
        for (ShopOrder order : orderList) {
            OrderDetailsVO orderDetails = this.getOrderDetails(order.getOrderId().toString());
            details.add(orderDetails);
        }
        OrderDetailsResultVO resultVO = new OrderDetailsResultVO();
        resultVO.setData(details);
        resultVO.setCount(page.getTotal());
        return resultVO;
    }


    /**
     * 获取店铺全部的商品订单关系数据列表，并插入订单实体类
     *
     * @return
     */
    private List<OrderCommodity> getAllOrderCoList(int shopId) {
        List<OrderCommodity> orderCommodityList = orderMapper.selectAllOrderCoList(shopId);
        for (OrderCommodity orderCommodity : orderCommodityList) {
            ShopOrder order = orderMapper.selectShopOrderByOrderId(orderCommodity.getOrderId());
            orderCommodity.setShopOrder(order);
        }
        return orderCommodityList;
    }

    /**
     * 获取店铺的已接订单列表(按商品分类)
     *
     * @param shopId
     * @return
     */
    @Transactional
    @Override
    public List<CommodityOrder> getCommdityListByShop(int shopId) {
        List<OrderCommodity> orderCommodityList = getAllOrderCoList(shopId);
        if (orderCommodityList.size() > 0) {
            Map<Integer, CommodityOrder> map = new HashMap<>();
            for (OrderCommodity orderCommodity : orderCommodityList) {
                int coId = orderCommodity.getCommodityId();
                if (!map.containsKey(coId)) {
                    CommodityOrder commodityOrder = new CommodityOrder();
                    commodityOrder.setCommodityId(coId);

                    //商品详情数据直接从OrderCommodity中获取，防止商品被删除后无数据
                    Commodity commodity = new Commodity();
                    commodity.setId(coId);
                    commodity.setName(orderCommodity.getName());
                    commodity.setPrice(orderCommodity.getUnitPrice());
                    commodity.setMonthlySales(orderCommodity.getMonthlySales());
                    //commodity.setPraise(orderCommodity.getPraise());
                    commodity.setPictUrl(orderCommodity.getPictUrl());
                    commodity.setIntroduction(orderCommodity.getIntroduction());
                    commodityOrder.setCommodity(commodity);
                    commodityOrder.setTotalNum(orderMapper.sumCoCount(coId));
                    List<OrderCommodity> list = new ArrayList<>();
                    list.add(orderCommodity);
                    commodityOrder.setOrderCommodityList(list);
                    map.put(coId, commodityOrder);
                } else {
                    map.get(coId).getOrderCommodityList().add(orderCommodity);
                }
            }

            return new ArrayList<>(map.values());
        } else {
            return null;
        }
    }

    /**
     * 更新订单状态
     *
     * @param orderId
     * @param state   0：待支付;  1：已接单;  2：待完成; 3：已完成
     * @return
     */
    @Transactional
    @Override
    public boolean updateOrderState(String orderId, int state) {
        if (StringUtils.isBlank(orderId)) {
            return false;
        }
        boolean result = orderMapper.updateOrderState(Long.valueOf(orderId), state) > 0;
        if (result) {
            if (state == 1) {
                //活动订单支付成功后 修改活动记录为已支付
                ShopOrder order = orderMapper.selectShopOrderByOrderId(Long.valueOf(orderId));
                if (order.getActivityNot() == 1){
                    activityService.changeRecordstatus(3, Integer.parseInt(order.getActivityRecordId()));
                }

                //state更新为1：已接单 创建历史订单
                if (historyOrderService.creatHistoryOrder(orderId)) {
                    return true;
                } else throw new RuntimeException();
            } else if (state > 1) {
                //更新历史订单状态
                return historyOrderMapper.updateHistoryorderState(Long.valueOf(orderId), state) > 0;
            }
        }
        return false;
    }

    /**
     * 暂时勿删，暂时勿删，暂时勿删
     * 获取店铺除了未支付外的所有订单按照日期和支付方式查询(payType = -1查询所有支付方式订单)
     * 暂时勿删，暂时勿删，暂时勿删
     * @param shopId
     * @param pageable
     * @return
     */
//    private PageImpl<Order> selectOrdersByShopAndPayTypeAndDate(int shopId, int payType, Date date, Pageable pageable) {
//        //state 0：待支付;  1：已接单;  2：待完成; 3：已完成
//        List<Order> orderList;
//        int count;
//        if (payType == -1) {
//            orderList = orderMapper.selectOrdersByShopAllAndDate(shopId, date, pageable);
//            count = orderMapper.countOrdersByShopAllAndDate(shopId, date);
//
//        } else {
//            orderList = orderMapper.selectOrdersByShopAndPayTypeAndDate(shopId, payType, date, pageable);
//            count = orderMapper.countOrdersByShopAndPayTypeAndDate(shopId, payType, date);
//
//        }
//
//        for (Order order : orderList) {
//            order.setOrderCoList(getOrderCoList(order.getOrderId().toString()));
//        }
//
//        return new PageImpl<>(orderList, pageable, count);
//    }


    /**
     * 删除订单（二维码订单且未支付的）
     *
     * @param orderId
     * @return
     */
    @Override
    public boolean deleteOrder(String orderId) {
        if (StringUtils.isBlank(orderId)) {
            return false;
        }
        return orderMapper.deleteOrder(Long.valueOf(orderId)) > 0;
    }

}
