package xll.baitaner.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xll.baitaner.service.entity.Spec;
import xll.baitaner.service.mapper.SpecMapper;

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
}
