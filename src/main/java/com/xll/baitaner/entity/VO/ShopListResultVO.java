package com.xll.baitaner.entity.VO;

import com.xll.baitaner.entity.Shop;
import lombok.Data;

import java.util.List;

/**
 * @author zhulu
 * @version 1.0
 * @description com.xll.baitaner.entity.VO
 * @date 2019/12/8
 */
@Data
public class ShopListResultVO {
    private List<Shop> data;

    private long count;
}
