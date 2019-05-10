package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.CmsPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

/**
 * Created by 周大侠
 * 2019-03-31 16:35
 */
@Api(value = "cms页面管理接口", description = "cms页面管理接口，提供页面的增、删、改、查")
public interface CmsPageControllerApi {
    /**
     * 页面查询
     * @param page
     * @param size
     * @param pageRequest
     * @return
     */
    @ApiOperation("分页查询页面列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页记录数", required = true, paramType = "path", dataType = "int")
    })
    QueryResponseResult findCmsPageList(int page, int size, CmsPageRequest pageRequest);

    /**
     * 增加页面
     * @param cmsPage
     * @return
     */
    @ApiOperation("增加页面")
    CmsPageResult addCmsPage(CmsPage cmsPage);

    /**
     * 修改页面
     * @param id
     * @param cmsPage
     * @return
     */
    @ApiOperation("修改页面")
    CmsPageResult editCmsPage(String id, CmsPage cmsPage);

    /**
     * 删除页面
     * @param id
     * @return
     */
    @ApiOperation("删除页面")
    ResponseResult deleteCmsPage(String id);

    /**
     * 查询单个页面
     * @param id
     * @return
     */
    @ApiOperation("查询单个页面")
    CmsPageResult findCmsPage(String id);

    /**
     * 发布页面
     * @param pageId
     * @return
     */
    @ApiOperation("页面发布")
    ResponseResult post(String pageId) throws Exception;

    @ApiOperation("增加页面")
    CmsPageResult saveCmsPage( CmsPage cmsPage);

    @ApiOperation("一键发布")
    CmsPostPageResult postPageQuick(CmsPage cmsPage);
}
