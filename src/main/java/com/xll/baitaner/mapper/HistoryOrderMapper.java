package com.xll.baitaner.mapper;

import com.xll.baitaner.entity.ShopOrderDate;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 接口名：HistoryOrderMapper
 * 描述：历史订单相关mapper
 * 创建者：xie
 * 日期：2019/7/3/003
 **/
public interface HistoryOrderMapper {

    /**
     * 记录店铺付款成功订单的日期
     *
     * @param shopOrderDate
     * @return
     */
    @Insert("INSERT INTO shop_order_date (shop_id,order_date) VALUES (#{shopOrderDate.shopId},#{shopOrderDate.orderDate})")
    @Options(useGeneratedKeys = true, keyProperty = "shopOrderDate.id")
    int insertOrderDate(@Param("shopOrderDate") ShopOrderDate shopOrderDate);

    /**
     * 查询shop_order_date店铺对应的日期记录
     *
     * @param shopId
     * @param date
     * @return
     */
    @Select("SELECT * FROM shop_order_date WHERE shop_id = #{shopId} AND order_date = #{date}")
    ShopOrderDate selectShopOrderDate(@Param("shopId") int shopId, @Param("date") String date);

    /**
     * 判断历史订单是否已存在
     *
     * @param shopOrderDateId
     * @param orderId
     * @return
     */
    @Select("SELECT COUNT(1) FROM history_order WHERE date_id = #{shopOrderDateId} AND order_id = #{orderId}")
    int selectCountHistoryOrder(@Param("shopOrderDateId") int shopOrderDateId, @Param("orderId") String orderId);

    /**
     * 以shopOrderDateId为基准插入historyorder
     *
     * @param shopOrderDateId
     * @param orderId
     * @param payType
     * @param state
     * @return
     */
    @Insert("INSERT INTO history_order (date_id,order_id,pay_type,state) VALUES (#{shopOrderDateId},#{orderId},#{payType},#{state})")
    int insertHistoryOrder(@Param("shopOrderDateId") int shopOrderDateId, @Param("orderId") Long orderId,
                           @Param("payType") int payType, @Param("state") int state);

    /**
     * 从history_order查询具体日期的历史订单列表
     *
     * @param shopOrderDateId
     * @return
     */
    @Select("SELECT o.order_id FROM history_order ho JOIN `shop_order` o ON o.order_id = ho.order_id " +
            "WHERE ho.date_id = #{shopOrderDateId} ORDER BY o.create_date DESC")
    List<String> selectDateOrderList(@Param("shopOrderDateId") int shopOrderDateId);


    /**
     * 从history_order查询具体日期的历史订单列表根据state
     *
     * @param shopOrderDateId
     * @param state           0：待支付;  1：已接单;  2：待完成; 3：已完成
     * @return
     */
    @Select("SELECT o.order_id FROM history_order ho JOIN `shop_order` o ON o.order_id = ho.order_id " +
            "WHERE ho.date_id = #{shopOrderDateId} AND ho.state = #{state} ORDER BY o.create_date DESC")
    List<String> selectDateOrderListByState(@Param("shopOrderDateId") int shopOrderDateId, @Param("state") int state);


    /**
     * 从history_order查询具体日期的历史订单列表根据PayType
     *
     * @param shopOrderDateId
     * @param payType         0：在线支付  1：二维码支付
     * @return
     */
    @Select("SELECT o.order_id FROM history_order ho JOIN `shop_order` o ON o.order_id = ho.order_id " +
            "WHERE ho.date_id = #{shopOrderDateId} AND ho.pay_type = #{payType} ORDER BY o.create_date DESC")
    List<String> selectDateOrderListByPayType(@Param("shopOrderDateId") int shopOrderDateId, @Param("payType") int payType);

    /**
     * 更改history_order的state 通过orderId
     *
     * @param orderId
     * @param state   0：待支付;  1：已接单;  2：待完成; 3：已完成
     * @return
     */
    @Update("UPDATE `history_order` SET state = #{state} WHERE order_id = #{orderId}")
    int updateHistoryorderState(@Param("orderId") Long orderId, @Param("state") int state);

    /**
     * 查询店铺记录的订单日期ShopOrderDate列表
     *
     * @param shopId
     * @return
     */
    @Select("SELECT * FROM shop_order_date WHERE shop_id = #{shopId} ORDER BY order_date DESC")
    List<ShopOrderDate> selectShopOrderDateList(@Param("shopId") int shopId);

}
