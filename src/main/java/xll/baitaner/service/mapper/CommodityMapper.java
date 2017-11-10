package xll.baitaner.service.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import xll.baitaner.service.entity.Commodity;

import java.util.List;

/**
 * 描述：商品管理mapper
 * 创建者：xie
 * 日期：2017/10/10
 **/
@Repository
public interface CommodityMapper {

    /**
     * 查询店铺中所有商品列表
     * 上下架均显示
     * @param shopId
     * @return
     */
    @Select("SELECT * FROM commodity WHERE ShopId = #{shopId} AND Disable = 1 LIMIT #{page.offset},#{page.size}")
    List<Commodity> selectAllCoList(@Param("shopId") int shopId, @Param("page") Pageable page);

    /**
     * 查询店铺中所有商品总数
     * @param shopId
     * @return
     */
    @Select("SELECT COUNT(*) FROM commodity WHERE ShopId = #{shopId} AND Disable = 1")
    int countAllCoList(@Param("shopId") int shopId);

    /**
     * 查询店铺中所有上架商品列表
     * @param shopId
     * @return
     */
    @Select("SELECT * FROM commodity WHERE ShopId = #{shopId} AND State = 1 AND Disable = 1 " +
            "LIMIT #{page.offset},#{page.size}")
    List<Commodity> selectCoList(@Param("shopId") int shopId, @Param("page") Pageable page);

    /**
     * 查询店铺中所有上架商品列表总数
     * @param shopId
     * @return
     */
    @Select("SELECT COUNT(*) FROM commodity WHERE ShopId = #{shopId} AND State = 1 AND Disable = 1")
    int countCoList(@Param("shopId") int shopId);

    /**
     * 获取单个商品详情数据
     * @param id
     * @return
     */
    @Select("SELECT * FROM commodity WHERE Id = #{id}")
    Commodity selectCommodity(@Param("id") int id);

    /**
     * 新增商品信息
     * @param co
     * @return
     */
    @Insert("INSERT INTO commodity (ShopId,Name,Price,MonthlySales,Praise,PictUrl,Introduction,State) " +
            "VALUES (#{co.shopId},#{co.name},#{co.price},#{co.monthlySales}," +
            "#{co.praise},#{co.pictUrl},#{co.introduction},#{co.state})")
    int insertCommodity(@Param("co") Commodity co);

    /**
     * 更新商品信息
     * @param co
     * @return
     */
    @Update("UPDATE commodity SET Name = #{co.name}, Price = #{co.price}, MonthlySales = #{co.monthlySales}, " +
            "Praise = #{co.praise}, PictUrl = #{co.pictUrl}, Introduction = #{co.introduction}, State = #{co.state} " +
            "WHERE Id  = #{co.id}")
    int updateCommodity(@Param("co") Commodity co);

    /**
     * 删除商品信息
     * @param id
     * @return
     */
    @Delete("DELETE FROM commodity WHERE Id = #{id}")
    int deleteCommodity(@Param("id") int id);

    /**
     * 更新商品的disable状态，替代删除方法
     * @param id
     * @return
     */
    @Update("UPDATE commodity SET Disable = 0 WHERE Id  = #{id}")
    int updateCoDisabel(@Param("id") int id);

    /**
     * 更新上下架状态
     * @param id
     * @return
     */
    @Update("UPDATE commodity SET State = IF(State=0,1,0) WHERE Id  = #{id}")
    int updateCoState(@Param("id") int id);
}
