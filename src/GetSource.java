import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
 
public class GetSource {
 
 public String GetCode(String str_url) throws Exception{
 
  URL url = new URL(str_url);
  URLConnection connection = url.openConnection();
  
  String code = "";

  //Creation du buffer de connection
  BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
  String strLine = "";
 
  //Loop through every line in the source
  while ((strLine = in.readLine()) != null){
	  code += strLine + '\n';
 
  }
  return code;
 }
 
}