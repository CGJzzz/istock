package io.github.kingschan1204.istock.module.maindata.services;

import io.github.kingschan1204.istock.module.maindata.po.ShareHolding;
import io.github.kingschan1204.istock.module.maindata.repository.ShareHoldingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShareHoldingService {
    @Autowired
    private MongoTemplate template;
    @Autowired
    private ShareHoldingRepository shareHoldingRepository;

    //用户查看自己的全部持有股票
    public List<ShareHolding> search(String account){
        Query query = new Query();
        query.addCriteria(Criteria.where("account").is(account));
        List<ShareHolding> list = template.find(query, ShareHolding.class);
        return list;
    }

    //供服务器查询用户特点股票持有量
    public List<ShareHolding> searchSpecificCode(String account,String code){
        Query query = new Query();
        query.addCriteria(Criteria.where("account").is(account));
        List<ShareHolding> list = template.find(query, ShareHolding.class);
        return list;
    }

    public void save(ShareHolding shareHolding){
        if(shareHolding!=null){
            template.save(shareHolding);
        }
    }

    public boolean insert(ShareHolding shareHolding){
        template.insert(shareHolding);
        return true;
    }
}
