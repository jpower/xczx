package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.system.SysDictionary;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by 周大侠
 * 2019-04-06 13:49
 */
public interface SysDicthinaryRespository extends MongoRepository<SysDictionary, String>{
    SysDictionary findByDType(String type);
}
