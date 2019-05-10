package com.xuecheng.learning.service;

import com.xuecheng.framework.domain.learning.XcLearningCourse;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.learning.dao.XcLearningCourseRepository;
import com.xuecheng.learning.dao.XcTaskHisRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

/**
 * @author Administrator
 * @version 1.0
 **/
@Service
public class LearningService {

    @Autowired
    XcLearningCourseRepository xcLearningCourseRepository;

    @Autowired
    XcTaskHisRepository xcTaskHisRepository;



    @Transactional
    public ResponseResult addchoosecourse(String userId, String courseId, XcTask xcTask) {
        XcLearningCourse learningCourse = xcLearningCourseRepository.findByUserIdAndCourseId(userId, courseId);
        if(learningCourse != null) {
            //更新选课记录
            //课程的开始时间
            learningCourse.setStartTime(null);
            learningCourse.setEndTime(null);
            learningCourse.setStatus("501001");
            xcLearningCourseRepository.save(learningCourse);

        }else {
            XcLearningCourse learningCourse1 = new XcLearningCourse();
            learningCourse1.setUserId(userId);
            learningCourse1.setCourseId(courseId);
            learningCourse1.setStartTime(null);
            learningCourse1.setEndTime(null);
            learningCourse1.setStatus("501001");
            xcLearningCourseRepository.save(learningCourse1);
        }
        Optional<XcTaskHis> taskHisOptional = xcTaskHisRepository.findById(xcTask.getId());
        // 保存历史选课信息
        if(!taskHisOptional.isPresent()) {
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask, xcTaskHis);
            xcTaskHis.setCreateTime(new Date());
            xcTaskHisRepository.save(xcTaskHis);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

}
