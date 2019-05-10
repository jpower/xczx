package com.xuecheng.order.service;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.order.dao.XcTaskHisRepository;
import com.xuecheng.order.dao.XcTaskRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by 周大侠
 * 2019-04-15 11:43
 */
@Service
public class TaskService {
    @Autowired
    private XcTaskRepository xcTaskRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private XcTaskHisRepository xcTaskHisRepository;

    /**
     * 查询指定时间之前 指定数量的信息
     *
     * @param date
     * @param size
     * @return
     */
    public List<XcTask> findTastList(Date date, int size) {

        PageRequest pageRequest = PageRequest.of(0, size);
        Page<XcTask> page = xcTaskRepository.findByUpdateTimeBefore(date, pageRequest);
        return page.getContent();

    }

    /**
     * 修改未完成任务的修改时间
     *
     * @param date
     * @param id
     */
    @Transactional
    public void editTaskUpdateTime(Date date, String id) {
        xcTaskRepository.editTaskUpdateTime(date, id);
    }


    /**
     * 往mq中发送信息
     *
     * @param exchange
     * @param routingkey
     * @param xcTask
     */
    @Transactional
    public void convertAndSend(String exchange, String routingkey, XcTask xcTask) {
        Optional<XcTask> taskOptional = xcTaskRepository.findById(xcTask.getId());
        if (taskOptional.isPresent()) {
            // 修改该信息中的修改时间为当前时间 避免再次取到
            this.editTaskUpdateTime(new Date(), xcTask.getId());

            // 发送
            rabbitTemplate.convertAndSend(exchange, routingkey, taskOptional.get());


        }

    }

    @Transactional
    public int editTaskVersion(XcTask xcTask) {
        return xcTaskRepository.editTaskVersion(xcTask.getVersion(), xcTask.getId());
    }

    @Transactional
    public void finishTask(String id) {
        Optional<XcTask> xcTaskOptional = xcTaskRepository.findById(id);
        if(xcTaskOptional.isPresent()) {
            XcTaskHis xcTaskHis = new XcTaskHis();

            BeanUtils.copyProperties(xcTaskOptional.get(), xcTaskHis);
            xcTaskHis.setDeleteTime(new Date());
            xcTaskHisRepository.save(xcTaskHis);
            xcTaskRepository.deleteById(id);
        }

    }
}
