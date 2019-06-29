package com.xll.baitaner.mapper;

import com.xll.baitaner.entity.*;
import org.apache.ibatis.annotations.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 描述：订单管理mapper
 * 创建者：xie
 * 日期：2017/10/14
 **/

@Repository
public interface OrderMapper {

    /**
     * 插入订单
     *
     * @param order
     * @return
     */
    @Insert("INSERT INTO `order` (orderId,openId,shopId,date,payType,remarks,totalMoney,postage,state," +
            "name,sex,address,phone,isActivity,activityId) " +
            "VALUES (#{order.orderId},#{order.openId},#{order.shopId},#{order.date},#{order.payType},#{order.remarks}," +
            "#{order.totalMoney},#{order.postage},#{order.state},#{order.name},#{order.sex},#{order.address}," +
            "#{order.phone},#{order.isActivity},#{order.activityId})")
    int insertOrder(@Param("order") Order order);

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
    int insertOrder(@Param("order") ShopOrder order);

    /**
     * 插入订单商品列表 带spec规格
     *
     * @param co
     * @param count
     * @param orderId
     * @return
     */
    @Insert("INSERT INTO orderlist (commodityId,count,orderId,name,price,monthlySales,pictUrl,introduction,specId,specName,specPrice) " +
            "VALUES (#{co.id},#{count},#{orderId},#{co.name},#{co.price},#{co.monthlySales},#{co.pictUrl}," +
            "#{co.introduction},#{spec.id},#{spec.name},#{spec.price})")
    int insertOrderListSpec(@Param("co") Commodity co, @Param("count") int count,
                            @Param("orderId") String orderId, @Param("spec") Spec spec);

    /**
     * 插入订单商品列表
     *
     * @param co
     * @param count
     * @param orderId
     * @return
     */
    @Insert("INSERT INTO orderlist (commodityId,count,orderId,name,price,monthlySales,pictUrl,introduction) " +
            "VALUES (#{co.id},#{count},#{orderId},#{co.name},#{co.price},#{co.monthlySales},#{co.pictUrl}," +
            "#{co.introduction})")
    int insertOrderList(@Param("co") Commodity co, @Param("count") int count, @Param("orderId") String orderId);

    /**
     * 根据订单编号获取订单详情
     *
     * @param orderId
     * @return
     */
    @Select("SELECT o.*, s.shopName, s.shopLogoUrl FROM `order` o JOIN shop s ON o.shopId = s.id WHERE o.OrderId = #{orderId}")
    Order selectOrder(@Param("orderId") String orderId);

    /**
     * 查询对应用户的订单列表
     *
     * @param openId
     * @return
     */
    @Select("SELECT o.*, s.shopName, s.shopLogoUrl FROM `order` o JOIN shop s ON o.shopId = s.id " +
            "WHERE o.openId = #{openId} ORDER BY date DESC LIMIT #{page.offset},#{page.size}")
    List<Order> seleceOrdersByClientId(@Param("openId") String openId, @Param("page") Pageable page);

    /**
     * 查询对应用户的订单列表总个数
     *
     * @param openId
     * @return
     */
    @Select("SELECT COUNT(*) FROM `order` WHERE openId = #{openId}")
    int countOrdersByClientId(@Param("openId") String openId);

    /**
     * 根据订单编号获取订单商品详情
     *
     * @param orderId
     * @return
     */
    @Select("SELECT ol.*, o.name AS clientName, o.remarks AS orderRemarks FROM orderlist ol " +
            "JOIN `order` o ON o.orderId = ol.orderId WHERE ol.orderId = #{orderId}")
    List<OrderCommodity> selectOrderCoListByOrderId(@Param("orderId") String orderId);

    /**
     * 获取店铺的未付款订单 （二维码支付的订单）
     *
     * @param shopId
     * @param page
     * @return
     */
    @Select("SELECT * FROM `order` WHERE shopId = #{shopId} AND state = 0 AND payType = 1 " +
            "ORDER BY date DESC LIMIT #{page.offset},#{page.size} ")
    List<Order> selectNoPayOrdersByShop(@Param("shopId") int shopId, @Param("page") Pageable page);

