package xll.baitaner.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import xll.baitaner.service.entity.Shop;
import xll.baitaner.service.entity.ShopBanner;
import xll.baitaner.service.mapper.ShopMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 描述：店铺模块service
 **/
@Service
public class ShopManageService {

    @Autowired
    private ShopMapper shopMapper;

    /**
     * 创建店铺
     * @param shop
     * @return
     */
    public String addShop(Shop shop){
        if(shopMapper.selectShopByUser(shop.getOpenId()) != null){
            return "用户已创建店铺";
        }

        boolean result = shopMapper.insertShop(shop) > 0;
        if(result){
            shopMapper.insertShopBanner(shop.getId());
        }

        return  result ? null : "创建店铺失败";
    }

    /**
     * 根据Id获取店铺信息
     * @param clientId
     * @return
     */
    public Shop getShopById(int shopId){
        return shopMapper.selectShopById(shopId);
    }

    /**
     * 获取用户拥有的店铺信息
     * @param clientId
     * @return
     */
    public Shop getShopByUser(String openId){
        return shopMapper.selectShopByUser(openId);
    }

    /**
     * 获取店铺banner图
     * @param shopId
     * @return
     */
    public ShopBanner getShopBanner(int shopId){
        return shopMapper.selectShopBanner(shopId);
    }

    /**
     *  更新店铺banner图
     * @param shopBanner
     * @return
     */
    public boolean updateShopBanner(ShopBanner shopBanner){
        return shopMapper.updateShopBanner(shopBanner) > 0;
    }

    /**
     * 更新店铺信息,店铺名、店铺简介、店主微信名、店主微信、联系电话、店铺地址、店铺logo、访问人次
     * @param shop
     * @return
     */
    public boolean updateShopInfo(Shop shop){
        return shopMapper.updateShopInfo(shop) > 0;
    }

    /**
     * 更新小程序内部支付开启状态
     * @param shopId
     * @param state
     * @return
     */
    public boolean updateShopPayPlatform(int shopId, int state){
        return shopMapper.updateShopPayPlatform(shopId, state) > 0;
    }

    /**
     * 上传支付二维码图片路径
     * @param shopId
     * @param state
     * @return
     */
    public String updateShopPayQrcodeUrl(int shopId, String openId, String url){
        if(getShopPayQrcodeUrl(shopId) == null && getShopPayQrcodeUrl(shopId) == ""){
            return "支付二维码图片路径为空";
        }
        return shopMapper.updateShopPayQrcodeUrl(shopId, openId, url) > 0 ? null : "开启失败";
    }

    /**
     * 获取店铺支付二维码
     * @param shopId
     * @return
     */
    public String getShopPayQrcodeUrl(int shopId){
        return shopMapper.selectShopPayQrcodeUrl(shopId);
    }

    /**
     * 修改店铺二维码支付开启状态
     * @param shopId
     * @param state
     * @return
     */
    public String updateShopPayQrcode(int shopId, int state){
        if(getShopPayQrcodeUrl(shopId) == null && getShopPayQrcodeUrl(shopId) == ""){
            return "开启二维码支付失败，未上传支付二维码";
        }

        return shopMapper.updateShopPayPlatform(shopId, state) > 0 ? null : "开启失败";
    }

    /**
     * 更新店铺营业状态
     * @param shopId
     * @param state
     * @return
     */
    public boolean updateShopState(int shopId, int state){return shopMapper.updateShopState(shopId, state) > 0;}


    /**
     * 访问人数增加
     * @param id
     * @return
     */
    public boolean addNumber(int shopId){
        return shopMapper.updateNumber(shopId) > 0;
    }

    /**
     * 删除对应店铺
     * @param id
     * @return
     */
    public boolean deleteShop(int shopId){return shopMapper.deleteShop(shopId) > 0;}

    /**
     * 新增用户浏览过的店铺
     * @param openId
     * @param shopId
     * @return
     */
    public String addShopUser(String openId, int shopId){
        if(shopMapper.selectShopUser(openId, shopId) > 0 ){
            return "已加入浏览过得店铺";
        }else if(shopMapper.selectShopByUser(openId) !=null && shopMapper.selectShopByUser(openId).getId() == shopId){
            return "用户自己的店铺，不用记录";
        }
        else {
            return shopMapper.insertShopUser(openId, shopId) > 0 ? null : "数据插入失败";
        }
    }

    /**
     * 获取店铺首页数据
     * @param openId
     * @return
     */
    public List<Shop> getShopHomeData(String openId){
        //获取店铺首页数据
        List<Shop> shopList = null;
        Shop ownerShop = shopMapper.selectShopByUser(openId);
        if(ownerShop != null){
            shopList.add(ownerShop);
            shopList.addAll(1, shopMapper.selectUserShopId(openId));
        }else {
            shopList = shopMapper.selectUserShopId(openId);
        }

        return shopList;
    }
}
