package com.xll.baitaner.service;

import com.xll.baitaner.entity.ReceiverAddress;

import java.util.List;

/**
 * @author denghuohuo 2019/6/25
 */
public interface ProfileService {

    /**
     * 查询用户所有收货地址列表
     *
     * @param openId
     * @return
     */
    List<ReceiverAddress> getAddressList(String openId);

    /**
     * 获取单个收货地址数据
     *
     * @param addressId
     * @return
     */
    ReceiverAddress getAddress(int addressId);

    /**
     * 新增收货地址
     *
     * @param address
     * @return
     */
    boolean addAddress(ReceiverAddress address);

    /**
     * 更新收货地址信息
     *
     * @param address
     * @return
     */
    boolean updateAddress(ReceiverAddress address);

    /**
     * 删除收货地址
     *
     * @param addressId
     * @return
     */
    boolean deleteAddress(int addressId);

    /**
     * 收货地址设为默认地址
     *
     * @param addressId
     * @param openId
     * @return
     */
    boolean updateAddressState(int addressId, String openId);

    /**
     * 获取用户默认地址
     *
     * @param openId
     * @return
     */
    ReceiverAddress getDefaultAddress(String openId);
}
