package xll.baitaner.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import xll.baitaner.service.entity.Shop;
import xll.baitaner.service.mapper.ShopMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 描述：店铺管理模块service
 * 创建者：xie
 * 日期：2017/9/19
 **/
@Service
public class ShopManageService {

    @Autowired
    private ShopMapper shopMapper;

    /**
     * 新增店铺
     * @param shop
     * @return
     */
    public String addShop(Shop shop){
        if(shopMapper.selectShop(shop.getClientId()) != null){
            return "用户已拥有店铺了！";
        }
        return shopMapper.insertShop(shop) > 0 ? null : "新增失败";
    }

    /**
     * 获取用户的店铺信息
     * @param clientId
     * @return
     */
    public Shop getShop(String clientId){
        return shopMapper.selectShop(clientId);
    }

    /**
     * 更新用户的店铺信息
     * @param shop
     * @return
     */
    public boolean updateShop(Shop shop){
        return shopMapper.updateShop(shop) > 0;
    }

    /**
     * 文件上传写入路径
     * @param multReq
     * @return
     */
    public String upload(MultipartHttpServletRequest multReq, String fileName){
        FileInputStream inputStream = null;
        try {
            inputStream = (FileInputStream)(multReq.getFile("file").getInputStream());
            if(fileName == null || fileName == ""){
                fileName = java.util.UUID.randomUUID().toString();
            }
            FileOutputStream fileOut = new FileOutputStream("");
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
