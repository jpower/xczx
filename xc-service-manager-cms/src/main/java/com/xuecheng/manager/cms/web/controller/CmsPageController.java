package com.xuecheng.manager.cms.web.controller;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.CmsPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manager.cms.service.CmsPageService;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Created by 周大侠
 * 2019-03-31 16:52
 */
@RestController
@RequestMapping("/cms/page")
public class CmsPageController implements CmsPageControllerApi {

    @Autowired
    private CmsPageService cmsPageService;

    /**
     * 分页查询cms页面
     *
     * @param page
     * @param size
     * @param pageRequest
     * @return
     */
    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findCmsPageList(@PathVariable("page") int page, @PathVariable(name = "size") int size, CmsPageRequest pageRequest) {

        QueryResult<CmsPage> queryResult = new QueryResult<>();
        Page<CmsPage> pageList = cmsPageService.findCmsPageList(page, size, pageRequest);
        queryResult.setList(pageList.getContent());
        queryResult.setTotal(pageList.getTotalElements());
        QueryResponseResult result = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
        return result;

    }

    /**
     * 增加页面
     *
     * @param cmsPage
     * @return
     */
    @Override
    @PostMapping("/add")
    public CmsPageResult addCmsPage(@RequestBody CmsPage cmsPage) {
        CmsPage page = cmsPageService.addCmsPage(cmsPage);
        CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, page);
        return cmsPageResult;
    }

    /**
     * 修改页面
     *
     * @param id
     * @param cmsPage
     * @return
     */
    @Override
    @PutMapping("edit/{id}")
    public CmsPageResult editCmsPage(@PathVariable("id") String id, @RequestBody CmsPage cmsPage) {
        CmsPage page = cmsPageService.updateCmsPage(id, cmsPage);

        CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, page);
        return cmsPageResult;
    }

    /**
     * 删除页面
     *
     * @param id
     * @return
     */
    @Override
    @DeleteMapping("del/{id}")
    public ResponseResult deleteCmsPage(@PathVariable("id") String id) {
        cmsPageService.deleteCmsPage(id);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 根据Id查询单个页面
     *
     * @param id
     * @return
     */
    @Override
    @GetMapping("/get/{id}")
    public CmsPageResult findCmsPage(@PathVariable("id") String id) {
        CmsPage cmsPage = cmsPageService.findCmsPageById(id);

        return new CmsPageResult(CommonCode.SUCCESS, cmsPage);
    }

    @Override
    @PostMapping("/postPage/{pageId}")
    public ResponseResult post(@PathVariable("pageId") String pageId) throws Exception {
        cmsPageService.postPage(pageId);
        return new ResponseResult(CommonCode.SUCCESS);

    }


}
