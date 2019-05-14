package io.github.kingschan1204.istock.module.maindata.services;

import io.github.kingschan1204.istock.module.maindata.po.Authority;
import io.github.kingschan1204.istock.module.maindata.repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthorityService {
    @Autowired
    private MongoTemplate template;
    @Autowired
    private AuthorityRepository  authorityRepository;

    public void addAuthority( Authority authority){
        template.save(authority);
    }
}
