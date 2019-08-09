package com.xll.baitaner.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhulu
 * @version 1.0
 * @description com.xll.baitaner.entity
 * @date 2019/8/9
 */
@Data
@ApiModel(value = "版本信息实体类", description = "版本信息的描述")
public class Version {

    /**
     * id
     */
    @ApiModelProperty(value = "数据库中唯一标致id", name = "id")

    private int id;

    /**
     * versionId
     */
    @ApiModelProperty(value = "活动Id", name = "versionId")

    private int versionId;
}
