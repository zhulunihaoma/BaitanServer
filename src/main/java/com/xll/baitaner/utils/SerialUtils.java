package com.xll.baitaner.utils;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.RandomStringUtils;

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
     * 生成不重复的商户号
     * 生成规则：
     * 年月日+系统时间戳+随机三位数
     *
     * @return
     */
    public synchronized static String getSerialId() {
        StringBuffer sbf = new StringBuffer();
        SimpleDateFormat sfm = new SimpleDateFormat("yyMMddmmssSSS");
        sbf.append(sfm.format(new Date()));
        sbf.append(RandomStringUtils.random(3,false,true));
        return sbf.toString();
    }

    /**
     * 生成订单
     * @param shopId
     * @return
     */
    public synchronized static Long getSerialOrderId(Integer shopId) {
        StringBuffer sbf = new StringBuffer();
        sbf.append(shopId);
        sbf.append(RandomStringUtils.random(2,false,true));
        SimpleDateFormat sfm = new SimpleDateFormat("ssSSS");
        sbf.append(sfm.format(new Date()));
        sbf.append(RandomStringUtils.random(3,false,true));
        return Long.valueOf(sbf.toString());
    }
}
