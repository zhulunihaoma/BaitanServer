package com.xll.baitaner.controller;

import com.xll.baitaner.service.TemplateService;
import com.xll.baitaner.utils.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类名：TemplateController
 * 描述：微信模板消息
 * 创建者：xie
 * 日期：2019/7/9/009
 **/
@Api(value = "微信模板消息controller")
@RestController
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    @ApiOperation(value = "新增formId", notes = "新增formId 用于发送模板消息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "openId", value = "用户openId", required = true, dataType = "String"),
            @ApiImplicitParam(name = "formId", value = "formId", required = true, dataType = "String")
    })
    @PostMapping("addformid")
    public ResponseResult addFormId(String openId, String formId){
        boolean res = templateService.addFormId(openId, formId);
        return ResponseResult.result(res? 0 : 1, res ? "success" : "fail", null);
    }

}
