package com.xll.baitaner.controller;

import com.xll.baitaner.service.WeChatService;
import com.xll.baitaner.utils.Constant;
import com.xll.baitaner.utils.LogUtils;
import com.xll.baitaner.utils.ResponseResult;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * https://www.eastzebra.cn/service/
 * controller
 */
@RestController
public class HomeController {
    private String TAG = "HomeController";

    @Autowired
    private WeChatService weChatService;
    /**
     * 是否在正式环境
     */
    @Value("${baitaner.runtime}")
    private boolean runtimeOrDev;

    @GetMapping("/")
    public ResponseResult testhome() {
        //创建自定义菜单
        JSONObject menu_json = new JSONObject();
        JSONObject menu = new JSONObject();
        menu.put("type", "miniprogram");
        menu.put("name", "一键开店");
        menu.put("url", "https://www.eastzebra.cn");
        menu.put("appid", Constant.APPLET_APP_ID);
        menu.put("pagepath", "pages/home/intropage/intropage");
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(menu);
        menu_json.put("button", jsonArray);

        LogUtils.info(TAG, "微信公众号创建自定义菜单");
        weChatService.creatWXPublicMenu(menu_json);

        return ResponseResult.result(0, runtimeOrDev ? "online" : "test", runtimeOrDev ? "正式环境" : "测试环境");
    }
}
