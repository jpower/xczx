package com.xuecheng.manage_course.web.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by 周大侠
 * 2019-04-05 16:23
 */
@RestController
@RequestMapping("/course")
public class CourseController implements CourseControllerApi {
    @Autowired
    private CourseService courseService;

    /**
     * 查询该课程的课程计划
     * @param courseId
     * @return
     */
    @Override
    @GetMapping("/teachplan/list/{courseId}")
    public TeachplanNode findTeachplanList(@PathVariable("courseId") String courseId) {

        return courseService.findTeachplanNodeById(courseId);
    }

    /**
     * 添加课程计划
     * @param teachplan
     * @return
     */
    @Override
    @PostMapping("/teachplan/add")
    public ResponseResult addTeachplan(@RequestBody Teachplan teachplan) {
        return courseService.addTeachplan(teachplan);
    }

    /**
     * 查询课程列表
     * @param page
     * @param size
     * @param courseListRequest
     * @return
     */
    @Override
    @GetMapping("/coursebase/list/{page}/{size}")
    public QueryResponseResult findCourseList(@PathVariable("page") int page,@PathVariable("size") int size, CourseListRequest courseListRequest) {
        return courseService.findCourseList(page, size ,courseListRequest);
    }

    /**
     * 根据Id查询课程信息
     * @param courseId
     * @return
     */
    @Override
    @GetMapping("/courseview/{courseId}")
    public CourseBase getCourseBaseById(@PathVariable("courseId") String courseId) {

        return courseService.getCourseBaseById(courseId);
    }

    @Override
    @PostMapping("/coursebase/add")
    public ResponseResult addCourseBase(@RequestBody @Valid CourseBase courseBase, BindingResult bindingResult) {
        // 校验字段
        if(bindingResult.hasErrors()) {
            ExceptionCast.exception(bindingResult.getFieldError().getDefaultMessage());
        }
        return courseService.addCourseBase(courseBase);
    }

    /**
     * 修改课程信息
     * @param courseId
     * @param courseBase
     * @return
     */
    @Override
    @PutMapping("/coursebase/edit/{courseId}")
    public ResponseResult updateCoursebase(@PathVariable("courseId") String courseId, @RequestBody @Valid CourseBase courseBase, BindingResult bindingResult) {
        // 校验字段
        if(bindingResult.hasErrors()) {
            ExceptionCast.exception(bindingResult.getFieldError().getDefaultMessage());
        }
       return courseService.updateCoursebase(courseId, courseBase);
    }

    /**
     * 查询课程营销信息
     * @param courseId
     * @return
     */
    @Override
    @GetMapping("/coursemarket/get/{courseId}")
    public CourseMarket getCourseMarketById(@PathVariable("courseId") String courseId) {
        return courseService.getCourseMarketById(courseId);
    }

    /**
     * 修改课程营销信息
     * @param courseId
     * @param courseMarket
     * @return
     */
    @Override
    @PutMapping("/coursemarket/edit/{courseId}")
    public ResponseResult updateCourseMarket(@PathVariable("courseId") String courseId, @RequestBody CourseMarket courseMarket) {
        return courseService.updateCourseMarket(courseId,courseMarket);
    }

    @Override
    @PostMapping("/coursepic/add")
    public ResponseResult addCoursePic(String courseId, String pic) {
        return courseService.addCoursePic(courseId, pic);
    }

    /**
     * 查询课程图片
     * @param courseId
     * @return
     */
    @Override
    @GetMapping("/coursepic/list/{courseId}")
    public CoursePic findCoursePicList(@PathVariable("courseId") String courseId) {
        return courseService.findCoursePic(courseId);
    }

    /**
     * 删除课程图片
     * @param courseId
     * @return
     */
    @Override
    @DeleteMapping("/coursepic/delete/{courseId}")
    public ResponseResult deleteCoursePic(@PathVariable("courseId") String courseId) {
        return courseService.deleteCoursePic(courseId);
    }
}
