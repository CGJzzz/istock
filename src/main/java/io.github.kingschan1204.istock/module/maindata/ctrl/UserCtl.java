package io.github.kingschan1204.istock.module.maindata.ctrl;


import com.alibaba.fastjson.JSONObject;
import io.github.kingschan1204.istock.module.maindata.po.User;
import io.github.kingschan1204.istock.module.maindata.services.UserService;
import netscape.javascript.JSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;
import sun.awt.geom.AreaOp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class UserCtl {
    @Autowired
    private UserService userService;

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

    @RequestMapping("buy")
    public String buy(){
        return "user/buy";
    }

    @RequestMapping("sell")
    public String sell(){
        return "user/sell";
    }
}
