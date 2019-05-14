package io.github.kingschan1204.istock.module.maindata.ctrl;


import com.alibaba.fastjson.JSONObject;
import io.github.kingschan1204.istock.module.maindata.po.Authority;
import io.github.kingschan1204.istock.module.maindata.po.User;
import io.github.kingschan1204.istock.module.maindata.services.AuthorityService;
import io.github.kingschan1204.istock.module.maindata.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Controller
public class UserCtl {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthorityService authorityService;

    @RequestMapping("/userLogin")
    public String login(){
        return "user/login";
    }

    @RequestMapping(value = "/checkUser",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject checkLogin(@RequestParam String accountName,
                                 @RequestParam String password,
                                 HttpServletRequest request,
                                 HttpServletResponse response){

        //设置状态码部分,以下为测试部分
        JSONObject jsonObject=new JSONObject();
        System.out.println(accountName);
//        response.setStatus(200);//设置response headers http状态码为200
//        return new ResponseEntity(HttpStatus.CONFLICT);
//        jsonObject.put("code",200);//设置login页面状态码,自定义 --配合ajax

        //核心代码
        HttpSession session=request.getSession();
        User user = userService.queryUserByLogin(accountName,password);
        if(user!=null){
            session.setAttribute("account",accountName);
            session.setAttribute("balance",user.getBalance());
            jsonObject.put("code",200);//设置login页面状态码,自定义
        }else {
            //401 Unauthorized - [*]：表示用户没有权限（令牌、用户名、密码错误）。
            session.setAttribute("account",null);
            session.setAttribute("balance",0d);
            jsonObject.put("code",401);
        }
        //设置用户信息查询部分
        return jsonObject;
    }

    @RequestMapping("testUser")
    @ResponseBody
    public JSONObject estUser(@RequestParam String accountName,
                           @RequestParam String password){
        System.out.println(accountName+password);
        userService.addUser(accountName,password);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("test","sadsadsa2");
        return jsonObject;
    }

    @RequestMapping("authorize")
    @ResponseBody
    public String AskAuthorize(@RequestParam(required = true) String account,
                               @RequestParam(required = true) String dateTime,
                               @RequestParam(required = true) String code,
                               @RequestParam(required = true) Long num,
                               @RequestParam(required = true) Double priceOrder,
                               @RequestParam(required = true) String behavior,
                               @Autowired Authority authority){
        authority.setAccount(account);
        authority.setCode(code);
        authority.setDate(dateTime);
        authority.setNumberOfShare(num);
        authority.setPriceOrder(priceOrder);
        authority.setBehavior(behavior);
        authorityService.addAuthority(authority);
        return "受理";
    }

    @RequestMapping("sell")
    public String sell(@Autowired Authority authority){
        authority.setCode("000002");
        authorityService.addAuthority(authority);
        authorityService.add();
        return "user/sell";
    }

}
