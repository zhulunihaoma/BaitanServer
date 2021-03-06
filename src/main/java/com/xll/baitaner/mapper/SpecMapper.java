package com.xll.baitaner.mapper;

import com.xll.baitaner.entity.Spec;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 规格数据接口
 */
@Repository
public interface SpecMapper {

    /**
     * 新增规格
     *
     * @param spec
     * @return
     */
    @Insert("INSERT INTO spec (commodityId, name, price, retail_price,stock) VALUES (#{spec.commodityId}, #{spec" +
            ".name}, #{spec.price}, #{spec.retailPrice},#{spec.stock})")
    @Options(useGeneratedKeys = true, keyProperty = "spec.id")
    int insertSpec(@Param("spec") Spec spec);

    @Select("SELECT * FROM spec WHERE id = #{specId}")
    Spec selectSpec(@Param("specId") int specId);

    /**
     * 更新规格数据
     *
     * @param spec
     * @return
     */
    @Update("UPDATE spec SET commodityId = #{spec.commodityId}, name = #{spec.name} ,price = #{spec.price}, " +
            "retail_price=#{spec.retailPrice},stock = #{spec.stock} WHERE id = #{spec.id}")
    int updateSpec(@Param("spec") Spec spec);

    /**
     * 更新规格数据中对应的商品id，用于新增商品
     *
     * @param coId
     * @param specId
     * @return
     */
    @Update("UPDATE spec SET commodityId = #{coId} WHERE id = #{specId}")
    int updateSpecCoId(@Param("coId") int coId, @Param("specId") int specId);

    /**
     * 删除规格
     *
     * @param id
     * @return
     */
    @Delete("DELETE FROM spec WHERE id = #{id}")
    int deleteSpec(@Param("id") int id);

    /**
     * 获取商品对应的规格
     *
     * @param commodityId
     * @return
     */
    @Select("SELECT * FROM spec WHERE commodityId = #{commodityId}")
    List<Spec> selectSpecList(@Param("commodityId") int commodityId);
}
