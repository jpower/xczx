package com.xuecheng.api.sys;

import com.xuecheng.framework.domain.system.SysDictionary;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Created by 周大侠
 * 2019-04-06 13:43
 */
@Api(value="数据字典接口",description = "数据字典接口，提供数据字典的管理")
public interface SysDicthinaryControllerApi {
    @ApiOperation("根据type查询对应的字典")
    SysDictionary findDicthinaryByType(String type);
}
