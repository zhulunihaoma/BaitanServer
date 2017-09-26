package xll.baitaner.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xll.baitaner.service.entity.Shop;
import xll.baitaner.service.mapper.ShopMapper;

/**
 * 描述：店铺管理模块service
 * 创建者：xie
 * 日期：2017/9/19
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
        if(shopMapper.selectShop(shop.getClientId()) != null){
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
}
