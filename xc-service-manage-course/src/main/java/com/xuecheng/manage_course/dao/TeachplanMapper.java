package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by 周大侠
 * 2019-04-05 15:36
 */
@Mapper
public interface TeachplanMapper {
    TeachplanNode findTeachplanNodeById(String id);
}
