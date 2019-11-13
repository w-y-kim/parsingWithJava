package c.parsing;

import java.io.IOException;
import java.net.*;
import java.io.*;

public class Download {

 public void downloadUrl(String urlStr) {
	 
  try{
   URL url=new URL(urlStr);
   
   HttpURLConnection http = (HttpURLConnection)url.openConnection();
   int statusCode = http.getResponseCode();
   InputStream is = http.getInputStream();

   System.out.println("(1) http connection response code:" + statusCode);
   OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream("copy.html"),"UTF-8");
   BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
   
   
   String line="";
   while((line=br.readLine())!=null){
    fw.write(line+"\r\n");
    fw.flush();
   }
   br.close();
   is.close();
   fw.close();
   
   System.out.println("(2) downlaod html");
  }catch(MalformedURLException e){
   e.printStackTrace();
  }catch(IOException e){
   e.printStackTrace();
  }
 }
}