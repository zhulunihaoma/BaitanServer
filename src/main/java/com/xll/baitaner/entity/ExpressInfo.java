package com.xll.baitaner.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author dengyy
 * @date 2019/8/27
 */
@Data
public class ExpressInfo {

    private Integer id;

    private Integer shopId;

    private Integer shopOrderId;

    private String expressId;

    private Date createDate;

    private Date updateDate;
}
