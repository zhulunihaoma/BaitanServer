package com.xll.baitaner.entity.VO;

import com.xll.baitaner.entity.Commodity;
import lombok.Data;

import java.util.List;

/**
 * 返回商品结果
 * @author dengyy
 * @date 19-7-8
 */
@Data
public class CommodityResultVO {

    private List<Commodity> data;

    private long count;
}
