package xll.baitaner.service.service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xll.baitaner.service.entity.Commodity;
import xll.baitaner.service.entity.Sort;
import xll.baitaner.service.entity.Spec;
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

    @Autowired
    private SortService sortService;

    /**
     * 新增商品
     * 附带规格 将商品 id插入规格表
     * @param commodity
     * @param specList
     * @return
     */
    public boolean addCommodity(Commodity commodity){
        int count = commodityMapper.countSortCoList(commodity.getShopId(), commodity.getSortId());
        commodity.setTurn(count);
        boolean res = commodityMapper.insertCommodity(commodity) > 0;

        if (res){
            int coid = commodity.getId();
            if (commodity.getSpecs() != null)
            {
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
     * 分页查询店铺中对应分类的商品列表（上下架）
     * @param shopId
     * @param storId
     * @param pageable
     * @return
     */
    public PageImpl<Commodity> getStorColist(int shopId, int storId, Pageable pageable)
    {
        List<Commodity> commodityList = commodityMapper.selectSortCoList(shopId, storId, pageable);
        for (int i=0; i<commodityList.size(); i++) {
            Commodity commodity = commodityList.get(i);
            List<Spec> specList = specService.getSpecList(commodity.getId());
            commodity.setSpecList(specList);
        }
        int count = commodityMapper.countSortCoList(shopId, storId);
        return new PageImpl<Commodity>(commodityList, pageable, count);
    }

    /**
     * 获取店铺中所有商品列表
     * 上下架均显示
     * @param shopId
     * @return
     */
    public PageImpl<Commodity> getAllCoList(int shopId, Pageable pageable){
        List<Commodity> commodityList = commodityMapper.selectAllCoList(shopId, pageable);
        for (int i=0; i<commodityList.size(); i++) {
            Commodity commodity = commodityList.get(i);
            List<Spec> specList = specService.getSpecList(commodity.getId());
            commodity.setSpecList(specList);
        }
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
        for (int i=0; i<commodityList.size(); i++) {
            Commodity commodity = commodityList.get(i);
            List<Spec> specList = specService.getSpecList(commodity.getId());
            commodity.setSpecList(specList);
        }
        int count = commodityMapper.countCoList(shopId);
        return new PageImpl<Commodity>(commodityList, pageable, count);
    }

    /**
     * 查询店铺中对应分类的商品列表（上架）
     * @param shopId
     * @param storId
     * @param pageable
     * @return
     */
    public List<Commodity> getStorOnColist(int shopId, int storId)
    {
        List<Commodity> commodityList = commodityMapper.selectSortCo(shopId, storId);
        for (int i=0; i<commodityList.size(); i++) {
            Commodity commodity = commodityList.get(i);
            List<Spec> specList = specService.getSpecList(commodity.getId());
            commodity.setSpecList(specList);
        }
        return commodityList;
    }

    /**
     * 查询店铺中的商品列表（上架）,分类包裹
     * @param shopid
     * @return
     */
    public JSONArray getSortCo(int shopid){
        List<Sort> sortList = sortService.getSortList(shopid);
        JSONArray jsonArray = new JSONArray();
        for (int i=0; i<sortList.size(); i++) {
            Sort sort = sortList.get(i);
            List<Commodity> commodityList = getStorOnColist(shopid, sort.getId());
            JSONArray array = JSONArray.fromObject(commodityList);
            JSONObject object = JSONObject.fromObject(sort);
            object.put("commodityList", array);
            jsonArray.add(object);
        }
        return jsonArray;
    }

    /**
     * 获取单个商品的详情数据
     * @param commodityId
     * @return
     */
    public Commodity getCommodity(int commodityId){
        Commodity commodity = commodityMapper.selectCommodity(commodityId);
        commodity.setSpecList(specService.getSpecList(commodityId));
        return commodity;
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

    /**
     * 分类下更新商品到指定位置顺序
     * @param co
     * @param turn
     * @return
     */
    @Transactional
    public  boolean updateCoTurn(Commodity co, int turn)
    {
        int coId = co.getId();
        int storId = co.getSortId();
        int coTurn = co.getTurn();
        boolean res;
        if (coTurn > turn){
            res = commodityMapper.additionTurn(storId, turn, coTurn) > 0;
            if (res){
                if (commodityMapper.updateCoTurn(coId, turn) > 0){
                    return true;
                }else {
                    throw new RuntimeException();
                }
            }
        }
        else if (coTurn < turn){
            res = commodityMapper.subtractTurn(storId, turn, coTurn) > 0;
            if (res){
                if (commodityMapper.updateCoTurn(coId, turn) > 0){
                    return true;
                }else {
                    throw new RuntimeException();
                }
            }
        }
        return false;
    }
}
