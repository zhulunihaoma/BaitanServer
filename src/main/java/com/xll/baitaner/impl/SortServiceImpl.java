package com.xll.baitaner.impl;

import com.xll.baitaner.entity.Sort;
import com.xll.baitaner.mapper.SortMapper;
import com.xll.baitaner.service.SortService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 描述：分类管理业务
 * 创建者：XLL
 * 日期：2018/11/26.
 **/
@Service
public class SortServiceImpl implements SortService {

    @Resource
    private SortMapper sortMapper;

    /**
     * 新增分类
     *
     * @param shopId
     * @param sortName
     * @return
     */
    @Override
    public String addSort(int shopId, String sortName) {
        Sort sort = new Sort();
        sort.setShopId(shopId);
        sort.setSortName(sortName);

        //新增的分类位置为分类总数
        int count = sortMapper.countSortList(shopId);
        sort.setSortOrder(count);

        boolean result = sortMapper.addSort(sort) > 0;

        return result ? null : "Add Failed";
    }

    /**
     * 更新分类名称
     *
     * @param sort
     * @return
     */
    @Override
    public String updateSortName(Sort sort) {
        boolean result = sortMapper.updateSortName(sort.getId(), sort.getSortName()) > 0;
        return result ? null : "Update Failed";
    }


    /**
     * 更新分类位置
     *
     * @param sort
     * @param order
     * @return
     */
    @Transactional
    @Override
    public boolean updateSortOrder(Sort sort, int order) {
        int shopId = sort.getShopId();
        int curOrder = sort.getSortOrder();
        boolean result;
        if (curOrder > order) {
            result = sortMapper.additionOrder(shopId, order, curOrder) > 0;
            if (result) {
                if (sortMapper.updateSortOrder(sort.getId(), order) > 0) {
                    return true;
                } else {
                    throw new RuntimeException();
                }
            }
        } else if (curOrder < order) {
            result = sortMapper.subtractOrder(shopId, curOrder, order) > 0;
            if (result) {
                if (sortMapper.updateSortOrder(sort.getId(), order) > 0) {
                    return true;
                } else {
                    throw new RuntimeException();
                }
            }
        }

        return false;
    }

    /**
     * 删除分类
     *
     * @param sort
     * @return
     */
    @Transactional
    @Override
    public boolean deleteSort(Sort sort) {
        int sortId = sort.getId();
        boolean result = sortMapper.deleteSort(sortId) > 0;

        if (result) {
            int count = sortMapper.countSortList(sort.getShopId());

            if (sort.getSortOrder() < count - 1) {
                boolean re = sortMapper.subtractOrder(sort.getShopId(), sort.getSortOrder(), count) > 0;
                if (re) {
                    return true;
                } else {
                    throw new RuntimeException();
                }
            } else if (sort.getSortOrder() == count - 1) {
                return true;
            }
        }

        return false;
    }

    /**
     * 按顺序获取分类列表
     *
     * @param shopId
     * @return
     */
    @Override
    public List<Sort> getSortList(int shopId) {
        return sortMapper.selectSortList(shopId);
    }
}
