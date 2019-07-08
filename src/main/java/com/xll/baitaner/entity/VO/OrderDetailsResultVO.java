package com.xll.baitaner.entity.VO;

import lombok.Data;

import java.util.List;

/**
 * @author dengyy
 * @date 19-7-8
 */
@Data
public class OrderDetailsResultVO {

    private List<OrderDetailsVO> data;

    private long count;
}
