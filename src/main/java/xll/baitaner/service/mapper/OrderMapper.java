package xll.baitaner.service.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
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
     * 查询对应用户的订单列表
     * @param clientId
     * @return
     */
    @Select("SELECT * FROM `order` WHERE ClientId = #{clientId}")
    List<Order> seleceOrderListByClientId(@Param("clientId") String clientId);

    /**
     * 根据订单编号获取订单商品详情
     * @param orderId
     * @return
     */
    @Select("SELECT * FROM orderlist WHERE OrderId = #{orderId}")
    List<OrderCommodity> selectOrderCoListByOrderId(@Param("orderId") String orderId);
}
