package xll.baitaner.service.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 描述：随机序列化生成
 * 创建者：xie
 * 日期：2017/10/15
 **/
public class SerialUtils {

    /**
     * 生成不重复的订单号
     * 生成规则：
     * 年月日+系统时间戳+随机四位数
     * @return
     */
    public synchronized static String getSerialId(){
        Random rand = new Random();
        StringBuffer sbf = new StringBuffer();
        SimpleDateFormat sfm = new SimpleDateFormat("yyyyMMddHHmmss");
        sbf.append(sfm.format(new Date()));
        sbf.append(rand.nextInt(9999)%9000+1000);
        System.err.println(sbf);
        return sbf.toString();
    }
}
