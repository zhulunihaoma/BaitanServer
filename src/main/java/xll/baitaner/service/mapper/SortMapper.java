package xll.baitaner.service.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import xll.baitaner.service.entity.Sort;

import java.util.List;

/**
 * 店铺首页分类数据操作类
 */
@Repository
public interface SortMapper {

    /**
     * 新增分类 //todo order为分类条目总数
     * @param sort
     * @return
     */
    @Insert("INSERT INTO sort (shopId,sortName,sortOrder) VALUES (#{sort.shopId},#{sort.sortName},#{sort.sortOrder})")
    int addSort(@Param("sort") Sort sort);

    /**
     * 更新类别 //todo order需要动态变化
     * @param sort
     * @return
     */
    @Update("UPDATE sort SET sortName = #{sort.sortName}, sortOrder = #{sort.sortOrder} WHERE id = #{sort.id}")
    int updateSort(@Param("sort") Sort sort);

    /**
     * 删除分类 //todo order需要动态变化
     * @param sortId
     * @return
     */
    @Delete("DELETE FROM sort WHERE id = #{sortId}")
    int deleteSort(@Param("sortId") int sortId);

    /**
     * 按顺序获取分类列表
     * @param shopId
     * @return
     */
    @Select("SLECT * FROM sort WHERE shopId = #{shopId} ORDER BY sortOrder")
    List<Sort> selectSortList(@Param("shopId") int shopId);
}
