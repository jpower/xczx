package com.xuecheng.manage_course.web.controller;

import com.xuecheng.api.sys.SysDicthinaryControllerApi;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_course.service.SysDicthinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by 周大侠
 * 2019-04-06 13:45
 */
@RestController
@RequestMapping("/sys/dictionary")
public class SysDicthinaryController implements SysDicthinaryControllerApi {
    @Autowired
    private SysDicthinaryService dicthinaryService;

    @Override
    @GetMapping("/get/{type}")
    public SysDictionary findDicthinaryByType(@PathVariable("type") String type) {
        return dicthinaryService.findDicthinaryByType(type);
    }
}
