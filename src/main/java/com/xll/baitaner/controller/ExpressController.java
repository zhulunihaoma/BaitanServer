package com.xll.baitaner.controller;

import com.xll.baitaner.entity.ExpressInfo;
import com.xll.baitaner.utils.ResponseResult;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dengyy
 * @date 2019/8/27
 */
@RestController
public class ExpressController {

    @PutMapping("express/add")
    public ResponseResult addOne(ExpressInfo express) {
        return ResponseResult.result(0, "success", "");
    }
}
