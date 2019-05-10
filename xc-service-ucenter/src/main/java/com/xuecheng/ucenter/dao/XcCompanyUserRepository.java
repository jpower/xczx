package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by 周大侠
 * 2019-04-13 13:43
 */
public interface XcCompanyUserRepository extends JpaRepository<XcCompanyUser, String> {
    XcCompanyUser findByUserId(String userId);
}
