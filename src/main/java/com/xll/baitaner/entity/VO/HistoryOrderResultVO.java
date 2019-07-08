package com.xll.baitaner.entity.VO;

import lombok.Data;

import java.util.List;

/**
 * @author dengyy
 * @date 19-7-8
 */
@Data
public class HistoryOrderResultVO {

    private List<HistoryOrderVO> data;

    private long count;
}
