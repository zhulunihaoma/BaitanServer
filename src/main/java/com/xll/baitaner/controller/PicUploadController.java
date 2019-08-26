package com.xll.baitaner.controller;

import com.xll.baitaner.service.PicUploadService;
import com.xll.baitaner.utils.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * 描述：图片上传controller
 * 创建者：xie
 * 日期：2017/9/27
 **/
@Slf4j
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
        String res = picUploadService.picupload(multReq);
        //压缩图片
        picUploadService.zipPicLoad(res);
        return ResponseResult.result(res != null ? 0 : 1, res != null ? "successs" : "fail", res);
    }

    /**
     * 图片压缩
     *
     * @return
     */
    @PostMapping("/zipservicepic")
    public ResponseResult zipPic() {
        String oldpath = ClassUtils.getDefaultClassLoader().getResource("").getPath()
                + "../../../../webapps/servicepicture/";
        String newpath = ClassUtils.getDefaultClassLoader().getResource("").getPath()
                + "../../../../webapps/zippicture/";
        File oldFile = new File(oldpath);
        File[] files = oldFile.listFiles();
        for (File file : files) {
            String oldName = oldpath.concat(file.getName());
            String newName = newpath.concat(file.getName());
            try {
                Thumbnails.of(oldName).scale(1f).outputQuality(0.25f).toFile(newName);
            } catch (IOException e) {
                log.error("图片转换压缩错误,name:{},e:{}", file.getName(), e);
                e.printStackTrace();
            }
        }
        return ResponseResult.result(0, "success", 1);

    }
}
