package io.github.kingschan1204.istock.module.maindata.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Document(collection = "user")
public class User {

    @Id
    private String code;//股票id
    private String type;
    private String shareName;//股票名字
    private Double fistBuyPrice;//买入价格
    private Long firstBuyDate;//购买日期
    private Double soldPrice;//售出价格
    private Long soldDate;//售出日期
    private Long shareNumber;//股票数量
    private BigDecimal accountBalance;//账户余额
    private String account;
    private String password;
}
