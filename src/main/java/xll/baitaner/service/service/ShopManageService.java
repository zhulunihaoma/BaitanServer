package xll.baitaner.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import xll.baitaner.service.entity.Shop;
import xll.baitaner.service.mapper.ShopMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 描述：店铺管理模块service
 **/
@Service
public class ShopManageService {

    @Autowired
    private ShopMapper shopMapper;

    /**
     * 新增店铺
     * @param shop
     * @return
     */
    public String addShop(Shop shop){
        if(shopMapper.selectShop(shop.getOpenId()) != null){
            return "用户已拥有店铺了！";
        }
        return shopMapper.insertShop(shop) > 0 ? null : "新增失败";
    }

    /**
     * 获取用户的店铺信息
     * @param clientId
     * @return
     */
    public Shop getShop(String clientId){
        return shopMapper.selectShop(clientId);
    }

    /**
     * 更新用户的店铺信息
     * @param shop
     * @return
     */
    public boolean updateShop(Shop shop){
        return shopMapper.updateShop(shop) > 0;
    }

    /**
     * 删除对应店铺
     * @param id
     * @return
     */
    public boolean deleteShop(int id){return shopMapper.deleteShop(id) > 0;}

    /**
     * 更新店铺营业状态
     * @param shopId
     * @param state
     * @return
     */
    public boolean updateShopState(int shopId, int state){return shopMapper.updateShopState(shopId, state) > 0;}
}
