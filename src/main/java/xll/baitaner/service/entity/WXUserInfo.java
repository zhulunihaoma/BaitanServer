package xll.baitaner.service.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 微信用户实体类（小程序）
 */
@Getter
@Setter
@ApiModel(value = "微信用户实体类（小程序）", description = "微信用户实体类（小程序）")
public class WXUserInfo {

    private int id;

    /**
     * 用户的标识
     */
    @ApiModelProperty(value="用户的标识",name="openId")
    private String openId;

    /**
     * 昵称
     */
    @ApiModelProperty(value="昵称",name="nickname")
    private String nickname;

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
     * 用户所在国家
     */
    @ApiModelProperty(value="用户所在国家",name="country")
    private String country;

    /**
     * 用户所在省份
     */
    @ApiModelProperty(value="用户所在省份",name="province")
    private String province;

    /**
     * 用户所在城市
     */
    @ApiModelProperty(value="用户所在城市",name="city")
    private String city;

    /**
     * 用户的语言，简体中文为zh_CN
     */
    @ApiModelProperty(value="用户的语言，简体中文为zh_CN",name="language")
    private String language;

    /**
     * 用户在开放平台的唯一标识符
     */
    @ApiModelProperty(value="用户在开放平台的唯一标识符(公众号模板提醒时使用)",name="unionid")
    private String unionid;
}
