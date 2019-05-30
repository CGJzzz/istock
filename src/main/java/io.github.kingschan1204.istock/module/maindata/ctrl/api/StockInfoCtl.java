package io.github.kingschan1204.istock.module.maindata.ctrl.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.github.kingschan1204.istock.module.maindata.po.*;
import io.github.kingschan1204.istock.module.maindata.services.AuthorityService;
import io.github.kingschan1204.istock.module.maindata.services.ShareHoldingService;
import io.github.kingschan1204.istock.module.maindata.services.StockInfoService;
import io.github.kingschan1204.istock.module.maindata.services.UserService;
import io.github.kingschan1204.istock.module.maindata.vo.StockVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import java.util.List;

/**
 * @author chenguoxiang
 * @create 2018-11-02 15:49
 **/
@Api(description = "股票信息类")
@Controller
public class StockInfoCtl {
    private Logger log = LoggerFactory.getLogger(StockInfoCtl.class);
    @Autowired
    private StockInfoService stockInfoService;
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private UserService userService;
    @Autowired
    private ShareHoldingService shareHoldingService;
    @Autowired
    private AuthorityInQuery inQuery;
    @Autowired
    private AuthorityOutQuery outQuery;

    @ApiOperation(value = "股票信息", notes = "根据传入股票代码参数返回列表信息")
    @GetMapping("/stock/info/{code}")
    public String stockInfo(@PathVariable String code, Model model,
                            HttpServletRequest request) {
        JSONObject json = stockInfoService.getStockInfo(code);
        String account = (String) request.getSession().getAttribute("account");
        List<ShareHolding> list= shareHoldingService.search(account);
        StockVo stockVo = JSON.toJavaObject(json, StockVo.class);
        model.addAttribute("pagetitle", String.format("%s-%s", code, stockVo.getName()));
        model.addAttribute("data", json.toJSONString());
        model.addAttribute("stock", JSON.toJSONString(stockVo));
        model.addAttribute("stockprice", stockVo.getPrice());
        model.addAttribute("infoList", list);

        //统一时间
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        String orderDate = df.format(localDateTime);
        HttpSession session = request.getSession();
        session.setAttribute("orderDate", orderDate);
        return "/stock/info/stock_info";
    }

    //买入委托函数
    //逻辑 用户下单-扣钱成功-receive用户委托,quartz实现差额补全
    @ApiOperation(value = "委托买入", notes = "未实行前后端分离,需要用户先登录后才能使用委托买入卖出功能")
    @PostMapping(value = "/stock/authorize/in/{code}")
    @ResponseBody
    public JSONObject receiveInAnthority(@PathVariable String code,
                                         @RequestParam(required = true) Long num,
                                         @RequestParam(required = true) Double priceOrder,
                                         @Autowired Authority authority,
                                         HttpServletRequest request) {
        //例:http://localhost/stock/authorize/000001?num=100&priceOrder=10.05&behavior=in
        HttpSession session = request.getSession();
        String account = (String) session.getAttribute("account");
        String orderDate = (String) session.getAttribute("orderDate");
        JSONObject jsonObject = new JSONObject();
        User user = userService.queryUserAfterLogin(account);
        String behavior = "in";
        if (account != null) {
            if (user != null) {
                synchronized (StockInfoCtl.class) {
                    //不使用自动装箱
                    Double total = Double.valueOf(priceOrder * num);
                    if (Double.compare(user.getBalance(), total) >= 0) {
                        authority.setAccount(account);
                        authority.setCode(code);
                        authority.setDate(orderDate);
                        authority.setNumberOfShare(num);
                        authority.setPriceOrder(priceOrder);
                        authority.setBehavior(behavior);
                        authority.setStatus("receive");
                        authority.setPriceFinal(0.0d);

                        //委托订单号
                        String authorityOrderSerial = orderDate + account + code;
                        authority.setAuthorityOrderSerial(authorityOrderSerial);
                        inQuery.addAuthority(authority);
                        authorityService.addAuthority(authority);
                        jsonObject.put("code", 200);//设置login页面状态码,自定义
                        jsonObject.put("authorityOrderSerial", authorityOrderSerial);
                        user.setBalance(user.getBalance() - total);
                        session.setAttribute("balance", user.getBalance());
                        userService.saveUser(user);
                        return jsonObject;

                    } else {
                        jsonObject.put("code", 409);
                        jsonObject.put("tips", "余额不足");
                        return jsonObject;
                    }
                }
            } else {
                jsonObject.put("code", 401);
                jsonObject.put("tips", "账号不存在");
                return jsonObject;
            }
        } else {
            jsonObject.put("code", 401);
            jsonObject.put("tips", "请登录");
            return jsonObject;
        }

    }

