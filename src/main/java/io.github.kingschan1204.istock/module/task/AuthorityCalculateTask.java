package io.github.kingschan1204.istock.module.task;

import io.github.kingschan1204.istock.module.maindata.po.Authority;
import io.github.kingschan1204.istock.module.maindata.po.User;
import io.github.kingschan1204.istock.module.maindata.repository.AuthorityRepository;
import io.github.kingschan1204.istock.module.maindata.services.AuthorityService;
import io.github.kingschan1204.istock.module.maindata.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class AuthorityCalculateTask implements Job {
    @Autowired
    private MongoTemplate mongoTemplate;
    //用途是? 2019-5-16 11:17:19
    @Autowired
    private UserService userService;
    @Autowired
    private AuthorityService authorityService;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //买入查询,返回队列,按价格高到低,数量多到少
        Query queryIn=new Query();
        queryIn.addCriteria(Criteria.where("behavior").is("in"));
        queryIn.with(new Sort(Sort.Direction.DESC,"priceOrder").and(new Sort(Sort.Direction.DESC,"numberOfShare")));
        List<Authority> listIn=mongoTemplate.find(queryIn,Authority.class);

        //卖出查询,返回队列,按价格高到低,数量多到少
        Query queryOut=new Query();
        queryIn.addCriteria(Criteria.where("behavior").is("out"));
        queryIn.with(new Sort(Sort.Direction.ASC,"priceOrder").and(new Sort(Sort.Direction.DESC,"numberOfShare")));
        List<Authority> listOut=mongoTemplate.find(queryIn,Authority.class);
        if(listOut.size()>0&& listIn.size()>0){
            for(Authority authorityIn:listIn){
                for(Authority authorityOut:listOut){
                    if(authorityIn.getCode().equals(authorityOut.getCode())){
                        if(authorityIn.getPriceOrder()>authorityOut.getPriceOrder()){
                            Long numIn=authorityIn.getNumberOfShare();
                            Long numOut=authorityOut.getNumberOfShare();
                            if(Long.compare((numIn),0L)>0){
                                if(Long.compare(numOut,0L)>0){
                                    if(numIn>numOut){
                                        numIn=numIn-numOut;
                                        numOut=0L;

                                        Query queryInUser=new Query();
                                        queryIn.addCriteria(Criteria.where("account").is(authorityIn.getAccount()));
                                        List<User> listInUser=mongoTemplate.find(queryIn,User.class);

                                        Query queryOutUser=new Query();
                                        queryIn.addCriteria(Criteria.where("account").is(authorityOut.getAccount()));
                                        List<User> listOutUser=mongoTemplate.find(queryIn,User.class);
                                        if(listInUser.size()==1&&listOutUser.size()==1){

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}
