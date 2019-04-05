package com.xuecheng.manager.cms.service;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.manager.cms.dao.CmsConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by 周大侠
 * 2019-04-02 15:37
 */
@Service
public class CmsConfigService {
    @Autowired
    private CmsConfigRepository cmsConfigRepository;

    public CmsConfig findCmsConfigById(String id) {
        Optional<CmsConfig> configOptional = cmsConfigRepository.findById(id);

        return configOptional.isPresent() ? configOptional.get() : null;

    }

}
