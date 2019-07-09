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


}
