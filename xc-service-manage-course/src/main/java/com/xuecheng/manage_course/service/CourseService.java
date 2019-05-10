package com.xuecheng.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.pojo.CourseView;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.*;
import com.xuecheng.manage_course.feignclient.CmsPageClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
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

    @Autowired
    private CourseMarketRepository courseMarketRepository;

    @Value("${course-publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course-publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course-publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course-publish.siteId}")
    private String publish_siteId;
    @Value("${course-publish.templateId}")
    private String publish_templateId;
    @Value("${course-publish.previewUrl}")
    private String previewUrl;
    @Autowired
    private CmsPageClient cmsPageClient;
    @Autowired
    private CoursePubRepository coursePubRepository;

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

    /**
     * 查询课程页面所需的数据 包括基本信息、图片、营销、课程计划
     * @param id
     * @return
     */
    public CourseView findCourseView(String id) {
        CourseView courseView= new CourseView();

        //查询课程基本信息
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(id);
        if(courseBaseOptional.isPresent()){
            CourseBase courseBase = courseBaseOptional.get();
            courseView.setCourseBase(courseBase);
        }
        //查询课程图片
        Optional<CoursePic> picOptional = coursePicRepository.findById(id);
        if(picOptional.isPresent()){
            CoursePic coursePic = picOptional.get();
            courseView.setCoursePic(coursePic);
        }

        //课程营销信息
        Optional<CourseMarket> marketOptional = courseMarketRepository.findById(id);
        if(marketOptional.isPresent()){
            CourseMarket courseMarket = marketOptional.get();
            courseView.setCourseMarket(courseMarket);
        }

        //课程计划信息
        TeachplanNode teachplanNode = teachplanMapper.findTeachplanNodeById(id);
        courseView.setTeachplanNode(teachplanNode);

        return courseView;



    }

    /**
     * 预览课程详情页面
     * @param courseId
     * @return
     */
    public CoursePublishResult coursePreview(String courseId) {
        // 根据配置信息 创建出一个页面
        CmsPage cmsPage = createCmsPage(courseId);

        // 远程调用cms服务保存页面
        CmsPageResult cmsPageResult = cmsPageClient.saveCmsPage(cmsPage);
        if(!cmsPageResult.isSuccess()) {
            ResponseResult.FAIL();
        }
        CmsPage cmsPage1 = cmsPageResult.getCmsPage();

        if(cmsPage1 == null) {
            ExceptionCast.exception(CmsCode.CMS_PAGE_ISNULL);
        }
        String pageId = cmsPage1.getPageId();

        String previewUrl1 = previewUrl + pageId;
        return new CoursePublishResult(CommonCode.SUCCESS, previewUrl1);
    }



    /**
     * 课程发布
     * @param courseId
     * @return
     */
    @Transactional
    public CoursePublishResult publishCourse(String courseId) {
        CmsPage cmsPage = createCmsPage(courseId);

        // 调用页面服务的发布页面接口
        CmsPostPageResult cmsPostPageResult = cmsPageClient.postPageQuick(cmsPage);
        if(!cmsPostPageResult.isSuccess()) {
            return new CoursePublishResult(CommonCode.FAIL, null);
        }
        CourseBase courseBase = this.getCourseBaseById(courseId);
        // 修改发布状态
        courseBase.setStatus("202002");
        courseBaseRepository.save(courseBase);

        //保存课程索引信息
        //先创建一个coursePub对象
        CoursePub coursePub = createCoursePub(courseId);
        //将coursePub对象保存到数据库
        saveCoursePub(courseId, coursePub);
        return new CoursePublishResult(CommonCode.SUCCESS, cmsPostPageResult.getPageUrl());

    }

    /**
     * 根据配置信息创建出CmsPage
     * @param courseId
     * @return
     */
    private CmsPage createCmsPage(String courseId) {
        CourseBase courseBase = this.getCourseBaseById(courseId);
        if(courseBase == null) {
            // 课程不存在
            ExceptionCast.exception(CourseCode.COURSE_ISNULL);
        }
        //准备cmsPage信息
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);//站点id
        cmsPage.setDataUrl(publish_dataUrlPre+courseId);//数据模型url
        cmsPage.setPageName(courseId+".html");//页面名称
        cmsPage.setPageAliase(courseBase.getName());//页面别名，就是课程名称
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);//页面物理路径
        cmsPage.setPageWebPath(publish_page_webpath);//页面webpath
        cmsPage.setTemplateId(publish_templateId);//页面模板id
        return cmsPage;
    }
    //将coursePub对象保存到数据库
    private CoursePub saveCoursePub(String id,CoursePub coursePub){

        CoursePub coursePubNew = null;
        //根据课程id查询coursePub
        Optional<CoursePub> coursePubOptional = coursePubRepository.findById(id);
        if(coursePubOptional.isPresent()){
            coursePubNew = coursePubOptional.get();
        }else{
            coursePubNew = new CoursePub();
        }

        //将coursePub对象中的信息保存到coursePubNew中
        BeanUtils.copyProperties(coursePub,coursePubNew);
        coursePubNew.setId(id);
        //时间戳,给logstach使用
        coursePubNew.setTimestamp(new Date());
        //发布时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        coursePubNew.setPubTime(date);
        coursePubRepository.save(coursePubNew);
        return coursePubNew;
    }
    //创建coursePub对象
    private CoursePub createCoursePub(String id){
        CoursePub coursePub = new CoursePub();
        //根据课程id查询course_base
        Optional<CourseBase> baseOptional = courseBaseRepository.findById(id);
        if(baseOptional.isPresent()){
            CourseBase courseBase = baseOptional.get();
            //将courseBase属性拷贝到CoursePub中
            BeanUtils.copyProperties(courseBase,coursePub);
        }

        //查询课程图片
        Optional<CoursePic> picOptional = coursePicRepository.findById(id);
        if(picOptional.isPresent()){
            CoursePic coursePic = picOptional.get();
            BeanUtils.copyProperties(coursePic, coursePub);
        }

        //课程营销信息
        Optional<CourseMarket> marketOptional = courseMarketRepository.findById(id);
        if(marketOptional.isPresent()){
            CourseMarket courseMarket = marketOptional.get();
            BeanUtils.copyProperties(courseMarket, coursePub);
        }

        //课程计划信息
        TeachplanNode teachplanNode = teachplanMapper.findTeachplanNodeById(id);
        String jsonString = JSON.toJSONString(teachplanNode);
        //将课程计划信息json串保存到 course_pub中
        coursePub.setTeachplan(jsonString);
        return coursePub;

    }
}
