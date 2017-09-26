package xll.baitaner.service.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import xll.baitaner.service.entity.Shop;

/**
 * 描述：店铺管理数据操作类
 * 创建者：xie
 * 日期：2017/9/19
 **/
@Repository
public interface ShopMapper {

    /**
     * 新增店铺数据
     * @param shop
     * @return
     */
    @Insert("INSERT INTO shop (ClientId,Name,Introduction,Contacts,Sex,Phone,Address) " +
            "VALUES (#{shop.clientId},#{shop.name},#{shop.introduction},#{shop.contacts}," +
            "#{shop.sex},#{shop.phone},#{shop.address},#{shop.picUrl1},#{shop.picUrl2},#{shop.picUrl3})")
    int insertShop(@Param("shop") Shop shop);

    /**
     * 查询用户的店铺信息
     * @param clientId
     * @return
     */
    @Select("SELECT * FROM shop WHERE ClientId  = #{clientId}")
    Shop selectShop(@Param("clientId") String clientId);

    /**
     * 更新用户的店铺信息
     * @param shop
     * @return
     */
    @Update("UPDATE shop SET Name = #{shop.name}, Introduction = #{shop.introduction}, Contacts = #{shop.contacts}, " +
            "Sex = #{shop.sex}, Phone = #{shop.phone} " +
            "WHERE ClientId  = #{clientId}")
    int updateShop(@Param("shop") Shop shop);
}