    /**
     * 获取店铺的未付款订单 （二维码支付的订单） 总个数
     *
     * @param shopId
     * @return
     */
    @Select("SELECT COUNT(*) FROM `order` WHERE shopId = #{shopId} AND state = 0 AND payType = 1")
    int countNoPayOrdersByShop(@Param("shopId") int shopId);

    /**
     * 更新订单状态
     *
     * @param orderId
     * @param state   0：待支付;  1：已接单;  2：待完成; 3：已完成
     * @return
     */
    @Update("UPDATE `order` SET State = #{state} WHERE OrderId = #{orderId}")
    int updateOrderState(@Param("orderId") String orderId, @Param("state") int state);

    /**
     * 查询店铺对应状态的订单列表
     *
     * @param shopId
     * @param state  0：待支付;  1：已接单;  2：待完成; 3：已完成
     * @return
     */
    @Select("SELECT * FROM `order` WHERE shopId = #{shopId} AND state = #{state} ORDER BY date DESC " +
            "LIMIT #{page.offset},#{page.size}")
    List<Order> selectOrdersByShop(@Param("shopId") int shopId, @Param("state") int state, @Param("page") Pageable page);

    /**
     * 查询店铺所有已接以及历史订单列表
     *
     * @param shopId
     * @return
     */
    @Select("SELECT * FROM `order` WHERE shopId = #{shopId} AND state != 0 AND date = #{date} ORDER BY date DESC " +
            "LIMIT #{page.offset},#{page.size}")
    List<Order> selectOrdersByShopAllAndDate(@Param("shopId") int shopId, @Param("date") Date date, @Param("page") Pageable page);


    /**
     * 查询店铺所有已接不同支付方式订单列表
     *
     * @param shopId
     * @param payType 0：在线;  1：二维码;
     * @return
     */
    @Select("SELECT * FROM `order` WHERE shopId = #{shopId} AND state != 0 AND payType = #{payType} AND date = #{date} ORDER BY date DESC " +
            "LIMIT #{page.offset},#{page.size}")
    List<Order> selectOrdersByShopAndPayTypeAndDate(@Param("shopId") int shopId, @Param("payType") int payType, @Param("date") Date date, @Param("page") Pageable page);

    /**
     * 查询店铺对应状态的已接以及历史订单订单列表总个数
     *
     * @param shopId
     * @param state  0：待支付;  1：已接单;  2：待完成; 3：已完成
     * @return
     */
    @Select("SELECT COUNT(*) FROM `order` WHERE shopId = #{shopId} AND state = #{state}")
    int countOrdersByShop(@Param("shopId") int shopId, @Param("state") int state);


    /**
     * 查询店铺的订单列表总个数
     *
     * @param shopId
     * @return
     */
    @Select("SELECT COUNT(*) FROM `order` WHERE shopId = #{shopId} AND state != 0 AND date = #{date}")
    int countOrdersByShopAllAndDate(@Param("shopId") int shopId, @Param("date") Date date);


    /**
     * 查询店铺对应支付方式的已接以及历史订单列表总个数
     *
     * @param shopId
     * @param payType 0：在线支付  1：二维码支付
     * @return
     */
    @Select("SELECT COUNT(*) FROM `order` WHERE shopId = #{shopId} AND payType = #{payType} AND state != 0 AND date = #{date}")
    int countOrdersByShopAndPayTypeAndDate(@Param("shopId") int shopId, @Param("payType") int payType, @Param("date") Date date);


    /**
     * 获取店铺全部已接订单的商品详情
     *
     * @param shopId
     * @return
     */
    @Select("SELECT o.*, od.name AS clientName, od.remarks AS orderRemarks FROM orderlist o " +
            "JOIN `order` od ON od.orderId = o.orderId " +
            "WHERE od.state = 1 AND od.shopId = #{shopId}")
    List<OrderCommodity> selectAllOrderCoList(@Param("shopId") int shopId);

    /**
     * 查询商品在所有已接订单中的总个数
     *
     * @param coId
     * @return
     */
    @Select("SELECT SUM(o.Count) FROM orderlist o " +
            "JOIN `order` od ON od.orderId = o.orderId  " +
            "WHERE o.commodityId = #{coId} AND od.state = 1")
    int sumCoCount(@Param("coId") int coId);

