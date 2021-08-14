package com.tydic.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.tydic.entity.XinshipuEntity;
import com.tydic.utils.WebClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 鸭屮
 * @version 1.0
 * @date 2021/8/9 9:28
 * 爬食谱分类
 */

@Slf4j
public class XinshipuCrawlerImpl2 {
    static boolean flag = true;
    static String PREURL = "https://www.xinshipu.com";
    static List<XinshipuEntity> list = new ArrayList<>();
    private final static int flushRows = 2000;
    static int cnt = 1;

    public static void main(String[] args) throws Exception {
        String url = PREURL + "/chuyoufenlei";
//        String url = "https://www.xinshipu.com//zuofa/62273";
//        List<XinshipuEntity> list = new ArrayList<>();
        log.info("begin=============");
        WebClient webClient = WebClientUtil.webClient();
        HtmlPage page = WebClientUtil.getPage(webClient, url);
        int pageNum = 18;
//        Elements elements = Jsoup.parse(page.asXml()).select(".bpannel");
        for (;pageNum<=20;++pageNum){
            Document document = Jsoup.connect(url + "?page=" + pageNum).get();
            Elements elements = document.select("div[class=bpannel mt23]");
            List<String> categorys = elements.select("li span[class=r-name]").eachText();
            int size = elements.select("li").size();
            for (int i = 0; i < size; ++i) {
                XinshipuEntity xinshipuEntity = new XinshipuEntity();
                xinshipuEntity.setCategory(categorys.get(i));
                Elements ele = elements.select("li a");
                String href = ele.get(i).attr("href");
//                if (href.startsWith("/jiachangzuofa")) {
//                    secondPage(href, xinshipuEntity);
//                } else if (href.startsWith("/zuofa")) {
//                thirdPage(documentTmp, xinshipuEntity);
                secondPage(href, xinshipuEntity);
//                }
            }
        }
        if (list.size() > 0) {
            writeExcel("d:/baidu/xinshipu/心食谱" + (cnt - flushRows) + "_" + cnt + ".xlsx", 0, "家常菜谱" + cnt, list);
        }
//        writeExcel("d:/tmp/test01/心食谱" + (cnt - flushRows) + "_" + cnt + ".xlsx", 0, "家常菜谱" + cnt, list);
    }


    /**
     * 二级页面
     */
    public static void secondPage(String href, XinshipuEntity xinshipuEntity) throws Exception {
        int pageNo = 1;
        try {
            while (Jsoup.connect(PREURL + href + "?page=" + pageNo).get().baseUri().indexOf("page") != -1) {
                Document documentTmp = Jsoup.connect(PREURL + href + "?page=" + pageNo++).get();
//            List<String> list = documentTmp.select("div[class=new-menu mt20]").eachText();
                Elements findHref = documentTmp.select("div[class=new-menu mt20] a");
                for (Element element : findHref) {
                    String url = element.attr("href");
                    try {
                        Document dt = Jsoup.connect(PREURL + url).get();
                        thirdPage(dt, xinshipuEntity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


//                writeIn(xinshipuEntity);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 三级页面
     *
     * @param documentTmp
     * @param xinshipuEntity
     */
    public static void thirdPage(Document documentTmp, XinshipuEntity xinshipuEntity) {
        XinshipuEntity temp = new XinshipuEntity();
        temp.setCategory(xinshipuEntity.getCategory());
        String foodName = documentTmp.select("div[class=re-up] h1").text();
        Elements detailElements = documentTmp.select("div[class=bpannel mt20 p15 re-steps]");
        temp.setFoodName(foodName);
        //先判断页面材料做法的格式是哪种（有两种情况span和div）
        if (detailElements.select("div[class=dd food-material]").eachText().size() > 0) {
            temp.setMaterial(detailElements.select("div[class=dd food-material]").text());
//            temp.setCook(detailElements.select(".dd li").text());
            Elements elements = detailElements.select(".dd li");
            List<String> strings = new ArrayList<>();
            for (Element element : elements) {
                strings.add(element.text());
            }
            temp.setCook(StrUtil.join("\n", strings));
        } else {
            List<String> dt = detailElements.select(".dt").eachText();
            List<String> dd = detailElements.select(".dd").eachText();
            for (int j = 0; j < dt .size(); ++j) {
                if ("材料".equals(dt.get(j))) {
                    temp.setMaterial(dd.get(j));
                } else if ("做法".equals(dt.get(j))) {
                    List<String> strings = detailElements.select("div[class=dd]").get(j).select("p").eachText();
                    temp.setCook(StrUtil.join("\n", strings));
                }
            }
        }
        list.add(temp);
        log.info("xinshipuEntity{}:{},", cnt, temp);

//        System.out.println("list长度:"+list.size());
//        list.forEach(System.out::println);
        if (0 == list.size() % flushRows) {
            // 写入excel

            writeExcel("d:/baidu/xinshipu/心食谱" + (cnt - flushRows) + "_" + cnt + ".xlsx", 0, "家常菜谱" + cnt, list);
        }
        cnt++;

    }

    public static void writeExcel(String pathName, int sheetNo, String sheetName, List data) {
        // 写入excel
        ExcelWriter excelWriter = EasyExcel.write(pathName).build();
        WriteSheet writeSheet = EasyExcel.writerSheet(sheetNo, sheetName).head(XinshipuEntity.class).build();
//        writeSheet.set
        excelWriter.write(data, writeSheet);
        excelWriter.finish();
        data.clear();
    }
}
