package com.tydic.utils;

import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;

import static java.lang.System.out;

@Slf4j
public class KuaiDaiLiUtils {
    private static final String orderId = "942572654059625";
    private static final String apiKey = "j135fdc7lk3oyvyifggjp2cqkq78sb2j";
    public static Integer idtime = 0;
    public static String ip = "";
    private static int getIpCnt=0;

    // 获取余额
    public static Integer getIpbalance() throws Exception {
        KuaiDaiLiIpbalance kuaiDaiLiIpbalance = null;
        String str = HttpClientUtil.doHttpGet("https://dps.kdlapi.com/api/getipbalance?orderid=" + orderId + "&signature=" + apiKey + "",null);
        kuaiDaiLiIpbalance = JSONObject.parseObject(str, KuaiDaiLiIpbalance.class); //获取API返回内容
        return kuaiDaiLiIpbalance == null ? 0 : kuaiDaiLiIpbalance.getData().getBalance();
    }

    //    获取ip的时常
    static Integer getdpsvalidtime() throws Exception {
        JSONObject jsonObject = null;
        String str = HttpClientUtil.doHttpGet("https://dps.kdlapi.com/api/getdpsvalidtime?orderid=" + orderId + "&proxy=" + ip + "&signature=" + apiKey + "",null);
        jsonObject = JSONObject.parseObject(str);
        //  查询成功
        if (Integer.parseInt(jsonObject.get("code").toString()) == 0) {
            return Integer.parseInt(jsonObject.getJSONObject("data").get(ip).toString());
        } else return null;
    }


    //    获取ip，每次只提取一个IP
    public static String getIp() throws Exception {
        boolean a = true;
        while (a&&getIpCnt<=500) {
            getIpCnt++;
            //        每个IP的开始时间
            try {
                String str = HttpClientUtil.doHttpGet("https://dps.kdlapi.com/api/getdps?orderid=" + orderId + "&signature=" + apiKey + "&num=1&sep=1&format=json&dedup=1",null);
                KuaiDaiLiDps kuaiDaiLiDps = JSONObject.parseObject(str, KuaiDaiLiDps.class); //获取API返回内容
                if (kuaiDaiLiDps.getData() != null) {
//                    获取ip
                    ip = kuaiDaiLiDps.getData().getProxyList().get(0);
                    idtime = getdpsvalidtime();
                    log.info("校验IP:{}",ip);
                    if (checkIp()) {
                        a = false;
                    } else {
                        out.println("重新获取IP");
                        log.info("无效IP:{}，重新获取",ip);
                        ;
                    }
                } else {
//                    获取的ip为空，去查看当天是否还有余额
                    if (getIpbalance() == 0) {
                        log.info("当天可提取ip为0");
                        a = false;
                    }
                }
            } finally {

            }
        }
        return ip;
    }

    // 校验ip
    public static boolean checkIp() throws Exception {
        String str = HttpClientUtil.doProxyHttpGet("https://www.xiachufang.com/search/?keyword=干煸四季豆", null);
        log.info("str:{}",str);
        return null == str ? false : true;
    }
}
