package com.xuecheng.manager.cms.web.controller;

import com.xuecheng.api.cms.CmsConfigControllerApi;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.manager.cms.service.CmsConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by 周大侠
 * 2019-04-02 15:36
 */
@RestController
@RequestMapping("/cms/config")
public class CmsConfigController implements CmsConfigControllerApi {
    @Autowired
    private CmsConfigService cmsConfigService;

    /**
     * 根据页面id查询页面配置信息
     * @param id
     * @return
     */
    @Override
    @GetMapping("/{id}")
    public CmsConfig getModel(@PathVariable(name = "id") String id) {


        return cmsConfigService.findCmsConfigById(id);
    }
}
