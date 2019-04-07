package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;

/**
 * Created by Administrator.
 */

@Api(value="课程管理接口",description = "课程管理接口，提供课程的增、删、改、查")
public interface CourseControllerApi {
    @ApiOperation("课程计划查询")
    TeachplanNode findTeachplanList(String courseId);

    @ApiOperation("添加课程计划")
    ResponseResult addTeachplan(Teachplan teachplan);

    @ApiOperation("查询课程列表")
    QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest);

    @ApiOperation("查询课程信息")
    CourseBase getCourseBaseById(String courseId);

    @ApiOperation("新增课程")
    ResponseResult addCourseBase(CourseBase courseBase, BindingResult bindingResult);

    @ApiOperation("修改课程信息")
    ResponseResult updateCoursebase(String courseId, CourseBase courseBase,BindingResult bindingResult);

    @ApiOperation("查询课程营销信息")
    CourseMarket getCourseMarketById(String courseId);

    @ApiOperation("修改课程营销信息")
    ResponseResult updateCourseMarket(String courseId, CourseMarket courseMarket);

    @ApiOperation("保存课程图片")
    ResponseResult addCoursePic(String courseId, String fileId);

    @ApiOperation("查询课程图片")
    CoursePic findCoursePicList(String courseId);

    @ApiOperation("删除课程图片")
    ResponseResult deleteCoursePic(String courseId);


}