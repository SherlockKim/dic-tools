package com.tydic.utils;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class KuaiDaiLiDps {

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
         * count
         */
        @JSONField(name = "count")
        private Integer count;
        /**
         * proxyList
         */
        @JSONField(name = "proxy_list")
        private List<String> proxyList;
        /**
         * todayLeftCount
         */
        @JSONField(name = "today_left_count")
        private Integer todayLeftCount;
        /**
         * dedupCount
         */
        @JSONField(name = "dedup_count")
        private Integer dedupCount;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public List<String> getProxyList() {
            return proxyList;
        }

        public void setProxyList(List<String> proxyList) {
            this.proxyList = proxyList;
        }

        public Integer getTodayLeftCount() {
            return todayLeftCount;
        }

        public void setTodayLeftCount(Integer todayLeftCount) {
            this.todayLeftCount = todayLeftCount;
        }

        public Integer getDedupCount() {
            return dedupCount;
        }

        public void setDedupCount(Integer dedupCount) {
            this.dedupCount = dedupCount;
        }
    }
}
