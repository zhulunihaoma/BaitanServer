package com.xll.baitaner.impl;

import com.xll.baitaner.entity.ExpressInfo;
import com.xll.baitaner.mapper.ExpressInfoMapper;
import com.xll.baitaner.service.ExpressInfoService;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dengyy
 * @date 2019/8/27
 */
@Data
@Service
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
    public List<String> queryExpressIds(Integer shopId, Integer orderId) {
        List<String> integers = expressInfoMapper.selectExpressIdsById(shopId, orderId);
        if (CollectionUtils.isEmpty(integers)) {
            return new ArrayList<>();
        }
        return integers;
    }
}
