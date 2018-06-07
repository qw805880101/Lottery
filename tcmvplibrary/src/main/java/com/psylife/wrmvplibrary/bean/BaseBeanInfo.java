package com.psylife.wrmvplibrary.bean;

import java.util.Map;

public class BaseBeanInfo {

    private Map responseData;
    private String sign;
    private String respCode;
    private String respDesc;

    public Map getResponseData() {
        return responseData;
    }

    public void setResponseData(Map responseData) {
        this.responseData = responseData;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespDesc() {
        return respDesc;
    }

    public void setRespDesc(String respDesc) {
        this.respDesc = respDesc;
    }

}
