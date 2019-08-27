package com.xll.baitaner.mapper;

import com.xll.baitaner.entity.Commodity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 描述：商品信息管理mapper
 * 创建者：xie
 * 日期：2019-2-23
 **/
@Repository
public interface CommodityMapper {

    /**
     * 新增商品信息
     *
     * @param co
     * @return
     */
    @Insert("INSERT INTO commodity (shopId,sortId,name,price,retail_price,postage,monthlySales,pictUrl,zipPicUrl," +
            "introduction," +
            "state,turn,stock,datetime) VALUES (#{co.shopId},#{co.sortId},#{co.name},#{co.price},#{co.retailPrice}," +
            "#{co.postage}," +
            "#{co.monthlySales},#{co.pictUrl},#{co.zipPicUrl}," +
            "#{co.introduction},#{co.state},#{co.turn},#{co.stock},NOW(0))")
    @Options(useGeneratedKeys = true, keyProperty = "co.id")
    int insertCommodity(@Param("co") Commodity co);

    /**
     * 更新商品信息
     *
     * @param co
     * @return
     */
    @Update("UPDATE commodity SET name=#{co.name},price=#{co.price},retail_price=#{retailPrice},postage=#{co.postage}," +
            "monthlySales=#{co.monthlySales}, " +
            "pictUrl = #{co.pictUrl},zipPicUrl=#{co.zipPicUrl},introduction=#{co.introduction},stock=#{co.stock} " +
            "WHERE id  = #{co.id}")
    int updateCommodity(@Param("co") Commodity co);

    /**
     * 分页查询店铺中对应分类的商品列表（上下架）
     *
     * @param shopId
     * @param sortId
     * @return
     */
    @Select("SELECT * FROM commodity WHERE shopId = #{shopId} AND sortId = #{sortId} AND activable = 1 ORDER BY turn")
    List<Commodity> selectSortCoList(@Param("shopId") int shopId, @Param("sortId") int sortId);

    /**
     * 查询店铺中对应分类的商品列表（上架）
     *
     * @param shopId
     * @param sortId
     * @return
     */
    @Select("SELECT * FROM commodity WHERE shopId = #{shopId} AND sortId = #{sortId} AND state = 1 AND activable = 1 ORDER BY turn")
    List<Commodity> selectSortCo(@Param("shopId") int shopId, @Param("sortId") int sortId);

    /**
     * 查询店铺中对应分类的所有商品总数
     *
     * @param shopId
     * @return
     */
    @Select("SELECT COUNT(*) FROM commodity WHERE shopId = #{shopId} AND sortId = #{sortId} AND activable = 1")
    int countSortCoList(@Param("shopId") int shopId, @Param("sortId") int sortId);

    /**
     * 分页查询店铺中所有商品列表
     * 先按分类排序，再按分类内商品排序
     * 上下架均显示
     *
     * @param shopId
     * @return
     */
    @Select("SELECT " +
            " co.*  " +
            "FROM " +
            " commodity co " +
            " JOIN ( SELECT * FROM sort WHERE shopId = #{shopId} ORDER BY sortOrder ) so ON co.sortId = so.id  " +
            "WHERE " +
            " co.activable = 1  " +
            "ORDER BY " +
            " so.sortOrder, " +
            " co.turn ASC")
    List<Commodity> selectAllCoList(@Param("shopId") int shopId);

    /**
     * 查询店铺中所有上架商品列表
     * 先按分类排序，再按分类内商品排序
     *
     * @param shopId
     * @return
     */
    @Select("SELECT " +
            " co.*  " +
            "FROM " +
            " commodity co " +
            " JOIN ( SELECT * FROM sort WHERE shopId = #{shopId} ORDER BY sortOrder ) so ON co.sortId = so.id  " +
            "WHERE " +
            " co.activable = 1 AND state = 1 " +
            "ORDER BY " +
            " so.sortOrder, " +
            " co.turn ASC")
    List<Commodity> selectCoList(@Param("shopId") int shopId);

    /**
     * 获取单个商品详情数据
     *
     * @param id
     * @return
     */
    @Select("SELECT * FROM commodity WHERE Id = #{id}")
    Commodity selectCommodity(@Param("id") int id);

    /**
     * 更新商品的disable状态，替代删除方法
     *
     * @param id
     * @return
     */
    @Update("UPDATE commodity SET activable = 0 WHERE id  = #{id}")
    int updateCoDisabel(@Param("id") int id);

    /**
     * 更新上下架状态
     *
     * @param id
     * @return
     */
    @Update("UPDATE commodity SET state = IF(state=0,1,0) WHERE id  = #{id} AND activable = 1")
    int updateCoState(@Param("id") int id);

    /**
     * 修改商品的位置顺序到指定位置
     *
     * @param coId
     * @param turn
     * @return
     */
    @Update("UPDATE commodity SET turn = #{turn} WHERE id  = #{coId}")
    int updateCoTurn(@Param("coId") int coId, @Param("turn") int turn);

    /**
     * start到end间的商的位置顺序+1
     *
     * @param sortId
     * @param start
     * @param end
     * @return
     */
    @Update("UPDATE commodity SET turn = turn + 1 WHERE sortId = #{sortId} AND turn >= #{start} AND turn <= #{end}")
    int additionTurn(@Param("sortId") int sortId, @Param("start") int start, @Param("end") int end);

    /**
     * start到end间的商的位置顺序-1
     *
     * @param sortId
     * @param start
     * @param end
     * @return
     */
    @Update("UPDATE commodity SET turn = turn - 1 WHERE sortId = #{sortId} AND turn >= #{start} AND turn <= #{end}")
    int subtractTurn(@Param("sortId") int sortId, @Param("start") int start, @Param("end") int end);
}
