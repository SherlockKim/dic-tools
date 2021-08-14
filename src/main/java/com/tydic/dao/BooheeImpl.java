//package com.tydic.dao;
//
//import cn.hutool.core.io.FileUtil;
//import cn.hutool.core.util.StrUtil;
//import com.alibaba.excel.EasyExcel;
//import com.alibaba.excel.ExcelWriter;
//import com.alibaba.excel.write.metadata.WriteSheet;
//import com.alibaba.fastjson.JSONArray;
//import com.gargoylesoftware.htmlunit.WebClient;
//import com.gargoylesoftware.htmlunit.html.HtmlPage;
//import com.tydic.entity.BooheelEntity;
//import com.tydic.utils.WebClientUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Slf4j
//public class BooheeImpl {
//    // 读取的菜品路径
//    private final static String srcFilePath = "c:/booheetxt/a.txt";
//    private final static int flushRows = 100;
//    public static void main(String[] args) throws Exception {
//        log.info("begin=============");
//        WebClient webClient = WebClientUtil.webClient();
//
//        List<String> srcList = FileUtil.readLines(srcFilePath, "utf-8");
//        int cnt = 1;
//        List<BooheelEntity> list = new ArrayList<>();
//        for (String name : srcList) {
//            name="腊肉炒花菜";
//            log.info("name:{}", name);
//            BooheelEntity booheelEntity = new BooheelEntity();
//            booheelEntity.setName(name);
//            Elements elements = new Elements();
//            for (int i = 1; i < 4; i++) {
//                String searchUrl = "http://www.boohee.com/food/search?keyword=" + name + "&page=" + i;
//                HtmlPage page = WebClientUtil.getPage(webClient, searchUrl);
//                Elements elements1 = Jsoup.parse(page.asXml()).select(".food-list .item .text-box h4 a");
//                if (elements1.size() == 0) {
//                    break;
//                } else elements.addAll(elements1);
//            }
//            if (elements.size() == 0) {
//                continue;
//            }
//            // 选取最匹配的一个
//            List<String> nameList = elements.eachText();
//            int elementIndex = 0;
//            for (String nameStr : nameList) {
//                if (nameStr.startsWith(name)) {
//                    booheelEntity.setBoolheelName(nameStr);
//                    break;
//                }
//                elementIndex++;
//            }
//            if (StrUtil.isEmpty(booheelEntity.getBoolheelName())) {
//                elementIndex = 0;
//                booheelEntity.setBoolheelName(nameList.get(elementIndex));
//            }
//
//            log.info("BoolheelName:{}", booheelEntity.getBoolheelName());
//            String href = elements.get(elementIndex).attr("href");
//            String title = elements.get(elementIndex).attr("title");
//
//            String detailUrl = "http://www.boohee.com" + href;
//            HtmlPage detailPage = WebClientUtil.getPage(webClient, detailUrl);
//            Elements detailElements = Jsoup.parse(detailPage.asXml()).select(".container .widget-food-detail>.content,.nutr-tag,.widget-more");
//
//            for (Element detailElement : detailElements) {
//                if ("content".equals(detailElement.className())) {
////                    booheelEntity.setContent(StrUtil.join("\n", detailElement.select(".basic-infor li").eachText()));
////                    System.out.println(detailElement.select(".basic-infor li b").eachText()+"1111111111111111");
////                    System.out.println(detailElement.select(".basic-infor li").eachText()+"1111111111111111");
//                    List<String> lib = detailElement.select(".basic-infor li b").eachText();
//                    List<String> li = detailElement.select(".basic-infor li").eachText();
//                    for(int i=0;i<li.size();i++){
//                        li.set(i,li.get(i).replaceAll(lib.get(i),""));
//                    }
////                    li.forEach(System.out::println);
//                    for (int i=0;i<lib.size();i++)
//                    {
//                        if (lib.get(i).equals("别名：")){
//                            booheelEntity.setAlias(li.get(i));
//                        }else if (lib.get(i).equals("热量：")){
//                            booheelEntity.setHeat(li.get(i));
//                        }else if (lib.get(i).equals("分类：")) {
//                            booheelEntity.setClassification(li.get(i));
//                        }
//                    }
////                    Object obj = JSONArray.toJSON(booheelEntity);
////                    String json = obj.toString();
////                    System.out.println("将boohee对象转成json:" + json);
//                } else if (detailElement.className().contains("nutr-tag")) {
////                    booheelEntity.setNutrTag(StrUtil.join("\n", detailElement.select(".content dl[class!=header] dd").eachText()));
//                    List<String> dt = detailElement.select(".content dl[class!=header] dd span[class=dt] ").eachText();
//                    List<String> dd = detailElement.select(".content dl[class!=header] dd span[class=dd] ").eachText();
////                    System.out.println(detailElement.select(".content dl[class!=header] dd span[class=dt] ").eachText()+"999999999999");
////                    System.out.println(detailElement.select(".content dl[class!=header] dd span[class=dd] ").eachText()+"999999999999");
//                    for (int i=0;i<dt.size();i++)
//                    {
//                        if (dt.get(i).equals("热量(大卡)")){
//                            booheelEntity.setReliang(dd.get(i));
//                        }else if (dt.get(i).equals("碳水化合物(克)")){
//                            booheelEntity.setTanshui(dd.get(i));
//                        }else if (dt.get(i).equals("脂肪(克)")){
//                            booheelEntity.setZhifang(dd.get(i));
//                        }else if (dt.get(i).equals("蛋白质(克)")){
//                            booheelEntity.setDanbai(dd.get(i));
//                        }else if (dt.get(i).equals("纤维素(克)")){
//                            booheelEntity.setQianwei(dd.get(i));
//                        }
//                    }
//                }
////                else if (detailElement.className().contains("widget-unit")){
////                    booheelEntity.setWidgetUnit(StrUtil.join("\n",detailElement.select(".content tbody tr").eachText()));
////                }
//                else if (detailElement.className().contains("widget-more")) {
//                    //booheelEntity.setWidgetMore(StrUtil.join("\n", detailElement.select(".content ul li").eachText()));
//                    List<String> h3 = detailElement.select("h3").eachText();
//                    List<String> h3div = detailElement.select("h3+div ul").eachText();
//                    System.out.println(detailElement.select(".content ul li").eachText());
//                    System.out.println(detailElement.select("h3").eachText() + "1000000000000000");
//                    System.out.println(detailElement.select("h3+div li").eachText() + "000000000000000");
//                    for(int i=0;i<h3.size();i++){
//                        if (h3.get(i).equals("主料")||h3.get(i).equals("原料"))
//                        {
//                            booheelEntity.setZhuliao(h3div.get(i));
//                        }else if (h3.get(i).equals("辅料")){
//                            booheelEntity.setFuliao(h3div.get(i));
//                        }else if (h3.get(i).equals("调料")){
//                            booheelEntity.setTiaoliao(h3div.get(i));
//                        }
//                    }
//                }
//            }
//            log.info("booheelEntity:{}", booheelEntity);
//            list.add(booheelEntity);
//            if (0 == list.size()%flushRows) {
//                // 写入excel
//                list.forEach(System.out::println);
//                writeExcel("c:/tmp/test01/薄荷健康"+ (cnt-flushRows) +"_" + cnt +".xlsx",0,"食品库"+cnt,list);
//            }
//            cnt++;
//        }
//        if (list.size()>0)  {
//            writeExcel("c:/tmp/test01/薄荷健康"+ (cnt-flushRows) +"_" + cnt +".xlsx",0,"食品库"+cnt,list);
//        }
//    }
//    public static void writeExcel(String pathName,int sheetNo, String sheetName, List data) {
//        // 写入excel
//        ExcelWriter excelWriter = EasyExcel.write(pathName).build();
//        WriteSheet writeSheet = EasyExcel.writerSheet(sheetNo, sheetName).head(BooheelEntity.class).build();
////        writeSheet.set
//        excelWriter.write(data, writeSheet);
//        excelWriter.finish();
//        data.clear();
//    }
//}
