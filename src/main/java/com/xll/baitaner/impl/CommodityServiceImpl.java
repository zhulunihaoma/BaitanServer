package com.xll.baitaner.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xll.baitaner.entity.Commodity;
import com.xll.baitaner.entity.Sort;
import com.xll.baitaner.entity.Spec;
import com.xll.baitaner.entity.VO.CommodityResultVO;
import com.xll.baitaner.mapper.CommodityMapper;
import com.xll.baitaner.service.CommodityService;
import com.xll.baitaner.service.SortService;
import com.xll.baitaner.service.SpecService;
import com.xll.baitaner.utils.LogUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：商品管理服务
 * 创建者：xie
 * 日期：201802/23
 **/
@Service
public class CommodityServiceImpl implements CommodityService {

    private final String TAG = "Baitaner-CommodityService";

    @Resource
    private CommodityMapper commodityMapper;

    @Resource
    private SpecService specService;

    @Resource
    private SortService sortService;

    /**
     * 新增商品
     * 附带规格 将商品 id插入规格表
     *
     * @param commodity
     * @return
     */
    @Override
    public boolean addCommodity(Commodity commodity) {
        int count = commodityMapper.countSortCoList(commodity.getShopId(), commodity.getSortId());
        commodity.setTurn(count);
        boolean res = commodityMapper.insertCommodity(commodity) > 0;

        if (res) {
            int coid = commodity.getId();
            if (commodity.getSpecs() != null) {
                int[] specList = commodity.getSpecs();
                if (specList.length > 0) {
                    for (int i = 0; i < specList.length; i++) {
                        LogUtils.info(TAG, coid + "---" + specList[i]);
                        boolean update = false;
                        update = specService.updateSpecCoId(coid, specList[i]);
                        if (!update) {
                            LogUtils.warn(TAG, "Update spec " + specList[i] + " commodity id " + coid + " failed");
                        }
                    }
                }
            }
            return true;
        } else {
            LogUtils.warn(TAG, "Add commodity failed");
            return false;
        }
    }

    /**
     * 修改商品信息
     *
     * @param commodity
     * @return
     */
    @Override
    public boolean updateCommodity(Commodity commodity) {
        return commodityMapper.updateCommodity(commodity) > 0;
    }

    /**
     * 分页查询店铺中对应分类的商品列表（上下架）
     *
     * @param shopId
     * @param storId
     * @return
     */
    @Override
    public CommodityResultVO getStorColist(int shopId, int storId, Integer offset, Integer size) {
        CommodityResultVO resultVO = new CommodityResultVO();
        Page<Commodity> page =
                PageHelper.startPage(offset, size).doSelectPage(() -> commodityMapper.selectSortCoList(shopId, storId));
        List<Commodity> commodityList = page.getResult();
        if (commodityList == null) {
            resultVO.setData(new ArrayList<>());
            resultVO.setCount(0);
            return resultVO;
        }
        for (int i = 0; i < commodityList.size(); i++) {
            Commodity commodity = commodityList.get(i);
            List<Spec> specList = specService.getSpecList(commodity.getId());
            commodity.setSpecList(specList);
        }
        resultVO.setData(commodityList);
        resultVO.setCount(page.getTotal());
        return resultVO;
    }

    /**
     * 获取店铺中所有商品列表
     * 上下架均显示
     *
     * @param shopId
     * @return
     */
    @Override
    public CommodityResultVO getAllCoList(int shopId, Integer offset, Integer size) {
        CommodityResultVO resultVO = new CommodityResultVO();
        Page<Commodity> page =
                PageHelper.startPage(offset, size).doSelectPage(() -> commodityMapper.selectAllCoList(shopId));
        List<Commodity> commodityList = page.getResult();
        if (commodityList == null) {
            resultVO.setData(new ArrayList<>());
            resultVO.setCount(0);
            return resultVO;
        }
        for (int i = 0; i < commodityList.size(); i++) {
            Commodity commodity = commodityList.get(i);
            List<Spec> specList = specService.getSpecList(commodity.getId());
            commodity.setSpecList(specList);
        }
        resultVO.setData(commodityList);
        resultVO.setCount(page.getTotal());
        return resultVO;
    }

    /**
     * 获取店铺已上架商品列表
     *
     * @param shopId
     * @return
     */
    @Override
    public CommodityResultVO getCoList(int shopId, Integer offset, Integer size) {
        CommodityResultVO resultVO = new CommodityResultVO();
        Page<Commodity> page = PageHelper.startPage(offset, size).doSelectPage(() -> commodityMapper.selectCoList(shopId));
        List<Commodity> commodityList = page.getResult();
        if (commodityList == null) {
            resultVO.setData(new ArrayList<>());
            resultVO.setCount(0);
            return resultVO;
        }
        for (int i = 0; i < commodityList.size(); i++) {
            Commodity commodity = commodityList.get(i);
            List<Spec> specList = specService.getSpecList(commodity.getId());
            commodity.setSpecList(specList);
        }
        resultVO.setData(commodityList);
        resultVO.setCount(page.getTotal());
        return resultVO;
    }

