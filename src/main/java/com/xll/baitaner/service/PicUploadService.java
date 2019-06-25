package com.xll.baitaner.service;

import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * @author denghuohuo 2019/6/25
 */
public interface PicUploadService {

    /**
     * 图片上传
     *
     * @param multReq
     * @return
     */
    String picupload(MultipartHttpServletRequest multReq);
}
