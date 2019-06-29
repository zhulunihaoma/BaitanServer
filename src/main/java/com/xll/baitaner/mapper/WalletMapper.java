package com.xll.baitaner.mapper;

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

    /**
     * 根据日期查询店铺的提现记录
     *
     * @param shop
     * @param dateStr
     * @return
     */
    @Select("select amount from `shop_wallet` where shop_id=#{shopId} and operator='DEC' and DATE_FORMAT(create_data,'%Y-%m-%d')=#{dateStr} LIMIT #{page.offset},#{page.size}")
    List<String> getWalletWithAmountByDate(@Param("shopId") Integer shop, @Param("dateStr") String dateStr, @Param("page") Pageable page);
}
