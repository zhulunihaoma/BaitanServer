package com.xll.baitaner.service;

import com.xll.baitaner.entity.ExpressInfo;

import java.util.List;

/**
 * @author dengyy
 * @date 2019/8/27
 */
public interface ExpressInfoService {

    /**
     * 插入快递单号
     * @param express
     * @return
     */
    int addExpress(ExpressInfo express);

    /**
     * 查询店铺订单号
     * @param shopId
     * @param orderId
     * @return
     */
    List<String> queryExpressIds(Integer shopId, Integer orderId);
}
