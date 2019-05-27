package xll.baitaner.service.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhulu
 * @version 1.0
 * @description xll.baitaner.service.entity
 * @date 2019/3/1
 */
@Getter
@Setter
@ApiModel(value = "点赞/砍价关系", description = "点赞/砍价关系信息")
public class SupportRecord {

    /**
     * id
     */
    @ApiModelProperty(value="数据库中唯一标致id",name="id")

    private int id;

    /**
     * recordId
     */
    @ApiModelProperty(value="活动id",name="activityId")

    private int activityId;

    /**
     * recordId
     */
    @ApiModelProperty(value="活动参加记录id",name="recordId")

    private int recordId;

    /**
     * openId
     */
    @ApiModelProperty(value="openId",name="openId")

    private String openId;

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
}
