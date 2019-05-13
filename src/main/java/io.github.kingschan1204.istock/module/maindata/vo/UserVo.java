package io.github.kingschan1204.istock.module.maindata.vo;

import java.math.BigDecimal;

/*
创建用户实体类
2019-5-9 16:22:37 by cgj
 */
public class UserVo {
    private String id;
    private String account;
    private String password;
    private Double balance;//账户余额

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public UserVo() {
    }
}
