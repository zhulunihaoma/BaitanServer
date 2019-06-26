package com.xll.baitaner.controller;

import com.xll.baitaner.service.PicUploadService;
import com.xll.baitaner.utils.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;

/**
 * 描述：图片上传controller
 * 创建者：xie
 * 日期：2017/9/27
 **/
@Api(value = "图片上传controller")
@RestController
public class PicUploadController {

    @Value("${baitaner.imagehost}")
    private String imagehost;

    @Resource
    private PicUploadService picUploadService;

    /**
     * 图片上传接口
     *
     * @param multReq
     * @return
     */
    @ApiOperation(
            value = "图片上传",
            notes = "返回图片name, 获取图片路径：http://www.eastzebra.cn/servicepicture/ + 图片name"
    )
    @PostMapping("/picupload")
    public ResponseResult uploadPic(MultipartHttpServletRequest multReq) {
        System.out.print(multReq);
        String res = picUploadService.picupload(multReq);
        return ResponseResult.result(res != null ? 0 : 1, res != null ? "successs" : "fail", res);
    }
}
