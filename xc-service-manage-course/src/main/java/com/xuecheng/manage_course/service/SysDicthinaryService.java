package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_course.dao.SysDicthinaryRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 周大侠
 * 2019-04-06 13:48
 */
@Service
public class SysDicthinaryService {
    @Autowired
    private SysDicthinaryRespository dicthinaryRespository;
    public SysDictionary findDicthinaryByType(String type) {
        return dicthinaryRespository.findByDType(type);

    }
}
