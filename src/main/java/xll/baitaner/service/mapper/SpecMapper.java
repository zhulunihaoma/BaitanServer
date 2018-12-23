package xll.baitaner.service.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import xll.baitaner.service.entity.Spec;

/**
 * 规格数据接口
 */
@Repository
public interface SpecMapper {

    /**
     * 新增规格
     * @param spec
     * @return
     */
    @Insert("INSERT INTO spec (commodityId, name, price, stock) VALUES (#{spec.commodityId}, #{spec.name}, #{spec.price}, #{spec.stock})")
    @Options(useGeneratedKeys = true, keyProperty = "spec.id")
    int insertSpec(@Param("spec") Spec spec);

    /**
     * 更新规格
     * @param spec
     * @return
     */
    @Update("UPDATE spec SET commodityId = #{spec.commodityId}, name = #{spec.name} ,price = #{spec.price}, " +
            "stock = #{spec.stock} WHERE id = #{spec.id}")
    int updateSpec(@Param("spec") Spec spec);

    /**
     * 删除规格
     * @param id
     * @return
     */
    @Delete("DELETE FROM spec WHERE id = #{id}")
    int deleteSpec(@Param("id") int id);
}
