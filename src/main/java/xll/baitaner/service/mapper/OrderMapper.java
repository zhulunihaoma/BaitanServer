package xll.baitaner.service.mapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import xll.baitaner.service.entity.*;

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
     * @param order
     * @return
     */
    @Insert("INSERT INTO `order` (OrderId,ClientId,ReceiverAddressId,ShopId,Date,ArriveDate,Remarks,TotalMoney,State," +
            "Name,Sex,Address,Phone) " +
            "VALUES (#{order.orderId},#{order.clientId},#{order.receiverAddressId},#{order.shopId},#{order.date}," +
            "#{order.arriveDate},#{order.remarks},#{order.totalMoney},#{order.state},#{re.name},#{re.sex}," +
            "#{re.address},#{re.phone})")
    int insertOrder(@Param("order") Order order, @Param("re") ReceiverAddress re);

    /**
     * 插入订单商品列表
     * @param commodityId
     * @param count
     * @param orderId
     * @return
     */
    @Insert("INSERT INTO orderlist (CommodityId,Count,OrderId,Name,Price,MonthlySales,Praise,PictUrl,Introduction) " +
            "VALUES (#{commodityId},#{count},#{orderId},#{co.name},#{co.price},#{co.monthlySales},#{co.praise}," +
            "#{co.pictUrl},#{co.introduction})")
    int insertOrderList(@Param("commodityId") int commodityId, @Param("count") int count,
                        @Param("orderId") String orderId, @Param("co") Commodity co);


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
    @Select("SELECT * FROM `order` WHERE ClientId = #{clientId} ORDER BY Date DESC LIMIT #{page.offset},#{page.size}")
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
    @Select("SELECT * FROM orderlist WHERE OrderId = #{orderId}")
    List<OrderCommodity> selectOrderCoListByOrderId(@Param("orderId") String orderId);

    /**
     * 获取店铺全部已接订单的商品详情
     * @param shopId
     * @return
     */
    @Select("SELECT o.*, od.Name AS clientName, od.Remarks AS orderRemarks FROM orderlist o " +
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
    @Select("SELECT * FROM `order` WHERE ShopId = #{shopId} AND State = #{state} ORDER BY Date DESC " +
            "LIMIT #{page.offset},#{page.size}")
    List<Order> selectOrdersByShop(@Param("shopId") int shopId, @Param("state") int state, @Param("page") Pageable page);

    /**
     * 查询店铺对应状态的订单列表总个数
     * @param shopId
     * @param state 0：待支付 1：待送达 2：已完成
     * @return
     */
    @Select("SELECT COUNT(*) FROM `order` WHERE ShopId = #{shopId} AND State = #{state}")
    int countOrdersByShop(@Param("shopId") int shopId, @Param("state") int state);



    /**
     * 插入店铺及历史日期
     * @param ho
     * @return
     */
    @Insert("INSERT INTO shophistory (ShopId,HistoryDate) VALUES (#{ho.shopId},#{ho.historyDate})")
    @Options(useGeneratedKeys = true, keyProperty = "ho.id")
    int insertShopHistory(@Param("ho") HistoryOrder ho);

    /**
     * 查询shophistory是否已存在店铺对应的日期条目
     * @param shopId
     * @param date
     * @return
     */
    @Select("SELECT * FROM shophistory WHERE ShopId = #{shopId} AND HistoryDate = #{date}")
    HistoryOrder selectShopHistory(@Param("shopId") int shopId, @Param("date") Date date);

    /**
     * 查询店铺的历史订单实体类列表
     * @param shopId
     * @param page
     * @return
     */
    @Select("SELECT * FROM shophistory WHERE ShopId = #{shopId} ORDER BY HistoryDate DESC LIMIT #{page.offset},#{page.size}")
    List<HistoryOrder> selectHistoryOrderList(@Param("shopId") int shopId, @Param("page") Pageable page);

    /**
     * 查询店铺的历史订单实体类列表总个数
     * @param shopId
     * @return
     */
    @Select("SELECT COUNT(*) FROM shophistory WHERE ShopId = #{shopId}")
    int countHistoryOrderList(@Param("shopId") int shopId);

    /**
     * 以shophistoryID为基准插入订单
     * @param historyId
     * @param orderId
     * @return
     */
    @Insert("INSERT INTO historyorder (ShopHistoryId,OrderId) VALUES (#{historyId},#{orderId})")
    int insertHistoryOrder(@Param("historyId") int historyId, @Param("orderId") String orderId);

    /**
     * 从historyorder查询具体日期的历史订单列表
     * @param historyId
     * @return
     */
    @Select("SELECT o.* FROM historyorder ho JOIN `order` o ON o.OrderId = ho.OrderId " +
            "WHERE ho.ShopHistoryId = #{historyId}")
    List<Order> selectDateOrderList(@Param("historyId") int historyId);
}
