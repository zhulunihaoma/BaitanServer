package com.xll.baitaner.controller;

import com.xll.baitaner.entity.ExpressInfo;
import com.xll.baitaner.service.ExpressInfoService;
import com.xll.baitaner.utils.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author dengyy
 * @date 2019/8/27
 */
@RestController
public class ExpressController {

    @Resource
    ExpressInfoService expressInfoService;

    @PutMapping("express/add")
    public ResponseResult addOne(ExpressInfo express) {
        try {
            int ex = expressInfoService.addExpress(express);
            return ResponseResult.result(0, "success", ex);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.result(1, "fail", e);
        }
    }

    @GetMapping("express/ids")
    public ResponseResult getExpressId(Integer shopId, Integer id) {
        try {
            List<Integer> ids = expressInfoService.queryExpressIds(shopId, id);
            return ResponseResult.result(0, "success", ids);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.result(1, "fail", e);
        }
    }
}
