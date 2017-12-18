package xll.baitaner.service.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import xll.baitaner.service.utils.ResponseResult;

/**
 * 店铺列表展示首页controller
 */
@RestController
public class ShopHomeController {

    /**
     * 是否在正式环境
     */
    @Value("${baitaner.runtime}")
    private boolean runtimeOrDev;

    @GetMapping("/")
    public ResponseResult testhome(){
        return ResponseResult.result(0, runtimeOrDev ? "online" : "test", runtimeOrDev ? "正式环境" : "测试环境");
    }
}
