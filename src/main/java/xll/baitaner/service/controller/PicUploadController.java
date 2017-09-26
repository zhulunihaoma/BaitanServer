package xll.baitaner.service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import xll.baitaner.service.entity.Shop;
import xll.baitaner.service.utils.ResponseResult;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * 描述：
 * 创建者：xie
 * 日期：2017/9/27
 **/
@RestController
public class PicUploadController {

    /**
     * 图片上传接口
     * @param multipartFile
     * @return
     */
    @RequestMapping("/picupload")
    public ResponseResult uploadPic(@RequestParam("file") MultipartFile multipartFile){
        System.out.print(multipartFile);
        try {
            FileInputStream inputStream = (FileInputStream)multipartFile.getInputStream();
            System.out.print("---------");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
