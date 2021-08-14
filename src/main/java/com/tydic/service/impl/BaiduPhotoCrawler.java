package com.tydic.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tydic.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Wanglj
 * @date 2021/7/19 15:40
 */
@Slf4j
public class BaiduPhotoCrawler {

    private static final String photoApiUrl = "https://image.baidu.com/search/acjson";

    // 读取的菜品路径
    private final String pathname = "D:/baidu/test.txt";

    //输出图片路径
    private String path = "D:/baidu/foodPhoto/";

    //每次生成图片数目
    private Integer perNumber = 50;

    //遍历次数
    private Integer time = 10;

    public static void main(String[] args) {
        long begin = System.currentTimeMillis();
        BaiduPhotoCrawler baiDuPhotoCrawler = new BaiduPhotoCrawler();
        baiDuPhotoCrawler.getPhotoFile();
        long end = System.currentTimeMillis();
        System.out.println("下载完成！ 耗时：" + (end - begin) / 1000 + "秒");
    }

    public void getPhotoFile() {
//        File txtFile = new File("D:\\test.txt");

        File txtFile = new File(pathname);
        //String path = this.getClass().getClassLoader().getResource("./static/dish.txt").getPath();
        //File txtFile = new File(path);
        ArrayList<String> dishList = txt2String(txtFile);
        ArrayList<ArrayList<String>> arrayListArrayList = new ArrayList<>();
        if (dishList.size() < 10) {
            arrayListArrayList.add(dishList);
        }
        //如果数量大于2000，分割ArrayList进行多线程；
        else {
            for (int i = 0; i < dishList.size(); i++) {
                int num = i / 10;
                if (i % 10 == 0) {   // i = 0, 2000, 4000...
                    arrayListArrayList.add(new ArrayList<String>(10));
                }
                if (arrayListArrayList.size() == num + 1) {
                    arrayListArrayList.get(num).add(dishList.get(i));
                }
            }
        }
        System.out.println("unitFile: 分割数量：" + arrayListArrayList.size());
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        for (int i = 0; i < arrayListArrayList.size(); i++) {
            UnitThread unitThread = new UnitThread(arrayListArrayList, i);
            threadPoolExecutor.execute(unitThread);
            try {
                if (i % 5 == 0) {
                    Thread.sleep(5000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        threadPoolExecutor.shutdown(); // shutdown线程池会把已经提交的剩余线程执行完然后关闭，  shutdownNow是直接关闭执行中的线程返回剩余没执行的线程
        while (true) {  //等线程全部执行完毕
            //System.out.println("线程池剩余线程数量：" + threadPoolExecutor.getActiveCount());
            if (threadPoolExecutor.isTerminated()) {
                System.out.println("线程全部运行完毕");
                break;
            }
        }
    }

    //内部线程类
    public class UnitThread extends Thread {
        private int pageIndex;
        private ArrayList<ArrayList<String>> arrayListArrayList;

        public UnitThread(ArrayList<ArrayList<String>> arrayListArrayList, int pageIndex) { //线程不能取得局部变量，只能作为参数传进来，ArrayList是引用变量，所以值可以直接修改，不需要返回结果。
            this.pageIndex = pageIndex;
            this.arrayListArrayList = arrayListArrayList;
        }

        @Override
        public void run() {
            System.out.println("线程" + pageIndex + "开始");
            dealUrl(arrayListArrayList.get(pageIndex));
            //arrayListArrayList.set(this.pageIndex, unitedCategory); //把新的list传回给list集合
            System.out.println("线程" + pageIndex + "运行完毕");
        }
    }


    /**
     * 读取txt文件的内容
     *
     * @param file 想要读取的文件对象
     * @return 返回文件内容
     */
    public ArrayList<String> txt2String(File file) {
        ArrayList<String> resultList = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
                resultList.add(s);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }


    public void dealUrl(List<String> dishList) {
        for (String dish : dishList) {
            getResponseUrl(dish);
        }
    }

    /**
     * 通过dishName获取内容cid
     *
     * @param dishName
     * @return
     * @throws IOException
     */
    public void getResponseUrl(String dishName) {
        int fileNum = 1000;
        for (int i = 0; i < time; i++) {
            String json = getContentByUrl(perNumber, i, dishName);
            JSONObject jsonObject = JSON.parseObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int j = 0; j < jsonArray.size(); j++) {
                String hoverURL = jsonArray.getJSONObject(j).getString("thumbURL");
                log.info("i：{} j:{} 菜品名称：{} ， 图片路径：{}", i, j, dishName, hoverURL);
                //log.info("图片url:{}", hoverURL);
                if (jsonArray.getJSONObject(j) == null || jsonArray.getJSONObject(j).toString().isEmpty() || hoverURL == null || hoverURL.isEmpty()) {
                    continue;
                }

                getFile(dishName, hoverURL, fileNum);
                fileNum++;
            }
        }
    }


    // 3-2  建立URL连接请求
    private void getFile(String dishName, String photoUrl, int fileNum) {
        // long begin = System.currentTimeMillis();
        InputStream inputStream = null;
        try {
            URL url = new URL(photoUrl);
            URLConnection urlConnection = url.openConnection();
            String refererUrl = "https://image.baidu.com"; // 设置协议
            urlConnection.setRequestProperty("Referer", refererUrl);
            urlConnection.setRequestProperty("Sec-Fetch-Mode", "no-cors");
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36");
            urlConnection.setConnectTimeout(10 * 1000);
            inputStream = urlConnection.getInputStream();
            // System.out.println("开始下载... 共:" + (urlConnection.getContentLength() / 1024) + "Kb");
//            File path = new File("D:\\BaiDuPhoto\\" + dishName);
            File file = new File(path + dishName);
            //File path = new File("wyy/opt/cp/baiduPhoto/" + dishName);
            if (!file.exists()) {
                file.mkdirs();
            }
            log.info("dishName:{} photoUrl:{}", dishName, photoUrl);
            FileUtils.copyInputStreamToFile(inputStream, new File(path + dishName + File.separator + getFileName(fileNum)));
            // FileUtils.copyInputStreamToFile(inputStream, new File("wyy/opt/cp/baiduPhoto/" + dishName + File.separator + getFileName()));
            // long end = System.currentTimeMillis();
            // System.out.println("下载完成！ 耗时：" + (end - begin) / 1000 + "秒");
        } catch (IOException e) {
            System.err.println("获取inputStream失败");
        }
    }

    /**
     * 拼接文件名
     * type  1-图片  2-视频
     *
     * @return String
     */
    private String getFileName(int fileNum) {
        String fileNameTemplate = "{}.{}";//文件名示例：20210713180637564632430.jpg
        String fileName = fileNum + "";
        return StrUtil.format(fileNameTemplate, fileName, "jpg");

    }

    private String getContentByUrl(Integer size, Integer page, String word) {
        Integer pn = page * size;
        HashMap<String, String> header = new HashMap<>();
        header.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
        header.put("Connection", "keep-alive");
        header.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:60.0) Gecko/20100101 Firefox/60.0");
        header.put("Upgrade-Insecure-Requests", "1");
        HashMap<String, Object> params = new HashMap<>();
        params.put("charset", "UTF-8");
        params.put("tn", "resultjson_com");
        params.put("logid", "7811942975840032864");
        params.put("ipn", "rj");
        params.put("ct", "201326592");
        params.put("fp", "result");
        params.put("cl", "2");
        params.put("lm", "-1");//动图
        params.put("ie", "utf-8");
        params.put("oe", "utf-8");
        params.put("st", "-1");
        params.put("ic", "0");
        params.put("istype", "2");
        params.put("qc", "");
        params.put("nc", "1");
        params.put("pn", pn);
        params.put("rn", size);//每页数量
        params.put("word", word);
        params.put("gsm", "1e");
        params.put("1627660029676", "");
        String response = HttpClientUtil.doHttpGet(BaiduPhotoCrawler.photoApiUrl, params);
        log.info("返回参数：{}", response);
        return response;
    }
}


