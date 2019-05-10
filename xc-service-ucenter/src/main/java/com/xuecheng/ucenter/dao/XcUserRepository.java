package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by 周大侠
 * 2019-04-13 11:41
 */
public interface XcUserRepository extends JpaRepository<XcUser, String> {
    XcUser findByUsername(String username);
}
