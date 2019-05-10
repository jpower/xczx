package com.xuecheng.order.dao;

import com.xuecheng.framework.domain.task.XcTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

/**
 * Created by 周大侠
 * 2019-04-15 11:40
 */
public interface XcTaskRepository extends JpaRepository<XcTask, String> {
    Page<XcTask> findByUpdateTimeBefore(Date date, Pageable pageable);

    @Modifying
    @Query(value = "update xc_task set update_time = ? where id = ?", nativeQuery = true)
    void editTaskUpdateTime(Date date, String id);

    @Modifying
    @Query(value = "update xc_task set version = ?1 + 1 where version = ?1 and id = ?2", nativeQuery = true)
    int editTaskVersion(int version, String id);
}
