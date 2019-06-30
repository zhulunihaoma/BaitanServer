package com.xll.baitaner.mapper;

import com.xll.baitaner.entity.ShopWallet;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author denghuohuo 2019/6/29
 */
@Repository
public interface WalletMapper {

    final String walletFields = "id,shop_id,order_id,user_id,amount,operator,create_data";

    /**
     * 根据日期查询店铺的提现记录
     *
     * @param shop
     * @param dateStr
     * @return
     */
    @Select("select amount from `shop_wallet` where shop_id=#{shopId} and operator='DEC' and DATE_FORMAT(create_data,'%Y-%m-%d')=#{dateStr} LIMIT #{page.offset},#{page.size}")
    List<String> getWalletWithAmountByDate(@Param("shopId") Integer shop, @Param("dateStr") String dateStr, @Param("page") Pageable page);

    @Insert("insert into `shop_wallet` (shop_id,order_id,user_id,amount,operator) " +
            "values (#{wallet.shopId},#{wallet.orderId},#{wallet.userId},#{wallet.amount},#{wallet.operator})")
    int insertWalletRecord(@Param("wallet") ShopWallet wallet);

    /**
     * 根据shopId查询流水记录
     *
     * @param shopId
     * @return
     */
    @Select("select " + walletFields + "from `shop_wallet` where shop_id=#{shopId}")
    List<ShopWallet> selectAllByShopId(@Param("shopId") Integer shopId);
}
