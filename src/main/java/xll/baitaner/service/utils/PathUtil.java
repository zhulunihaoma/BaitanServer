package xll.baitaner.service.utils;

import org.springframework.util.ClassUtils;

import java.io.File;

/**
 * 描述：路径工具类
 * 创建者：luoyw
 * 日期：2017/8/22 0022.
 **/
public class PathUtil {
    private static String uploadPath;
    /**
     * 获取上传路径
     * （在开发模式下，工程根目录/upload
     *   在部署模式下，app根目录/upload)
     * @param runtimeOrDev 是否正式环境
     * @return
     */
    public static synchronized String getUploadPath(boolean runtimeOrDev){
        if(uploadPath==null){
            if(runtimeOrDev){
                //TODO 待验证部署后，路径是否正确
                uploadPath = ClassUtils.getDefaultClassLoader().getResource("").getPath()
                        + "../../../../webapps/upload/";
            }else {
                uploadPath = ClassUtils.getDefaultClassLoader().
                        getResource("").getPath() + "../../../../Baitaner/src/main/resources/static/upload/";
            }
            System.out.println("PathUtil.getUploadPath:"+uploadPath);
            File f = new File(uploadPath);
            if(!f.isDirectory()&&!f.exists()){
                f.mkdir();
            }
        }
        return uploadPath;
    }

    /**
     *  获取上传路径(基础路径+name)
     * （在开发模式下，工程根目录/upload
     *   在部署模式下，app根目录/upload)
     * @param name 文件名
     * @param runtimeOrDev 是否正式环境
     * @return
     */
    public static synchronized String getUploadPath(String name,boolean runtimeOrDev){
        return getUploadPath(runtimeOrDev)+name;
    }
}
