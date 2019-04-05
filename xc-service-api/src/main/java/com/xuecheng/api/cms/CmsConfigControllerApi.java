package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Created by 周大侠
 * 2019-04-02 15:34
 */
@Api(value = "cms配置管理接口", description = "cms配置管理接口，提供数据模型的管理，查询接口")
public interface CmsConfigControllerApi {

    @ApiOperation("根据id查询页面配置信息")
    CmsConfig getModel(String id);
}
