package com.xll.baitaner.service;

/**
 * 接口名：TemplateService
 * 描述：微信模板消息业务
 * 创建者：xie
 * 日期：2019/7/9/009
 **/
public interface TemplateService {

    /**
     * 新增formid 用于发送模板消息
     * @param openId
     * @param formId
     * @return
     */
    public boolean addFormId(String openId, String formId);

    /**
     * 获取用户的fromId用于发送模板消息
     * @param openId
     * @return
     */
    public String getFormId(String openId);

    /**
     * 将已用过的formid状态更新为不可用
     * @param formId
     * @return
     */
    public boolean updateFormidUsed(String openId, String formId);
}
