package io.github.kingschan1204.istock.module.maindata.vo;

import java.math.BigDecimal;

/*
创建用户实体类
2019-5-9 16:22:37 by cgj
 */
public class UserVo {
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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;

    public UserVo() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getShareName() {
        return shareName;
    }

    public void setShareName(String shareName) {
        this.shareName = shareName;
    }

    public Double getFistBuyPrice() {
        return fistBuyPrice;
    }

    public void setFistBuyPrice(Double fistBuyPrice) {
        this.fistBuyPrice = fistBuyPrice;
    }

    public Long getFirstBuyDate() {
        return firstBuyDate;
    }

    public void setFirstBuyDate(Long firstBuyDate) {
        this.firstBuyDate = firstBuyDate;
    }

    public Double getSoldPrice() {
        return soldPrice;
    }

    public void setSoldPrice(Double soldPrice) {
        this.soldPrice = soldPrice;
    }

    public Long getSoldDate() {
        return soldDate;
    }

    public void setSoldDate(Long soldDate) {
        this.soldDate = soldDate;
    }

    public Long getShareNumber() {
        return shareNumber;
    }

    public void setShareNumber(Long shareNumber) {
        this.shareNumber = shareNumber;
    }

    public BigDecimal getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(BigDecimal accountBalance) {
        this.accountBalance = accountBalance;
    }
}
