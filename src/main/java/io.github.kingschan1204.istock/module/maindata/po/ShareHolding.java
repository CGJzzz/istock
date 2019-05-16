package io.github.kingschan1204.istock.module.maindata.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "shareholding")
public class ShareHolding {
    @Id
    private String id;
    private String account;
    private String code;
    private String nameOfShare;
    private Double number;
    private Double priceFirst;
}
