package io.github.kingschan1204.istock.module.maindata.services;

import io.github.kingschan1204.istock.module.maindata.po.User;
import io.github.kingschan1204.istock.module.maindata.repository.UserRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MongoTemplate template;

    /**
     * 用户登录功能
     * 2019-5-10 10:35:23
     * 怎么测试查询成功 tag***
     */
    public User queryUserByLogin(String account,String password){
        Query query=new Query();
        query.addCriteria(Criteria.where("account").is(account).and("password").is(password));
        List<User> list=template.find(query,User.class);
        if (list.size()==1){
            User user=list.get(0);
            return user;
        }else {
            return null;
        }
    }
    //添加用户功能,不允许重复accountName->account(数据库属性)
    public boolean addUser(String accountName,String password){
//        测试连接成功
        Query query=new Query();
//        query.addCriteria(Criteria.where("account").is(accountName).and("password").is(password));
        query.addCriteria(Criteria.where("account").is(accountName));
        List<User> list=template.find(query,User.class);
        if(list.size()==0){
            User user =new User();
            user.setAccount(accountName);
            user.setPassword(password);
            user.setBalance((double) 0);
            template.save(user);
            return true;
        }else{
            return false;
        }
    }

    //进入用户委托界面,下单之前默认用户已经登录了,如果没有登录会跳转到登录界面,
    //此方法仅用于登录后查询用户
    public User queryUserAfterLogin(String account){
        Query query=new Query();
        query.addCriteria(Criteria.where("account").is(account));
        List<User> list=template.find(query,User.class);
        if (list.size()==1){
            User user=list.get(0);
            return user;
        }else {
            return null;
        }
    }

    public boolean saveUser(User user){
        if(user!=null){
            template.save(user);
            return true;
        }else {
            return false;
        }
    }
}
