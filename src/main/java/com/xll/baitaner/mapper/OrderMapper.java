package com.xll.baitaner.mapper;

import com.xll.baitaner.entity.OrderCommodity;
import com.xll.baitaner.entity.ShopOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 描述：订单管理mapper
 * 创建者：xie
 * 日期：2017/10/14
 **/

@Repository
public interface OrderMapper {

    String shopOrder = " `order_id`,`open_id`,`shop_id`,`address_id`,`create_date`,`pay_type`,`remarks`,`total_money`,`postage`,`state`," +
            "`activity_not`,`activity_id`,`del_flag` ";

    /**
     * 插入订单数据
     *
     * @param order
     * @return
     */
    @Insert("INSERT INTO `shop_order` (order_id,open_id,shop_id,address_id,pay_type,remarks,total_money,postage,state," +
            "activity_not,activity_id) " +
            "VALUES (#{order.orderId},#{order.openId},#{order.shopId},#{order.addressId},#{order.payType},#{order.remarks}," +
            "#{order.totalMoney},#{order.postage},#{order.state},#{order.activityNot},#{order.activityId})")
    int insertShopOrder(@Param("order") ShopOrder order);

    /**
     * 根据订单号查询订单信息
     *
     * @param orderId
     * @return
     */
    @Select("select" + shopOrder + "from `shop_order` where order_id=#{orderId} and del_flag=0")
    ShopOrder selectShopOrderByOrderId(@Param("orderId") Long orderId);

    /**
     * 查询对应用户的订单列表
     *
     * @param openId
     * @return
     */
    @Select("select" + shopOrder + "from `shop_order` where open_id=#{openId} and del_flag=0 " +
            "order by create_date desc")
    List<ShopOrder> selectOrdersByOpenId(@Param("openId") String openId);

    /**
     * 获取店铺的未付款订单 （二维码支付的订单）
     *
     * @param shopId
     * @return
     */
    @Select("SELECT" + shopOrder + "FROM `shop_order` WHERE shop_id = #{shopId} AND state = 0 AND pay_type = 1 " +
            "ORDER BY create_date DESC")
    List<ShopOrder> selectNoPayOrdersByShopId(@Param("shopId") int shopId);

    /**
     * 更新订单状态
     *
     * @param orderId
     * @param state   0：待支付;  1：已接单;  2：待完成; 3：已完成
     * @return
     */
    @Update("UPDATE `shop_order` SET state = #{state} WHERE order_id = #{orderId}")
    int updateOrderState(@Param("orderId") Long orderId, @Param("state") int state);

    /**
     * 查询店铺对应状态的订单列表
     *
     * @param shopId
     * @param state  0：待支付;  1：已接单;  2：待完成; 3：已完成
     * @return
     */
    @Select("SELECT" + shopOrder + "FROM `shop_order` WHERE shop_id = #{shopId} AND state = #{state} ORDER BY " +
            "create_date DESC")
    List<ShopOrder> selectShopOrdersByShop(@Param("shopId") int shopId, @Param("state") int state);

    /**
     * 获取店铺全部已接订单的商品详情
     *
     * @param shopId
     * @return
     */
    @Select("SELECT o.*, ra.name AS clientName, od.remarks AS orderRemarks FROM order_commodity o  " +
            "JOIN `shop_order` od ON od.order_id = o.order_id  " +
            "JOIN receiver_address ra ON od.address_id = ra.id  " +
            "WHERE od.state = 1 AND od.shop_id = 20")
    List<OrderCommodity> selectAllOrderCoList(@Param("shopId") int shopId);

    /**
     * 查询商品在所有已接订单中的总个数
     *
     * @param coId
     * @return
     */
    @Select("SELECT SUM(o.count) FROM order_commodity o " +
            "JOIN `shop_order` od ON od.order_id = o.order_id  " +
            "WHERE o.commodity_id = #{coId} AND od.state = 1")
    int sumCoCount(@Param("coId") int coId);

    /**
     * 删除订单（二维码订单且未支付的）
     *
     * @param orderId
     * @return
     */
    @Update("update `shop_order` set del_flag=1 where order_id=#{orderId}")
    int deleteOrder(@Param("orderId") Long orderId);
}
