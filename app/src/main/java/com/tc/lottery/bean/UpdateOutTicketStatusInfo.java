package com.tc.lottery.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 出票状态参数
 */
public class UpdateOutTicketStatusInfo implements Serializable {

    private List<TerminalLotteryInfo> terminalLotteryDtos;
    private String merOrderId;

    public List<TerminalLotteryInfo> getTerminalLotteryDtos() {
        return terminalLotteryDtos;
    }

    public void setTerminalLotteryDtos(List<TerminalLotteryInfo> terminalLotteryDtos) {
        this.terminalLotteryDtos = terminalLotteryDtos;
    }

    public String getMerOrderId() {
        return merOrderId;
    }

    public void setMerOrderId(String merOrderId) {
        this.merOrderId = merOrderId;
    }
}
