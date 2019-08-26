package com.xll.baitaner.mapper;

import com.xll.baitaner.entity.OrderCommodity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单商品表
 *
 * @author denghuohuo 2019/6/29
 */
@Repository
public interface OrderCommodityMapper {

    /**
     * 插入订单商品
     *
     * @param orderCommodity
     * @return
     */
    @Insert("INSERT INTO `order_commodity` " +
            "(commodity_id,count,order_id,name,unit_price,pict_url,zip_pic_url,introduction,spec_id,spec_name," +
            "spec_price) VALUES (#{co.commodityId},#{co.count},#{co.orderId},#{co.name},#{co.unitPrice}," +
            "#{co.pictUrl},#{co.zipPicUrl},#{co.introduction},#{co.specId},#{co.specName},#{co.specPrice})")
    int insertOrderList(@Param("co") OrderCommodity orderCommodity);

    /**
     * 根据订单号查询商品信息
     * @param orderId
     * @return
     */
    @Select("select * from order_commodity where order_id=#{orderId}")
    List<OrderCommodity> selectOrderCommodityByOrderId(@Param("orderId") Long orderId);

}
