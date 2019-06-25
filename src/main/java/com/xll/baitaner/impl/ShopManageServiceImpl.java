package com.xll.baitaner.impl;

import com.xll.baitaner.entity.Shop;
import com.xll.baitaner.entity.ShopBanner;
import com.xll.baitaner.mapper.ShopMapper;
import com.xll.baitaner.service.ShopManageService;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 描述：店铺模块service
 **/
@Service
public class ShopManageServiceImpl implements ShopManageService {

    @Resource
    private ShopMapper shopMapper;

    /**
     * 创建店铺
     *
     * @param shop
     * @return
     */
    @Override
    public String addShop(Shop shop) {
        if (shopMapper.selectShopByUser(shop.getOpenId()) != null) {
            return "用户已创建店铺";
        }

        boolean result = shopMapper.insertShop(shop) > 0;
        if (result) {
            shopMapper.insertShopBanner(shop.getId());
        }

        return result ? null : "创建店铺失败";
    }

    /**
     * 根据Id获取店铺信息
     *
     * @param shopId
     * @return
     */
    @Override
    public Shop getShopById(int shopId) {
        return shopMapper.selectShopById(shopId);
    }

    /**
     * 获取用户拥有的店铺信息
     *
     * @param openId
     * @return
     */
    @Override
    public Shop getShopByUser(String openId) {
        return shopMapper.selectShopByUser(openId);
    }

    /**
     * 获取店铺banner图
     *
     * @param shopId
     * @return
     */
    @Override
    public ShopBanner getShopBanner(int shopId) {
        return shopMapper.selectShopBanner(shopId);
    }

    /**
     * 更新店铺banner图
     *
     * @param shopBanner
     * @return
     */
    @Override
    public boolean updateShopBanner(ShopBanner shopBanner) {
        return shopMapper.updateShopBanner(shopBanner) > 0;
    }

    /**
     * 更新店铺信息,店铺名、店铺简介、店主微信名、店主微信、联系电话、店铺地址、店铺logo、访问人次
     *
     * @param shop
     * @return
     */
    @Override
    public boolean updateShopInfo(Shop shop) {
        return shopMapper.updateShopInfo(shop) > 0;
    }

    /**
     * 更新小程序内部支付开启状态
     *
     * @param shopId
     * @param state
     * @return
     */
    @Override
    public boolean updateShopPayPlatform(int shopId, int state) {
        return shopMapper.updateShopPayPlatform(shopId, state) > 0;
    }

    /**
     * 上传支付二维码图片路径
     *
     * @param shopId
     * @param openId
     * @param url
     * @return
     */
    @Override
    public String updateShopPayQrcodeUrl(int shopId, String openId, String url) {
        if (getShopPayQrcodeUrl(shopId) == null && getShopPayQrcodeUrl(shopId) == "") {
            return "支付二维码图片路径为空";
        }
        return shopMapper.updateShopPayQrcodeUrl(shopId, openId, url) > 0 ? null : "开启失败";
    }

    /**
     * 获取店铺支付二维码
     *
     * @param shopId
     * @return
     */
    @Override
    public String getShopPayQrcodeUrl(int shopId) {
        return shopMapper.selectShopPayQrcodeUrl(shopId);
    }

    /**
     * 修改店铺二维码支付开启状态
     *
     * @param shopId
     * @param state
     * @return
     */
    @Override
    public String updateShopPayQrcode(int shopId, int state) {
        if (getShopPayQrcodeUrl(shopId) == null && getShopPayQrcodeUrl(shopId) == "") {
            return "开启二维码支付失败，未上传支付二维码";
        }
        return shopMapper.updateShopPayPlatform(shopId, state) > 0 ? null : "开启失败";
    }

    /**
     * 更新店铺营业状态
     *
     * @param shopId
     * @param state
     * @return
     */
    @Override
    public boolean updateShopState(int shopId, int state) {
        return shopMapper.updateShopState(shopId, state) > 0;
    }


    /**
     * 访问人数增加
     *
     * @param shopId
     * @return
     */
    @Override
    public boolean addNumber(int shopId) {
        return shopMapper.updateNumber(shopId) > 0;
    }

    /**
     * 删除对应店铺
     *
     * @param shopId
     * @return
     */
    @Override
    public boolean deleteShop(int shopId) {
        return shopMapper.deleteShop(shopId) > 0;
    }

    /**
     * 新增用户浏览过的店铺
     *
     * @param openId
     * @param shopId
     * @return
     */
    @Override
    public String addShopUser(String openId, int shopId) {
        if (shopMapper.selectShopUser(openId, shopId) > 0) {
            return "已加入浏览过得店铺";
        } else if (shopMapper.selectShopByUser(openId) != null && shopMapper.selectShopByUser(openId).getId() == shopId) {
            return "用户自己的店铺，不用记录";
        } else {
            return shopMapper.insertShopUser(openId, shopId) > 0 ? null : "数据插入失败";
        }
    }

    /**
     * 获取用户浏览过的店铺
     *
     * @param openId
     * @return
     */
    @Override
    public List<Shop> getShopListForUser(String openId) {
        return shopMapper.selectShopListForUser(openId);
    }

    /**
     * 获取店铺首页数据
     *
     * @param openId
     * @return
     */
    @Override
    public JSONObject getShopHomeData(String openId) {
        //获取店铺首页数据
        List<Shop> shopList = null;
        Shop ownerShop = shopMapper.selectShopByUser(openId);
        shopList = shopMapper.selectShopListForUser(openId);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("owerShop", ownerShop);
        jsonObject.put("shopList", shopList);

        return jsonObject;
    }
}
