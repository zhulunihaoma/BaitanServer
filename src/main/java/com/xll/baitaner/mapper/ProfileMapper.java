package com.xll.baitaner.mapper;

import com.xll.baitaner.entity.ReceiverAddress;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 描述：个人功能类（收货地址）mapper
 * 创建者：xie
 * 日期：2017/10/11
 **/
@Repository
public interface ProfileMapper {

    /**
     * 查询用户所有收货地址列表
     *
     * @param openId
     * @return
     */
    @Select("SELECT * FROM receiver_address WHERE openId = #{openId} AND Disable = 1")
    List<ReceiverAddress> selectAddressList(@Param("openId") String openId);

    /**
     * 获取单个收货地址数据
     *
     * @param id
     * @return
     */
    @Select("SELECT * FROM receiver_address WHERE Id = #{id} AND Disable = 1")
    ReceiverAddress selectAddress(@Param("id") int id);

    /**
     * 新增收货地址
     *
     * @param ad
     * @return
     */
    @Insert("INSERT INTO receiver_address (openId,Name,Sex,Address,Phone,defaultNot) " +
            "VALUES (#{ad.openId},#{ad.name},#{ad.sex},#{ad.address},#{ad.phone},#{ad.defaultNot})")
    @Options(useGeneratedKeys = true, keyProperty = "ad.id", keyColumn = "id")
    int insertAddress(@Param("ad") ReceiverAddress ad);

    /**
     * 更新收货地址信息
     *
     * @param ad)
     * @return
     */
    @Update("UPDATE receiver_address SET Name = #{ad.name}, Sex = #{ad.sex}, Address = #{ad.address}, " +
            "Phone = #{ad.phone}, defaultNot = #{ad.defaultNot} WHERE Id  = #{ad.id}")
    int updateAddress(@Param("ad") ReceiverAddress ad);

    /**
     * 删除收货地址
     *
     * @param id
     * @return
     */
    @Delete("DELETE FROM receiver_address WHERE Id = #{id}")
    int deleteAddress(@Param("id") int id);

    /**
     * 收货地址设为默认地址
     *
     * @param id
     * @param openId
     * @return
     */
    @Update("UPDATE receiver_address SET defaultNot = IF(Id=#{id},1,0) WHERE openId = #{openId}")
    int updateAddressState(@Param("id") int id, @Param("openId") String openId);

    /**
     * 查询用户默认地址
     *
     * @param openId
     * @return
     */
    @Select("SELECT * FROM receiver_address WHERE openId = #{openId} AND defaultNot = 1 AND Disable = 1")
    ReceiverAddress selectDefaultAddress(@Param("openId") String openId);


    /*************************店铺经营数据*******************************/

    /**
     * 查询今日已接订单数
     *
     * @param shopId
     * @return
     */
    @Select("SELECT COUNT(1) FROM `shop_order` WHERE shop_id=#{shopId} AND state > 0 AND del_flag=0 " +
            "AND DATEDIFF(DATE(NOW()),DATE(create_date))=0")
    int selectTodayReceivedOrderCount(@Param("shopId") int shopId);

    /**
     * 查询昨日已接订单数
     *
     * @param shopId
     * @return
     */
    @Select("SELECT COUNT(1) FROM `shop_order` WHERE shop_id=#{shopId} AND state > 0 AND del_flag=0 " +
            "AND DATEDIFF(DATE(NOW()),DATE(create_date))=1")
    int selectYesterdayReceivedOrderCount(@Param("shopId") int shopId);

    /**
     * 查询店铺今日总营业额
     *
     * @param shopId
     * @return
     */
    @Select("SELECT total_money FROM `shop_order` " +
            "WHERE shop_id = #{shopId} AND state > 0 AND del_flag=0 AND DATEDIFF(Date(NOW()),DATE(create_date)) = 0")
    List<String> selectTodaySalesByShopId(@Param("shopId") int shopId);

    /**
     * 查询店铺昨日营业额
     *
     * @param shopId
     * @return
     */
    @Select("SELECT total_money FROM `shop_order` " +
            "WHERE shop_id = #{shopId} AND state > 0 AND del_flag=0 AND DATEDIFF(Date(NOW()),DATE(create_date)) = 1")
    List<String> selectYesterdaySalesByShopId(@Param("shopId") int shopId);


    /**
     * 查询店铺本周营业额
     *
     * @param shopId
     * @return
     */
    @Select("SELECT total_money FROM `shop_order` " +
            "WHERE shop_id = #{shopId} AND state > 0 AND del_flag=0 " +
            "AND YEARWEEK(DATE_FORMAT(create_date,'%Y-%m-%d')) = YEARWEEK(NOW())")
    List<String> selectThisWeekSalesByShopId(@Param("shopId") int shopId);

    /**
     * 查询店铺上周营业额
     *
     * @param shopId
     * @return
     */
    @Select("SELECT total_money FROM `shop_order` " +
            "WHERE shop_id = #{shopId} AND state > 0 AND del_flag=0 AND " +
            "YEARWEEK(DATE_FORMAT(create_date,'%Y-%m-%d')) = YEARWEEK(NOW()) - 1")
    List<String> selectLastWeekSalesByShopId(@Param("shopId") int shopId);

    /**
     * 查询店铺本月总营业额
     *
     * @param shopId
     * @return
     */
    @Select("SELECT total_money FROM `shop_order` " +
            "WHERE shop_id = #{shopId} AND state > 0 AND del_flag=0 AND " +
            "PERIOD_DIFF(DATE_FORMAT(NOW(),'%Y%m'),DATE_FORMAT(create_date,'%Y%m')) = 0")
    List<String> selectThismonthSalesByShopId(@Param("shopId") int shopId);

    /**
     * 查询店铺上个月营业额
     *
     * @param shopId
     * @return
     */
    @Select("SELECT total_money FROM `shop_order` " +
            "WHERE shop_id = #{shopId} AND state > 0 AND del_flag=0 AND " +
            "PERIOD_DIFF(DATE_FORMAT(NOW(),'%Y%m'),DATE_FORMAT(create_date,'%Y%m')) = 1")
    List<String> selectLastmonthSalesByShopId(@Param("shopId") int shopId);

    /**
     * 查询店铺总营业额
     *
     * @param shopId
     * @return
     */
    @Select("SELECT total_money FROM `shop_order` " +
            "WHERE shop_id = #{shopId} AND state > 0 AND del_flag=0")
    List<String> selectTotalSalesByShopId(@Param("shopId") int shopId);
}
