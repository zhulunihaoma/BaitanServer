package xll.baitaner.service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xll.baitaner.service.utils.ResponseResult;

/**
 * 店铺列表展示首页controller
 */
@RestController
public class ShopHomeController {

    @GetMapping("/")
    public ResponseResult testhome(){
        return ResponseResult.result(0, "success", "HelloworldTest");
    }
}
