package com.xll.baitaner.impl;

import com.xll.baitaner.entity.Spec;
import com.xll.baitaner.mapper.SpecMapper;
import com.xll.baitaner.service.SpecService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SpecServiceImpl implements SpecService {

    @Resource
    private SpecMapper specMapper;

    /**
     * 新增商品规格 返回规格id
     *
     * @param spec
     * @return
     */
    @Override
    public int addSpec(Spec spec) {
        boolean result = specMapper.insertSpec(spec) > 0;
        if (result) {
            return spec.getId();
        } else {
            return -1;
        }
    }

    /**
     * 获取规格详情
     *
     * @param specId
     * @return
     */
    @Override
    public Spec getSpec(int specId) {
        return specMapper.selectSpec(specId);
    }

    /**
     * 更新规格
     *
     * @param spec
     * @return
     */
    @Override
    public String updateSpec(Spec spec) {
        boolean res = specMapper.updateSpec(spec) > 0;
        if (!res)
            return "update spec failed";
        return null;
    }

    /**
     * 更新规格数据中对应的商品id，用于新增商品
     *
     * @param coId
     * @param specId
     * @return
     */
    @Override
    public boolean updateSpecCoId(int coId, int specId) {
        return specMapper.updateSpecCoId(coId, specId) > 0;
    }

    /**
     * 删除规格
     *
     * @param specId
     * @return
     */
    @Override
    public String deleteSpec(int specId) {
        boolean res = specMapper.deleteSpec(specId) > 0;
        if (!res)
            return "delete database failed";
        return null;
    }

    /**
     * 获取商品对应的规格
     *
     * @param commodityId
     * @return
     */
    @Override
    public List<Spec> getSpecList(int commodityId) {
        return specMapper.selectSpecList(commodityId);
    }
}
