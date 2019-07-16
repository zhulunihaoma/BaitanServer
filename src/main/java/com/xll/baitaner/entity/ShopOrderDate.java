package com.xll.baitaner.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 类名：ShopOrderDate
 * 描述：记录付款成功订单的下单日期实体类
 *      为前端提供以日期为基础封装的历史订单列表数据
 * 创建者：xie
 * 日期：2019/7/3/003
 **/
@Data
public class ShopOrderDate {

    private Integer id;

    private Integer shopId;

    private String orderDate;
}

