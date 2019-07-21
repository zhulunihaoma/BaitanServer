package com.xll.baitaner.mapper;

import com.xll.baitaner.entity.Shop;
import com.xll.baitaner.entity.ShopBanner;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 店铺模块数据操作类
 **/
@Repository
public interface ShopMapper {

    /**
     * 新增店铺数据
     *
     * @param shop
     * @return
     */
    @Insert("INSERT INTO shop (openId,shopName,shopIntroduction,ownerName,wxNumber,contactNumber,shopAddress,shopLogoUrl" +
            ",payPlatform,payQrcode,payQrcodeUrl,shopState,number,shopWxacode) VALUES (#{shop.openId},#{shop.shopName}," +
            "#{shop.shopIntroduction},#{shop.ownerName},#{shop.wxNumber},#{shop.contactNumber},#{shop.shopAddress}," +
            "#{shop.shopLogoUrl},#{shop.payPlatform},#{shop.payQrcode},#{shop.payQrcodeUrl},#{shop.shopState},#{shop.number},#{shop.shopWxacode})")
    @Options(useGeneratedKeys = true, keyProperty = "shop.id")
    int insertShop(@Param("shop") Shop shop);

    /**
     * 新增店铺banner图数据
     *
     * @param shopId
     * @return
     */
    @Insert("INSERT INTO shopbanner (shopId) VALUES (#{shopId})")
    int insertShopBanner(@Param("shopId") int shopId);

    /**
     * 查询店铺信息
     *
     * @param id
     * @return
     */
    @Select("SELECT * FROM shop WHERE id  = #{id}")
    Shop selectShopById(@Param("id") int id);

    /**
     * 根据店铺id查询拥有者openId
     *
     * @param shopId
     * @return
     */
    @Select("select openId from shop where id=#{shopId}")
    String getOpenIdByShopId(@Param("shopId") Integer shopId);

    /**
     * 查询用户拥有的店铺信息
     *
     * @param openId
     * @return
     */
    @Select("SELECT * FROM shop WHERE openId  = #{openId}")
    Shop selectShopByUser(@Param("openId") String openId);

    /**
     * 查询店铺banner图
     *
     * @param shopId
     * @return
     */
    @Select("SELECT * FROM shopbanner WHERE shopId  = #{shopId}")
    ShopBanner selectShopBanner(@Param("shopId") int shopId);

    /**
     * 更新店铺banner图
     *
     * @param shopBanner
     * @return
     */
    @Update("UPDATE shopbanner SET shopPicUrl1 = #{shopBanner.shopPicUrl1}, shopPicUrl2 = #{shopBanner.shopPicUrl2}, " +
            "shopPicUrl3 = #{shopBanner.shopPicUrl3} WHERE shopId = #{shopBanner.shopId}")
    int updateShopBanner(@Param("shopBanner") ShopBanner shopBanner);

    /**
     * 更新店铺信息,店铺名、店铺简介、店主微信名、店主微信、联系电话、店铺地址、店铺logo、店铺平台支付开启状态、店铺二维码支付开启状态、店铺支付二维码路径、访问人次
     *
     * @param shop
     * @return
     */
    @Update("UPDATE shop SET shopName = #{shop.shopName}, shopIntroduction = #{shop.shopIntroduction}, " +
            "ownerName = #{shop.ownerName}, wxNumber = #{shop.wxNumber}, contactNumber = #{shop.contactNumber}, " +
            "shopAddress = #{shop.shopAddress}, shopLogoUrl = #{shop.shopLogoUrl},payPlatform = #{shop.payPlatform}," +
            "payQrcode = #{shop.payQrcode},payQrcodeUrl = #{shop.payQrcodeUrl}, number = #{shop.number}, shopWxacode = #{shop.shopWxacode} " +
            "WHERE id  = #{shop.id}")
    int updateShopInfo(@Param("shop") Shop shop);

    /**
     * 更新小程序内部支付开启状态
     *
     * @param id
     * @param state
     * @return
     */
    @Update("UPDATE shop SET payPlatform = #{state} WHERE id = #{id}")
    int updateShopPayPlatform(@Param("id") int id, @Param("state") int state);

    /**
     * 更新支付二维码图片路径
     *
     * @param id
     * @param openId
     * @return
     */
    @Update("UPDATE shop SET payQrcodeUrl = #{url} WHERE id = #{id} AND openId = #{openId}")
    int updateShopPayQrcodeUrl(@Param("id") int id, @Param("openId") String openId, @Param("url") String url);

    /**
     * 获取店铺支付二维码
     *
     * @param id
     * @return
     */
    @Select("SELECT payQrcodeUrl FROM shop WHERE id = #{id}")
    String selectShopPayQrcodeUrl(@Param("id") int id);

    /**
     * 更新店铺状态
     *
     * @param id
     * @param state
     * @return
     */
    @Update("UPDATE shop SET shopState = #{state} WHERE Id = #{id}")
    int updateShopState(@Param("id") int id, @Param("state") int state);

    /**
     * 更新店铺访问人数
     *
     * @param id
     * @return
     */
    @Update("UPDATE shop SET number = number+1 WHERE id = #{id}")
    int updateNumber(@Param("id") int id);

    /**
     * 删除店铺
     *
     * @param id
     * @return
     */
    @Delete("DELETE FROM shop WHERE id = #{id}")
    int deleteShop(@Param("id") int id);

    /**
     * 插入用户浏览过的店铺
     *
     * @param openId
     * @param shopId
     * @return
     */
    @Insert("INSERT INTO shopuser (openId, shopId) VALUES (#{openId}, #{shopId})")
    int insertShopUser(@Param("openId") String openId, @Param("shopId") int shopId);

    /**
     * 查询是用户是否已浏览过店铺
     *
     * @param openId
     * @param shopId
     * @return
     */
    @Select("SELECT COUNT(*) FROM shopuser WHERE openId = #{openId} AND shopId = #{shopId}")
    int selectShopUser(@Param("openId") String openId, @Param("shopId") int shopId);

    /**
     * 查询用户浏览过的店铺
     *
     * @param openId
     * @return
     */
    @Select("SELECT s.* FROM shopuser su JOIN shop s ON s.id = su.shopId WHERE su.openId = #{openId}")
    List<Shop> selectShopListForUser(@Param("openId") String openId);

    /**
     * 根据shop_id、wxacode_scene、wxacode_page查询二维码路径
     * @param shopid
     * @param scene
     * @param page
     * @return
     */
    @Select("SELECT wxacode_path FROM shop_wxacode WHERE shop_id = #{shopid} AND wxacode_scene = #{scene} AND wxacode_page = #{page}")
    String selectShapWXacodePath(@Param("shopid") int shopid, @Param("scene") String scene, @Param("page") String page);

    /**
     * 插入shop_id、wxacode_scene、wxacode_page、wxacode_path 二维码路径数据
     * @param shopid
     * @param scene
     * @param page
     * @param path
     * @return
     */
    @Insert("INSERT INTO shop_wxacode (shop_id, wxacode_scene, wxacode_page, wxacode_path) VALUES(#{shopid}, #{scene}, #{page}, #{path})")
    int insertShapWXacode(@Param("shopid") int shopid, @Param("scene") String scene, @Param("page") String page, @Param("path") String path);

}
