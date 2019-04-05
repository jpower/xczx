package com.xuecheng.cms.client.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.cms.client.dao.CmsPageRepository;
import com.xuecheng.cms.client.dao.CmsSiteRepository;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.ExceptionCast;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;

/**
 * Created by 周大侠
 * 2019-04-04 20:55
 */
@Service
@Slf4j
public class PageService {
    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    private CmsSiteRepository cmsSiteRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    /**
     * 发布页面
     *
     * @param pageId
     */
    public void publishPage(String pageId) throws IOException {
        CmsPage cmsPage = getCmsPage(pageId);

//        CmsSite cmsSite = getCmsSite(cmsPage);

        // 得到保存在GridFs上的静态页面Id 从GridFs上下载静态页面页面
        String staticPage = dowloadPage(cmsPage.getHtmlFileId());

        // 生成的静态页面保存路径 =页面物理路径 + 页面名字
        String pagePath = cmsPage.getPagePhysicalPath() + cmsPage.getPageName();

        // 发布静态页面
        publish(staticPage, pagePath);
    }

    /**
     * 获取页面信息
     * @param pageId
     * @return
     */
    private CmsPage getCmsPage(String pageId) {
        Optional<CmsPage> pageOptional = cmsPageRepository.findById(pageId);
        if (!pageOptional.isPresent()) {
            ExceptionCast.exception(CmsCode.CMS_PAGE_ISNULL);
        }
        return pageOptional.get();
    }

    /**
     * 获取页面所属站点信息
     * @param cmsPage
     * @return
     */
    private CmsSite getCmsSite(CmsPage cmsPage) {
        // 获取站点信息
        Optional<CmsSite> optional = cmsSiteRepository.findById(cmsPage.getSiteId());
        if (!optional.isPresent()) {
            ExceptionCast.exception(CmsCode.CMS_SITE_EMPTY);
        }
        return optional.get();
    }

    /**
     * 发布静态页面
     *
     * @param pageContent
     */
    private void publish(String pageContent, String pagePath) throws IOException {
        if (StringUtils.isEmpty(pageContent)) {
            // 静态页面内容不存在
            log.error("静态页面内容不存在");
            ExceptionCast.exception(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        InputStream inputStream = IOUtils.toInputStream(pageContent, "utf-8");
        IOUtils.copy(inputStream, new FileOutputStream(pagePath));
    }

    /**
     * 从GridFs上下载页面
     *
     * @param htmlFileId
     * @return
     */
    private String dowloadPage(String htmlFileId) throws IOException {
        if (StringUtils.isEmpty(htmlFileId)) {
            // 页面信息中的静态页面ID为Null
            log.error("下载页面时 页面信息中的静态页面ID为Null");
            ExceptionCast.exception(CmsCode.CMS_GENERATEHTML_HTMLISNULL);

        }
        // 查找文件开始
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(htmlFileId)));
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());

        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);

        String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");

        return content;
    }

}

