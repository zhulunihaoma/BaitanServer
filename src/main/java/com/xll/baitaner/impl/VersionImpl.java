package com.xll.baitaner.impl;

import com.xll.baitaner.entity.Version;
import com.xll.baitaner.mapper.VersionMapper;
import com.xll.baitaner.service.ActivityService;
import com.xll.baitaner.service.VersionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zhulu
 * @version 1.0
 * @description com.xll.baitaner.impl
 * @date 2019/8/9
 */
@Service
public class VersionImpl implements VersionService {
    @Resource
    private VersionMapper versionMapper;
    /**
     * 根据版本id获取活版本信息
     *
     * @param id
     * @return
     */
    @Override
    public Version selectVersionById(int id){
        return versionMapper.selectVersionById(id);
   }
}
