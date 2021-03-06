package com.xll.baitaner.impl;

import com.xll.baitaner.entity.ReceiverAddress;
import com.xll.baitaner.entity.ShopStatistics;
import com.xll.baitaner.mapper.ProfileMapper;
import com.xll.baitaner.service.ProfileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 描述：个人功能类（收货地址）service
 * 创建者：xie
 * 日期：2017/10/11
 **/
@Service
public class ProfileServiceImpl implements ProfileService {

    @Resource
    private ProfileMapper profileMapper;

    /**
     * 查询用户所有收货地址列表
     *
     * @param openId
     * @return
     */
    @Override
    public List<ReceiverAddress> getAddressList(String openId) {
        return profileMapper.selectAddressList(openId);
    }

    /**
     * 获取单个收货地址数据
     *
     * @param addressId
     * @return
     */
    @Override
    public ReceiverAddress getAddress(int addressId) {
        return profileMapper.selectAddress(addressId);
    }

    /**
     * 新增收货地址
     * 地址数据非空校验
     * @param address
     * @return
     */
    @Override
    public Integer addAddress(ReceiverAddress address) {
        if(StringUtils.isBlank(address.getName()) ||
           StringUtils.isBlank(address.getAddress()) ||
           StringUtils.isBlank(address.getPhone()))
            return 0;

        List<ReceiverAddress> list = getAddressList(address.getOpenId());
        if (list.size() == 0) {
            address.setDefaultNot(1);
        }
        try {
            profileMapper.insertAddress(address);
            return address.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 更新收货地址信息（原收货地址disable改为false，添加新收货地址）
     *
     * @param address
     * @return
     */
    @Transactional
    public boolean editAddress(ReceiverAddress address) {
        int id = address.getId();
        boolean re1 = deleteAddress(id);
        boolean re2 = addAddress(address) > 0;
        return re1 && re2;
    }

    /**
     * 更新收货地址信息
     *
     * @param address
     * @return
     */
    @Override
    public boolean updateAddress(ReceiverAddress address) {
        return profileMapper.updateAddress(address) > 0;
    }

    /**
     * 删除收货地址
     * 收货地址设为无效，代替删除
     * @param addressId
     * @return
     */
    @Override
    public boolean deleteAddress(int addressId) {
        //return profileMapper.deleteAddress(addressId) > 0;
        return profileMapper.updateAddressDisable(addressId) > 0;
    }

    /**
     * 收货地址设为默认地址
     *
     * @param addressId
     * @param openId
     * @return
     */
    @Override
    public boolean updateAddressState(int addressId, String openId) {
        return profileMapper.updateAddressState(addressId, openId) > 0;
    }

    /**
     * 获取用户默认地址
     *
     * @param openId
     * @return
     */
    @Override
    public ReceiverAddress getDefaultAddress(String openId) {
        return profileMapper.selectDefaultAddress(openId);
    }

    /**
     * 获取当前店铺销售统计数据
     *
     * @param shopId
     * @return
     */
    @Override
    public ShopStatistics getStatistics(int shopId) {

        int todayReceivedOrder = profileMapper.selectTodayReceivedOrderCount(shopId);
        int yesterdayReceivedOrder = profileMapper.selectYesterdayReceivedOrderCount(shopId);

//        float todySales = profileMapper.selectTodaySalesByShop(shopId);
//        float yesterdaySales = profileMapper.selectYesterdaySalesByShop(shopId);
//
//        float thisweekSales = profileMapper.selectThisWeekSalesByShop(shopId);
//        float lastweekSales = profileMapper.selectLastWeekSalesByShop(shopId);
//
//        float thismonthSales = profileMapper.selectThismonthSalesByShop(shopId);
//        float lastmonthSales = profileMapper.selectLastmonthSalesByShop(shopId);
//
//        float totalSales = profileMapper.selectTotalSalesByShop(shopId);

        ShopStatistics statistics = new ShopStatistics();
        statistics.setShopId(shopId);
        statistics.setTodayReceivedOrder(todayReceivedOrder);
        statistics.setYesterdayReceivedOrder(yesterdayReceivedOrder);

        statistics.setTodaySales(calculateStatistics(profileMapper.selectTodaySalesByShopId(shopId)));
        statistics.setYesterdaySales(calculateStatistics(profileMapper.selectYesterdaySalesByShopId(shopId)));

        statistics.setThisweekSales(calculateStatistics(profileMapper.selectThisWeekSalesByShopId(shopId)));
        statistics.setLastweekSales(calculateStatistics(profileMapper.selectLastWeekSalesByShopId(shopId)));

        statistics.setThismonthSales(calculateStatistics(profileMapper.selectThismonthSalesByShopId(shopId)));
        statistics.setLastmonthSales(calculateStatistics(profileMapper.selectLastmonthSalesByShopId(shopId)));

        statistics.setTotalSales(calculateStatistics(profileMapper.selectTotalSalesByShopId(shopId)));

        return statistics;
    }

    /**
     * 计算总额
     * @param totalMoneys
     * @return
     */
    private String calculateStatistics(List<String> totalMoneys) {
        BigDecimal total = new BigDecimal("0.00");
        if (CollectionUtils.isEmpty(totalMoneys)) {
            return total.toString();
        }
        for (String money : totalMoneys) {
            total = total.add(new BigDecimal(money));
        }
        return total.toString();
    }
}
