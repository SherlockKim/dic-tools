package com.tydic.utils;

import com.alibaba.fastjson.annotation.JSONField;

public class KuaiDaiLiIpbalance {

    /**
     * msg
     */
    @JSONField(name = "msg")
    private String msg;
    /**
     * code
     */
    @JSONField(name = "code")
    private Integer code;
    /**
     * data
     */
    @JSONField(name = "data")
    private DataDTO data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public static class DataDTO {
        /**
         * balance 剩余IP数量
         */
        @JSONField(name = "balance")
        private Integer balance;

        public Integer getBalance() {
            return balance;
        }

        public void setBalance(Integer balance) {
            this.balance = balance;
        }
    }
}
