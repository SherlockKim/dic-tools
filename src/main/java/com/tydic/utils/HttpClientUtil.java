package com.tydic.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.util.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@SuppressWarnings("all")
@Slf4j
public class HttpClientUtil {
    private static CloseableHttpClient httpClient = null;

    static {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        // 总连接池数量
        connectionManager.setMaxTotal(150);
        // 可为每个域名设置单独的连接池数量
        connectionManager.setMaxPerRoute(new HttpRoute(new HttpHost("home.meishichina.com")), 100);
        // setConnectTimeout：设置建立连接的超时时间
        // setConnectionRequestTimeout：从连接池中拿连接的等待超时时间
        // setSocketTimeout：发出请求后等待对端应答的超时时间
        //把代理设置到请求配置        代理IP     端口
       // HttpHost proxy = new HttpHost(KuaiDaiLiUtils.ip.split(":")[0], Integer.parseInt(KuaiDaiLiUtils.ip.split(":")[1]));
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(3000)
                .setConnectionRequestTimeout(5000)
                .setSocketTimeout(3000)
                .build();
        // 重试处理器，StandardHttpRequestRetryHandler
        HttpRequestRetryHandler retryHandler = new StandardHttpRequestRetryHandler();

        httpClient = HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(requestConfig)
                .setRetryHandler(retryHandler).build();
    }

    public static String doHttpGet(String uri, Map<String, Object> getParams) {
        CloseableHttpResponse response = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(uri);
            if (null != getParams && !getParams.isEmpty()) {
                List<NameValuePair> list = new ArrayList<>();
                for (Map.Entry<String, Object> param : getParams.entrySet()) {
                    list.add(new BasicNameValuePair(param.getKey(), param.getValue().toString()));
                }
                uriBuilder.setParameters(list);
            }
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
            httpGet.addHeader("Connection", "keep-alive");
            httpGet.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:60.0) Gecko/20100101 Firefox/60.0");
            httpGet.addHeader("Upgrade-Insecure-Requests", "1");

            response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            log.info("uri:{} statusCode:{}",uri,statusCode);
            if (HttpStatus.SC_OK == statusCode) {
                HttpEntity entity = response.getEntity();
                if (null != entity) {
                    String resStr = EntityUtils.toString(entity, "utf-8");
                    //return JSON.parseObject(resStr);
                    return resStr;
                }
            }
        } catch (Exception e) {
            log.error("CloseableHttpClient-get-请求异常", e);
        } finally {
            try {
                if (null != response)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String doHttpPost(String uri, Map<String, String> getParams) {
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(uri);
            if (null != getParams && !getParams.isEmpty()) {
                List<NameValuePair> list = new ArrayList<>();
                for (Map.Entry<String, String> param : getParams.entrySet()) {
                    list.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                }
                HttpEntity httpEntity = new UrlEncodedFormEntity(list, "utf-8");
                httpPost.setEntity(httpEntity);
            }
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK == statusCode) {
                HttpEntity entity = response.getEntity();
                if (null != entity) {
                    String resStr = EntityUtils.toString(entity, "utf-8");
                   // return JSON.parseObject(resStr);
                    return resStr;
                }
            }
        } catch (Exception e) {
            log.error("CloseableHttpClient-post-请求异常", e);
        } finally {
            try {
                if (null != response)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void doHttpGet(String uri, Map<String, String> getParams, String path, String fileName) {
        CloseableHttpResponse response = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(uri);
            if (null != getParams && !getParams.isEmpty()) {
                List<NameValuePair> list = new ArrayList<>();
                for (Map.Entry<String, String> param : getParams.entrySet()) {
                    list.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                }
                uriBuilder.setParameters(list);
            }
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK == statusCode) {
                HttpEntity entity = response.getEntity();
                if (null != entity) {
                    byte[] b = EntityUtils.toByteArray(entity);
                    if (!new File(path).exists()) {
                        new File(path).mkdir();
                    }
                    File imageFile = new File(path + "/" +fileName);
                    //创建输出流
                    FileOutputStream outputStream = new FileOutputStream(imageFile);
                    log.info("imageFile:{}",imageFile.getName());
                    //写入数据
                    outputStream.write(b);
                    //关闭输出流
                    outputStream.close();
                }
            }
        } catch (Exception e) {
            log.error("CloseableHttpClient-get-请求异常", e);
        } finally {
            try {
                if (null != response)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String doProxyHttpGet(String uri, Map<String, String> getParams) {
        InputStream is = null;
        HttpURLConnection httpUrl = null;
        ByteArrayOutputStream outStream = null;
        try {
            // 创建代理服务器
            InetSocketAddress addr = new InetSocketAddress(KuaiDaiLiUtils.ip.split(":")[0], Integer.parseInt(KuaiDaiLiUtils.ip.split(":")[1]));
            Proxy proxy = new Proxy(Proxy.Type.HTTP, addr); // http 代理
            //设置代理的用户名密码
            System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
            Authenticator.setDefault(new MyAuth("zhiliang666", "pn688xgm"));


            httpUrl = (HttpURLConnection) new URL(uri).openConnection(proxy);
            httpUrl.setRequestMethod("GET");
            httpUrl.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36");
            httpUrl.setRequestProperty("Accept-Encoding", "gzip");
            httpUrl.setRequestProperty("Referer", "no-referrer");
            httpUrl.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpUrl.setConnectTimeout(15000);
            httpUrl.setReadTimeout(20000);
            httpUrl.connect();

            int statusCode = httpUrl.getResponseCode();
            log.info("statusCode:{}",statusCode);

            if (statusCode != 200) {
                return null;
            } else {
                return "200";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpUrl != null) {
                httpUrl.disconnect();
            }
        }
        return null;
    }

    static class MyAuth extends Authenticator
    {
        private String user;
        private String pass;

        public MyAuth(String user, String pass)
        {
            this.user  = user;
            this.pass = pass;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return  new PasswordAuthentication(user, pass.toCharArray());
        }
    }

    // 原生代理请求
    public static void doProxyHttpGet(String uri, String filePath,String fileName) {
        InputStream is = null;
        HttpURLConnection httpUrl = null;
        ByteArrayOutputStream outStream = null;
        try {
            // 创建代理服务器
            InetSocketAddress addr = new InetSocketAddress(KuaiDaiLiUtils.ip.split(":")[0], Integer.parseInt(KuaiDaiLiUtils.ip.split(":")[1]));
            Proxy proxy = new Proxy(Proxy.Type.HTTP, addr); // http 代理
            httpUrl = (HttpURLConnection) new URL(uri).openConnection(proxy);
            httpUrl.setConnectTimeout(3000);
            httpUrl.setReadTimeout(60000);
            httpUrl.connect();
           // httpUrl.getInputStream();

            is = httpUrl.getInputStream();
            outStream = new ByteArrayOutputStream();

            //创建一个Buffer字符串
            byte[] buffer = new byte[1024];

            //每次读取的字符串长度，如果为-1，代表全部读取完毕
            int len = 0;
            //使用一个输入流从buffer里把数据读取出来
            while ((len = is.read(buffer)) != -1) {
                //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                outStream.write(buffer, 0, len);
            }
            byte[] b = outStream.toByteArray();

            if (!new File(filePath).exists()) {
                new File(filePath).mkdir();
            }
            File imageFile = new File(filePath + "/" +fileName);
            //创建输出流
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            //写入数据
            outputStream.write(b);
            //关闭输出流
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpUrl != null) {
                httpUrl.disconnect();
            }
            if (is != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

