package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by 周大侠
 * 2019-04-14 11:05
 */
@Mapper
public interface UserMenuMapper {
    List<XcMenu> findXcMenuByUserId(String id);
}
