package com.xll.baitaner.impl;

import com.xll.baitaner.entity.ExpressInfo;
import com.xll.baitaner.mapper.ExpressInfoMapper;
import com.xll.baitaner.service.ExpressInfoService;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dengyy
 * @date 2019/8/27
 */
@Data
public class ExpressInfoServiceImpl implements ExpressInfoService {

    @Resource
    ExpressInfoMapper expressInfoMapper;

    @Override
    public int addExpress(ExpressInfo express) {
        int exId = expressInfoMapper.insertOne(express);
        if (exId > 0) {
            return express.getId();
        }
        return 0;
    }

    @Override
    public List<Integer> queryExpressIds(Integer shopId, Integer orderId) {
        List<Integer> integers = expressInfoMapper.selectExpressIdsById(shopId, orderId);
        if (CollectionUtils.isEmpty(integers)) {
            return new ArrayList<>();
        }
        return integers;
    }
}
