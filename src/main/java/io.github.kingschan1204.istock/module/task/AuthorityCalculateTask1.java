package io.github.kingschan1204.istock.module.task;

import io.github.kingschan1204.istock.module.maindata.po.*;
import io.github.kingschan1204.istock.module.maindata.services.AuthorityService;
import io.github.kingschan1204.istock.module.maindata.services.ShareHoldingService;
import io.github.kingschan1204.istock.module.maindata.services.UserService;
import lombok.extern.slf4j.Slf4j;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;


import java.util.*;


@Slf4j
@Component
public class AuthorityCalculateTask1 implements Job{

        @Autowired
        private MongoTemplate mongoTemplate;

        //用途是? 2019-5-16 11:17:19
        @Autowired
        private UserService userService;
        @Autowired
        private AuthorityService authorityService;
        @Autowired
        private ShareHoldingService shareHoldingService;
        @Autowired
        private AuthorityInQuery inQuery;
        @Autowired
        private AuthorityOutQuery outQuery;

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException{
            if(inQuery.getMinHeap().size()<1||outQuery.getMaxHeap().size()<1)
                return ;

            //买卖队列
            Iterator<HashMap<String, ArrayList<Authority>>>InItor=inQuery.getMinHeap().iterator();
            Iterator<HashMap<String, ArrayList<Authority>>>OutItor=outQuery.getMaxHeap().iterator();

            //能进入撮合的hashmap队列
            ArrayList<HashMap<String, ArrayList<Authority>>>InArr=new ArrayList<>();
            ArrayList<HashMap<String, ArrayList<Authority>>>OutArr=new ArrayList<>();

            //临时hashmap 用于股票代码比较
            HashMap<String, ArrayList<Authority>> temi=inQuery.getMinHeap().peek();
            HashMap<String, ArrayList<Authority>> temo=outQuery.getMaxHeap().peek();
            //获取所有能进入撮合的买入卖出任务
            while(getcode(temi)<=getcode(temo)) {
                InArr.add(inQuery.getMinHeap().poll());
                if(inQuery.getMinHeap().size()>0)
                    temi=inQuery.getMinHeap().peek();
                else
                    break;
            }
            if(InArr.size()>0){
                while(getcode(temo)>=getcode(InArr.get(0))){
                        OutArr.add(outQuery.getMaxHeap().poll());
                        if(outQuery.getMaxHeap().size()>0)
                            temo=outQuery.getMaxHeap().peek();
                        else
                            break;
                }
            }
            else
                return;
            if(OutArr.size()==0)
                return ;

            //撮合操作
            int i=0;
            int j=OutArr.size()-1;
            while(i<InArr.size()&&j>=0){

                if(getcode(InArr.get(i))<getcode(OutArr.get(j)))
                    i++;
                else if(getcode(InArr.get(i))>getcode(OutArr.get(j)))
                    j--;
                else{
                    //撮合逻辑
                    System.out.println("进行撮合");

                    ArrayList<Authority>InAuArr=InArr.get(i).get(InArr.get(i).keySet().iterator().next());
                    ArrayList<Authority>OutAuArr=OutArr.get(j).get(OutArr.get(j).keySet().iterator().next());
                    int x=0;
                    int y=0;
                    while(InAuArr.get(x).getPriceOrder()>=OutAuArr.get(y).getPriceOrder()){
                        long sum;
                        if(InAuArr.get(x).getNumberOfShare()>OutAuArr.get(y).getNumberOfShare())
                            sum=OutAuArr.get(y).getNumberOfShare();
                        else
                            sum=InAuArr.get(x).getNumberOfShare();
                        InAuArr.get(x).setNumberOfShare(InAuArr.get(x).getNumberOfShare()-sum);
                        OutAuArr.get(y).setNumberOfShare(OutAuArr.get(y).getNumberOfShare()-sum);

                        Long InNum=InAuArr.get(x).getNumberOfShare();
                        Long OutNum=OutAuArr.get(y).getNumberOfShare();

                        //数据库操作

                        //in买入操作
                        //更新买家账户金额
                        Query queryInUser = new Query();
                        queryInUser.addCriteria(Criteria.where("account").is(InAuArr.get(x).getAccount()));
                        List<User> listInUser = mongoTemplate.find(queryInUser, User.class);
                        System.out.println("此时买方账号是"+listInUser.get(0));
                        listInUser.get(0).setBalance(listInUser.get(0).getBalance()+sum*(InAuArr.get(x).getPriceOrder()-OutAuArr.get(y).getPriceOrder()));
                        userService.saveUser(listInUser.get(0));
                        //更改买家持仓数目
                       List<ShareHolding> listOfIn=shareHoldingService.searchSpecificCode(InAuArr.get(x).getAccount(),
                                InAuArr.get(x).getCode());
                        ShareHolding shareHoldingIn=listOfIn.get(0);
                        System.out.println("买家持仓"+shareHoldingIn.getNumber());
                        shareHoldingIn.setNumber(shareHoldingIn.getNumber()+sum);
                        System.out.println("买家买后持仓"+shareHoldingIn.getNumber());
                        shareHoldingService.save(shareHoldingIn);
                        //更改委托单状态
                        Query queryInAu=new Query();
                        queryInAu.addCriteria(Criteria.where("id").is(InAuArr.get(x).getId()));
                        List<Authority>listInAu=mongoTemplate.find(queryInAu,Authority.class);
                        listInAu.get(0).setNumberOfShare(InNum);
                        if(InNum==0)
                            listInAu.get(0).setStatus("done");
                        authorityService.save(listInAu.get(0));

                        //out卖出操作
                        //更改卖家账户金额
                        Query queryOutUser = new Query();
                        queryOutUser.addCriteria(Criteria.where("account").is(OutAuArr.get(y).getAccount()));
                        List<User> listOutUser = mongoTemplate.find(queryOutUser, User.class);
                        System.out.println("此时卖方账号是"+listOutUser.get(0));
                        listOutUser.get(0).setBalance(listInUser.get(0).getBalance()+sum*OutAuArr.get(y).getPriceOrder());
                        userService.saveUser(listOutUser.get(0));
                        //更改卖家持仓
                        /*ShareHolding shareHoldingOut=shareHoldingService.searchSpecificCode(OutAuArr.get(y).getAccount(),
                                OutAuArr.get(y).getCode()).get(0);
                        shareHoldingOut.setNumber(shareHoldingOut.getNumber()-sum);
                        shareHoldingService.save(shareHoldingOut);*/
                        //更改卖家委托
                        Query queryOutAu=new Query();
                        queryOutAu.addCriteria(Criteria.where("id").is(OutAuArr.get(y).getId()));
                        List<Authority>listOutAu=mongoTemplate.find(queryOutAu,Authority.class);
                        listOutAu.get(0).setNumberOfShare(OutNum);
                        if(OutNum==0)
                            listOutAu.get(0).setStatus("done");
                        authorityService.save(listOutAu.get(0));
                        //数据库操作

                        //撮合结果
                        String result="卖家："+OutAuArr.get(y).getId()+" 买家： "+InAuArr.get(x).getId()+" 成交股价："+OutAuArr.get(y).getPriceOrder()+" 成交数量："+sum+" 股票代码："+getcode(InArr.get(i));
                        System.out.println(result);


                        if(InAuArr.get(x).getNumberOfShare()==0)
                            InAuArr.remove(x);
                        if(OutAuArr.get(y).getNumberOfShare()==0)
                            OutAuArr.remove(y);
                        if(x==InAuArr.size())break;
                        if(y==OutAuArr.size())break;
                    }
                    i++;j--;
                }
            }
            //将未撮合成功的重新放回队列
            for(i=0;i<InArr.size();i++){
                HashMap<String,ArrayList<Authority>>temp=InArr.get(i);
                if(temp.get(temp.keySet().iterator().next()).size()!=0)
                    inQuery.getMinHeap().add(temp);
            }
            for(j=0;j<OutArr.size();j++){
                HashMap<String,ArrayList<Authority>>temp=OutArr.get(j);
                if(temp.get(temp.keySet().iterator().next()).size()!=0)
                    outQuery.getMaxHeap().add(temp);
            }

        }

        public int getcode(HashMap<String, ArrayList<Authority>> p){
            return Integer.valueOf(p.keySet().iterator().next());
        }
}


