package newenergy.wx.product.util;

import newenergy.wx.product.pojo.Token;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;


/**
 * 通用工具类
 *
 * @author yangq
 * @date 2019-04-15
 */
public class CommonUtil {
    private static Logger log = LoggerFactory.getLogger(CommonUtil.class);
    public final static String token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

    /**
     * 发送https请求
     *
     * @param requestUrl 请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr 提交的数据
     * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
     */
    public static JSONObject httpsRequest(String requestUrl,String requestMethod,String outputStr){
        JSONObject jsonObject = null;
        RestTemplate restTemplate = CommonUtil.getInstance("utf-8");
        try{
            //创建SSLContext对象，并使用我们指定的信任管理器初始化
            switch(requestMethod){
                case "GET":
                    if (null == outputStr){
                        String a = restTemplate.getForObject(requestUrl,String.class);
                        jsonObject = JSONObject.fromObject(a);
//                        jsonObject = restTemplate.getForObject(requestUrl,JSONObject.class);
//                        URL url = new URL(requestUrl);
//                        HttpURLConnection conn = (HttpURLConnection)usrl.openConnection();
//                        conn.setDoOutput(true);
//                        conn.setDoInput(true);
//                        conn.setUseCaches(false);
//                        conn.setRequestMethod(requestMethod);
////                        OutputStream outputStream = conn.getOutputStream();
////                        outputStream.write(outputStr.getBytes("UTF-8"));
////                        outputStream.close();
//                        InputStream inputStream = conn.getInputStream();
//                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"utf-8");
//                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                        String str=null;
//                        StringBuffer buffer = new StringBuffer();
//                        while((str = bufferedReader.readLine())!=null){
//                            buffer.append(str);
//                        }
//                        bufferedReader.close();
//                        inputStreamReader.close();
//                        inputStream.close();
//                        inputStream = null;
//                        conn.disconnect();
//                        jsonObject = JSONObject.fromObject(buffer.toString());
                    }else{
                        jsonObject = restTemplate.getForObject(requestUrl,JSONObject.class,outputStr);
                    }
                    break;
                case "POST":
                    jsonObject = restTemplate.postForObject(requestUrl,outputStr,JSONObject.class);
//                    URL url = new URL(requestUrl);
//                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
//                    conn.setDoOutput(true);
//                    conn.setDoInput(true);
//                    conn.setUseCaches(false);
//                    conn.setRequestMethod(requestMethod);
//                    OutputStream outputStream = conn.getOutputStream();
//                    outputStream.write(outputStr.getBytes("UTF-8"));
//                    outputStream.close();
//                    InputStream inputStream = conn.getInputStream();
//                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"utf-8");
//                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                    String str=null;
//                    StringBuffer buffer = new StringBuffer();
//                    while((str = bufferedReader.readLine())!=null){
//                        buffer.append(str);
//                    }
//                    bufferedReader.close();
//                    inputStreamReader.close();
//                    inputStream.close();
//                    inputStream = null;
//                    conn.disconnect();
//                    jsonObject = JSONObject.fromObject(buffer.toString());
                    break;
                default:
                    System.out.println("null");
                    break;
            }
        }catch (Exception e){
            log.error("https请求异常：{}",e);
        }
        return jsonObject;
    }

    /**
     * 获取微信访问接口——access_token
     * @param appid
     * @param appsecret
     * @return
     */
    public static Token getAccessToken(String appid,String appsecret){
        Token token  = null;
        String requestUrl = token_url.replace("APPID",appid).replace("APPSECRET",appsecret);
        //发起GET请求获取凭证
        JSONObject jsonObject = httpsRequest(requestUrl,"GET",null);

        if (null != jsonObject){
            try{
                token = new Token();
                token.setAccessToken(jsonObject.getString("access_token"));
                token.setExpiresIn(jsonObject.getInt("expires_in"));
            }catch (JSONException e){
                token = null;
                log.error("获取token失败 errcode:{} errmsg:{}",jsonObject.getInt("errcode"),jsonObject.getString("errmsg"));
            }
        }
        return token;
    }

    /**
     * URL编码（utf-8）
     * @param source
     * @return
     */
    public static String urlEncodeUTF8(String source){
        String result = source;
        try{
            result = java.net.URLEncoder.encode(source,"utf-8");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return result;
    }

    public static RestTemplate getInstance(String charset) {
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> list = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> httpMessageConverter : list) {
            if(httpMessageConverter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) httpMessageConverter).setDefaultCharset(Charset.forName(charset));
            }
        }
        return restTemplate;
    }
}
