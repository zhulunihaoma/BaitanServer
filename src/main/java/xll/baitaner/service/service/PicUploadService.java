package xll.baitaner.service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import xll.baitaner.service.utils.PathUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 描述：图片上传服务
 * 创建者：xie
 * 日期：2017/9/27
 **/
@Service
public class PicUploadService {

    /**
     * 是否在正式环境
     */
    @Value("${baitaner.runtime}")
    private boolean runtimeOrDev;

    /**
     * 图片上传
     * @param multReq
     * @return
     */
    public String picupload(MultipartHttpServletRequest multReq){
        String fileName = java.util.UUID.randomUUID().toString();
        String result = upload(multReq,fileName);
        if(result == null){
            return fileName;
        }else {
            return null;
        }
    }

    /**
     * 文件上传写入路径
     * @param multReq
     * @return 文件名
     */
    public String upload(MultipartHttpServletRequest multReq, String fileName){
        FileInputStream inputStream = null;
        try {
            inputStream = (FileInputStream)(multReq.getFile("file").getInputStream());
            FileOutputStream fileOut = new FileOutputStream(PathUtil.getUploadPath(fileName,runtimeOrDev));
            byte[] buffer = new byte[1024];
            int read;
            while(true){
                read = inputStream.read(buffer,0,1024);
                if(read<1)
                    break;
                fileOut.write(buffer,0,read);
            }
            fileOut.flush();
            fileOut.close();
        }catch (IOException ex){
            ex.printStackTrace();
            return ex.getMessage();
        }
        return null;
    }

    /**
     * 删除单个文件
     * @param fileName
     * @return
     */
    public boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }
}
