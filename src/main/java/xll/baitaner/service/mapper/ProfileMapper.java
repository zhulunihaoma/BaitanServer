package xll.baitaner.service.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import xll.baitaner.service.entity.Activity;
import xll.baitaner.service.entity.ReceiverAddress;

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
     * @param openId
     * @return
     */
    @Select("SELECT * FROM receiveraddress WHERE openId = #{openId} AND Disable = 1")
    List<ReceiverAddress> selectAddressList(@Param("openId") String openId);

    /**
     * 获取单个收货地址数据
     * @param id
     * @return
     */
    @Select("SELECT * FROM receiveraddress WHERE Id = #{id} AND Disable = 1")
    ReceiverAddress selectAddress(@Param("id") int id);

    /**
     * 新增收货地址
     * @param ad
     * @return
     */
    @Insert("INSERT INTO receiveraddress (openId,Name,Sex,Address,Phone,IsDefault) " +
            "VALUES (#{ad.openId},#{ad.name},#{ad.sex},#{ad.address},#{ad.phone},#{ad.isDefault})")
    int insertAddress(@Param("ad") ReceiverAddress ad);

    /**
     * 更新收货地址信息
     * @param ad)
     * @return
     */
    @Update("UPDATE receiveraddress SET Name = #{ad.name}, Sex = #{ad.sex}, Address = #{ad.address}, " +
            "Phone = #{ad.phone}, IsDefault = #{ad.isDefault} WHERE Id  = #{ad.id}")
    int updateAddress(@Param("ad") ReceiverAddress ad);

    /**
     * 删除收货地址
     * @param id
     * @return
     */
    @Delete("DELETE FROM receiveraddress WHERE Id = #{id}")
    int deleteAddress(@Param("id") int id);

    /**
     * 更新收货地址的disable状态，替代删除方法
     * @param id
     * @return
     */
    @Update("UPDATE receiveraddress SET Disable = 0 WHERE Id  = #{id}")
    int updateAdrDisabel(@Param("id") int id);

    /**
     * 收货地址设为默认地址
     * @param id
     * @param openId
     * @return
     */
    @Update("UPDATE receiveraddress SET IsDefault = IF(Id=#{id},1,0) WHERE openId = #{openId}")
    int updateAddressState(@Param("id") int id, @Param("openId") String openId);

    /**
     * 查询用户默认地址
     * @param openId
     * @return
     */
    @Select("SELECT * FROM receiveraddress WHERE openId = #{openId} AND IsDefault = 1 AND Disable = 1")
    ReceiverAddress selectDefaultAddress(@Param("openId") String openId);


    /*************************店铺经营数据*******************************/

    /**
     * 查询今日已接订单数
     * @param shopId
     * @return
     */
    @Select("SELECT COUNT(*) FROM `order` WHERE ShopId = #{shopId} AND State > 0 AND DATEDIFF(Date(NOW()),DATE(date)) = 0")
    int selectTodayReceivedOrderCount(@Param("shopId") int shopId);

    /**
     * 查询昨日已接订单数
     * @param shopId
     * @return
     */
    @Select("SELECT COUNT(*) FROM `order` WHERE ShopId = #{shopId} AND State > 0 AND DATEDIFF(Date(NOW()),DATE(date)) = 1")
    int selectYesterdayReceivedOrderCount(@Param("shopId") int shopId);

    /**
     * 查询店铺今日总营业额
     * @param shopId
     * @return
     */
    @Select("SELECT CAST(COALESCE(SUM(TotalMoney),0) AS DECIMAL(8,2)) FROM `order` " +
            "WHERE ShopId = #{shopId} AND State > 0 AND DATEDIFF(Date(NOW()),DATE(date)) = 0")
    float selectTodaySalesByShop(@Param("shopId") int shopId);

    /**
     * 查询店铺昨日营业额
     * @param shopId
     * @return
     */
    @Select("SELECT CAST(COALESCE(SUM(TotalMoney),0) AS DECIMAL(8,2)) FROM `order` " +
            "WHERE ShopId = #{shopId} AND State > 0 AND DATEDIFF(Date(NOW()),DATE(date)) = 1")
    float selectYesterdaySalesByShop(@Param("shopId") int shopId);


    /**
     * 查询店铺本周营业额
     * @param shopId
     * @return
     */
    @Select("SELECT CAST(COALESCE(SUM(TotalMoney),0) AS DECIMAL(8,2)) FROM `order` " +
            "WHERE ShopId = #{shopId} AND State > 0 AND " +
            "YEARWEEK(DATE_FORMAT(date,'%Y-%m-%d')) = YEARWEEK(NOW())")
    float selectThisWeekSalesByShop(@Param("shopId") int shopId);

    /**
     * 查询店铺上周营业额
     * @param shopId
     * @return
     */
    @Select("SELECT CAST(COALESCE(SUM(TotalMoney),0) AS DECIMAL(8,2)) FROM `order` " +
            "WHERE ShopId = #{shopId} AND State > 0 AND " +
            "YEARWEEK(DATE_FORMAT(date,'%Y-%m-%d')) = YEARWEEK(NOW()) - 1")
    float selectLastWeekSalesByShop(@Param("shopId") int shopId);

    /**
     * 查询店铺本月总营业额
     * @param shopId
     * @return
     */
    @Select("SELECT CAST(COALESCE(SUM(TotalMoney),0) AS DECIMAL(8,2)) FROM `order` " +
            "WHERE ShopId = #{shopId} AND State > 0 AND " +
            "PERIOD_DIFF(DATE_FORMAT(NOW(),'%Y%m'),DATE_FORMAT(date,'%Y%m')) = 0")
    float selectThismonthSalesByShop(@Param("shopId") int shopId);

    /**
     * 查询店铺上个月营业额
     * @param shopId
     * @return
     */
    @Select("SELECT CAST(COALESCE(SUM(TotalMoney),0) AS DECIMAL(8,2)) FROM `order` " +
            "WHERE ShopId = #{shopId} AND State > 0 AND " +
            "PERIOD_DIFF(DATE_FORMAT(NOW(),'%Y%m'),DATE_FORMAT(date,'%Y%m')) = 1")
    float selectLastmonthSalesByShop(@Param("shopId") int shopId);

    /**
     * 查询店铺总营业额
     * @param shopId
     * @return
     */
    @Select("SELECT CAST(COALESCE(SUM(TotalMoney),0) AS DECIMAL(8,2)) FROM `order` " +
            "WHERE ShopId = #{shopId} AND State > 0")
    float selectTotalSalesByShop(@Param("shopId") int shopId);
}
