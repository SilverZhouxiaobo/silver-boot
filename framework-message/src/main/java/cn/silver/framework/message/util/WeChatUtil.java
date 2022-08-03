package cn.silver.framework.message.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class WeChatUtil {

    public static JSONObject httpSend(String requestUrl, JSONObject paramObj, HttpMethod method) {
        HttpResponse response;
        JSONObject resultObject = new JSONObject();
        switch (method) {
            case GET:
                if (MapUtils.isNotEmpty(paramObj)) {
                    StringBuilder builder = new StringBuilder("?");
                    for (Map.Entry<String, Object> entry : paramObj.entrySet()) {
                        builder.append(entry.getKey() + "=" + entry.getValue() + "&");
                    }
                    requestUrl += builder.toString();
                }
                response = HttpRequest.get(requestUrl).execute();
                break;
            case PUT:
                response = HttpRequest.put(requestUrl).body(paramObj.toJSONString()).execute();
                break;
            case POST:
            default:
                response = HttpRequest.post(requestUrl).body(paramObj.toJSONString()).execute();
                break;
        }
        if (response != null && StringUtils.isNotBlank(response.body()) && response.body().startsWith("{") && response.body().endsWith("}")) {
            resultObject = JSON.parseObject(response.body());
        }
        return resultObject;

    }

    /**
     * 1.发起https请求并获取结果
     *
     * @param requestUrl    请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr     提交的数据
     * @return JSONObject(通过JSONObject.get ( key)的方式获取json对象的属性值)
     */
    @SneakyThrows
    public static JSONObject httpRequest(String requestUrl, String requestMethod, String outputStr) {
        JSONObject jsonObject = null;
        StringBuffer buffer = new StringBuffer();
        // 创建SSLContext对象，并使用我们指定的信任管理器初始化
        TrustManager[] tm = {new MyX509TrustManager()};
        SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
        sslContext.init(null, tm, new java.security.SecureRandom());
        // 从上述SSLContext对象中得到SSLSocketFactory对象
        SSLSocketFactory ssf = sslContext.getSocketFactory();
        URL url = new URL(requestUrl);
        HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
        httpUrlConn.setSSLSocketFactory(ssf);
        httpUrlConn.setDoOutput(true);
        httpUrlConn.setDoInput(true);
        httpUrlConn.setUseCaches(false);
        // 设置请求方式（GET/POST）
        httpUrlConn.setRequestMethod(requestMethod);
        if ("GET".equalsIgnoreCase(requestMethod)) {
            httpUrlConn.connect();
        }
        // 当有数据需要提交时
        if (null != outputStr) {
            OutputStream outputStream = httpUrlConn.getOutputStream();
            // 注意编码格式，防止中文乱码
            outputStream.write(outputStr.getBytes(StandardCharsets.UTF_8));
            outputStream.close();
        }
        // 将返回的输入流转换成字符串
        InputStream inputStream = httpUrlConn.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String str = null;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
            bufferedReader.close();
            inputStreamReader.close();
            // 释放资源
            inputStream.close();
            inputStream = null;
            httpUrlConn.disconnect();
            jsonObject = JSONObject.parseObject(buffer.toString());
        }
        return jsonObject;
    }
}
