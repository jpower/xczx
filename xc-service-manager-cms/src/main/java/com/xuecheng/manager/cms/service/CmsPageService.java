package com.xuecheng.manager.cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.CmsPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manager.cms.config.RabbitMqConfig;
import com.xuecheng.manager.cms.dao.CmsPageRepository;
import com.xuecheng.manager.cms.dao.CmsSiteRepository;
import com.xuecheng.manager.cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.spring.web.json.Json;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Optional;
import java.util.Map;

/**
 * Created by 周大侠
 * 2019-03-31 17:05
 */
@Service
//@Transactional
public class CmsPageService {
    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CmsTemplateRepository templateRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private CmsSiteRepository siteRepository;

    // 交换机名称
    @Value("${xuecheng.mq.exchange}")
    private String exchange_cms_name;

    /**
     * 分页并根据条件查询亚页面列表
     *
     * @param page
     * @param size
     * @param cmsPageRequest
     * @return
     */
    public Page<CmsPage> findCmsPageList(int page, int size, CmsPageRequest cmsPageRequest) {
        if (page < 1) {
            page = 1;
        }
        if (size < 10) {
            size = 7;
        }
        // 封装查询条件
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());

        CmsPage cmsPage = new CmsPage();

        if (cmsPageRequest != null) {
            if (StringUtils.isNotEmpty(cmsPageRequest.getSiteId())) {
                cmsPage.setSiteId(cmsPageRequest.getSiteId());
            }
            if (StringUtils.isNotEmpty(cmsPageRequest.getTemplateId())) {
                cmsPage.setTemplateId(cmsPageRequest.getTemplateId());
            }
            if (StringUtils.isNotEmpty(cmsPageRequest.getPageAliase())) {
                cmsPage.setPageAliase(cmsPageRequest.getPageAliase());
            }

        }

        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
        Page<CmsPage> pageList = cmsPageRepository.findAll(example, PageRequest.of(page - 1, size));


