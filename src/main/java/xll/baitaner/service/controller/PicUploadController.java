package xll.baitaner.service.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import xll.baitaner.service.entity.Shop;
import xll.baitaner.service.service.PicUploadService;
import xll.baitaner.service.utils.ResponseResult;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * 描述：图片上传controller
 * 创建者：xie
 * 日期：2017/9/27
 **/
@Api(value = "图片上传controller", description = "图片上传模块接口")
@RestController
public class PicUploadController {

    @Value("${baitaner.imagehost}")
    private String imagehost;

    @Autowired
    private PicUploadService picUploadService;

    /**
     * 图片上传接口
     * @param multReq
     * @return
     */
    @ApiOperation(
            value = "图片上传",
            notes = "返回图片name, 获取图片路径：http://www.eastzebra.cn/servicepicture/ + 图片name"
    )
    @PostMapping("/picupload")
    public ResponseResult uploadPic(MultipartHttpServletRequest multReq){
        System.out.print(multReq);
        String res = picUploadService.picupload(multReq);
        return ResponseResult.result(res != null ? 0 : 1,res != null ? "successs" : "fail", res);
    }

//    @GetMapping("/picpath")
//    public ResponseResult getPicPath(){
//        return ResponseResult.result(0, "success", imagehost);
//    }
}
