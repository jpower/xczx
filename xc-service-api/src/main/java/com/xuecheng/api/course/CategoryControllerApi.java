package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Created by 周大侠
 * 2019-04-06 12:17
 */

@Api(value="课程分类管理接口",description = "课程分类接口，提供分类的增、删、改、查")
public interface CategoryControllerApi {

    @ApiOperation("查询课程分类")
    CategoryNode findCategoryList();
}
