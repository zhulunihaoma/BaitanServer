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
     * 新增分类
     * @param sort
     * @return
     */
    @Insert("INSERT INTO sort (shopId,sortName,sortOrder) VALUES (#{sort.shopId},#{sort.sortName},#{sort.sortOrder})")
    int addSort(@Param("sort") Sort sort);

    /**
     * 获取当前分类总数
     * @param shopId
     * @return
     */
    @Select("SELECT COUNT(*) FROM sort WHERE shopId = #{shopId}")
    int countSortList(@Param("shopId") int shopId);

    /**
     * 更新分类名称
     * @param sordId
     * @param name
     * @return
     */
    @Update("UPDATE sort SET sortName = #{name} WHERE id = #{sordId}")
    int updateSortName(@Param("sordId") int sordId, @Param("name") String name);

    /**
     * 更新分类位置
     * @param sordId
     * @param order
     * @return
     */
    @Update("UPDATE sort SET sortOrder = #{order} WHERE id = #{sordId}")
    int updateSortOrder(@Param("sordId") int sordId, @Param("order") int order);

    /**
     *  start到end间的sort的位置+1
     * @param shopId
     * @param start
     * @param end
     * @return
     */
    @Update("UPDATE sort SET sortOrder = sortOrder + 1 WHERE shopId = #{shopId} AND sortOrder >= #{start} AND sortOrder <= #{end}")
    int additionOrder(@Param("shopId") int shopId, @Param("start") int start, @Param("end") int end);

    /**
     *  start到end间的sort的位置-1
     * @param shopId
     * @param start
     * @param end
     * @return
     */
    @Update("UPDATE sort SET sortOrder = sortOrder - 1 WHERE shopId = #{shopId} AND sortOrder >= #{start} AND sortOrder <= #{end}")
    int subtractOrder(@Param("shopId") int shopId, @Param("start") int start, @Param("end") int end);

    /**
     * 删除分类 //todo order需要动态变化,对应商品也要删除
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
    @Select("SELECT * FROM sort WHERE shopId = #{shopId} ORDER BY sortOrder")
    List<Sort> selectSortList(@Param("shopId") int shopId);
}
