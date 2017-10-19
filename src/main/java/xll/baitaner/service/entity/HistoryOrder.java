package xll.baitaner.service.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 描述：历史订单实体类
 * 创建者：xie
 * 日期：2017/10/18
 **/
@Getter
@Setter
public class HistoryOrder {

    private int id;

    /**
     * 店铺Id
     */
    private int shopId;

    /**
     * 历史日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date historyDate;

    /**
     * 历史订单列表
     */
    private List<Order> orderList;
}
