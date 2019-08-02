package com.xll.baitaner.entity.VO;
import lombok.Data;

import java.util.List;

/**
 * @author zhulu
 * @version 1.0
 * @description com.xll.baitaner.entity.VO
 * @date 2019/8/2
 */
@Data
public class ActivityRecordResultVO {
    private List<ActivityRecordVO> data;
    private long count;
}
