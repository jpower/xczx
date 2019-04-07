package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.manage_course.dao.CourseCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 周大侠
 * 2019-04-06 12:22
 */
@Service
public class CourseCategoryService {
    @Autowired
    private CourseCategoryMapper categoryMapper;

    public CategoryNode findCategoryList() {
        return categoryMapper.findCategoryList();
    }



}