    @ApiOperation(value = "委托卖出", notes = "未实行前后端分离,需要用户先登录后才能使用委托买入卖出功能")
    @PostMapping(value = "/stock/authorize/out/{code}")
    @ResponseBody
    public JSONObject receiveOutAuthority(@PathVariable String code,
                                          @RequestParam(required = true) Long num,
                                          @RequestParam(required = true) Double priceOrder,
                                          @Autowired Authority authority,
                                          HttpServletRequest request) {
        HttpSession session = request.getSession();
        String account = (String) session.getAttribute("account");
        System.out.println(account+"***");
        String orderDate = (String) session.getAttribute("orderDate");
        JSONObject jsonObject = new JSONObject();
        User user = userService.queryUserAfterLogin(account);
        String behavior = "out";
        if (account != null) {
            if (user != null) {
                synchronized (StockInfoCtl.class) {
                    //查找用户名下所有持仓
                    List<ShareHolding> list = shareHoldingService.searchSpecificCode(account, code);
                    System.out.println(list);
                    if (list.size() > 0) {
                        //当用户此有该股数量>卖出的数量才可以委托
                        //此函数可以正确显示目前此有股票数量,但需要优化

                        Long total = 0L;

                        total = list.get(0).getNumber();

                        //System.out.println(total);
                        //System.out.println("当前持仓："+total);
                        // System.out.println("卖出数目:"+num);
                        //根据委托 减去委托卖出的数量->得到的当前数量,根据这个数才可以继续计算.
                       /* List<Authority> listOfAuthority = authorityService.searchByAccountCodeBehaviro(account, code, "out");
                        for (Authority a : listOfAuthority) {
                            total -= a.getNumberOfShare();
                        }*/
                        if (Long.compare(total, num) >= 0) {
                            authority.setAccount(account);
                            authority.setCode(code);
                            authority.setDate(orderDate);
                            authority.setNumberOfShare(num);
                            authority.setPriceOrder(priceOrder);
                            authority.setBehavior(behavior);
                            authority.setStatus("receive");
                            authority.setPriceFinal(0.0d);

                            ShareHolding shareHoldingIn = list.get(0);
                            shareHoldingIn.setNumber(shareHoldingIn.getNumber() - num);
                            System.out.println(shareHoldingIn + "*******___*");

                            shareHoldingService.save(shareHoldingIn);
                            System.out.println("卖出后余仓" + shareHoldingIn.getNumber());
                            //委托订单号
                            String authorityOrderSerial = orderDate + account + code;
                            authority.setAuthorityOrderSerial(authorityOrderSerial);
                            outQuery.addAuthority(authority);
                            authorityService.addAuthority(authority);
                            jsonObject.put("code", 200);//设置login页面状态码,自定义
                            jsonObject.put("tips", "委托成功");
                            jsonObject.put("authorityOrderSerial", authorityOrderSerial);

                            //数量关系
                            return jsonObject;
                        } else {
                            jsonObject.put("code", 409);
                            jsonObject.put("tips", "用户持有此股的股数小于卖出数额");
                            jsonObject.put("num", total);
                            return jsonObject;
                        }
                    } else {
                        jsonObject.put("code", 409);
                        jsonObject.put("tips", "用户并没有持有此股");
                        return jsonObject;
                    }
                }
            } else {
                jsonObject.put("code", 401);
                jsonObject.put("tips", "账号不存在");
                return jsonObject;
            }
        } else {
            jsonObject.put("code", 401);
            jsonObject.put("tips", "请登录");
            return jsonObject;
        }
    }
}
