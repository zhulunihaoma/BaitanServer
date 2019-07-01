package com.xll.baitaner.impl;

import com.xll.baitaner.entity.Commodity;
import com.xll.baitaner.entity.CommodityOrder;
import com.xll.baitaner.entity.HistoryOrder;
import com.xll.baitaner.entity.Order;
import com.xll.baitaner.entity.OrderCommodity;
import com.xll.baitaner.entity.ReceiverAddress;
import com.xll.baitaner.entity.Shop;
import com.xll.baitaner.entity.ShopOrder;
import com.xll.baitaner.entity.Spec;
import com.xll.baitaner.entity.VO.OrderDetailsVO;
import com.xll.baitaner.entity.VO.ShopOrderVO;
import com.xll.baitaner.mapper.CommodityMapper;
import com.xll.baitaner.mapper.OrderCommodityMapper;
import com.xll.baitaner.mapper.OrderMapper;
import com.xll.baitaner.mapper.ProfileMapper;
import com.xll.baitaner.mapper.ShopMapper;
import com.xll.baitaner.service.CommodityService;
import com.xll.baitaner.service.OrderService;
import com.xll.baitaner.service.SpecService;
import com.xll.baitaner.utils.SerialUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private CommodityMapper commodityMapper;

    @Resource
    private SpecService specService;

    @Resource
    OrderCommodityMapper orderCommodityMapper;

    @Resource
    private CommodityService commodityService;

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
        for (OrderCommodity orderCommodity : commodityList) {
            //获取商品信息
            Commodity commodity = commodityService.getCommodity(orderCommodity.getCommodityId());
            BigDecimal money = new BigDecimal("0.00");
            //有规格
            if (orderCommodity.getSpecId() > 0) {
                //获取商品规格
                Spec spec = specService.getSpec(orderCommodity.getSpecId());
                if (spec != null && spec.getCommodityId() != orderCommodity.getCommodityId()) {
                    money = new BigDecimal(spec.getPrice()).multiply(new BigDecimal(orderCommodity.getCount()));
                }
            } else {
                money = new BigDecimal(commodity.getPrice()).multiply(new BigDecimal(orderCommodity.getCount()));
            }
            total = total.add(money);
        }
        //运费
        total = total.add(new BigDecimal(order.getPostage()));
        order.setTotalMoney(total.toString());
        //订单状态
        order.setState(0);
        //下单
        boolean result = this.addOrder(order, commodityList);
        if (result) {
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
                //插入商品规格
                if (oc.getSpecId() > 0) {
                    Spec spec = specService.getSpec(oc.getSpecId());
                    if (spec != null && spec.getCommodityId() == oc.getCommodityId()) {
                        oc.setOrderId(orderId);
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
    private List<OrderCommodity> getOrderCoList(String orderId) {
        return orderCommodityMapper.selectOrderCommodityByOrderId(Long.valueOf(orderId));
    }

    /**
     * 获取用户的订单列表
     *
     * @param openId
     * @param pageable
     * @return
     */
    @Override
    public PageImpl<OrderDetailsVO> getOrderListByUser(String openId, Pageable pageable) {
        List<ShopOrder> orderList = orderMapper.selectOrdersByOpenId(openId, pageable);
        List<OrderDetailsVO> details = new ArrayList<>();
        for (ShopOrder order : orderList) {
            OrderDetailsVO orderDetails = this.getOrderDetails(order.getOrderId().toString());
            details.add(orderDetails);
        }
        int count = orderMapper.countOrdersByClientId(openId);
        return new PageImpl<>(details, pageable, count);
    }

    /**
     * 获取店铺的未付款订单 （二维码支付的订单）
     *
     * @param shopId
     * @param pageable
     * @return
     */
    @Override
    public PageImpl<OrderDetailsVO> getNoPayOrderListByShop(int shopId, Pageable pageable) {
        List<ShopOrder> orderList = orderMapper.selectNoPayOrdersByShopId(shopId, pageable);
        List<OrderDetailsVO> details = new ArrayList<>();
        for (ShopOrder order : orderList) {
            OrderDetailsVO orderDetails = this.getOrderDetails(order.getOrderId().toString());
            details.add(orderDetails);
        }
        int count = orderMapper.countNoPayOrdersByShop(shopId);
        return new PageImpl<>(details, pageable, count);
    }

    /**
     * 获取店铺待完成订单
     *
     * @param shopId
     * @param pageable
     * @return
     */
    @Override
    public PageImpl<OrderDetailsVO> getReadyOrderLisetShop(int shopId, Pageable pageable) {
        //state 0：待支付;  1：已接单;  2：待完成; 3：已完成
        List<ShopOrder> orderList = orderMapper.selectShopOrdersByShop(shopId, 2, pageable);
        List<OrderDetailsVO> details = new ArrayList<>();
        for (ShopOrder order : orderList) {
            OrderDetailsVO orderDetails = this.getOrderDetails(order.getOrderId().toString());
            details.add(orderDetails);
        }
        int count = orderMapper.countOrdersByShop(shopId, 2);
        return new PageImpl<>(details, pageable, count);
    }

    /**
     * 获取店铺的已接订单列表(按订单分类)
     *
     * @param shopId
     * @param pageable
     * @return
     */
    @Override
    public PageImpl<OrderDetailsVO> getTakenOrderListByShop(int shopId, Pageable pageable) {
        //state 0：待支付;  1：已接单;  2：待完成; 3：已完成
        List<ShopOrder> orderList = orderMapper.selectShopOrdersByShop(shopId, 1, pageable);
        List<OrderDetailsVO> details = new ArrayList<>();
        for (ShopOrder order : orderList) {
            OrderDetailsVO orderDetails = this.getOrderDetails(order.getOrderId().toString());
            details.add(orderDetails);
        }
        int count = orderMapper.countOrdersByShop(shopId, 1);
        return new PageImpl<>(details, pageable, count);
    }


    /**
     * 获取店铺全部的商品订单关系数据列表，并插入订单实体类
     *
     * @return
     */
    private List<OrderCommodity> getAllOrderCoList(int shopId) {
        List<OrderCommodity> orderCommodityList = orderMapper.selectAllOrderCoList(shopId);
        for (OrderCommodity orderCommodity : orderCommodityList) {
            OrderDetailsVO order = this.getOrderDetails(orderCommodity.getOrderId().toString());
            orderCommodity.setOrderDetails(order);
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
                    List<OrderCommodity> list = map.get(coId).getOrderCommodityList();
                    list.add(orderCommodity);
                    map.get(coId).setOrderCommodityList(list);
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
                //state更新为1：已接单 创建历史订单
                if (this.creatHistoryOrder(orderId)) {
                    return true;
                } else throw new RuntimeException();
            } else if (state > 1) {
                //更新历史订单状态
                return orderMapper.updateHistoryorderState(orderId, state) > 0;
            }
        }
        return false;
    }

    /**
     * 生成历史订单流程
     *
     * @param orderId
     * @return
     */
    @Transactional
    public boolean creatHistoryOrder(String orderId) {
        ShopOrder order = orderMapper.selectShopOrderByOrderId(Long.valueOf(orderId));
        int shopId = order.getShopId();
        Date historyDate = order.getCreateDate();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String datetostr = formatter.format(historyDate);
        ParsePosition pos = new ParsePosition(0);
        Date date = formatter.parse(datetostr, pos);

        HistoryOrder ho = orderMapper.selectShopHistory(shopId, date);
        if (ho != null) {
            //已存在该日期,直接插订单

            //先判断是否有此历史订单，再插入
            if (orderMapper.selectCountHistoryOrder(ho.getId(), orderId) > 0) {
                return false;
            } else {
                return orderMapper.insertHistoryOrder(ho.getId(), orderId, order.getPayType(), order.getState()) > 0;
            }
        } else {
            HistoryOrder hon = new HistoryOrder();
            hon.setShopId(shopId);
            hon.setHistoryDate(date);
            int result = orderMapper.insertShopHistory(hon);
            if (result > 0) {
                int id = hon.getId();
                //先判断是否有此历史订单，再插入
                if (orderMapper.selectCountHistoryOrder(id, orderId) > 0) {
                    return false;
                } else {
                    return orderMapper.insertHistoryOrder(id, orderId, order.getPayType(), order.getState()) > 0;
                }
            } else {
                return false;
            }
        }
    }

    /**
     * 获取历史订单列表
     * <p>
     * 获取订单管理和经营数据中历史订单列表
     *
     * @param shopId
     * @param type     获取历史订单种类
     * @param pageable
     * @return
     */
    @Transactional
    @Override
    public PageImpl<HistoryOrder> getHistoryOrderList(int shopId, int type, Pageable pageable) {
        List<HistoryOrder> list = orderMapper.selectHistoryOrderList(shopId, pageable);
        if (list.size() > 0) {
            for (HistoryOrder ho : list) {
                List<Order> orderList;
                if (type == 0) { //经营数据中 全部已付款订单
                    orderList = orderMapper.selectDateOrderList(ho.getId());
                } else if (type == 1) { //经营数据中 在线付款订单
                    orderList = orderMapper.selectDateOrderListByPayType(ho.getId(), 0);
                } else if (type == 2) { //经营数据中 二维码付款订单
                    orderList = orderMapper.selectDateOrderListByPayType(ho.getId(), 1);
                } else { //订单管理模块的历史订单  已完成订单
                    orderList = orderMapper.selectDateOrderListByState(ho.getId(), 3);
                }
                for (Order order : orderList) {
                    order.setOrderCoList(getOrderCoList(order.getOrderId().toString()));
                }
                ho.setOrderList(orderList);
            }
        }

        int count = orderMapper.countHistoryOrderList(shopId);
        return new PageImpl<>(list, pageable, count);
    }

    /**
     * 获取店铺除了未支付外的所有订单按照日期和支付方式查询(payType = -1查询所有支付方式订单)
     *
     * @param shopId
     * @param pageable
     * @return
     */
    private PageImpl<Order> selectOrdersByShopAndPayTypeAndDate(int shopId, int payType, Date date, Pageable pageable) {
        //state 0：待支付;  1：已接单;  2：待完成; 3：已完成
        List<Order> orderList;
        int count;
        if (payType == -1) {
            orderList = orderMapper.selectOrdersByShopAllAndDate(shopId, date, pageable);
            count = orderMapper.countOrdersByShopAllAndDate(shopId, date);

        } else {
            orderList = orderMapper.selectOrdersByShopAndPayTypeAndDate(shopId, payType, date, pageable);
            count = orderMapper.countOrdersByShopAndPayTypeAndDate(shopId, payType, date);

        }

        for (Order order : orderList) {
            order.setOrderCoList(getOrderCoList(order.getOrderId().toString()));
        }

        return new PageImpl<>(orderList, pageable, count);
    }


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
