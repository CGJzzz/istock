package io.github.kingschan1204.istock.module.maindata.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Document(collection = "user")
public class User {

    @Id
    private String id;
    private String account;
    private String password;
    private Double balance;//账户余额
}
