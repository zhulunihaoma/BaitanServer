package com.xll.baitaner.mapper;

import com.xll.baitaner.entity.HistoryOrder;
import com.xll.baitaner.entity.Order;
import com.xll.baitaner.entity.OrderCommodity;
import com.xll.baitaner.entity.ShopOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
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
            "order by create_data desc LIMIT #{page.offset},#{page.size}")
    List<ShopOrder> selectOrdersByOpenId(@Param("openId") String openId, @Param("page") Pageable page);

    /**
     * 查询对应用户的订单列表总个数
     *
     * @param openId
     * @return
     */
    @Select("SELECT COUNT(*) FROM `shop_order` WHERE open_id = #{openId}")
    int countOrdersByClientId(@Param("openId") String openId);

    /**
     * 获取店铺的未付款订单 （二维码支付的订单）
     *
     * @param shopId
     * @param page
     * @return
     */
    @Select("SELECT" + shopOrder + "FROM `shop_order` WHERE shop_id = #{shopId} AND state = 0 AND pay_type = 1 " +
            "ORDER BY date DESC LIMIT #{page.offset},#{page.size} ")
    List<ShopOrder> selectNoPayOrdersByShopId(@Param("shopId") int shopId, @Param("page") Pageable page);

    /**
     * 获取店铺的未付款订单 （二维码支付的订单） 总个数
     *
     * @param shopId
     * @return
     */
    @Select("SELECT COUNT(*) FROM `shop_order` WHERE shop_id = #{shopId} AND state = 0 AND pay_type = 1")
    int countNoPayOrdersByShop(@Param("shopId") int shopId);

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
            "create_date DESC LIMIT #{page.offset},#{page.size}")
    List<ShopOrder> selectShopOrdersByShop(@Param("shopId") int shopId, @Param("state") int state,
                                           @Param("page") Pageable page);

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
    @Select("SELECT COUNT(*) FROM `shop_order` WHERE shop_id = #{shopId} AND state = #{state}")
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
    @Select("SELECT o.*, od.name AS clientName, od.remarks AS orderRemarks FROM order_commodity o " +
            "JOIN `shop_order` od ON od.order_id = o.order_id WHERE od.state = 1 AND od.shop_id = #{shopId}")
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
    int insertHistoryOrder(@Param("historyId") int historyId, @Param("orderId") Long orderId,
                           @Param("payType") int payType, @Param("state") int state);

    /**
     * 从historyorder查询具体日期的历史订单列表
     *
     * @param historyId
     * @return
     */
    @Select("SELECT o.* FROM historyorder ho JOIN `shop_order` o ON o.order_id = ho.orderId " +
            "WHERE ho.shopHistoryId = #{historyId} ORDER BY o.create_date DESC")
    List<Order> selectDateOrderList(@Param("historyId") int historyId);


    /**
     * 从historyorder查询具体日期的历史订单列表根据state
     *
     * @param historyId
     * @param state     0：待支付;  1：已接单;  2：待完成; 3：已完成
     * @return
     */
    @Select("SELECT o.* FROM historyorder ho JOIN `shop_order` o ON o.order_id = ho.orderId " +
            "WHERE ho.shopHistoryId = #{historyId} AND ho.state = #{state} ORDER BY o.create_date DESC")
    List<Order> selectDateOrderListByState(@Param("historyId") int historyId, @Param("state") int state);


    /**
     * 从historyorder查询具体日期的历史订单列表根据PayType
     *
     * @param historyId
     * @param payType   0：在线支付  1：二维码支付
     * @return
     */
    @Select("SELECT o.* FROM historyorder ho JOIN `shop_order` o ON o.order_id = ho.orderId " +
            "WHERE ho.shopHistoryId = #{historyId} AND ho.payType = #{payType} ORDER BY o.create_date DESC")
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
    @Update("update `shop_order` set del_flag=1 where order_id=#{orderId}")
    int deleteOrder(@Param("orderId") Long orderId);
}
