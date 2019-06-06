package xll.baitaner.service.utils;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xll.baitaner.service.controller.ActivityController;
import xll.baitaner.service.mapper.ActivityMapper;

import java.util.Date;

/**
 * @author zhulu
 * @version 1.0
 * @description xll.baitaner.service.utils 定时任务类
 * @date 2019/5/24
 */
@Component //service
@Configuration   //扫描配置
@EnableScheduling //定时开启


public class ScheduleConfig {
    @Autowired
    private ActivityMapper activityMapper;

    @Scheduled(cron = "0 0 * * * ?" )
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    public  boolean changeRecordstatus (){
        //生成下单时间
      Date currenttime =  new Date(System.currentTimeMillis());
System.out.println("每秒钟执行一次==="+currenttime);
        return activityMapper.CheckActivityEndtime(0,currenttime) >0;


    }

}
