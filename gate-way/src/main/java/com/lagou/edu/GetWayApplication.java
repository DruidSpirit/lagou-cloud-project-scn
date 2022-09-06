package com.lagou.edu;

import com.lagou.edu.filter.BlackListFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableDiscoveryClient
public class GetWayApplication implements CommandLineRunner {

    @Value("${ip-visit-setting.visit-time}")
    private Long visitTime;

    public static void main(String[] args) {
        SpringApplication.run(GetWayApplication.class,args);
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("spring容器启动完成，开启定时线程池");
        //  建立定时线程池
        ScheduledExecutorService executorServicePeriod = Executors.newScheduledThreadPool(1);

        //  每隔一段时间定时清除IP临时容器
        executorServicePeriod.scheduleAtFixedRate(() -> {
            if ( BlackListFilter.ipTemContent.size() > 0 ) {
                System.out.println("开始清除临时数据");
                BlackListFilter.ipTemContent.clear();
            }
        }, 0, visitTime, TimeUnit.SECONDS);
    }
}
