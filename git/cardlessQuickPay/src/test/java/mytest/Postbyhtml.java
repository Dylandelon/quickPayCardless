package mytest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Postbyhtml {
	public static void connectionUrl(String username,String password){
        String url = "https://101.231.206.160/PGWPortal/RecvOrder.do";

        String responseMessage = ""; 
        StringBuffer response = new StringBuffer(); 
        HttpURLConnection httpConnection = null; 
        OutputStreamWriter out = null; 
        BufferedReader reader = null; 
        try { 
          URL urlPost = new URL(url); 
          httpConnection = (HttpURLConnection) urlPost.openConnection(); 
          httpConnection.setDoOutput(true); 
          httpConnection.setDoInput(true); 
          // 参数长度太大，不能用get方式 
          httpConnection.setRequestMethod("POST"); 
          // 不使用缓存 
          httpConnection.setUseCaches(false); 
          // URLConnection.setInstanceFollowRedirects是成员函数，仅作用于当前函数 
          httpConnection.setInstanceFollowRedirects(true); 
          // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的 
          // 意思是正文是urlencoded编码过的form参数 
          httpConnection.setRequestProperty("Connection", "Keep-Alive");
          // 设置请求头信息
          httpConnection.setRequestProperty("Charset", "UTF-8");
          // 设置边界
          String BOUNDARY = "----------" + System.currentTimeMillis();
          httpConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
          //httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 

          // 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成， 
          // 要注意的是connection.getOutputStream会隐含的进行connect。 
          // 实际上只是建立了一个与服务器的tcp连接，并没有实际发送http请求。 

          httpConnection.connect(); 
          out = new OutputStreamWriter(httpConnection.getOutputStream(),"UTF-8"); 

          // 正文，正文内容其实跟get的URL中'?'后的参数字符串一致 

          /*JSONObject jsonObject=new JSONObject(); 
          try{
              jsonObject.put("username", username); 
              jsonObject.put("password", password); 
              jsonObject.put("captcha", captcha); 
          }catch(Exception e){
              e.printStackTrace();
          }*/
          /*
          Map parames = new HashMap<String, String>();  
            parames.put("username", "username");  
            parames.put("username", "username");  
            parames.put("captcha", "captcha");  
          */
          // 构建请求参数  
            StringBuffer sb = new StringBuffer();  
            sb.append("username="+username+"&password="+password);
          //写入参数,DataOutputStream.writeBytes将字符串中的16位的unicode字符以8位的字符形式写道流里面 
          out.write(sb.toString()); 
          System.out.println("send_url:" + url);  
          System.out.println("send_data:" + sb.toString());  
          // flush and close 
          out.flush(); 

        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
             try { 
                if (null != out) { 
                  out.close(); 
                } 
                if (null != reader) { 
                  reader.close(); 
                } 
                if (null != httpConnection) { 
                  httpConnection.disconnect(); 
                } 
              } catch (Exception e2) { 
                  e2.printStackTrace();
              }
        }  

        try {
          reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream(),"UTF-8")); 
          while ((responseMessage = reader.readLine()) != null) { 
              response.append(responseMessage); 
              response.append("\n"); 
          } 

          if (!"failure".equals(response.toString())) { 
              System.out.println("success");
          } else { 
              System.out.println("failue");
          } 
          // 将该url的配置信息缓存起来 
          System.out.println(response.toString()); 
          System.out.println(httpConnection.getResponseCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
public static void main(String[] args){
    connectionUrl("张三","123456");
}

}