    /**
     * 插入店铺及历史日期
     *
     * @param ho
     * @return
     */
    @Insert("INSERT INTO shophistory (shopId,historyDate) VALUES (#{ho.shopId},#{ho.historyDate})")
    @Options(useGeneratedKeys = true, keyProperty = "ho.id")
    int insertShopHistory(@Param("ho") HistoryOrder ho);

    /**
     * 查询shophistory是否已存在店铺对应的日期条目
     *
     * @param shopId
     * @param date
     * @return
     */
    @Select("SELECT * FROM shophistory WHERE shopId = #{shopId} AND historyDate = #{date}")
    HistoryOrder selectShopHistory(@Param("shopId") int shopId, @Param("date") Date date);

    /**
     * 查询店铺的历史订单实体类列表
     *
     * @param shopId
     * @param page
     * @return
     */
    @Select("SELECT * FROM shophistory WHERE ShopId = #{shopId} ORDER BY historyDate DESC LIMIT #{page.offset},#{page.size}")
    List<HistoryOrder> selectHistoryOrderList(@Param("shopId") int shopId, @Param("page") Pageable page);

    /**
     * 查询店铺的历史订单实体类列表总个数
     *
     * @param shopId
     * @return
     */
    @Select("SELECT COUNT(*) FROM shophistory WHERE shopId = #{shopId}")
    int countHistoryOrderList(@Param("shopId") int shopId);

    /**
     * 判断历史订单是否已存在
     *
     * @param historyId
     * @param orderId
     * @return
     */
    @Select("SELECT COUNT(*) FROM historyorder WHERE shopHistoryId = #{historyId} AND orderId = #{orderId}")
    int selectCountHistoryOrder(@Param("historyId") int historyId, @Param("orderId") String orderId);

    /**
     * 以shophistoryID为基准插入订单
     *
     * @param historyId
     * @param orderId
     * @return
     */
    @Insert("INSERT INTO historyorder (shopHistoryId,orderId,payType,state) VALUES (#{historyId},#{orderId},#{payType},#{state})")
    int insertHistoryOrder(@Param("historyId") int historyId, @Param("orderId") String orderId, @Param("payType") int payType, @Param("state") int state);

    /**
     * 从historyorder查询具体日期的历史订单列表
     *
     * @param historyId
     * @return
     */
    @Select("SELECT o.* FROM historyorder ho JOIN `order` o ON o.orderId = ho.orderId " +
            "WHERE ho.shopHistoryId = #{historyId} ORDER BY o.date DESC")
    List<Order> selectDateOrderList(@Param("historyId") int historyId);


    /**
     * 从historyorder查询具体日期的历史订单列表根据state
     *
     * @param historyId
     * @param state     0：待支付;  1：已接单;  2：待完成; 3：已完成
     * @return
     */
    @Select("SELECT o.* FROM historyorder ho JOIN `order` o ON o.orderId = ho.orderId " +
            "WHERE ho.shopHistoryId = #{historyId} AND ho.state = #{state} ORDER BY o.date DESC")
    List<Order> selectDateOrderListByState(@Param("historyId") int historyId, @Param("state") int state);


    /**
     * 从historyorder查询具体日期的历史订单列表根据PayType
     *
     * @param historyId
     * @param payType   0：在线支付  1：二维码支付
     * @return
     */
    @Select("SELECT o.* FROM historyorder ho JOIN `order` o ON o.orderId = ho.orderId " +
            "WHERE ho.shopHistoryId = #{historyId} AND ho.payType = #{payType} ORDER BY o.date DESC")
    List<Order> selectDateOrderListByPayType(@Param("historyId") int historyId, @Param("payType") int payType);

    /**
     * 更改historyorder 的state 通过orderId
     *
     * @param orderId
     * @param state   0：待支付;  1：已接单;  2：待完成; 3：已完成
     * @return
     */
    @Update("UPDATE `historyorder` SET State = #{state} WHERE OrderId = #{orderId}")
    int updateHistoryorderState(@Param("orderId") String orderId, @Param("state") int state);


    /**
     * 删除订单（二维码订单且未支付的）
     *
     * @param orderId
     * @return
     */
    @Delete("DELETE FROM `order` WHERE orderId = #{orderId}")
    int deleteOrder(@Param("orderId") String orderId);
}
