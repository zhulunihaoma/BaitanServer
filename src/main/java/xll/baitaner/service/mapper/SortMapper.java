package xll.baitaner.service.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

/**
 * 店铺首页分类数据操作类
 */
@Repository
public interface SortMapper {

    @Insert("INSERT INTO sort (shopId.sortName,sortOrder) VALUES (#{sort.shopId},#{sort.sortName},#{sort.sortOrder})")
    int addSort(@Param("sort") Sort sort);
}
