package com.xll.baitaner.service;

import com.xll.baitaner.entity.Version;

/**
 * @author zhulu
 * @version 1.0
 * @description com.xll.baitaner.service
 * @date 2019/8/9
 */
public interface VersionService {

    /**
     * 根据版本id获取活版本信息
     *
     * @param id
     * @return
     */
    Version selectVersionById(int id);
}
