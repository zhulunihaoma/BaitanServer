package com.xll.baitaner.controller;

import com.xll.baitaner.entity.Version;
import com.xll.baitaner.service.VersionService;
import com.xll.baitaner.utils.ResponseResult;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


import org.springframework.web.bind.annotation.*;


/**
 * @author zhulu
 * @version 1.0
 * @description com.xll.baitaner.controller
 * @date 2019/8/9
 */
@Api(value = "版本信息controller")
@RestController
public class VersionController {
  @Resource VersionService versionService;
    /**
     * 根据版本id获取活版本信息
     *
     * @param id
     * @return
     */
    @GetMapping("selectVersionById")
    public ResponseResult selectVersionById(int id){
        return ResponseResult.result(0, "success", versionService.selectVersionById(id));

    }
}
