package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.manage_course.service.CourseCategoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sound.midi.Soundbank;
import java.util.List;
import java.util.Optional;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestDao {
    @Autowired
    CourseBaseRepository courseBaseRepository;
    @Autowired
    CourseMapper courseMapper;

    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    CourseCategoryMapper courseCategoryMapper;
    @Test
    public void testCourseBaseRepository(){
        Optional<CourseBase> optional = courseBaseRepository.findById("402885816240d276016240f7e5000002");
        if(optional.isPresent()){
            CourseBase courseBase = optional.get();
            System.out.println(courseBase);
        }

    }

    @Test
    public void testCourseMapper(){
        CourseBase courseBase = courseMapper.findCourseBaseById("402885816240d276016240f7e5000002");
        System.out.println(courseBase);

    }
    @Test
    public void teachplanNode() {
        TeachplanNode teachplanNodeById = teachplanMapper.findTeachplanNodeById("4028e581617f945f01617f9dabc40000");
        List<TeachplanNode> children = teachplanNodeById.getChildren();
//        children.stream().forEach(t-> System.out.println(t.getPname()));
//        System.out.println(teachplanNodeById.getPname());


    }

    @Test
    public void categoryNode() {
        CategoryNode categoryList = courseCategoryMapper.findCategoryList();
        System.out.println(categoryList.getName());
        categoryList.getChildren().stream().forEach(t -> System.out.println(t.getName()));
    }
}
