package io.github.kingschan1204.istock.module.spider.timerjob.impl;

import io.github.kingschan1204.istock.module.spider.crawl.info.InfoCrawlJob;
import io.github.kingschan1204.istock.module.spider.timerjob.ITimerJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 代码info信息更新命令封装
 * @author chenguoxiang
 * @create 2019-04-01 16:21
 **/
@Slf4j
public class InfoTimerJobImpl implements ITimerJob{

    private InfoCrawlJob infoCrawlJob;

    @Override
    public void execute(COMMAND command) throws Exception {
        switch (command){
            case START:
                if (null == infoCrawlJob) {
                    log.info("开启info更新线程!");
                    infoCrawlJob = new InfoCrawlJob();
                    new Thread(infoCrawlJob, "InfoCrawlJob").start();
                }
                break;
            case STOP:
                if (null != infoCrawlJob) {
                    log.info("关闭thsinfo更新线程!");
                    infoCrawlJob.stopTask();
                    infoCrawlJob = null;
                }
                break;
        }
    }
}
