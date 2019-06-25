package com.xll.baitaner.service;

import com.xll.baitaner.entity.Spec;

import java.util.List;

/**
 * @author denghuohuo 2019/6/25
 */
public interface SpecService {

    /**
     * 新增商品规格 返回规格id
     *
     * @param spec
     * @return
     */
    int addSpec(Spec spec);

    /**
     * 获取规格详情
     *
     * @param specId
     * @return
     */
    Spec getSpec(int specId);

    /**
     * 更新规格
     *
     * @param spec
     * @return
     */
    String updateSpec(Spec spec);

    /**
     * 更新规格数据中对应的商品id，用于新增商品
     *
     * @param coId
     * @param specId
     * @return
     */
    boolean updateSpecCoId(int coId, int specId);

    /**
     * 删除规格
     *
     * @param specId
     * @return
     */
    String deleteSpec(int specId);

    /**
     * 获取商品对应的规格
     *
     * @param commodityId
     * @return
     */
    List<Spec> getSpecList(int commodityId);
}