    /**
     * 查询店铺中对应分类的商品列表（上架）
     *
     * @param shopId
     * @param storId
     * @return
     */
    private List<Commodity> getStorOnColist(int shopId, int storId) {
        List<Commodity> commodityList = commodityMapper.selectSortCo(shopId, storId);
        for (int i = 0; i < commodityList.size(); i++) {
            Commodity commodity = commodityList.get(i);
            List<Spec> specList = specService.getSpecList(commodity.getId());
            commodity.setSpecList(specList);
        }
        return commodityList;
    }

    /**
     * 查询店铺中的商品列表（上架）,分类包裹
     *
     * @param shopid
     * @return
     */
    @Override
    public JSONArray getSortCo(int shopid) {
        List<Sort> sortList = sortService.getSortList(shopid);
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < sortList.size(); i++) {
            Sort sort = sortList.get(i);
            List<Commodity> commodityList = getStorOnColist(shopid, sort.getId());
            JSONArray array = JSONArray.fromObject(commodityList);
            JSONObject object = JSONObject.fromObject(sort);
            object.put("commodityList", commodityList);
            jsonArray.add(object);
        }
        return jsonArray;
    }

    /**
     * 获取单个商品的详情数据
     *
     * @param commodityId
     * @return
     */
    @Override
    public Commodity getCommodity(int commodityId) {
        Commodity commodity = commodityMapper.selectCommodity(commodityId);
        commodity.setSpecList(specService.getSpecList(commodityId));
        return commodity;
    }

    /**
     * 删除商品(更改为修改可用状态)
     *
     * @param commodityId
     * @return
     */
    @Override
    public boolean deleteCommodity(int commodityId) {
        return commodityMapper.updateCoDisabel(commodityId) > 0;
    }

    /**
     * 修改商品上下架状态
     *
     * @param commodityId
     * @return
     */
    @Override
    public boolean updateCoState(int commodityId) {
        return commodityMapper.updateCoState(commodityId) > 0;
    }

    /**
     * 分类下更新商品到指定位置顺序
     *
     * @param co
     * @param turn
     * @return
     */
    @Transactional
    @Override
    public String updateCoTurn(Commodity co, int turn) {
        String error = null;
        int coId = co.getId();
        int storId = co.getSortId();
        int coTurn = co.getTurn();

        if (turn < 0){
            error = "已经置顶商品无法上移";
            return error;
        }

        boolean res;
        if (coTurn > turn) {
            res = commodityMapper.additionTurn(storId, turn, coTurn) > 0;
            if (res) {
                if (commodityMapper.updateCoTurn(coId, turn) > 0) {
                    error = null;
                } else {
                    error = "上移商品失败";
                    throw new RuntimeException();
                }
            }
        } else if (coTurn < turn) {
            res = commodityMapper.subtractTurn(storId, turn, coTurn) > 0;
            if (res) {
                if (commodityMapper.updateCoTurn(coId, turn) > 0) {
                    error = null;
                } else {
                    error = "下移商品失败";
                    throw new RuntimeException();
                }
            }
        }else {
            error = "商品位置无需移动";
        }
        return error;
    }

    /**
     * 增加商品月销售量
     * @param commodityId
     * @param count
     * @return
     */
    @Override
    public boolean increaseMonthlySales(int commodityId, int count) {
        return commodityMapper.increaseMonthlySales(commodityId, count) > 0;
    }

    /**
     * 更新商品库存  减少或增加
     * @param commodityId
     * @param count
     * @param type  0：减少  1：增加
     * @return
     */
    @Override
    public boolean updateCommodityStock(int commodityId, int count, int type) {
        if (type == 0){
            //减少商品库存
            Commodity commodity = this.getCommodity(commodityId);
            if (commodity == null || (commodity.getStock() - count) < 0)
                return false;
            return commodityMapper.reduceCommodityStock(commodityId, count) > 0;
        }
        else {
            return commodityMapper.increaseCommodityStock(commodityId, count) > 0;
        }
    }

    /**
     * 更新商品规格库存  减少或增加
     * @param commodityId
     * @param specId
     * @param count
     * @param type  0：减少  1：增加
     * @return
     */
    @Override
    public boolean updateCommoditySpecStock(int commodityId, int specId, int count, int type) {
        if (type == 0){
            Spec spec = specService.getSpec(specId);
            if (spec == null || spec.getCommodityId() != commodityId || (spec.getStock() - count) < 0)
                return false;
            return commodityMapper.reduceCommoditySpecStock(commodityId, specId, count) > 0;
        }
        else {
            return commodityMapper.increaseCommoditySpecStock(commodityId, specId, count) > 0;
        }
    }
}
