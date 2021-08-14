package com.tydic.service.impl;

import cn.hutool.core.io.FileUtil;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.tydic.utils.KuaiDaiLiUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import java.util.List;

@Slf4j
public class XiaChuFangServiceImpl01 {
    // 读取的菜品路径
    private final static String srcFilePath = "D:/baidu/fruit20210731.txt";
    // 输出的图片路径
    private static String fileDir = "D:/tmp1/test062/";

    public static WebClient newWebClient() {
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);
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
        webClient.getOptions().setTimeout(60000);
        return webClient;
    }

    public static void main(String[] args) throws Exception {
        log.info("begin=============");
        WebClient webClient = newWebClient();

        List<String> nameArray = FileUtil.readLines(srcFilePath,"utf-8");
        int cnt = 1;
        for (String name : nameArray) {
            log.info("name:{}",name);
            String searchUrl = "https://www.xiachufang.com/search/?keyword="+name;
            if (!KuaiDaiLiUtils.checkIp()) {
                webClient = newWebClient();
            }
            HtmlPage page1 = null;
            try {
                page1 = webClient.getPage(searchUrl);
            } catch (Exception e) {
                webClient = newWebClient();
                Thread.sleep(1000);
                page1 = webClient.getPage(searchUrl);
            }

            Elements elements = Jsoup.parse(page1.asXml()).select(".search-result-list .normal-recipe-list .list li");
            int hiveCnt=0;
            int picNum=1;
            // 取前三有菜品的数据
            for (int k=0; k<elements.size()&&k<10; k++) {
                if (hiveCnt>2) break;
                String str = elements.get(k).select("li div a").attr("href").toString();
                String recordsStr = elements.get(k).select("li div .info .stats .score").last().html();
                if (StringUtils.isEmpty(recordsStr) || recordsStr.trim().equals("0")) {
                    continue;
                }

                int records = Integer.valueOf(recordsStr);
                int pageSize=18;  int pageNum = (int)Math.ceil((double)(records)/pageSize);
                log.info("str:{} records:{} pageNum:{}",str,records,pageNum);
                try {
                    String url = "https://www.xiachufang.com"+str+"dishes/?page=[]";
                    for (int i = 1; i<=pageNum&&i<=25; i++) {
                        try {
                            if (!KuaiDaiLiUtils.checkIp()) {
                                webClient = newWebClient();
                            }
                            HtmlPage page = null;
                            try {
                                page = webClient.getPage(url.replace("[]", i + ""));
                            } catch (Exception e) {
                                webClient = newWebClient();
                                Thread.sleep(1000);
                                page = webClient.getPage(url.replace("[]", i + ""));
                            }

                            List<String> list1 = Jsoup.parse(page.asXml()).select(".pure-u .dish-280 .cover img").eachAttr("data-src");
                            for (String st : list1) {
                                String picUrlTmp = st.split("\\?")[0];
                                String picName = picNum + "_" +picUrlTmp.substring(picUrlTmp.lastIndexOf("/")+1);

                                log.info("picUrl:{} fileDir:{} name:{}",st,fileDir+cnt+name,picName);
                                //HttpClientUtil.doHttpGet(st,null,fileDir+cnt+name,picName);
                                String filePath = fileDir+cnt+name;
                                FileUtil.appendString (st + "||" + filePath + "||" + picName+"\n","d://tmp1/picUrl.txt", "UTF-8");
                                picNum++;
                            }
                        } catch (Exception e) {
                            log.error("name:{} page:{}",name,i,e);
                        }

                        if (picNum>500) break;
                    }
                    hiveCnt++;
                    Thread.sleep(1000);
                } catch (Exception e) {
                    log.error("name:{}异常",name,e);
                }
            }
            cnt++;
        }
    }
}
