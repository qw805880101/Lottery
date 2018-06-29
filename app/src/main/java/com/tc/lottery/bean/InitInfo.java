package com.tc.lottery.bean;

import java.util.List;

/**
 * Created by tianchao on 2018/4/17.
 */

public class InitInfo {

    private String version;
    /**
     * 0 待激活
     * 1 已激活
     * 2 待维修
     * 3 已暂停
     * 4 设备无票
     */
    private String terminalStatus;
    /**
     * 广告地址1
     */
    private String img1;
    /**
     * 广告地址2
     */
    private String img2;
    /**
     * 广告地址3
     */
    private String img3;

    private List<String> imgs;

    private List<TerminalLotteryInfo> terminalLotteryDtos;
    /**
     * 更新状态
     * 00 不更新
     * 01 强制更新
     * 02 非强制更新
     */
    private String updateStatus;
    /**
     * 更新地址
     */
    private String updateAddress;
    /**
     * 附加信息
     */
    private String msgExt;
    /**
     * 自定义保留域
     */
    private String misc;

    private String respCode;
    private String respDesc;

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTerminalStatus() {
        return terminalStatus;
    }

    public void setTerminalStatus(String terminalStatus) {
        this.terminalStatus = terminalStatus;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getImg3() {
        return img3;
    }

    public void setImg3(String img3) {
        this.img3 = img3;
    }

    public List<TerminalLotteryInfo> getTerminalLotteryDtos() {
        return terminalLotteryDtos;
    }

    public void setTerminalLotteryDtos(List<TerminalLotteryInfo> terminalLotteryDtos) {
        this.terminalLotteryDtos = terminalLotteryDtos;
    }

    public String getUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(String updateStatus) {
        this.updateStatus = updateStatus;
    }

    public String getUpdateAddress() {
        return updateAddress;
    }

    public void setUpdateAddress(String updateAddress) {
        this.updateAddress = updateAddress;
    }

    public String getMsgExt() {
        return msgExt;
    }

    public void setMsgExt(String msgExt) {
        this.msgExt = msgExt;
    }

    public String getMisc() {
        return misc;
    }

    public void setMisc(String misc) {
        this.misc = misc;
    }
}
