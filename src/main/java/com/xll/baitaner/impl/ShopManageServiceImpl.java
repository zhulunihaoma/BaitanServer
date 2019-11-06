package com.xll.baitaner.impl;

import com.github.pagehelper.util.StringUtil;
import com.xll.baitaner.entity.Shop;
import com.xll.baitaner.entity.ShopBanner;
import com.xll.baitaner.mapper.ShopMapper;
import com.xll.baitaner.service.ShopManageService;
import com.xll.baitaner.service.WeChatService;
import com.xll.baitaner.utils.LogUtils;
import com.xll.baitaner.utils.ResponseResult;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 描述：店铺模块service
 **/
@Service
public class ShopManageServiceImpl implements ShopManageService {

    private String TAG = "Baitaner-ShopManageServiceImpl";

    @Resource
    private ShopMapper shopMapper;

    @Resource
    private WeChatService weChatService;

    /**
     * 创建店铺
     * TODO 创建店铺要做数据校验 新增下创建时间
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

    /**
     * 获取店铺下各二维码存储文件名， 根据sceneStr和page先查询数据库是否存在，避免重复生成
     * @param shopid        店铺id
     * @param scene         二维码携带的信息
     * @param page          二维码跳转的页面
     * @return  二维码文件名
     */
    @Override
    public ResponseResult getWXacodePath(Integer shopId, String scene, String page) {
        if (shopId == null || shopId <= 0)
            return ResponseResult.result(1, "fail", "该shopId不合规");

        if (this.getShopById(shopId) == null)
            return ResponseResult.result(1, "fail", "该shopId无店铺");

        if (StringUtils.isBlank(scene) || StringUtils.isBlank(page))
            return ResponseResult.result(1, "fail", "scene或page不合规");

        try {
            //判断数据库中是否存储该参数生成额二维码  防止重复生成
            String path = shopMapper.selectShapWXacodePath(shopId, scene, page);
            if (StringUtils.isBlank(path)){
                //生成二维码
                String acodePath = weChatService.creatWXacodeUnlimited(scene, page);
                if (StringUtils.isBlank(acodePath)){
                    LogUtils.info(TAG, "生成二维码  scene： " + scene + " page: " + page + "  失败!");
                    return ResponseResult.result(1, "fail", "生成二维码失败!");
                }

                //新二维码
                if (path == null){
                    path = acodePath;
                    if (shopMapper.insertShapWXacode(shopId, scene, page, path) <= 0){
                        LogUtils.info(TAG, "新增二维码路径  scene： " + scene + " page: " + page + "  失败!");
                    }
                }
                else if (path == ""){
                    path = acodePath;
                    if (shopMapper.updateShapWXacode(shopId, scene, page, path) <= 0){
                        LogUtils.info(TAG, "更新二维码路径  scene： " + scene + " page: " + page + "  失败!");
                    }
                }
            }
            return ResponseResult.result(0, "success", path);
        }catch (Exception e){
            return ResponseResult.result(1, "fail", "生成二维码失败!");
        }
    }
}
