package xll.baitaner.service.entity;

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
