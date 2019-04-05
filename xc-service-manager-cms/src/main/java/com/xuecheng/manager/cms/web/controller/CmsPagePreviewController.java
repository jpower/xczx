package com.xuecheng.manager.cms.web.controller;

import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manager.cms.service.CmsPageService;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by 周大侠
 * 2019-04-02 17:08
 */
@Controller
@RequestMapping("/cms/preview")
public class CmsPagePreviewController extends BaseController {
    @Autowired
    private CmsPageService cmsPageService;

    /**
     * 根据id 预览页面
     * 查询模板文件和模板数据生成静态页面给前端展示
     * @param id
     * @throws IOException
     * @throws TemplateException
     */
    @GetMapping("/{id}")
    public void preview(@PathVariable(name = "id") String id) throws IOException, TemplateException {
        String pageHtml = cmsPageService.getPageHtml(id);
        if(StringUtils.isEmpty(pageHtml)) {
            ExceptionCast.exception(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        PrintWriter writer = response.getWriter();
        writer.write(pageHtml);

    }


}
