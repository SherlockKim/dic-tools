package com.tydic.service.impl;

import com.tydic.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
public class XiaChuFangPicServiceImpl {
    public static void main(String[] args) {
        String srcFile = "D:/baidu/xiaochufang.txt";
        int parallel = 15;

        FileInputStream fis = null;
        Reader rd = null;
        BufferedReader br = null;
        int fileRows = 0;
        try {
            // 获取文件路径
            fis = new FileInputStream(srcFile);
            // 指定字符编码
            rd = new InputStreamReader(fis, "UTF-8");
            br = new BufferedReader(rd);

            // 多线程处理
            ExecutorService executor = Executors.newFixedThreadPool(parallel);
            XiaChuFangPicServiceImpl tast = new XiaChuFangPicServiceImpl();
            String line = null;
            while ((line = br.readLine()) != null) {
                fileRows++;
                // if (fileRows<40680) continue;
                log.info("当前文件行数:{}", fileRows);
                Future<?> future = executor.submit(tast.new SendTask(line, fileRows));
                try {
                    if (fileRows % parallel == 0) {
                        future.get(5, TimeUnit.SECONDS);
                        log.info("当前处理行数{}", fileRows);
                    }
                    if (fileRows % 1000 == 0) {
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    log.error("号码:{} 当前行数:{} 多线程处理异常:{}", line, fileRows, e.getMessage(), e);
                }
            }
            executor.shutdown();
        } catch (Exception e) {
            log.error("获取活动文件异常:src:{} 异常信息:{}", srcFile, e.getMessage(), e);
        } finally {
            try {
                if (null != br)
                    br.close();
                if (null != rd)
                    rd.close();
                if (null != fis)
                    fis.close();
            } catch (IOException e) {
                log.error("获取活动文件异常:src:{} 异常信息:{}", srcFile, e.getMessage(), e);
            }
        }


    }

    // 多线程处理任务
    public class SendTask implements Runnable {
        String line;
        int i;

        public SendTask(String line, int i) {
            this.line = line;
            this.i = i;
        }

        public void run() {
            log.info("当前线程{} 发送消息{} 当前行数:{}", Thread.currentThread().getName(), line, i);
            String[] strArray = line.split("\\|\\|");
            HttpClientUtil.doHttpGet(strArray[0], null, strArray[1], strArray[2]);
        }
    }
}
