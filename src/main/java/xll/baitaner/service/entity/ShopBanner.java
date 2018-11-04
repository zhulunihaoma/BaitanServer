package xll.baitaner.service.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 店铺首页的轮播图路径
 */
@Getter
@Setter
@ApiModel(value = "店铺首页的轮播图路径", description = "店铺首页的轮播图路径,三张")
public class ShopBanner {

    /**
     * 店铺Id
     */
    @ApiModelProperty(value="店铺id",name="shopId")
    private int shopId;

    /**
     * 店铺轮播图1地址
     */
    @ApiModelProperty(value="店铺轮播图1地址（默认为空）",name="shopPicUrl1")
    private String shopPicUrl1;

    /**
     * 店铺轮播图2地址
     */
    @ApiModelProperty(value="店铺轮播图2地址（默认为空）",name="shopPicUrl2")
    private String shopPicUrl2;

    /**
     * 店铺轮播图3地址
     */
    @ApiModelProperty(value="店铺轮播图3地址（默认为空）",name="shopPicUrl3")
    private String shopPicUrl3;
}
