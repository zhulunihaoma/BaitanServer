package xll.baitaner.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xll.baitaner.service.entity.Spec;
import xll.baitaner.service.mapper.SpecMapper;

import java.util.List;

@Service
public class SpecService {

    @Autowired
    private SpecMapper specMapper;

    /**
     * 新增商品规格 返回规格id
     * @param spec
     * @return
     */
    public int addSpec(Spec spec){
        boolean result = specMapper.insertSpec(spec) > 0;
        if (result){
            return spec.getId();
        }
        else {
            return -1;
        }
    }

    /**
     * 更新规格
     * @param spec
     * @return
     */
    public String updateSpec(Spec spec){
        boolean res = specMapper.updateSpec(spec) > 0;
        if (!res)
            return "update spec failed";
        return null;
    }

    /**
     * 更新规格数据中对应的商品id，用于新增商品
     * @param coId
     * @param specId
     * @return
     */ 
    public boolean updateSpecCoId(int coId, int specId)
    {
        return specMapper.updateSpecCoId(coId, specId) > 0;
    }

    /**
     * 删除规格
     * @param specId
     * @return
     */
    public String deleteSpec(int specId){
        boolean res = specMapper.deleteSpec(specId) > 0;
        if (!res)
            return "delete database failed";
        return null;
    }

    /**
     * 获取商品对应的规格
     * @param commodityId
     * @return
     */
    public List<Spec> getSpecList(int commodityId){
        return specMapper.selectSpecList(commodityId);
    }
}
