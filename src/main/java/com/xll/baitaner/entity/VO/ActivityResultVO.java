package com.xll.baitaner.entity.VO;

import java.util.List;
import lombok.Data;

/**
 * @author zhulu
 * @version 1.0
 * @description com.xll.baitaner.entity.VO
 * @date 2019/7/30
 */
@Data
public class ActivityResultVO {
    private  List<ActivityVO> data;
    private long count;
}
