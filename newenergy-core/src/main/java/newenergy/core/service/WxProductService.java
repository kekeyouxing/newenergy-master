package newenergy.core.service;


import newenergy.core.config.WxProductConfig;

public class WxProductService {
    protected WxProductConfig wxProductConfig;

    public void setConfig(WxProductConfig config) {
        this.wxProductConfig = config;
    }
    public WxProductConfig getWxProductConfig(){
        return this.wxProductConfig;
    }
}
