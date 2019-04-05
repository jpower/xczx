package com.xuecheng.manager.cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by 周大侠
 * 2019-03-31 17:06
 */
public interface CmsPageRepository extends MongoRepository<CmsPage,String> {
    /**
     * 根据页面名称，站点ID，webPath查询
     * @param name
     * @param siteId
     * @param path
     * @return
     */
    CmsPage findByPageNameAndSiteIdAndPageWebPath(String name, String siteId, String path);
}
