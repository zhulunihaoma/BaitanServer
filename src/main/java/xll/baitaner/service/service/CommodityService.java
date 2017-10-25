package xll.baitaner.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xll.baitaner.service.entity.Commodity;
import xll.baitaner.service.mapper.CommodityMapper;

import java.util.List;

/**
 * 描述：商品管理服务
 * 创建者：xie
 * 日期：2017/10/10
 **/
@Service
public class CommodityService {

    @Autowired
    private CommodityMapper commodityMapper;

    /**
     * 获取店铺中所有商品列表
     * 上下架均显示
     * @param shopId
     * @return
     */
    public PageImpl<Commodity> getAllCoList(int shopId, Pageable pageable){
        List<Commodity> commodityList = commodityMapper.selectAllCoList(shopId, pageable);
        int count = commodityMapper.countAllCoList(shopId);
        return new PageImpl<Commodity>(commodityList, pageable, count);
    }

    /**
     * 获取店铺已上架商品列表
     * @param shopId
     * @return
     */
    public PageImpl<Commodity> getCoList(int shopId, Pageable pageable){
        List<Commodity> commodityList = commodityMapper.selectCoList(shopId, pageable);
        int count = commodityMapper.countCoList(shopId);
        return new PageImpl<Commodity>(commodityList, pageable, count);
    }

    /**
     * 获取单个商品的详情数据
     * @param commodityId
     * @return
     */
    public Commodity getCommodity(int commodityId){
        return commodityMapper.selectCommodity(commodityId);
    }

    /**
     * 新增商品
     * @param commodity
     * @return
     */
    public boolean addCommodity(Commodity commodity){
        return commodityMapper.insertCommodity(commodity) > 0;
    }

    /**
     * 修改商品信息
     * @param commodity
     * @return
     */
    public boolean updateCommodity(Commodity commodity){
        return commodityMapper.updateCommodity(commodity) > 0;
    }

    /**
     * 修改商品信息（原商品disable改为false，添加新商品）
     * @param commodity
     * @return
     */
    @Transactional
    public boolean editCommodity(Commodity commodity){
        int id = commodity.getId();
        boolean re1 = deleteCommodity(id);
        boolean re2 = addCommodity(commodity);
        return re1 && re2;
    }

    /**
     * 删除商品(更改为修改可用状态)
     * @param id
     * @return
     */
    public boolean deleteCommodity(int commodityId){
//        return commodityMapper.deleteCommodity(commodityId) > 0;
        return commodityMapper.updateCoDisabel(commodityId) > 0;
    }

    /**
     * 修改商品上下架状态
     * @param commodityId
     * @return
     */
    public boolean updateCoState(int commodityId){
        return commodityMapper.updateCoState(commodityId) > 0;
    }
}
