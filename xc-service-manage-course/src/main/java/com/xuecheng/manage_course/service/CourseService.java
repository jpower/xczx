package com.xuecheng.manage_course.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.*;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Created by 周大侠
 * 2019-04-05 16:20
 */
@Service
public class CourseService {

    @Autowired
    private TeachplanMapper teachplanMapper;

    @Autowired
    private TeachplanRepository teachplanRepository;

    @Autowired
    private CourseBaseRepository courseBaseRepository;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseMarketRepository marketRepository;

    @Autowired
    private CoursePicRepository coursePicRepository;

    /**
     * 根据课程id查询该课程计划 形成课程计划树
     *
     * @param courseId
     * @return
     */
    public TeachplanNode findTeachplanNodeById(String courseId) {

        return teachplanMapper.findTeachplanNodeById(courseId);
    }

    @Transactional
    public ResponseResult addTeachplan(Teachplan teachplan) {

        if (teachplan == null || StringUtils.isEmpty(teachplan.getCourseid()) ||
                StringUtils.isEmpty(teachplan.getStatus()) ||
                StringUtils.isEmpty(teachplan.getPname())) {
            // 请求参数错误
            ExceptionCast.exception(CommonCode.INVALID_PARAM);
        }
        // 获取该课程计划的课程ID
        String courseid = teachplan.getCourseid();
        Optional<CourseBase> optional = courseBaseRepository.findById(courseid);
        if (!optional.isPresent()) {
            // 课程不存在
            ExceptionCast.exception(CourseCode.COURSE_ISNULL);
        }
        CourseBase courseBase = optional.get();
        // 获取该课程计划的父计划
        String parentid = teachplan.getParentid();
        // 如果没有选择父计划
        if (StringUtils.isEmpty(parentid)) {
            // 查询该课程计划的课程在课程计划表中的ID
            // 作为该课程计划的parentID
            parentid = getTeachplanRoot(courseid, courseBase);
            teachplan.setParentid(parentid);
            teachplan.setGrade("2");
        }
        teachplan.setGrade("3");
//        teachplan.setStatus("0");
        teachplanRepository.save(teachplan);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 查询需要保存课程计划的课程在课程计划表中的ID (课程计划根节点ID)
     * 注意！ 这个根节点可能不存在
     * 如果没有则需要创建出课程在课程计划表中的根节点
     *
     * @param courseId
     * @return
     */
    private String getTeachplanRoot(String courseId, CourseBase courseBase) {
        List<Teachplan> teachplans = teachplanRepository.findByCourseidAndParentidIs(courseId, "0");
        // 如果该课程在课程计划表还没有根节点那么需要添加该课程的根节点
        if (teachplans == null || teachplans.size() <= 0) {
            Teachplan rootTeachplan = new Teachplan();
            rootTeachplan.setCourseid(courseId);
            rootTeachplan.setParentid("0");
            rootTeachplan.setGrade("1");
            rootTeachplan.setStatus("0");
            courseBase.setDescription(courseBase.getName());
            Teachplan save = teachplanRepository.save(rootTeachplan);
            return save.getId();

        }
        return teachplans.get(0).getId();
    }


    /**
     * 查询课程列表
     *
     * @param page
     * @param size
     * @param courseListRequest
     * @return
     */
    public QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest) {

        PageHelper.startPage(page, size);
        List<CourseBase> courseList = courseMapper.findCourseList();
        PageInfo pageInfo = new PageInfo(courseList);

        QueryResult queryResult = new QueryResult();
        queryResult.setTotal(pageInfo.getTotal());
        queryResult.setList(pageInfo.getList());
        QueryResponseResult result = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
        return result;
    }

    /**
     * 查询课程信息
     *
     * @param courseId
     * @return
     */
    public CourseBase getCourseBaseById(String courseId) {
        Optional<CourseBase> baseOptional = courseBaseRepository.findById(courseId);
        if (!baseOptional.isPresent()) {
            return null;
        }
        return baseOptional.get();
    }

    /**
     * 修改课程信息
     *
     * @param courseId
     * @param courseBase
     * @return
     */
    public ResponseResult updateCoursebase(String courseId, CourseBase courseBase) {
        CourseBase courseBaseById = this.getCourseBaseById(courseId);
        if (courseBaseById == null) {
            // 课程不存在
            ExceptionCast.exception(CourseCode.COURSE_ISNULL);
        }
        courseBaseRepository.save(courseBase);
        return new ResponseResult(CommonCode.SUCCESS);

    }

    /**
     * 查询课程营销信息
     *
     * @param courseId
     * @return
     */
    public CourseMarket getCourseMarketById(String courseId) {
        Optional<CourseMarket> courseMarketOptional = marketRepository.findById(courseId);
        if (!courseMarketOptional.isPresent()) {
            return null;
        }
        return courseMarketOptional.get();
    }

    /**
     * 修改课程营销信息
     *
     * @param courseId
     * @param courseMarket
     * @return
     */
    public ResponseResult updateCourseMarket(String courseId, CourseMarket courseMarket) {
        CourseMarket courseMarketById = this.getCourseMarketById(courseId);
        if (courseMarketById == null) {
            courseMarket.setId(courseId);
        }
        marketRepository.save(courseMarket);

        return new ResponseResult(CommonCode.SUCCESS);

    }

    /**
     * 添加课程
     * @param courseBase
     * @return
     */
    public ResponseResult addCourseBase(CourseBase courseBase) {
        if(courseBase == null) {
            ExceptionCast.exception(CourseCode.COURSE_ISNULL);
        }
        courseBase.setId(null);
        courseBase.setStatus("202001");
        courseBaseRepository.save(courseBase);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 保存课程图片
     * @param courseId
     * @param fileId
     * @return
     */
    public ResponseResult addCoursePic(String courseId, String fileId) {
        CourseBase courseBase = this.getCourseBaseById(courseId);
        if(courseBase == null) {
            ExceptionCast.exception(CourseCode.COURSE_ISNULL);
        }
        CoursePic coursePic = new CoursePic();
        coursePic.setCourseid(courseId);
        coursePic.setPic(fileId);
        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);

    }

    /**
     * 查询课程图片
     * @param courseId
     * @return
     */
    public CoursePic findCoursePic(String courseId) {
        Optional<CoursePic> picOptional = coursePicRepository.findById(courseId);
        if(picOptional.isPresent()) {
            return picOptional.get();
        }
        return null;
    }

    /**
     * 删除课程图片
     * @param courseId
     * @return
     */
    public ResponseResult deleteCoursePic(String courseId) {
        CoursePic coursePic = this.findCoursePic(courseId);
        if(coursePic == null) {
            ExceptionCast.exception(CourseCode.COURSE_PIC_ISNULL);
        }

        coursePicRepository.deleteById(courseId);
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
