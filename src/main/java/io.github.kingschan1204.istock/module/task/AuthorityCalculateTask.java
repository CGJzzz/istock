package io.github.kingschan1204.istock.module.task;

import io.github.kingschan1204.istock.module.maindata.po.Authority;
import io.github.kingschan1204.istock.module.maindata.po.ShareHolding;
import io.github.kingschan1204.istock.module.maindata.po.User;
import io.github.kingschan1204.istock.module.maindata.services.AuthorityService;
import io.github.kingschan1204.istock.module.maindata.services.ShareHoldingService;
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
    @Autowired
    private ShareHoldingService shareHoldingService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //买入查询,返回队列,按价格高到低,数量多到少
        Query queryIn = new Query();
        queryIn.addCriteria(Criteria.where("behavior").is("in"));
        //    按代码从小到大
        //买方按价格从高到低
        //买方按数量从多到少
        queryIn.with(new Sort(Sort.Direction.ASC, "code").and(new Sort(Sort.Direction.DESC, "priceOrder")).and(new Sort(Sort.Direction.DESC, "numberOfShare")));
        List<Authority> listIn = mongoTemplate.find(queryIn, Authority.class);
        System.out.println("此时买方是"+listIn);

        //卖出查询,返回队列,按价格高到低,数量多到少
        Query queryOut = new Query();
        queryOut.addCriteria(Criteria.where("behavior").is("out"));
        //    按代码从小到大
        //,卖方按价格从低到高
        //买方按数量从多到少
        queryOut.with(new Sort(Sort.Direction.ASC, "code").and(new Sort(Sort.Direction.ASC, "priceOrder")).and(new Sort(Sort.Direction.DESC, "numberOfShare")));
        List<Authority> listOut = mongoTemplate.find(queryOut, Authority.class);
        System.out.println("此时卖方是"+listOut);
        //有委托才有撮合
        if (listOut.size() > 0 && listIn.size() > 0) {
            //并不确定每一步都能撮合完某一单,于是用for两层循环
            for (Authority authorityIn : listIn) {
                for (Authority authorityOut : listOut) {
                    if (authorityIn.getCode().equals(authorityOut.getCode())
                            &&authorityIn.getStatus().equals("receive")
                            &&authorityOut.getStatus().equals("receive")) {

                        //限价只考虑买入比自己出价低的股份 getPriceOrder->下单时的价格
                        if (authorityIn.getPriceOrder() >= authorityOut.getPriceOrder()) {
                            //numIn->下单买入数量. numOut->下单卖出数量
                            Long numIn = authorityIn.getNumberOfShare();
                            Long numOut = authorityOut.getNumberOfShare();
                            //先找出买卖双方的User账号->需要更改余额
                            Query queryInUser = new Query();
                            queryInUser.addCriteria(Criteria.where("account").is(authorityIn.getAccount()));
                            List<User> listInUser = mongoTemplate.find(queryInUser, User.class);
                            System.out.println("此时买方账号是"+listInUser.get(0));

                            Query queryOutUser = new Query();
                            queryOutUser.addCriteria(Criteria.where("account").is(authorityOut.getAccount()));
                            List<User> listOutUser = mongoTemplate.find(queryOutUser, User.class);
                            System.out.println("此时卖方账号是"+listOutUser.get(0));

                            if (listInUser.size() == 1 && listOutUser.size() == 1) {
                                User userIn = listInUser.get(0);
                                User userOut = listOutUser.get(0);
                                if (numIn >= numOut) {
                                    //卖方全部卖出持有股,买方需求股数=买方需求股数-卖方卖出股数
//                                numIn=numIn-numOut;
//                                numOut=0L;
                                    authorityOut.setStatus("done");
                                    authorityOut.setNumberOfShare(0L);
                                    authorityIn.setNumberOfShare(numIn - numOut);

                                    //更新买方余额
                                    //买方先扣钱,扣完钱补回差额所以是+
                                    userIn.setBalance(userIn.getBalance() + authorityOut.getPriceOrder()*numOut);
                                    //更新卖方余额
                                    userOut.setBalance(userOut.getBalance() + numOut * authorityOut.getPriceOrder());
                                    authorityService.save(authorityIn);
                                    authorityService.save(authorityOut);
                                    userService.saveUser(userIn);
                                    userService.saveUser(userOut);

                                    //更新持仓数据
                                    List<ShareHolding> listOfIn=shareHoldingService.searchSpecificCode(authorityIn.getAccount(),
                                            authorityIn.getCode());
                                    ShareHolding shareHoldingIn;
                                    if(listOfIn.size()==1){
                                        shareHoldingIn=listOfIn.get(0);
                                        shareHoldingIn.setNumber(shareHoldingIn.getNumber()+numOut);
                                        shareHoldingService.save(shareHoldingIn);
                                    }else {
                                        shareHoldingIn =new ShareHolding();
                                        shareHoldingIn.setNumber(numOut);
                                        shareHoldingIn.setAccount(authorityIn.getAccount());
                                        shareHoldingIn.setCode(authorityIn.getCode());
                                        shareHoldingService.insert(shareHoldingIn);
                                    }
                                    //更新卖方持仓
                                    ShareHolding shareHoldingOut=shareHoldingService.searchSpecificCode(authorityOut.getAccount(),
                                            authorityOut.getCode()).get(0);
                                    shareHoldingOut.setNumber(shareHoldingOut.getNumber()-numOut);
                                    shareHoldingService.save(shareHoldingOut);
                                } else {
                                    authorityIn.setStatus("done");
                                    authorityIn.setNumberOfShare(0L);
                                    authorityOut.setNumberOfShare(numOut - numIn);
                                    //更新买方余额
                                    userIn.setBalance(userIn.getBalance() - numIn * authorityOut.getPriceOrder());
                                    //更新卖方余额
                                    userOut.setBalance(userOut.getBalance() + numIn * authorityOut.getPriceOrder());
                                    authorityService.save(authorityIn);
                                    authorityService.save(authorityOut);
                                    userService.saveUser(userIn);
                                    userService.saveUser(userOut);

                                    ShareHolding shareHoldingIn;
                                    List<ShareHolding> listOfIn =shareHoldingService.searchSpecificCode(authorityIn.getAccount(),
                                            authorityIn.getCode());
                                    if(listOfIn.size()==1){
                                        shareHoldingIn=listOfIn.get(0);
                                        shareHoldingIn.setNumber(shareHoldingIn.getNumber()+numIn);
                                        shareHoldingService.save(shareHoldingIn);
                                    }else {
                                        shareHoldingIn=new ShareHolding();
                                        shareHoldingIn.setAccount(authorityIn.getAccount());
                                        shareHoldingIn.setCode(authorityIn.getCode());
                                        shareHoldingIn.setNumber(numIn);
                                        shareHoldingService.insert(shareHoldingIn);
                                    }

                                    ShareHolding shareHoldingOut=shareHoldingService.searchSpecificCode(authorityOut.getAccount(),
                                            authorityOut.getCode()).get(0);
                                    shareHoldingOut.setNumber(shareHoldingOut.getNumber()-numIn);
                                    shareHoldingService.save(shareHoldingOut);
                                }
                            }
                        }
                    }
                }
                authorityIn.setStatus("false");
            }
            //全部循环结束后设置卖出委托状态为false
            Query qFinal = new Query();
            qFinal.addCriteria(Criteria.where("behavior").is("out").and("status").is("receive"));
            List<Authority> listFinal = mongoTemplate.find(qFinal, Authority.class);
            for (Authority a : listFinal) {
                a.setStatus("false");
            }
        }

    }
}
