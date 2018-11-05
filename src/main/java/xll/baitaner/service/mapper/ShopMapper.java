package xll.baitaner.service.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import xll.baitaner.service.entity.Shop;
import xll.baitaner.service.entity.ShopBanner;

import java.util.List;

/**
 * 店铺模块数据操作类
 **/
@Repository
public interface ShopMapper {

    /**
     * 新增店铺数据
     * @param shop
     * @return
     */
    @Insert("INSERT INTO shop (openId,shopName,shopIntroduction,ownerName,wxNumber,contactNumber,shopAddress,shopLogoUrl" +
            ",payPlatform,payQrcode,payQrcodeUrl,shopState,number) VALUES (#{shop.openId},#{shop.shopName}," +
            "#{shop.shopIntroduction},#{shop.ownerName},#{shop.wxNumber},#{shop.contactNumber},#{shop.contactNumber}," +
            "#{shop.shopAddress},#{shop.shopLogoUrl},#{shop.payPlatform},#{shop.payQrcode},#{shop.payQrcodeUrl},#{shop.shopState},#{shop.number})")
    @Options(useGeneratedKeys = true, keyProperty = "area.id")
    int insertShop(@Param("shop") Shop shop);

    /**
     * 新增店铺banner图数据
     * @param shopId
     * @return
     */
    @Insert("INSERT INTO shopbanner (shopId) VALUES (#{shopId})")
    int insertShopBanner(@Param("shopId") int shopId);

    /**
     * 查询店铺信息
     * @param id
     * @return
     */
    @Select("SELECT * FROM shop WHERE id  = #{id}")
    Shop selectShopById(@Param("id") int id);

    /**
     * 查询用户拥有的店铺信息
     * @param openId
     * @return
     */
    @Select("SELECT * FROM shop WHERE openId  = #{openId}")
    Shop selectShopByUser(@Param("openId") String openId);

    /**
     * 查询店铺banner图
     * @param shopId
     * @return
     */
    @Select("SELECT * FROM shopbanner WHERE shopId  = #{shopId}")
    ShopBanner selectShopBanner(@Param("shopId") int shopId);

    /**
     * 更新店铺banner图
     * @param shopBanner
     * @return
     */
    @Update("UPDATE shopbanner SET shopPicUrl1 = #{shopBanner.shopPicUrl1}, shopPicUrl2 = #{shopBanner.shopPicUrl2}, " +
            "shopPicUrl3 = #{shopBanner.shopPicUrl3} WHERE shopId = #{shopBanner.shopId}")
    int updateShopBanner(@Param("shopBanner") ShopBanner shopBanner);

    /**
     * 更新店铺信息,店铺名、店铺简介、店主微信名、店主微信、联系电话、店铺地址、店铺logo、访问人次
     * @param shop
     * @return
     */
    @Update("UPDATE shop SET shopName = #{shop.shopName}, shopIntroduction = #{shop.shopIntroduction}, " +
            "ownerName = #{shop.ownerName}, wxNumber = #{shop.wxNumber}, contactNumber = #{shop.contactNumber}, " +
            "shopAddress = #{shop.shopAddress}, shopLogoUrl = #{shop.shopLogoUrl}, number = #{shop.number} WHERE id  = #{shop.id}")
    int updateShopInfo(@Param("shop") Shop shop);

    /**
     * 更新小程序内部支付开启状态
     * @param id
     * @param state
     * @return
     */
    @Update("UPDATE shop SET payPlatform = #{state} WHERE id = #{id}")
    int updateShopPayPlatform(@Param("id") int id, @Param("state") int state);

    /**
     * 更新店铺二维码支付开启状态
     * @param id
     * @param state
     * @return
     */
    @Update("UPDATE shop SET payQrcode = #{state} WHERE id = #{id}")
    int updateShopPayQrcode(@Param("id") int id, @Param("state") int state);

    /**
     * 更新支付二维码图片路径
     * @param id
     * @param state
     * @return
     */
    @Update("UPDATE shop SET payQrcodeUrl = #{url} WHERE id = #{id} AND openId = #{openId}")
    int updateShopPayQrcodeUrl(@Param("id") int id, @Param("openId") String openId, @Param("url") String url);

    /**
     * 获取店铺支付二维码
     * @param id
     * @return
     */
    @Select("SELECT payQrcodeUrl FROM shop WHERE id = #{id}")
    String selectShopPayQrcodeUrl(@Param("id") int id);

    /**
     * 更新店铺状态
     * @param id
     * @param state
     * @return
     */
    @Update("UPDATE shop SET shopState = #{shopState} WHERE Id = #{id}")
    int updateShopState(@Param("id") int id, @Param("state") int state);

    /**
     * 更新店铺访问人数
     * @param id
     * @return
     */
    @Update("UPDATE shop SET number = number+1 WHERE id = #{id}")
    int updateNumber(@Param("id") int id);

    /**
     * 删除店铺
     * @param id
     * @return
     */
    @Delete("DELETE FROM shop WHERE id = #{id}")
    int deleteShop(@Param("id") int id);

    /**
     * 插入用户浏览过的店铺
     * @param openId
     * @param shopId
     * @return
     */
    @Insert("INSERT INTO shopuser (openId, shopId) VALUES (#{openId}, #{shopId}")
    int insertShopUser(@Param("openId") String openId, @Param("shopId") int shopId);

    /**
     * 查询是用户是否已浏览过店铺
     * @param openId
     * @param shopId
     * @return
     */
    @Select("SELECT * FROM shopuser WHERE openId = #{openId} AND shopId = #{shopId}")
    int selectShopUser(@Param("openId") String openId, @Param("shopId") int shopId);

    /**
     * 查询用户浏览过的店铺
     * @param openId
     * @return
     */
    @Select("SELECT s.* FROM shopuser su JOIN shop s ON s.id = su.shopId WHERE su.openId = #{openId}")
    List<Shop> selectShopListForUser(@Param("openId") String openId);
}
