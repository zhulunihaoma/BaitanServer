package com.xll.baitaner.mapper;

import com.xll.baitaner.entity.ShopWallet;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author denghuohuo 2019/6/29
 */
@Repository
public interface WalletMapper {

    String walletFields = "id,shop_id,order_id,open_id,amount,operator,create_data";

    /**
     * 根据日期查询店铺的提现记录
     *
     * @param shop
     * @param dateStr
     * @return
     */
    @Select("select create_data,amount from `shop_wallet` where shop_id=#{shopId} and operator='DEC' and DATE_FORMAT" +
            "(create_data,'%Y-%m-%d')=#{dateStr} LIMIT #{page.offset},#{page.size}")
    List<ShopWallet> getWalletWithAmountByDate(@Param("shopId") Integer shop, @Param("dateStr") String dateStr,
                                               @Param("page") Pageable page);

    /**
     * 查找店铺全部提现记录
     *
     * @param shop
     * @param page
     * @return
     */
    @Select("select create_data,amount from `shop_wallet` where shop_id=#{shopId} and operator='DEC' LIMIT #{page.offset},#{page.size}")
    List<ShopWallet> getWalletAllAmount(@Param("shopId") Integer shop, @Param("page") Pageable page);

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
     * @param shopId
     * @return
     */
    @Select("select " + walletFields + " from `shop_wallet` where shop_id=#{shopId}")
    List<ShopWallet> selectAllByShopId(@Param("shopId") Integer shopId);

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
