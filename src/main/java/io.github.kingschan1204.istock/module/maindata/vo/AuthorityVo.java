package io.github.kingschan1204.istock.module.maindata.vo;

public class AuthorityVo {
    private String id;
    private String AuthorityOrderSerial;//委托订单号
    private String account;

    //日期

    private String code;
    private String date;
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

    public String getAuthorityOrderSerial() {
        return AuthorityOrderSerial;
    }

    public void setAuthorityOrderSerial(String authorityOrderSerial) {
        AuthorityOrderSerial = authorityOrderSerial;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public Long getNumberOfShare() {
        return numberOfShare;
    }

    public void setNumberOfShare(Long numberOfShare) {
        this.numberOfShare = numberOfShare;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getPriceOrder() {
        return priceOrder;
    }

    public void setPriceOrder(Double priceOrder) {
        this.priceOrder = priceOrder;
    }

    public Double getPriceFinal() {
        return priceFinal;
    }

    public void setPriceFinal(Double priceFinal) {
        this.priceFinal = priceFinal;
    }

    public Double getPriceTotal() {
        return priceTotal;
    }

    public void setPriceTotal(Double priceTotal) {
        this.priceTotal = priceTotal;
    }

    public String getBehavior() {
        return behavior;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }

    public AuthorityVo() {
    }
}
