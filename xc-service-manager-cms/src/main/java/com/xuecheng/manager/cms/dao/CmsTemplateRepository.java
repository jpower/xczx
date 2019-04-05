package com.xuecheng.manager.cms.dao;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by 周大侠
 * 2019-04-02 16:47
 */
public interface CmsTemplateRepository extends MongoRepository<CmsTemplate,String> {
}
