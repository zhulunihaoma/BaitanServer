package com.xll.baitaner.entity.VO;

import lombok.Data;

import java.util.List;

/**
 * @author dengyy
 * @date 19-7-23
 */
@Data
public class WithdrawDateVO {

    private String date;

    private List<WithdrawVO> list;
}
