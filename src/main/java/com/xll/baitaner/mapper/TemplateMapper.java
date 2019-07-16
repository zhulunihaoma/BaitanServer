package com.xll.baitaner.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * 接口名：TemplateMapper
 * 描述：
 * 创建者：xie
 * 日期：2019/7/10/010
 **/
@Repository
public interface TemplateMapper {

    /**
     * 插入formid数据
     * @param openId
     * @param formId
     * @return
     */
    @Insert("INSERT INTO template_formid (openId,formId,creatDate) VALUES (#{openId},#{formId},CURDATE())")
    int insertFormid(@Param("openId") String openId, @Param("formId") String formId);

    /**
     * 查询是否已存在formId
     * @param openId
     * @return
     */
    @Select("SELECT COUNT(*) FROM template_formid WHERE openId = #{openId} AND formId = #{formId}")
    int selectCountFormid(@Param("openId") String openId, @Param("formId") String formId);

    /**
     * 查询用户在7天有效期内可用的第一个formid
     * @param openId
     * @return
     */
    @Select("SELECT formId FROM template_formid " +
            "WHERE openId = #{openId} AND " +
            "isUsed = 0 AND " +
            "DATEDIFF(CURDATE(),creatDate) < 7 " +
            "LIMIT 1  ")
    String selectFormId(@Param("openId") String openId);

    /**
     * 将已用过的formid状态更新为不可用
     * @param openId
     * @param formId
     * @return
     */
    @Update("UPDATE template_formid SET isUsed = 1 WHERE openId = #{openId} AND formId = #{formId}")
    int updateFormidUsed(@Param("openId") String openId, @Param("formId") String formId);

    /**
     * 删除超过期限天数的formid
     * @return
     */
    @Delete("DELETE FROM template_formid WHERE DATEDIFF(CURDATE(),creatDate) >= #{day}")
    int deleteFormid(@Param("day") int day);
}
