package xll.baitaner.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import xll.baitaner.service.entity.Shop;
import xll.baitaner.service.service.ShopManageService;
import xll.baitaner.service.utils.ResponseResult;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * 描述：店铺管理controller
 * 创建者：xie
 * 日期：2017/9/19
 **/
@RestController
public class ShopManageController {

    @Autowired
    private ShopManageService shopManageService;

    /**
     * 店铺管理店铺数据提交接口
     * @param shop
     * @return
     */
    @RequestMapping("/shopmanage/submitshop")
    public ResponseResult addShop(Shop shop){
        System.out.print(shop.getClientId());
        String result = shopManageService.addShop(shop);
        if(result == null){
            return ResponseResult.result(0, "sucess" , result);
        }
        else {
            return ResponseResult.result(1, "failed" , result);
        }
    }

    /**
     * 获取店铺数据
     * @return
     */
    @GetMapping("/shopmanage/getshop")
    public ResponseResult test(String clientId){
        return ResponseResult.result(0, "sucess" , shopManageService.getShop(clientId));
    }

    /**
     * 店铺管理修改店铺信息接口
     * @param shop
     * @return
     */
    @RequestMapping("/shopmanage/editshop")
    public ResponseResult editShop(Shop shop){
        System.out.print(shop.getClientId());
        boolean result = shopManageService.updateShop(shop);
        return ResponseResult.result(result ? 0 : 1, result ? "sucess" : "failure", null);
    }
}
