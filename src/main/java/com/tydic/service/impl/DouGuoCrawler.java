package com.tydic.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.tydic.entity.DouGuoEntity;
import com.tydic.entity.XinshipuEntity;
import com.tydic.utils.WebClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 鸭屮
 * @version 1.0
 * @date 2021/8/11 10:36
 */
@Slf4j
public class DouGuoCrawler {
    private final static int flushRows = 10000;
    static int cnt = 1;
    static String URL = "https://www.douguo.com";
    public static void main(String[] args) throws Exception {
//        WebClient webClient = WebClientUtil.webClient();
        List<DouGuoEntity> list = new ArrayList<>();
        for (int pageNum = 0; pageNum <= 166*12; pageNum = pageNum + 12) {
//            HtmlPage page = WebClientUtil.getPage(webClient, url+"/"+pageNum*12);
            Document document = null;
            try {
                document = Jsoup.connect(URL + "/caidan/" + pageNum).get();
            }catch (Exception e){
                e.printStackTrace();
            }
            Elements elements = document.select("div[class=menu-list]");
            int size = document.select(".menu-list li").size();
            for (int i = 0; i < size; ++i) {
                Elements li_a = elements.select("li a");
                String href = li_a.get(i).attr("href");
                String foodText = li_a.get(i).select("p").get(1).text();
                foodText = foodText.substring(0, foodText.indexOf("道"));
                int foodSize = Integer.valueOf(foodText);
                for (int j = 0; j < foodSize; j = j + 15) {
                    Document document1=null;
                    try {
                        document1 = Jsoup.connect(URL + "/" + href + "/" + j).get();
                        List<String> foodNames = document1.select("div[class=des-menu-list] li").select("a[class=cookname wb100]").eachText();
                        for (String foodName : foodNames) {
                            DouGuoEntity douGuoEntity = new DouGuoEntity();
                            douGuoEntity.setFoodName(foodName);
                            list.add(douGuoEntity);
                            log.info("douguoEntity{}:{},", cnt, douGuoEntity);
                            if (0 == list.size() % flushRows) {
                                // 写入excel
                                writeExcel("d:/baidu/douguo/豆果" + (cnt - flushRows) + "_" + cnt + ".xlsx", 0, "豆果美食" + cnt, list);
                            }
                            cnt++;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        }
        if(list.size()>0){
            writeExcel("d:/baidu/douguo/豆果" + (cnt - flushRows) + "_" + cnt + ".xlsx", 0, "豆果美食" + cnt, list);
        }

    }

    public static void writeExcel(String pathName, int sheetNo, String sheetName, List data) {
        // 写入excel
        ExcelWriter excelWriter = EasyExcel.write(pathName).build();
        WriteSheet writeSheet = EasyExcel.writerSheet(sheetNo, sheetName).head(DouGuoEntity.class).build();
//        writeSheet.set
        excelWriter.write(data, writeSheet);
        excelWriter.finish();
        data.clear();
    }
}
