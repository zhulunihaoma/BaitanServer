package com.xll.baitaner.service;

import com.xll.baitaner.entity.Sort;

import java.util.List;

/**
 * @author denghuohuo 2019/6/25
 */
public interface SortService {

    /**
     * 新增分类
     *
     * @param shopId
     * @param sortName
     * @return
     */
    String addSort(int shopId, String sortName);

    /**
     * 更新分类名称
     *
     * @param sort
     * @return
     */
    String updateSortName(Sort sort);

    /**
     * 更新分类位置
     *
     * @param sort
     * @param order
     * @return
     */
    boolean updateSortOrder(Sort sort, int order);

    /**
     * 删除分类
     *
     * @param sort
     * @return
     */
    boolean deleteSort(Sort sort);

    /**
     * 按顺序获取分类列表
     *
     * @param shopId
     * @return
     */
    List<Sort> getSortList(int shopId);
}
