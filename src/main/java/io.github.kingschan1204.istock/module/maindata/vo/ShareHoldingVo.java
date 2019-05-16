package io.github.kingschan1204.istock.module.maindata.vo;

public class ShareHoldingVo {
    private String id;
    private String account;
    private String code;
    private String nameOfShare;
    private Double number;
    private Double priceFirst;

    public ShareHoldingVo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNameOfShare() {
        return nameOfShare;
    }

    public void setNameOfShare(String nameOfShare) {
        this.nameOfShare = nameOfShare;
    }

    public Double getNumber() {
        return number;
    }

    public void setNumber(Double number) {
        this.number = number;
    }

    public Double getPriceFirst() {
        return priceFirst;
    }

    public void setPriceFirst(Double priceFirst) {
        this.priceFirst = priceFirst;
    }
}
