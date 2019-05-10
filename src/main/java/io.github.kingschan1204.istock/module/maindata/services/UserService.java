package io.github.kingschan1204.istock.module.maindata.services;

import io.github.kingschan1204.istock.module.maindata.po.User;
import io.github.kingschan1204.istock.module.maindata.repository.UserRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

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

    public void addUser(String accountName,String password){
        User user =new User();
        user.setAccount(accountName);
        user.setPassword(password);
        template.save(user);
    }
}
