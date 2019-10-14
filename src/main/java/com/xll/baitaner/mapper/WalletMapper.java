package com.xll.baitaner.mapper;

import com.xll.baitaner.entity.ShopWallet;
import org.apache.ibatis.annotations.InsertProvider;
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

    String walletFields = "id,shop_id,order_id,open_id,amount,operator,fee,pay_channel,create_date," +
            "status,reason,desc_remarks,transfer_time,payment_time";

    /**
     * 根据日期查询店铺的提现记录
     *
     * @param openId
     * @param dateStr
     * @return
     */
    @Select("select " + walletFields + " from `shop_wallet` where open_id=#{openId} and operator='DEC' and DATE_FORMAT" +
            "(create_date,'%Y-%m-%d')=#{dateStr} order by create_date desc")
    List<ShopWallet> getWalletWithAmountByDate(@Param("openId") String openId, @Param("dateStr") String dateStr);

    /**
     * 查找店铺全部提现记录
     *
     * @param openId
     * @return
     */
    @Select("select " + walletFields + " from `shop_wallet` where open_id=#{openId} and operator='DEC' order by " +
            "create_date desc")
    List<ShopWallet> getWalletAllAmount(@Param("openId") String openId);

    /**
     * 插入数据
     *
     * @param wallet
     * @return
     */
    @InsertProvider(type = ShopWalletProvider.class, method = "insertShopWallet")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertWalletRecord(ShopWallet wallet);

    /**
     * 根据shopId查询流水记录
     *
     * @param openId
     * @return
     */
    @Select("select " + walletFields + " from `shop_wallet` where open_id=#{openId}")
    List<ShopWallet> selectAllByOpenId(@Param("openId") String openId);

    /**
     * 根据orderId查询一条记录
     *
     * @param orderId
     * @return
     */
    @Select("select " + walletFields + " from `shop_wallet` where order_id=#{orderId} and operator=#{operator} limit 1")
    ShopWallet selectOneByOrderId(@Param("orderId") Long orderId, @Param("operator") String operator);

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
     * 查找钱方支付中48小时以前的数据，可提现
     *
     * @param openId
     * @return
     */
    @Select("select " + walletFields + " from `shop_wallet` where open_id=#{openId} and operator='ADD' and " +
            "pay_channel=0 and (create_date < (NOW()- INTERVAL 48 HOUR))")
    List<ShopWallet> selectBefore48HoursByOpenIdToQF(@Param("openId") String openId);

    /**
     * 钱方支付48小时以内的数据 不可提现
     *
     * @param openId
     * @return
     */
    @Select("select " + walletFields + " from `shop_wallet` where open_id=#{openId} and operator='ADD' and " +
            "pay_channel=0 and (create_date between (NOW()- INTERVAL 48 HOUR) and NOW())")
    List<ShopWallet> selectBetween48HoursByOpenIdToQF(@Param("openId") String openId);

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
                    UPDATE("shop_wallet");
                    if (wallet.getOpenId() != null) {
                        SET("open_id=#{openId}");
                    }
                    if (wallet.getOperator() != null) {
                        SET("operator=#{operator}");
                    }
                    if (wallet.getFee() != null) {
                        SET("fee=#{fee}");
                    }
                    if (wallet.getAmount() != null) {
                        SET("amount=#{amount}");
                    }
                    if (wallet.getOrderId() != null) {
                        SET("order_id=#{orderId}");
                    }
                    if (wallet.getShopId() != null) {
                        SET("shop_id=#{shopId}");
                    }
                    if (wallet.getPayChannel() != null) {
                        SET("pay_channel=#{payChannel}");
                    }
                    if (wallet.getStatus() != null) {
                        SET("status=#{status}");
                    }
                    if (wallet.getReason() != null) {
                        SET("reason=#{reason}");
                    }
                    if (wallet.getDescRemarks() != null) {
                        SET("desc_remarks=#{descRemarks}");
                    }
                    if (wallet.getPaymentTime() != null) {
                        SET("payment_time=#{paymentTime}");
                    }
                    if (wallet.getTransferTime() != null) {
                        SET("transfer_time=#{transferTime}");
                    }
                    WHERE("id=#{id}");
                }
            }.toString();
        }

        public String insertShopWallet(ShopWallet wallet) {
            return new SQL() {{
                INSERT_INTO("`shop_wallet`");
                if (wallet.getOpenId() != null) {
                    VALUES("open_id", "#{openId}");
                }
                if (wallet.getShopId() != null) {
                    VALUES("shop_id", "#{shopId}");
                }
                if (wallet.getStatus() != null) {
                    VALUES("status", "#{status}");
                }
                if (wallet.getAmount() != null) {
                    VALUES("amount", "#{amount}");
                }
                if (wallet.getPayChannel() != null) {
                    VALUES("pay_channel", "#{payChannel}");
                }
                if (wallet.getOrderId() != null) {
                    VALUES("order_id", "#{orderId}");
                }
                if (wallet.getOperator() != null) {
                    VALUES("operator", "#{operator}");
                }
                if (wallet.getFee() != null) {
                    VALUES("fee", "#{fee}");
                }
                if (wallet.getDescRemarks() != null) {
                    VALUES("desc_remarks", "#{descRemarks}");
                }
                if (wallet.getReason() != null) {
                    VALUES("reason", "#{reason}");
                }
                if (wallet.getPaymentTime() != null) {
                    VALUES("payment_time", "#{paymentTime}");
                }
                if (wallet.getTransferTime() != null) {
                    VALUES("transfer_time", "#{transferTime}");
                }
            }}.toString();
        }
    }
}
