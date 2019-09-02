package com.xll.baitaner.mapper;

import com.xll.baitaner.entity.ExpressInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author dengyy
 * @date 2019/8/27
 */
public interface ExpressInfoMapper {

    @Insert("insert into express_info (shop_id,shop_order_id,express_id) values(#{ex.shopId}," +
            "#{ex.shopOrderId},#{ex.expressId})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insertOne(@Param("ex") ExpressInfo ex);

    @Select("select express_id from express_info where shop_id=#{shopId} and shop_order_id=#{orderId}")
    List<String> selectExpressIdsById(@Param("shopId") Integer shopId, @Param("orderId") Integer orderId);
}
