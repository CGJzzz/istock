package io.github.kingschan1204.istock.module.maindata.ctrl;

import com.alibaba.fastjson.JSONObject;
import io.github.kingschan1204.istock.module.maindata.services.ShareHoldingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ShareHoldingCtl {
    @Autowired
    private ShareHoldingService shareHoldingService;

    @RequestMapping
    @ResponseBody
    public JSONObject addRecord(){
        JSONObject jsonObject=new JSONObject();
        return jsonObject;
    }
}
