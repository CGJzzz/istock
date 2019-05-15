package io.github.kingschan1204.istock.module.maindata.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "authority")
public class Authority {
    @Id
    private String id;
    private String AuthorityOrderSerial;//委托订单号

    private String account;

    //日期
    private String date;

    private String code;
    private Long numberOfShare;
    //是否成交
    private String status;
    //委托时价格
    private Double priceOrder;
    //委托单成功时价格
    private Double priceFinal;
    //最终成交价格
    private Double priceTotal;
    //买入in 卖出out
    private String behavior;
}
