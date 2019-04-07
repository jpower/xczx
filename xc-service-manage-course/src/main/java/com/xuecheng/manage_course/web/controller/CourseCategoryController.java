package com.xuecheng.manage_course.web.controller;

import com.xuecheng.api.course.CategoryControllerApi;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.manage_course.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by 周大侠
 * 2019-04-06 12:20
 */
@RestController
@RequestMapping("/category")
public class CourseCategoryController implements CategoryControllerApi {
    @Autowired
    private CourseCategoryService categoryService;

    /**
     * 查询课程分类列表
     * @return
     */
    @Override
    @GetMapping("/list")
    public CategoryNode findCategoryList() {


        return categoryService.findCategoryList();
    }
}
