package io.github.kingschan1204.istock.module.maindata.po;

import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import javax.annotation.PostConstruct;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
@Component

public class AuthorityOutQuery {
    @Autowired
    private MongoTemplate mongoTemplate;

    private PriorityBlockingQueue<HashMap<String,ArrayList<Authority>>> maxHeap;

    public AuthorityOutQuery() {
        maxHeap = new PriorityBlockingQueue<HashMap<String,ArrayList<Authority>>>(11, new Comparator<HashMap<String,ArrayList<Authority>>>() { //大顶堆，容量11
            @Override
            public int compare(HashMap<String,ArrayList<Authority>> u1, HashMap<String,ArrayList<Authority>> u2) {
                return Integer.valueOf(u2.keySet().iterator().next()) - Integer.valueOf(u1.keySet().iterator().next());
            }
        });

    }

    public HashMap<String, ArrayList<Authority>> getlocat(int code){
        Iterator<HashMap<String,ArrayList<Authority>>> itor=maxHeap.iterator();
        while(itor.hasNext() ){
            HashMap<String,ArrayList<Authority>>tem=itor.next();
            if(code==Integer.valueOf(tem.keySet().iterator().next()))
                return tem;
        }return null;
    }

    @PostConstruct
    public void init(){
        Query queryIn = new Query();
        queryIn.addCriteria(Criteria.where("behavior").is("out").where("status").is("receive"));
        List<Authority> listIn= mongoTemplate.find(queryIn,Authority.class);
        for (Authority i : listIn) {
            if(maxHeap.size()==0){
                HashMap<String, ArrayList<Authority>>p=new HashMap<>();
                ArrayList<Authority>tem=new ArrayList<Authority>();
                tem.add(i);
                p.put(i.getCode(),tem);
                maxHeap.add(p);
            }
            else{
                addAuthority(i);
            }
        }
    }

    public void  addAuthority(Authority au) {
        System.out.println("新增卖出委托");
            HashMap<String, ArrayList<Authority>> p = getlocat(Integer.valueOf(au.getCode()));
            if (p != null){
                p.get(p.keySet().iterator().next()).add(au);
                Collections.sort(p.get(p.keySet().iterator().next()),new Comparator<Authority>(){
                    public int compare(Authority u1,Authority u2){
                        return u1.getPriceOrder()>u2.getPriceOrder()?1:-1;
                    }
                });
            }
            else {
                p=new HashMap<>();
                ArrayList<Authority> temp = new ArrayList<Authority>();
                temp.add(au);
                p.put(au.getCode(), temp);
                maxHeap.add(p);
            }
    }

    public PriorityBlockingQueue<HashMap<String,ArrayList<Authority>>>getMaxHeap(){return  maxHeap;}
    public void setMaxHeap(PriorityBlockingQueue<HashMap<String,ArrayList<Authority>>>maxHeap){this.maxHeap=maxHeap;}

}
