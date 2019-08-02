package com.xll.baitaner.entity.VO;

import com.xll.baitaner.entity.*;
import lombok.Data;


/**
 * @author zhulu
 * @version 1.0
 * @description com.xll.baitaner.entity.VO
 * @date 2019/8/2
 */
@Data
public class ActivityRecordVO {
    private Activity activity;
    private Commodity commodity;
    private Shop shop;
    private WXUserInfo wxUserInfo;
    private ActivityRecord activityRecord;

}
