package com.xll.baitaner.entity.VO;

import com.xll.baitaner.entity.ShopWallet;
import lombok.Data;

import java.util.List;

/**
 * @author dengyy
 * @date 19-7-8
 */
@Data
public class WithdrawResultVO {

    private List<WithdrawVO> data;

    private long count;
}
