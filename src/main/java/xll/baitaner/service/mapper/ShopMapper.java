package xll.baitaner.service.mapper;

import org.apache.ibatis.annotations.*;
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
    @Insert("INSERT INTO shop (ClientId,Name,Introduction,Contacts,Sex,Phone,Address,PicUrl1,PicUrl2,PicUrl3) " +
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
            "Sex = #{shop.sex}, Phone = #{shop.phone}, Address = #{shop.address}, PicUrl1 = #{shop.picUrl1}, " +
            "PicUrl2 = #{shop.picUrl2}, PicUrl3 = #{shop.picUrl3} " +
            "WHERE ClientId  = #{shop.clientId}")
    int updateShop(@Param("shop") Shop shop);

    /**
     * 删除店铺
     * @param id
     * @return
     */
    @Delete("DELETE FROM shop WHERE Id = #{id}")
    int deleteShop(@Param("id") int id);

    /**
     * 更新店铺状态
     * @param id
     * @param state
     * @return
     */
    @Update("UPDATE shop SET State = #{state} WHERE Id = #{id}")
    int updateShopState(@Param("id") int id, @Param("state") int state);
}
