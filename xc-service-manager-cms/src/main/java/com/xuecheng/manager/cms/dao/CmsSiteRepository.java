package com.xuecheng.manager.cms.dao;

import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by 周大侠
 * 2019-04-08 14:35
 */
public interface CmsSiteRepository extends MongoRepository<CmsSite, String>{
}
