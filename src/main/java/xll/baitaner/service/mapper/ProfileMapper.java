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
     * @param clientId
     * @return
     */
    @Select("SELECT * FROM receiveraddress WHERE ClientId = #{clientId} AND Disable = 1")
    List<ReceiverAddress> selectAddressList(@Param("clientId") String clientId);

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
    @Insert("INSERT INTO receiveraddress (ClientId,Name,Sex,Address,Phone,IsDefault) " +
            "VALUES (#{ad.clientId},#{ad.name},#{ad.sex},#{ad.address},#{ad.phone},#{ad.isDefault})")
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
     * @param clientId
     * @return
     */
    @Update("UPDATE receiveraddress SET IsDefault = IF(Id=#{id},1,0) WHERE ClientId = #{clientId}")
    int updateAddressState(@Param("id") int id, @Param("clientId") String clientId);

    /**
     * 查询用户默认地址
     * @param clientId
     * @return
     */
    @Select("SELECT * FROM receiveraddress WHERE ClientId = #{clientId} AND IsDefault = 1 AND Disable = 1")
    ReceiverAddress selectDefaultAddress(@Param("clientId") String clientId);


    /**
     * 查询店铺今日总营业额
     * @param shopId
     * @return
     */
    @Select("SELECT CAST(COALESCE(SUM(TotalMoney),0) AS DECIMAL(8,2)) FROM `order` " +
            "WHERE ShopId = #{shopId} AND State > 0 AND DATE(Date) = DATE(NOW())")
    float selectTodaySalesByShop(@Param("shopId") int shopId);



    /**
     * 查询店铺总营业额
     * @param shopId
     * @return
     */
    @Select("SELECT CAST(COALESCE(SUM(TotalMoney),0) AS DECIMAL(8,2)) FROM `order` " +
            "WHERE ShopId = #{shopId} AND State > 0")
    float selectTotalSalesByShop(@Param("shopId") int shopId);
}
