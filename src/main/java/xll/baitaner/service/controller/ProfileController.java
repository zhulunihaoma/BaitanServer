package xll.baitaner.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xll.baitaner.service.entity.ReceiverAddress;
import xll.baitaner.service.service.ProfileService;
import xll.baitaner.service.utils.ResponseResult;

/**
 * 描述：个人功能类（收货地址）Controller
 * 创建者：xie
 * 日期：2017/10/11
 **/
@RestController
public class ProfileController {

    @Autowired
    private ProfileService profileService;


    /**
     * 获取用户所有收货地址列表
     * @param clientId
     * @return
     */
    @GetMapping("profilemanage/getaddresslist")
    public ResponseResult getAddressList(String clientId){
        return ResponseResult.result(0, "success", profileService.getAddressList(clientId));
    }

    /**
     * 获取单个收货地址数据
     * @param commodityId
     * @return
     */
    @GetMapping("profilemanage/getaddress")
    public ResponseResult getAddress(int addressId){
        return ResponseResult.result(0, "success", profileService.getAddress(addressId));
    }

    /**
     * 新增收货地址接口
     * @param address
     * @return
     */
    @RequestMapping("profilemanage/addaddress")
    public ResponseResult addAddress(ReceiverAddress address){
        boolean result = profileService.addAddress(address);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 修改收货地址接口
     * @param address
     * @return
     */
    @RequestMapping("profilemanage/editaddress")
    public ResponseResult editAddress(ReceiverAddress address){
        boolean result = profileService.updateAddress(address);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 删除收货地址
     * @param commodity
     * @return
     */
    @RequestMapping("profilemanage/deleteaddress")
    public ResponseResult deleteAddress(int addressId){
        boolean result = profileService.deleteAddress(addressId);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 修改收货地址是否默认状态接口
     * @param addressId
     * @param clientId
     * @return
     */
    @RequestMapping("profilemanage/updatadstate")
    public ResponseResult updateAddressState(int addressId, String clientId){
        boolean result = profileService.updateAddressState(addressId, clientId);
        return ResponseResult.result(result ? 0 : 1, result ? "success" : "fail", null);
    }

    /**
     * 获取用户默认收货地址接口
     * @param clientId
     * @return
     */
    @GetMapping("profilemanage/getdefaultaddress")
    public ResponseResult getDefaultAddress(String clientId){
        return ResponseResult.result(0, "success", profileService.getDefaultAddress(clientId));
    }

    /**
     * 获取当前店铺销售统计数据
     * @param shopId
     * @return
     */
    @GetMapping("profilemanage/getstatistics")
    public ResponseResult getShopStatistics(int shopId){
        return ResponseResult.result( 0, "success", profileService.getStatistics(shopId));
    }
}
