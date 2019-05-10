package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.ucenter.dao.UserMenuMapper;
import com.xuecheng.ucenter.dao.XcCompanyUserRepository;
import com.xuecheng.ucenter.dao.XcUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 周大侠
 * 2019-04-13 13:40
 */
@Service
public class UcenterService {
    @Autowired
    private XcUserRepository xcUserRepository;
    @Autowired
    private XcCompanyUserRepository companyUserRepository;

    @Autowired
    private UserMenuMapper userMenuMapper;
    /**
     * 根据用户名查询用户信息
     * @param username
     * @return
     */
    public XcUserExt findByUsername(String username) {
        // 查询用户基本信息
        XcUser xcUser = xcUserRepository.findByUsername(username);
        if(xcUser == null ) {
            // 账户不存在
            ExceptionCast.exception(AuthCode.AUTH_ACCOUNT_NOTEXISTS);
        }
        XcUserExt xcUserExt = new XcUserExt();
        // 查询用户公司信息
        XcCompanyUser companyUser = companyUserRepository.findByUserId(xcUser.getId());
        if(companyUser != null) {
            xcUserExt.setCompanyId(companyUser.getCompanyId());
        }
        BeanUtils.copyProperties(xcUser, xcUserExt);

        // 查询用户权限信息
        List<XcMenu> permissions = userMenuMapper.findXcMenuByUserId(xcUserExt.getId());
        xcUserExt.setPermissions(permissions);

        return xcUserExt;
    }
}
