package xll.baitaner.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xll.baitaner.service.entity.Commodity;
import xll.baitaner.service.mapper.CommodityMapper;
import xll.baitaner.service.utils.LogUtils;

import java.util.List;

/**
 * 描述：商品管理服务
 * 创建者：xie
 * 日期：201802/23
 **/
@Service
public class CommodityService {

    private String TAG = "Baitaner-CommodityService";
    @Autowired
    private CommodityMapper commodityMapper;

    @Autowired
    private SpecService specService;

    /**
     * 新增商品
     * 附带规格 将商品 id插入规格表
     * @param commodity
     * @param specList
     * @return
     */
    public boolean addCommodity(Commodity commodity){
        int count = commodityMapper.countAllCoList(commodity.getShopId());
        commodity.setTurn(count);
        boolean res = commodityMapper.insertCommodity(commodity) > 0;

        if (res){
            int coid = commodity.getId();
            int[] specList = commodity.getSpecs();
            if (specList.length > 0)
            {
                for (int i = 0; i < specList.length; i++){
                    LogUtils.info(TAG, coid + "---" + specList[i]);
                    boolean update = false;
                    update = specService.updateSpecCoId(coid, specList[i]);
                    if (!update){
                        LogUtils.warn(TAG, "Update spec " + specList[i] + " commodity id " + coid + " failed");
                    }
                }
            }
            return true;
        }else {
            LogUtils.warn(TAG, "Add commodity failed");
            return false;
        }
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
     * 删除商品(更改为修改可用状态)
     * @param commodityId
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
