package io.github.kingschan1204.istock.module.maindata.ctrl;

import com.alibaba.fastjson.JSONObject;
import io.github.kingschan1204.istock.module.maindata.po.ShareHolding;
import io.github.kingschan1204.istock.module.maindata.services.ShareHoldingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ShareHoldingCtl {
    @Autowired
    private ShareHoldingService shareHoldingService;

    @RequestMapping("/shareholding/add")
    @ResponseBody
    public JSONObject addRecord(String account,
                                String code,
                                String nameOfShare,
                                Long number,
                                Double priceFirst,
                                @Autowired ShareHolding shareHolding){
        shareHolding.setAccount(account);
        shareHolding.setCode(code);
        shareHolding.setNameOfShare(nameOfShare);
        shareHolding.setNumber(number);
        shareHolding.setPriceFirst(priceFirst);
        shareHoldingService.addShareHolding(shareHolding);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("code",200);
        return jsonObject;
    }

    public JSONObject returnUserHolding(String account){
        List<ShareHolding> list=shareHoldingService.search(account);
        JSONObject jsonObject=new JSONObject();
        if(list!=null){
            jsonObject.put("code",200);
            jsonObject.put("infoList",list);
        }else {
            jsonObject.put("code",409);
            jsonObject.put("infoList",null);
        }
        return jsonObject;
    }
}
