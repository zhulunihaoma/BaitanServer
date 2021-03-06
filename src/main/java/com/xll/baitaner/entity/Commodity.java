package com.xll.baitaner.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 描述：商品信息实体类
 * 创建者：xie
 * 日期：2017/10/10
 **/

@Data
@ApiModel(value = "商品实体类", description = "商品信息实体类")
public class Commodity {

    private int id;

    /**
     * 店铺ID
     */
    @ApiModelProperty(value = "店铺ID", name = "shopId")
    private int shopId;

    /**
     * 店铺首页的类别id
     */
    @ApiModelProperty(value = "店铺首页的类别id", name = "sortId")
    private int sortId;

    /**
     * 商品名
     */
    @ApiModelProperty(value = "商品名", name = "name")
    private String name;

    /**
     * 商品描述
     */
    @ApiModelProperty(value = "商品描述", name = "introduction")
    private String introduction;

    /**
     * 商品描述图片
     */
    @ApiModelProperty(value = "商品描述图片", name = "introductionPics")
    private String introductionPics;

    /**
     * 商品单价
     */
    @ApiModelProperty(value = "商品单价", name = "price")
    private String price;

    /**
     * 零售价
     */
    private String retailPrice;

    /**
     * 商品运费
     */
    @ApiModelProperty(value = "商品运费", name = "postage")
    private String postage;

    /**
     * 月销售数目
     */
    @ApiModelProperty(value = "月销售数目", name = "monthlySales")
    private int monthlySales;

    /**
     * 商品图片
     */
    @ApiModelProperty(value = "商品图片", name = "pictUrl")
    private String pictUrl;
    /**
     * 商品缩略图片
     */
    @ApiModelProperty(value = "商品缩略图片", name = "zipPicUrl")
    private String zipPicUrl;
    /**
     * 商品小程序码地址
     */
    @ApiModelProperty(value = "商品小程序码地址", name = "wxcodeUrl")
    private String wxcodeUrl;

    /**
     * 上下架状态
     * 上架（true）
     * 下架（false）
     */
    @ApiModelProperty(value = "上下架状态", name = "state")
    private boolean state;

    /**
     * 商品库存
     */
    @ApiModelProperty(value = "商品库存", name = "stock")
    private int stock;

    /**
     * 商品显示位置
     */
    @ApiModelProperty(value = "商品显示位置", name = "turn")
    private int turn;

    /**
     * 商品规格id列表
     */
    @ApiModelProperty(value = "商品规格id列表", name = "specs")
    private int[] specs;

    /**
     * 商品规格列表
     */
    @ApiModelProperty(value = "商品规格列表", name = "specList")
    private List<Spec> specList;
}
