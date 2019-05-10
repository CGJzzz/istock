package io.github.kingschan1204.istock.module.maindata.ctrl;


import com.alibaba.fastjson.JSONObject;
import io.github.kingschan1204.istock.module.maindata.services.UserService;
import netscape.javascript.JSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Controller
public class UserCtl {
    @Autowired
    private UserService userService;

    @RequestMapping("/temp")
    public String login(){
        return "login";
    }

    @RequestMapping(value = "/checkUser",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject checkLogin(@RequestParam String accountName,
                                 @RequestParam String password,
                                 HttpServletResponse response){

        //设置状态码部分
        JSONObject jsonObject=new JSONObject();
        System.out.println(accountName+password);
        response.setStatus(200);//设置response headers http状态码为200
//        return new ResponseEntity(HttpStatus.CONFLICT);
        jsonObject.put("code",200);//设置login页面状态码,自定义

        //设置用户信息查询部分
        return jsonObject;
    }
}
