package com.xll.baitaner.mapper;

import com.xll.baitaner.entity.Version;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @author zhulu
 * @version 1.0
 * @description com.xll.baitaner.mapper
 * @date 2019/8/9
 */
@Repository
public interface VersionMapper {
    /**
     * 根据活动id查询版本详情
     *
     * @param id
     * @return
     */
    @Select("SELECT * FROM version WHERE id = #{id}")
    Version selectVersionById(@Param("id") int id);


}
