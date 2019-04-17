package newenergy.core.config;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import newenergy.core.service.WxProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WxConfig {
    @Autowired
    private WxProperties properties;

    @Bean
    public WxPayConfig wxPayConfig(){
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(properties.getAppId());
        payConfig.setMchId(properties.getMchId());
        payConfig.setMchKey(properties.getMchKey());
        payConfig.setNotifyUrl(properties.getNotifyUrl());
        payConfig.setKeyPath(properties.getKeyPath());
        payConfig.setTradeType("JSAPI");
        payConfig.setSignType("MD5");
        return payConfig;
    }

    @Bean
    public WxPayService wxPayService(WxPayConfig payConfig){
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(payConfig);
        return wxPayService;
    }
    @Bean
    public WxProductConfig wxProductConfig(){
        WxProductConfig wxProductConfig = new WxProductConfig();
        wxProductConfig.setAppId(properties.getAppId());
        wxProductConfig.setAppSecret(properties.getAppSecret());
        return wxProductConfig;
    }
    @Bean
    public WxProductService wxProductService(WxProductConfig wxProductConfig){
        WxProductService wxProductService = new WxProductService();
        wxProductService.setConfig(wxProductConfig);
        return wxProductService;
    }
}
