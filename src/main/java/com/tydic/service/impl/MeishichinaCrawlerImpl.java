package com.tydic.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.tydic.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;


@Slf4j
public class MeishichinaCrawlerImpl {
    // 读取的菜品路径
    private final static String srcFilePath = "D:/baidu/fruit20210731.txt";
    private static final String pageUrl = "https://home.meishichina.com/search/{}/page/{}/";
    // 输出的图片路径
    private static String fileDir = "D:/tmp/test05/";
    public static void main(String[] args) {
        try {
            int cnt =1;
            List<String> nameArray = FileUtil.readLines(srcFilePath,"utf-8");
            for (String name:nameArray) {
                log.info("开始爬取：{}",name);
                // 获取总记录数
                Document documentTmp = Jsoup.connect(StrUtil.format(pageUrl, name,1)).get();
                String str = documentTmp.select(".ui_title_wrap span").html();
                if (StringUtils.isEmpty(str) || StringUtils.isEmpty(str.replaceAll("[^0-9]",""))) {
                    cnt++;
                    continue;
                }
                int records = Integer.valueOf(str.replaceAll("[^0-9]",""));

                int pageSize=20;  int pageNum = (int)Math.ceil((double)(records)/pageSize);
                log.info("str:{} records:{} pageNum:{}",str,records,pageNum);
                int picNum=1;
                for (int k=1; k<=pageNum&&k<=25; k++){
                    Document document = Jsoup.connect(StrUtil.format(pageUrl, name,k)).get();
                    Elements elements = document.select("#search_res_list ul li");
                    for (int i=0; i<elements.size(); i++) {
                        String picUrl = elements.get(i).select("div.pic a img").attr("data-src");
                        String picUrlTmp = picUrl.split("\\?")[0];
                        String picName = picNum + "_" +picUrlTmp.substring(picUrlTmp.lastIndexOf("/")+1);
                        log.info("picUrl:{} fileDir:{} name:{}",picUrl,fileDir+cnt+name,picName);
                        HttpClientUtil.doHttpGet("https:"+picUrl,null,fileDir+cnt+name,picName);
                        picNum++;
                    }

                    Thread.sleep(1000);
                }
                cnt++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
