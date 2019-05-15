package io.github.kingschan1204.istock.module.maindata.ctrl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.sym.NameN;
import io.github.kingschan1204.istock.module.maindata.po.Authority;
import io.github.kingschan1204.istock.module.maindata.services.AuthorityService;
import io.github.kingschan1204.istock.module.maindata.services.StockInfoService;
import io.github.kingschan1204.istock.module.maindata.vo.StockVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * @author chenguoxiang
 * @create 2018-11-02 15:49
 **/
@Controller
public class StockInfoCtl {
    private Logger log = LoggerFactory.getLogger(StockInfoCtl.class);
    @Autowired
    private StockInfoService stockInfoService;
    @Autowired
    private AuthorityService authorityService;

    @RequestMapping("/stock/info/{code}")
    public String stockInfo(@PathVariable String code, Model model,
                            HttpServletRequest request) {
        JSONObject json= stockInfoService.getStockInfo(code);
        StockVo stockVo = JSON.toJavaObject(json,StockVo.class);
        model.addAttribute("pagetitle",String.format("%s-%s",code,stockVo.getName()));
        model.addAttribute("data",json.toJSONString());
        model.addAttribute("stock", JSON.toJSONString(stockVo));
        model.addAttribute("stockprice",stockVo.getPrice());

        //统一时间
        String orderDate= DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());
        HttpSession session = request.getSession();
        session.setAttribute("orderDate",orderDate);
        return "/stock/info/stock_info";
    }

    //委托函数
    @RequestMapping(value="/stock/authorize/{code}")
    @ResponseBody
    public JSONObject receiveAnthority(@PathVariable String code,
                                   @RequestParam(required = true) Long num,
                                   @RequestParam(required = true) Double priceOrder,
                                   @RequestParam(required = true) String behavior,
                                   @Autowired Authority authority,
                                       HttpServletRequest request){
        //例:http://localhost/stock/authorize/000001?num=100&priceOrder=10.05&behavior=in
        HttpSession session=request.getSession();
        String account= (String) session.getAttribute("account");
        String orderDate= (String) session.getAttribute("orderDate");
        JSONObject jsonObject= new JSONObject();
        if(account!=null){
            authority.setAccount(account);
            authority.setCode(code);
            authority.setDate(orderDate);
            authority.setNumberOfShare(num);
            authority.setPriceOrder(priceOrder);
            authority.setBehavior(behavior);
            authority.setStatus("receive");
            authority.setPriceFinal(0.0d);
            authority.setPriceFinal(0.0d);

            //委托订单号
            String authorityOrderSerial=orderDate+account+code;
            authority.setAuthorityOrderSerial(authorityOrderSerial);
            authorityService.addAuthority(authority);
            jsonObject.put("code",200);//设置login页面状态码,自定义
            jsonObject.put("authorityOrderSerial",authorityOrderSerial);
        }else {
            jsonObject.put("code",401);
        }
        return jsonObject;
    }
}
