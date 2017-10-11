package xll.baitaner.service.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 描述：商品信息实体类
 * 创建者：xie
 * 日期：2017/10/10
 **/
public class Commodity {

    private int id;

    /**
     * 店铺ID
     */
    private int shopId;

    /**
     * 商品名
     */
    private String name;

    /**
     * 商品单价
     */
    private float price;

    /**
     * 月销售数目
     */
    private int monthlySales;

    /**
     * 好评率
     */
    private String praise;

    /**
     * 商品图片
     */
    private String pictUrl;

    /**
     * 商品描述
     */
    private String introduction;

    /**
     *  上下架状态
     *  0：上架（true）
     *  1：下架（false）
     */
    private boolean state;
}
