package xll.baitaner.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xll.baitaner.service.entity.ReceiverAddress;
import xll.baitaner.service.entity.ShopStatistics;
import xll.baitaner.service.mapper.ProfileMapper;

import java.util.List;

/**
 * 描述：个人功能类（收货地址）service
 * 创建者：xie
 * 日期：2017/10/11
 **/
@Service
public class ProfileService {

    @Autowired
    private ProfileMapper profileMapper;

    /**
     * 查询用户所有收货地址列表
     * @param clientId
     * @return
     */
    public List<ReceiverAddress> getAddressList(String clientId){
        return profileMapper.selectAddressList(clientId);
    }

    /**
     * 获取单个收货地址数据
     * @param addressId
     * @return
     */
    public ReceiverAddress getAddress(int addressId){
        return profileMapper.selectAddress(addressId);
    }

    /**
     * 新增收货地址
     * @param address
     * @return
     */
    public boolean addAddress(ReceiverAddress address){
        return profileMapper.insertAddress(address) > 0;
    }

    /**
     * 更新收货地址信息
     * @param address
     * @return
     */
    public boolean editAddress(ReceiverAddress address){
        return profileMapper.updateAddress(address) > 0;
    }

    /**
     * 删除收货地址
     * @param addressId
     * @return
     */
    public boolean deleteAddress(int addressId){
        return profileMapper.deleteAddress(addressId) > 0;
    }

    /**
     * 收货地址设为默认地址
     * @param addressId
     * @param clientId
     * @return
     */
    public boolean updateAddressState(int addressId, String clientId){
        return profileMapper.updateAddressState(addressId, clientId) > 0;
    }

    /**
     * 获取当前店铺销售统计数据 //todo 按实际订单计算所得
     * @param shopId
     * @return
     */
    public ShopStatistics getStatistics(int shopId){
        ShopStatistics statistics = new ShopStatistics();
        statistics.setShopId(shopId);
        statistics.setTodaySales(1000);
        statistics.setTotalSales(100);
        statistics.setTotalSales(10000);

        return statistics;
    }
}