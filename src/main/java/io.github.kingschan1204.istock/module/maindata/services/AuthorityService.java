package io.github.kingschan1204.istock.module.maindata.services;

import io.github.kingschan1204.istock.module.maindata.po.Authority;
import io.github.kingschan1204.istock.module.maindata.po.User;
import io.github.kingschan1204.istock.module.maindata.repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthorityService {
    @Autowired
    private MongoTemplate template;
    @Autowired
    private AuthorityRepository  authorityRepository;

    public void addAuthority( Authority authority){
        template.save(authority);
    }
    public void add(){
        Authority a=new Authority();
        a.setCode("000001");
        template.save(a);
    }

    public void save(Authority authority){
        if(authority!=null){
            template.save(authority);
        }
    }

    //查询委托,参数:用户账号account+股票代码code+behavior的数量

    public List<Authority> searchByAccountCodeBehaviro(String account, String code, String behaviro){
        Query query=new Query();
        //query.addCriteria(Criteria.where("account").is(account).and("code").is(code).and("behavior").is(behaviro));
        query.addCriteria(Criteria.where("account").ne("root1"));
        List<Authority> list=template.find(query,Authority.class);
        return list;
    }
}
