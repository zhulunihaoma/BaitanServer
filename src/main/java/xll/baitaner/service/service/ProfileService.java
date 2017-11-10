package xll.baitaner.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xll.baitaner.service.entity.ReceiverAddress;
import xll.baitaner.service.entity.ShopStatistics;
import xll.baitaner.service.mapper.OrderMapper;
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

    @Autowired
    private OrderMapper orderMapper;

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
        List<ReceiverAddress> list = getAddressList(address.getClientId());
        if(list.size() == 0){
            address.setDefault(true);
        }
        return profileMapper.insertAddress(address) > 0;
    }

    /**
     * 更新收货地址信息（原收货地址disable改为false，添加新收货地址）
     * @param address
     * @return
     */
    @Transactional
    public boolean editAddress(ReceiverAddress address){
        int id = address.getId();
        boolean re1 = deleteAddress(id);
        boolean re2 = addAddress(address);
        return re1 && re2;
    }

    /**
     * 更新收货地址信息
     * @param address
     * @return
     */
    public boolean updateAddress(ReceiverAddress address){
        return profileMapper.updateAddress(address) > 0;
    }

    /**
     * 删除收货地址
     * @param addressId
     * @return
     */
    public boolean deleteAddress(int addressId){
        return profileMapper.deleteAddress(addressId) > 0;
//        return profileMapper.updateAdrDisabel(addressId) > 0;
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
     * 获取用户默认地址
     * @param clientId
     * @return
     */
    public ReceiverAddress getDefaultAddress(String clientId){
        return profileMapper.selectDefaultAddress(clientId);
    }

    /**
     * 获取当前店铺销售统计数据
     * @param shopId
     * @return
     */
    public ShopStatistics getStatistics(int shopId){
        int orderCount = orderMapper.countOrdersByShop(shopId, 1);
        float todySales = profileMapper.selectTodaySalesByShop(shopId);
        float totalSales = profileMapper.selectTotalSalesByShop(shopId);
        ShopStatistics statistics = new ShopStatistics();
        statistics.setShopId(shopId);
        statistics.setTodaySales(todySales);
        statistics.setTotalSales(totalSales);
        statistics.setReceivedOrder(orderCount);

        return statistics;
    }
}
