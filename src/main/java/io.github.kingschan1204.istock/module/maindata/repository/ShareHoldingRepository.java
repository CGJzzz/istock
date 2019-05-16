package io.github.kingschan1204.istock.module.maindata.repository;

import io.github.kingschan1204.istock.module.maindata.po.ShareHolding;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShareHoldingRepository extends MongoRepository<ShareHolding,String> {
}
