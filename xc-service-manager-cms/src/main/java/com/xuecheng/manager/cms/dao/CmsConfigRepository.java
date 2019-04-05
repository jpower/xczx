package com.xuecheng.manager.cms.dao;

import com.xuecheng.framework.domain.cms.CmsConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by 周大侠
 * 2019-04-02 15:40
 */
public interface CmsConfigRepository extends MongoRepository<CmsConfig,String> {
}
