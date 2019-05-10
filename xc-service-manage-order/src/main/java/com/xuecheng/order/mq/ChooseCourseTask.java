package com.xuecheng.order.mq;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.config.RabbitMQConfig;
import com.xuecheng.order.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by 周大侠
 * 2019-04-15 9:58
 */
@Component
@Slf4j
public class ChooseCourseTask {


    @Autowired
    private TaskService taskService;


    @RabbitListener(queues = RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE)
    public void finishTask(XcTask xcTask) {
        log.info("接收成功信息");
        taskService.finishTask(xcTask.getId());
        log.info("完成选课删除添加选课信息");
    }

    /**
     * 定时向mq中发送 可以添加的选课信息
     */
    @Scheduled(cron = "0/3 * * * * *")
    public void sendChoosecourseTask() {
        // 取出1分钟之前未处理的信息
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(GregorianCalendar.MINUTE, -1);
        List<XcTask> taskList = taskService.findTastList(calendar.getTime(), 1000);

        // 遍历每个信息 发送到mq中
        taskList.forEach(t -> {
            log.info("当前未处理信息id:{}", t.getId());
            if (taskService.editTaskVersion(t) > 0) {

                String mqExchange = t.getMqExchange();

                // 获取routingkey
                String mqRoutingkey = t.getMqRoutingkey();

                // 往mq发送信息
                taskService.convertAndSend(mqExchange, mqRoutingkey, t);
                log.info("发送添加选课信息");
            }

        });

    }


    /*@Scheduled(cron = "0/3 * * * * *")
    // 上次任务开始后5秒执行 （必须等上次任务执行结束）
//    @Scheduled(fixedRate = 5000)
    // 上次任务执行完后3秒执行
//    @Scheduled(fixedDelay = 3000)
    public void task() throws InterruptedException {
        log.info("任务开始");

        Thread.sleep(500);

        log.info("任务结束");
    }

    @Scheduled(cron = "0/1 * * *  * *")
    // 上次任务开始后5秒执行 （必须等上次任务执行结束）
//    @Scheduled(fixedRate = 5000)
    // 上次任务执行完后3秒执行
//    @Scheduled(fixedDelay = 3000)
    public void task1() throws InterruptedException {
        log.info("任务开始1");

        Thread.sleep(200);

        log.info("任务结束1");
    }*/


}