        return pageList;
    }

    /**
     * 查询单个页面信息
     *
     * @param id
     * @return
     */
    public CmsPage findCmsPageById(String id) {
        Optional<CmsPage> pageOptional = cmsPageRepository.findById(id);
        if (pageOptional.isPresent()) {
            return pageOptional.get();
        }
        return null;
    }

    /**
     * 删除单个页面信息
     *
     * @param id
     */
    public void deleteCmsPage(String id) {
        CmsPage cmsPage = this.findCmsPageById(id);
        if (cmsPage == null) {
            // 删除页面不存在 抛出自定义异常
            ExceptionCast.exception(CmsCode.CMS_PAGE_ISNULL);
        }

        cmsPageRepository.deleteById(id);

    }

    /**
     * 增加页面
     *
     * @param cmsPage
     */
    public CmsPage addCmsPage(CmsPage cmsPage) {
        if (cmsPage == null) {
            // 页面信息为Null
            ExceptionCast.exception(CmsCode.CMS_REQUESTPARAMS_ERROR);
        }
        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (cmsPage1 != null) {
            // 页面已经存在
            ExceptionCast.exception(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        cmsPage.setPageId(null);
        CmsPage page = cmsPageRepository.save(cmsPage);
        return page;

    }

    /**
     * 修改页面
     *
     * @param id
     * @param cmsPage
     * @return
     */
    public CmsPage updateCmsPage(String id, CmsPage cmsPage) {
        CmsPage cmsPageById = this.findCmsPageById(id);
        if (cmsPageById == null) {
            // 需要修改的页面不存在
            ExceptionCast.exception(CmsCode.CMS_PAGE_ISNULL);
        }
        if (cmsPage == null) {
            // 修改条件为Null
            ExceptionCast.exception(CmsCode.CMS_REQUESTPARAMS_ERROR);
        }

        cmsPage.setPageId(id);
        return cmsPageRepository.save(cmsPage);


    }

    /**
     * 生成静态页面
     *
     * @return
     */
    public String getPageHtml(String pageId) {
        CmsPage cmsPage = this.findCmsPageById(pageId);
        if (cmsPage == null) {
            // 页面为null
            ExceptionCast.exception(CmsCode.CMS_PAGE_ISNULL);

        }
        // 获取模型数据
        Map modelData = getModelData(cmsPage.getDataUrl());

        // 获取模板文件
        String template = getTemplate(cmsPage.getTemplateId());

        //执行静态化
        String s = generateHtml(template, modelData);

        return s;


    }

    /**
     * 生成静态页面具体步骤
     *
     * @param template
     * @param modelData
     * @return
     * @throws IOException
     * @throws TemplateException
     */
    public String generateHtml(String template, Map modelData) {
        // 生成配置类
        Configuration configuration = new Configuration(Configuration.getVersion());

        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();

        stringTemplateLoader.putTemplate("template", template);
        configuration.setTemplateLoader(stringTemplateLoader);

        Template template1 = null;
        String html = null;
        try {
            template1 = configuration.getTemplate("template");
            html = FreeMarkerTemplateUtils.processTemplateIntoString(template1, modelData);
        } catch (IOException e) {
            e.printStackTrace();
            ExceptionCast.exception("生成静态页面失败");
        } catch (TemplateException e) {
            e.printStackTrace();
            ExceptionCast.exception("获取模板文件失败");
        }

        return html;


    }

    /**
     * 获取模板文件
     *
     * @param templateId
     * @return
     * @throws IOException
     */
    private String getTemplate(String templateId) {
        if (StringUtils.isEmpty(templateId)) {
            // 模板ID为Null
            ExceptionCast.exception(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }

        Optional<CmsTemplate> templateOptional = templateRepository.findById(templateId);
        if (!templateOptional.isPresent()) {
            // 模板为Null
            ExceptionCast.exception(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        CmsTemplate cmsTemplate = templateOptional.get();

        String templateFileId = cmsTemplate.getTemplateFileId();

        if (StringUtils.isEmpty(templateFileId)) {
            // 模板文件id为Null
            ExceptionCast.exception(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }

        // 查找文件开始
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());

        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);

        String content = null;
        try {
            content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
            ExceptionCast.exception("查询页面模板失败");
        }
        return content;
    }

    /**
     * 根据cmspage中的dataurl 查找模板数据
     *
     * @param dataUrl
     * @return
     */
    private Map getModelData(String dataUrl) {
        if (StringUtils.isEmpty(dataUrl)) {
            // dataUrl为空
            ExceptionCast.exception(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        ResponseEntity<Map> entity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = entity.getBody();
        return body;
    }

    /**
     * 执行静态化 往mq中发送消息发布页面消息
     *
     * @param pageId
     */
    public ResponseResult postPage(String pageId) {
        // 执行静态化
        String pageHtml = this.getPageHtml(pageId);

        CmsPage cmsPage = this.findCmsPageById(pageId);
        if (cmsPage == null) {
            ExceptionCast.exception(CmsCode.CMS_PAGE_ISNULL);
        }
        // 删除之前的文件
        String htmlFileId = cmsPage.getHtmlFileId();
        if (StringUtils.isNotEmpty(htmlFileId)) {
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(htmlFileId)));
        }
        // 将静态化文件保存至GridFs
        InputStream inputStream = null;
        try {
            inputStream = IOUtils.toInputStream(pageHtml, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
            ExceptionCast.exception("静态文件保存失败");
        }
        ObjectId objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());

        // 更新页面信息中的静态文件id
        cmsPage.setHtmlFileId(objectId.toHexString());
        cmsPageRepository.save(cmsPage);

        // 发送消息
        Map<String, String> map = new HashMap();
        map.put("pageId", pageId);
        String s = JSON.toJSONString(map);
        rabbitTemplate.convertAndSend(exchange_cms_name, cmsPage.getSiteId(), s);

        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 增加页面 如果存在则修改
     *
     * @param cmsPage
     * @return
     */
    public CmsPage saveCmsPage(CmsPage cmsPage) {
        if (cmsPage == null) {
            // 页面信息为Null
            ExceptionCast.exception(CmsCode.CMS_REQUESTPARAMS_ERROR);
        }
        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (cmsPage1 != null) {
            // 页面已经存在 则修改
            this.updateCmsPage(cmsPage1.getPageId(), cmsPage);
        }
        CmsPage page = cmsPageRepository.save(cmsPage);
        return page;


    }

    /**
     * 一键发布
     * 接收课程服务传来的页面信息
     * 保存或者更新页面信息
     * 根据页面ID 生成静态化页面 往mq中发送发布页面的消息
     * 返回页面发布所在地址
     * @param cmsPage
     * @return
     */
    public CmsPostPageResult postPageQuick(CmsPage cmsPage) {
        // 保存或更新页面信息
        CmsPage cmsPage1 = this.saveCmsPage(cmsPage);

        // 执行静态化 发布消息
        ResponseResult responseResult = this.postPage(cmsPage1.getPageId());
        if (!responseResult.isSuccess()) {
            new CmsPostPageResult(CommonCode.FAIL, null);
        }
        Optional<CmsSite> siteOptional = siteRepository.findById(cmsPage.getSiteId());
        CmsSite cmsSite = siteOptional.get();
        // 生成的静态页面访问路径 =站点domain路径 + 页面web路径 + 页面名字
        String pageUrl = cmsSite.getSiteDomain() + cmsPage1.getPageWebPath() + cmsPage1.getPageName();
        return new CmsPostPageResult(CommonCode.SUCCESS, pageUrl);
    }
}
