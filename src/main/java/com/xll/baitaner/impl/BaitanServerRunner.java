package com.xll.baitaner.impl;

import com.xll.baitaner.service.WeChatService;
import com.xll.baitaner.utils.Constant;
import com.xll.baitaner.utils.LogUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 类名：BaitanServerRunner
 * 描述：服务启动时执行方法
 * 创建者：xie
 * 日期：2019/11/24/024
 **/
@Component
public class BaitanServerRunner implements CommandLineRunner {
    private String TAG = "BaitanServerRunner";

    @Override
    public void run(String... args) throws Exception {
        LogUtils.info(TAG, "-----------------Server Start---------------");
    }
}
