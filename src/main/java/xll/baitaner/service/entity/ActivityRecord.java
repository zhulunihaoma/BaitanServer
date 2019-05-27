package xll.baitaner.service.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import net.sf.json.JSONObject;

import java.util.Date;

/**
 * @author zhulu
 * @version 1.0
 * @description xll.baitaner.service.entity
 * @date 2019/3/1
 */
@Getter
@Setter
public class ActivityRecord {
    private int id;

    /**
     * activityID
     */
    private int activityId;

    /**
     * 用户ID
     */
    private String openId;


    /**
     * 记录状态（0 未完成，1 可以购买 ，2 已完成）
     */

    @ApiModelProperty(value = "记录状态",name = "recordStatus")
    private int recordStatus;

    /**
     *  砍价活动有的当前砍到的价格
     */
    @ApiModelProperty(value = "当前砍到的价格",name = "currentPrice")
    private float currentPrice;


    /**
     * 商品信息id
     */

    @ApiModelProperty(value = "绑定的商品Id",name = "CommodityId")
    private int commodityId;

    /**
     * 昵称
     */
    @ApiModelProperty(value="昵称",name="nickName")
    private String nickName;

    /**
     * 用户头像
     */
    @ApiModelProperty(value="用户头像",name="avatarUrl")
    private String avatarUrl;

    /**
     * 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
     */
    @ApiModelProperty(value="用户的性别，值为1时是男性，值为2时是女性，值为0时是未知",name="gender")
    private String gender;

    /**
     *  活动状态1：启用 0：终止
     */

    private int status;

    /**
     *  点赞/砍价数量
     */

    private int supportCount;

    //新增选填的数据

    /**
     *  商品活动价格
     */
    @ApiModelProperty(value = "商品活动价格",name = "activityPrice")
    private float activityPrice;

    /**
     *  结束时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")

    private Date endTime;

    /**
     * 店铺名称
     */
    @ApiModelProperty(value="店铺名称",name="shopName")
    private String shopName;

    /**
     * 店铺logo（选填）
     */
    @ApiModelProperty(value="店铺logo（选填）",name="shopLogoUrl")
    private String shopLogoUrl;


    /**
     * 商品名
     */
    @ApiModelProperty(value="商品名",name="goodname")
    private String goodname;


}
