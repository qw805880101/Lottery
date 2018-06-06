package com.tc.lottery.bean;

import java.util.List;

public class OrderInfo {

    /**
     * 支付二维码
     */
    private String qrCode;
    /**
     * 订单编号
     */
    private String merOrderId;
    /**
     * 订单时间
     */
    private String merOrderTime;
    /**
     * 订单总金额
     */
    private String orderAmt;
    /**
     * 订单描述
     */
    private String orderDesc;
    /**
     * 订单状态
     * 0 未处理
     * 1 成功
     * 2 失败
     * 3 处理中
     */
    private String orderStatus;
    /**
     * 支付类型
     * 01 支付宝
     * 02 微信
     */
    private String payType;
    /**
     * 票箱列表名
     */
    private List<TerminalLotteryInfo> terminalLotteryDtos;
    /**
     * 通知地址
     */
    private String notifyUrl;

    private String respCode;
    private String respDesc;

    public String getMerOrderId() {
        return merOrderId;
    }

    public void setMerOrderId(String merOrderId) {
        this.merOrderId = merOrderId;
    }

    public String getMerOrderTime() {
        return merOrderTime;
    }

    public void setMerOrderTime(String merOrderTime) {
        this.merOrderTime = merOrderTime;
    }

    public String getOrderAmt() {
        return orderAmt;
    }

    public void setOrderAmt(String orderAmt) {
        this.orderAmt = orderAmt;
    }

    public String getOrderDesc() {
        return orderDesc;
    }

    public void setOrderDesc(String orderDesc) {
        this.orderDesc = orderDesc;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public List<TerminalLotteryInfo> getTerminalLotteryDtos() {
        return terminalLotteryDtos;
    }

    public void setTerminalLotteryDtos(List<TerminalLotteryInfo> terminalLotteryDtos) {
        this.terminalLotteryDtos = terminalLotteryDtos;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
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
