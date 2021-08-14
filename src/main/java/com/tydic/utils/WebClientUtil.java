package com.tydic.utils;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebClientUtil {
    public static WebClient proxyWebClient() {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        // 启动JS
        webClient.getOptions().setJavaScriptEnabled(true);
        //忽略ssl认证
        webClient.getOptions().setUseInsecureSSL(true);
        //禁用Css，可避免自动二次请求CSS进行渲染
        webClient.getOptions().setCssEnabled(false);
        //运行错误时，不抛出异常
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        // 设置Ajax异步
        webClient.getOptions().setJavaScriptEnabled(false);
        //webClient.setAjaxController(new NicelyResynchronizingAjaxController());

        //获取ip
        try {
            log.info("获取新的ip>>>>>>");
            KuaiDaiLiUtils.getIp();
            log.info("获取新的ip:{}",KuaiDaiLiUtils.ip);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (KuaiDaiLiUtils.ip.equals("")) {
            return null;
        }
        //代理设置
        webClient.getOptions().getProxyConfig().setProxyHost(KuaiDaiLiUtils.ip.split(":")[0]);
        webClient.getOptions().getProxyConfig().setProxyPort(Integer.parseInt(KuaiDaiLiUtils.ip.split(":")[1]));
        //认证设置
        DefaultCredentialsProvider cred = new DefaultCredentialsProvider();
        cred.addCredentials("zhiliang666", "pn688xgm");
        webClient.setCredentialsProvider(cred);
        webClient.getOptions().setTimeout(30000);
        return webClient;
    }

    public static WebClient webClient() {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        // 启动JS
        webClient.getOptions().setJavaScriptEnabled(true);
        //忽略ssl认证
        webClient.getOptions().setUseInsecureSSL(true);
        //禁用Css，可避免自动二次请求CSS进行渲染
        webClient.getOptions().setCssEnabled(false);
        //运行错误时，不抛出异常
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        // 设置Ajax异步
        webClient.getOptions().setJavaScriptEnabled(false);
        //webClient.setAjaxController(new NicelyResynchronizingAjaxController());

        webClient.getOptions().setTimeout(30000);
        return webClient;
    }

    public static HtmlPage getPageProxy(WebClient webClient, String searchUrl) throws Exception {
        int cycleCnt=0;
        HtmlPage page = null;
        try {
            cycleCnt++;
            page = webClient.getPage(searchUrl);
        } catch (Exception e) {
            Thread.sleep(1000);
            if (cycleCnt < 5) {
                if (!KuaiDaiLiUtils.checkIp()) {
                    webClient = proxyWebClient();
                }
                getPageProxy(webClient,searchUrl);
            }
        }
        return page;
    }

    public static HtmlPage getPage(WebClient webClient, String searchUrl) throws Exception {
        int cycleCnt=0;
        HtmlPage page = null;
        try {
            cycleCnt++;
            page = webClient.getPage(searchUrl);
        } catch (Exception e) {
            Thread.sleep(1000);
            if (cycleCnt < 5) {
                webClient = webClient();
                getPage(webClient,searchUrl);
            }
        }
        return page;
    }
}
