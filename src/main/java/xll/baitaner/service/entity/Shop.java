package xll.baitaner.service.entity;

import lombok.Getter;
import lombok.Setter;


/**
 * 描述：店铺实体类
 * 创建者：xie
 * 日期：2017/9/19
 **/
@Getter
@Setter
public class Shop {

    /**
     * 店铺ID
     */
    private int id;

    /**
     * 店铺主人ID
     */
    private String clientId;

    /**
     * 店铺名称
     */
    private String name;

    /**
     * 店铺简介
     */
    private String introduction;

    /**
     * 店铺联系人名称
     */
    private String contacts;

    /**
     * 店铺联系人性别 0:男士 1:女士
     */
    private int sex;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 店铺地址
     */
    private String address;

    /**
     * 轮播图1地址
     */
    private String picUrl1;

    /**
     * 轮播图2地址
     */
    private String picUrl2;

    /**
     * 轮播图3地址
     */
    private String picUrl3;

    /**
     *  店铺状态 1:营业  0;歇业
     */
    private int state;
}
