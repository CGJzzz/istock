package io.github.kingschan1204.istock.module.maindata.services;

import io.github.kingschan1204.istock.module.maindata.po.ShareHolding;
import io.github.kingschan1204.istock.module.maindata.repository.ShareHoldingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShareHoldingService {
    @Autowired
    private MongoTemplate template;
    @Autowired
    private ShareHoldingRepository shareHoldingRepository;

    public boolean addShareHolding(ShareHolding shareHolding){
        template.save(shareHolding);
        return true;
    }
}
