package com.xll.baitaner.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 金额相关操作
 *
 * @author dengyy
 * @date 19-7-23
 */
public class MoneyUtil {

    /**
     * 元转分
     *
     * @param num
     * @return
     */
    public static String changeY2F(String num) {
        //小数点两位
        BigDecimal yuan = new BigDecimal(num).setScale(2, RoundingMode.HALF_UP);
        return yuan.multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP).toPlainString();
    }

    /**
     * 分转元
     * @param num
     * @return
     */
    public static String changeF2Y(String num) {
        return new BigDecimal(num).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).toPlainString();
    }
}
