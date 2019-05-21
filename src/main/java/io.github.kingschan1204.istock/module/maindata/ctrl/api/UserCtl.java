package io.github.kingschan1204.istock.module.maindata.ctrl.api;


import com.alibaba.fastjson.JSONObject;
import io.github.kingschan1204.istock.module.maindata.po.User;
import io.github.kingschan1204.istock.module.maindata.services.AuthorityService;
import io.github.kingschan1204.istock.module.maindata.services.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Api(description = "用户功能")
@Slf4j
@Controller
public class UserCtl {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthorityService authorityService;

    @ApiOperation(value = "返回用户登录界面")
    @GetMapping("/userLogin")
    public String returnLogin(){
        return "user/login";
    }

    @ApiOperation(value = "用户登录验证", notes = "登陆成功返回200,登录失败返回401")
    @PostMapping(value = "/checkUser")
    @ResponseBody
    public JSONObject checkLogin(@RequestParam String accountName,
                                 @RequestParam String password,
                                 HttpServletRequest request){
//        response.setStatus(200);//设置response headers http状态码为200
//        return new ResponseEntity(HttpStatus.CONFLICT);
//        jsonObject.put("code",200);//设置login页面状态码,自定义 --配合ajax

        JSONObject jsonObject=new JSONObject();
        HttpSession session=request.getSession();
        User user = userService.queryUserByLogin(accountName,password);
        if(user!=null){
            session.setAttribute("account",accountName);
            session.setAttribute("balance",user.getBalance());
            jsonObject.put("code",200);//设置login页面状态码,自定义
            jsonObject.put("tips","登陆成功");
        }else {
            //401 Unauthorized - [*]：表示用户没有权限（令牌、用户名、密码错误）。
            session.setAttribute("account",null);
            session.setAttribute("balance",0d);
            jsonObject.put("code",401);
            jsonObject.put("tips","登陆失败");
        }
        //设置用户信息查询部分
        return jsonObject;
    }
}
