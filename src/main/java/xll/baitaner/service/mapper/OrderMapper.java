package xll.baitaner.service.mapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import xll.baitaner.service.entity.Order;
import xll.baitaner.service.entity.OrderCommodity;

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
     * @param order
     * @return
     */
    @Insert("INSERT INTO `order` (OrderId,ClientId,ReceiverAddressId,ShopId,Date,ArriveDate,Remarks,TotalMoney,State) " +
            "VALUES (#{order.orderId},#{order.clientId},#{order.receiverAddressId},#{order.shopId},#{order.date}," +
            "#{order.arriveDate},#{order.remarks},#{order.totalMoney},#{order.state})")
    int insertOrder(@Param("order") Order order);

    /**
     * 插入订单商品列表
     * @param commodityId
     * @param count
     * @param orderId
     * @return
     */
    @Insert("INSERT INTO orderlist (CommodityId,Count,OrderId) VALUES (#{commodityId},#{count},#{orderId})")
    int insertOrderList(@Param("commodityId") int commodityId, @Param("count") int count, @Param("orderId") String orderId);


    /**
     * 根据订单编号获取订单详情
     * @param orderId
     * @return
     */
    @Select("SELECT * FROM `order` WHERE OrderId = #{orderId}")
    Order selectOrder(@Param("orderId") String orderId);

    /**
     * 更新订单状态
     * @param orderId
     * @param state
     * @return
     */
    @Update("UPDATE `order` SET State = #{state} WHERE OrderId = #{orderId}")
    int updateOrderState(@Param("orderId") String orderId, @Param("state") int state);

    /**
     * 查询对应用户的订单列表
     * @param clientId
     * @return
     */
    @Select("SELECT * FROM `order` WHERE ClientId = #{clientId} LIMIT #{page.offset},#{page.size}")
    List<Order> seleceOrdersByClientId(@Param("clientId") String clientId, @Param("page") Pageable page);

    /**
     *查询对应用户的订单列表总个数
     * @param clientId
     * @return
     */
    @Select("SELECT COUNT(*) FROM `order` WHERE ClientId = #{clientId}")
    int countOrdersByClientId(@Param("clientId") String clientId);

    /**
     * 根据订单编号获取订单商品详情
     * @param orderId
     * @return
     */
    @Select("SELECT o.* ,co.`Name` AS commodityName FROM orderlist o " +
            "JOIN commodity co ON co.Id = o.CommodityId " +
            "WHERE OrderId = #{orderId}")
    List<OrderCommodity> selectOrderCoListByOrderId(@Param("orderId") String orderId);

    /**
     * 获取店铺全部已接订单的商品详情
     * @param orderId
     * @return
     */
    @Select("SELECT o.* ,co.`Name` AS commodityName FROM orderlist o " +
            "JOIN commodity co ON co.Id = o.CommodityId " +
            "JOIN `order` od ON od.OrderId = o.OrderId " +
            "WHERE od.State = 1 AND od.ShopId = #{shopId}")
    List<OrderCommodity> selectAllOrderCoList(@Param("shopId") int shopId);

    /**
     * 查询商品在所有已接订单中的总个数
     * @param coId
     * @return
     */
    @Select("SELECT SUM(o.Count) FROM orderlist o " +
            "JOIN `order` od ON od.OrderId = o.OrderId  " +
            "WHERE CommodityId = #{coId} AND od.State = 1")
    int sumCoCount(@Param("coId") int coId);

    /**
     * 查询店铺对应状态的订单列表
     * @param shopId
     * @param state
     * @return
     */
    @Select("SELECT * FROM `order` WHERE ShopId = #{shopId} AND State = #{state} LIMIT #{page.offset},#{page.size}")
    List<Order> selectOrdersByShop(@Param("shopId") int shopId, @Param("state") int state, @Param("page") Pageable page);

    /**
     * 查询店铺对应状态的订单列表总个数
     * @param shopId
     * @param state
     * @return
     */
    @Select("SELECT COUNT(*) FROM `order` WHERE ShopId = #{shopId} AND State = #{state}")
    int countOrdersByShop(@Param("shopId") int shopId, @Param("state") int state);
}
