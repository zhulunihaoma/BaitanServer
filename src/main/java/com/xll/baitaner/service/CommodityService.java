package com.xll.baitaner.service;

import com.xll.baitaner.entity.Commodity;
import com.xll.baitaner.entity.VO.CommodityResultVO;
import net.sf.json.JSONArray;

/**
 * @author denghuohuo 2019/6/25
 */
public interface CommodityService {

    /**
     * 新增商品
     * 附带规格 将商品 id插入规格表
     *
     * @param commodity
     * @return
     */
    boolean addCommodity(Commodity commodity);

    /**
     * 修改商品信息
     *
     * @param commodity
     * @return
     */
    boolean updateCommodity(Commodity commodity);

    /**
     * 分页查询店铺中对应分类的商品列表（上下架）
     *
     * @param shopId
     * @param storId
     * @return
     */
    CommodityResultVO getStorColist(int shopId, int storId, Integer offset, Integer size);

    /**
     * 获取店铺中所有商品列表
     * 上下架均显示
     *
     * @param shopId
     * @return
     */
    CommodityResultVO getAllCoList(int shopId, Integer offset, Integer size);

    /**
     * 获取店铺已上架商品列表
     *
     * @param shopId
     * @return
     */
    CommodityResultVO getCoList(int shopId, Integer offset, Integer size);

    /**
     * 查询店铺中的商品列表（上架）,分类包裹
     *
     * @param shopid
     * @return
     */
    JSONArray getSortCo(int shopid);

    /**
     * 获取单个商品的详情数据
     *
     * @param commodityId
     * @return
     */
    Commodity getCommodity(int commodityId);

    /**
     * 删除商品(更改为修改可用状态)
     *
     * @param commodityId
     * @return
     */
    boolean deleteCommodity(int commodityId);

    /**
     * 修改商品上下架状态
     *
     * @param commodityId
     * @return
     */

    boolean updateCoState(int commodityId);

    /**
     * 分类下更新商品到指定位置顺序
     *
     * @param co
     * @param turn
     * @return
     */
    String updateCoTurn(Commodity co, int turn);

    /**
     * 增加商品月销售量
     * @param commodityId
     * @param count
     * @return
     */
    boolean increaseMonthlySales(int commodityId, int count);

    /**
     * 更新商品库存  减少或增加
     * @param commodityId
     * @param count
     * @param type  0：减少  1：增加
     * @return
     */
    boolean updateCommodityStock(int commodityId, int count, int type);

    /**
     * 更新商品规格库存  减少或增加
     * @param commodityId
     * @param count
     * @param type  0：减少  1：增加
     * @return
     */
    boolean updateCommoditySpecStock(int commodityId, int specId, int count, int type);
}
