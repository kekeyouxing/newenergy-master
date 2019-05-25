package newenergy.wx.api.controller;

/**
 * Created by HUST Corey on 2019-04-22.
 */

import newenergy.db.util.StringUtilCorey;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 微信服务器Token验证，并非access_token
 */
@RestController
@RequestMapping("wx")
public class TokenController {
    @RequestMapping("validate")
    public String validation(String signature, String timestamp, String nonce, String echostr){
        if(StringUtilCorey.emptyCheck(signature) || StringUtilCorey.emptyCheck(timestamp)
        || StringUtilCorey.emptyCheck(nonce) || StringUtilCorey.emptyCheck(echostr))
            return "";
        List<String> msgs = new ArrayList<>();
        msgs.add(nonce);
        msgs.add(timestamp);
        msgs.add("hgdr_hust");
        Collections.sort(msgs);
        String result = DigestUtils.sha1Hex(msgs.get(0)+msgs.get(1)+msgs.get(2));
        System.out.println("result:"+result);
        System.out.println("signature:"+signature);
        if(result.equals(signature)){
            return echostr;
        }else{
            return "";
        }
    }
}
