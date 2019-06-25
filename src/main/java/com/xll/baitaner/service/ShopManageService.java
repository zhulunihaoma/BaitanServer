package com.xll.baitaner.service;

import com.xll.baitaner.entity.Shop;
import com.xll.baitaner.entity.ShopBanner;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * @author denghuohuo 2019/6/25
 */
public interface ShopManageService {

    /**
     * 创建店铺
     *
     * @param shop
     * @return
     */
    String addShop(Shop shop);

    /**
     * 根据Id获取店铺信息
     *
     * @param shopId
     * @return
     */
    Shop getShopById(int shopId);

    /**
     * 获取用户拥有的店铺信息
     *
     * @param openId
     * @return
     */
    Shop getShopByUser(String openId);

    /**
     * 获取店铺banner图
     *
     * @param shopId
     * @return
     */
    ShopBanner getShopBanner(int shopId);

    /**
     * 更新店铺banner图
     *
     * @param shopBanner
     * @return
     */
    boolean updateShopBanner(ShopBanner shopBanner);

    /**
     * 更新店铺信息,店铺名、店铺简介、店主微信名、店主微信、联系电话、店铺地址、店铺logo、访问人次
     *
     * @param shop
     * @return
     */
    boolean updateShopInfo(Shop shop);

    /**
     * 更新小程序内部支付开启状态
     *
     * @param shopId
     * @param state
     * @return
     */
    boolean updateShopPayPlatform(int shopId, int state);

    /**
     * 上传支付二维码图片路径
     *
     * @param shopId
     * @param openId
     * @param url
     * @return
     */
    String updateShopPayQrcodeUrl(int shopId, String openId, String url);

    /**
     * 获取店铺支付二维码
     *
     * @param shopId
     * @return
     */
    String getShopPayQrcodeUrl(int shopId);

    /**
     * 修改店铺二维码支付开启状态
     *
     * @param shopId
     * @param state
     * @return
     */
    String updateShopPayQrcode(int shopId, int state);

    /**
     * 更新店铺营业状态
     *
     * @param shopId
     * @param state
     * @return
     */
    boolean updateShopState(int shopId, int state);

    /**
     * 访问人数增加
     *
     * @param shopId
     * @return
     */
    boolean addNumber(int shopId);

    /**
     * 删除对应店铺
     *
     * @param shopId
     * @return
     */
    boolean deleteShop(int shopId);

    /**
     * 新增用户浏览过的店铺
     *
     * @param openId
     * @param shopId
     * @return
     */
    String addShopUser(String openId, int shopId);

    /**
     * 获取用户浏览过的店铺
     *
     * @param openId
     * @return
     */
    List<Shop> getShopListForUser(String openId);

    /**
     * 获取店铺首页数据
     *
     * @param openId
     * @return
     */
    JSONObject getShopHomeData(String openId);
}
