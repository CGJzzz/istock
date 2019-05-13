package io.github.kingschan1204.istock;

import io.github.kingschan1204.istock.common.startup.InitQuartzTaskRunner;
import io.github.kingschan1204.istock.module.maindata.services.StockService;
import io.github.kingschan1204.istock.module.maindata.services.UserService;
import io.github.kingschan1204.istock.module.spider.schedule.ScheduleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * spring boot 启动类
 *
 * @author kings.chan
 */
@Controller
@EnableCaching
@ServletComponentScan
@SpringBootApplication
public class Application {


    @Autowired
    private StockService stockService;

    @RequestMapping("/")
    public String index(Model model, HttpServletRequest request) {
        List<String> list = stockService.getAllIntruduce();
        HttpSession session=request.getSession();
        String accountName= (String) session.getAttribute("account");
        if(accountName!=null){
            model.addAttribute("accountName",accountName);
        }
        //2019-5-8 12:45:11 测试
        if(list.size()>=1){
            model.addAttribute("industry", list);
        }else {
            list.add("stockService出错");
            model.addAttribute("industry", list);
        }
        return "index";
    }

    @Bean
    public InitQuartzTaskRunner startupRunner() {
        return new InitQuartzTaskRunner();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        Thread thread = new Thread(new ScheduleJob());
        thread.setDaemon(true);
        thread.start();
    }
}