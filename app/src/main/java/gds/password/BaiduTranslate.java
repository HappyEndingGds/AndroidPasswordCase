package gds.password;

import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Author:  gds
 * Time: 2016/5/22 9:19
 * E-mail: guodongshenggds@foxmail.com
 */
public class BaiduTranslate implements Runnable {
    private static final String UTF8 = "utf-8";
    private static final String appId = "20160521000021628";
    private static final String token = "S5irgrIfTwamRSvXY1bW";
    private static final String url = "http://api.fanyi.baidu.com/api/trans/vip/translate";
    private static final Random random = new Random();

    private String srcContent;
    private String destContent;
    public String getDestContent() {
        return destContent;
    }

    private String srcLanguage;
    private String destLanguage;
    private Handler handler;


    public BaiduTranslate(String srcContent, String srcLanguage, String destLanguage, Handler handler) {
        this.srcContent = srcContent;
        this.srcLanguage = srcLanguage;
        this.destLanguage = destLanguage;
        this.handler = handler;
    }

    public void translateContent(String q, String from, String to) throws Exception{
                    int salt = random.nextInt(10000);
                    StringBuilder md5String = new StringBuilder();
                    md5String.append(appId).append(q).append(salt).append(token);
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    String md5 = MD5(md5String.toString(),md);

                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpost = new HttpPost(url);
                    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                    nvps.add(new BasicNameValuePair("q", q));
                    nvps.add(new BasicNameValuePair("from", from));
                    nvps.add(new BasicNameValuePair("to", to));
                    nvps.add(new BasicNameValuePair("appid", appId));
                    nvps.add(new BasicNameValuePair("salt", String.valueOf(salt)));
                    nvps.add(new BasicNameValuePair("sign", md5));
                    httpost.setEntity(new UrlEncodedFormEntity(nvps, UTF8));
                    HttpResponse httpResponse = httpClient.execute(httpost);

                    if(httpResponse.getStatusLine().getStatusCode()==200){
                        String result = null;
                        try {
                            result = EntityUtils.toString(httpResponse.getEntity(),UTF8);
                            MainActivity.myDebug(result);

                            //获取返回翻译结果
                            JSONObject resultJson = new JSONObject(result.toString());
                            JSONArray array = (JSONArray) resultJson.get("trans_result");
                            JSONObject dst = (JSONObject) array.get(0);
                            String text = dst.getString("dst");
                            text = URLDecoder.decode(text, UTF8);
                            Message msg = new Message();
                            msg.obj = text;
                            handler.sendMessage(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }//if
    }//translate

    //MD5加密算法
    public static String MD5(String strSrc,MessageDigest md) {
        byte[] bt = strSrc.getBytes();
        md.update(bt);
        String strDes = bytes2Hex(md.digest()); // to HexString
        return strDes;
    }
    private static String bytes2Hex(byte[] bts) {
        StringBuffer des = new StringBuffer();
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des.append("0");
            }
            des.append(tmp);
        }
        return des.toString();
    }

    @Override
    public void run() {
        try {
            translateContent(srcContent,srcLanguage,destLanguage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
