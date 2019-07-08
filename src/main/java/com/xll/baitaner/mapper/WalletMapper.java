package com.xll.baitaner.mapper;

import com.xll.baitaner.entity.ShopWallet;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author denghuohuo 2019/6/29
 */
@Repository
public interface WalletMapper {

    String walletFields = "id,shop_id,order_id,open_id,amount,operator,create_date," +
            "status,reason,desc_remarks,transfer_time,payment_time";

    /**
     * 根据日期查询店铺的提现记录
     *
     * @param openId
     * @param dateStr
     * @return
     */
    @Select("select create_date,amount from `shop_wallet` where open_id=#{openId} and operator='DEC' and DATE_FORMAT" +
            "(create_date,'%Y-%m-%d')=#{dateStr}")
    List<ShopWallet> getWalletWithAmountByDate(@Param("openId") String openId, @Param("dateStr") String dateStr);

    /**
     * 查找店铺全部提现记录
     *
     * @param openId
     * @return
     */
    @Select("select create_date,amount from `shop_wallet` where open_id=#{openId} and operator='DEC'")
    List<ShopWallet> getWalletAllAmount(@Param("openId") String openId);

    /**
     * 插入数据
     *
     * @param wallet
     * @return
     */
    @Insert("insert into `shop_wallet` (shop_id,order_id,open_id,amount,operator) " +
            "values (#{wallet.shopId},#{wallet.orderId},#{wallet.userId},#{wallet.amount},#{wallet.operator})")
    int insertWalletRecord(@Param("wallet") ShopWallet wallet);

    /**
     * 根据shopId查询流水记录
     *
     * @param openId
     * @return
     */
    @Select("select " + walletFields + " from `shop_wallet` where open_id=#{openId}")
    List<ShopWallet> selectAllByOpenId(@Param("openId") String openId);

    /**
     * 只能查询30天以内的提现数据
     *
     * @param openId
     * @return
     */
    @Select("SELECT * FROM `shop_wallet` WHERE open_id=#{openId} " +
            "AND  DATE_SUB(CURDATE(), INTERVAL 30 DAY ) <= DATE(create_date)")
    List<ShopWallet> queryWithdrawRecords(@Param("openId") String openId);

    /**
     * 查询提现记录更新
     *
     * @param wallet
     * @return
     */
    @UpdateProvider(type = ShopWalletProvider.class, method = "updateWithdrawById")
    @Options(useGeneratedKeys = true, keyProperty = "wallet.id", keyColumn = "id")
    int updateShopWalletWithdraw(ShopWallet wallet);

    class ShopWalletProvider {

        public String updateWithdrawById(ShopWallet wallet) {
            return new SQL() {
                {
                    UPDATE("`shop_wallet`");
                    if (wallet.getStatus() != null) {
                        SET("`status`=#{status}");
                    }
                    if (wallet.getReason() != null) {
                        SET("`reason`=#{reason}");
                    }
                    if (wallet.getDescRemarks() != null) {
                        SET("`desc_remarks`=#{descRemarks}");
                    }
                    if (wallet.getPaymentTime() != null) {
                        SET("`payment_time`=#{paymentTime}");
                    }
                    if (wallet.getTransferTime() != null) {
                        SET("`transfer_time`=#{transferTime}");
                    }
                    WHERE("`id`=#{id}");
                }
            }.toString();
        }
    }
}
