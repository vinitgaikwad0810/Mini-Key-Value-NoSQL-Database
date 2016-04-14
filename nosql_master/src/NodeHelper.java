import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



public class NodeHelper {
		
//	public static final boolean isMaster = true;
	
	public static final String NODE_IP = "_NodeIP";
	public static final String VERSION = "_Version";
	public static final String ACTION = "_Action";
	public static final String STATUS = "_Status";
	public static final String KEY = "Key";
	public static final String VALUE = "Value";
	
	public static final String RESTLET = "restlet";
	public static final String TEST = "test";
	
	
//	public static final int MASTER_EXTERNAL_PORT = 80;
//	public static final int MASTER_INTERNAL_PORT = 8080;
//	
//	public static final String SLAVE_1_IP = "52.34.240.250";	
//	public static final int SLAVE_1_EXTERNAL_PORT = 80;
//	public static final int SLAVE_1_INTERNAL_PORT = 8080;
//
//	public static final String SLAVE_2_IP = "52.26.144.117";
//	public static final int SLAVE_2_EXTERNAL_PORT = 80;
//	public static final int SLAVE_2_INTERNAL_PORT = 8080;
	
	public static JSONObject stringToJSON(String jsonData) {
		try {
			JSONParser parser = new JSONParser();			
			JSONObject obj = (JSONObject) parser.parse(jsonData);
			return obj;
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String createJSONStr(Map<String, String> map) {
//			StringWriter strw = new StringWriter();
//			JSONObject.writeJSONString(map, strw);
//			return strw.toString();

		    Gson gson = new GsonBuilder().setPrettyPrinting().create();
		    return gson.toJson(map);
	}

	public static int doHttpPost(String url, String jsonData) {
		
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost httpPost = new HttpPost(url);
			StringEntity params = new StringEntity(jsonData);
			httpPost.setEntity(params);		
			HttpResponse response = client.execute(httpPost);

			return response.getStatusLine().getStatusCode();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 404;
	}
	
	public static boolean pingHost(String host, int port, int timeout) {
	    try (Socket socket = new Socket()) {
	        socket.connect(new InetSocketAddress(host, port), timeout);
	        return true;
	    } catch (IOException e) {
	        return false; // Either timeout or unreachable or failed DNS lookup.
	    }
	}
	
	public static String doHttpGet(String url) {
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet get = new HttpGet(url);
			HttpResponse response = client.execute(get);
			if (response.getEntity() != null) {
				HttpEntity entity = response.getEntity();
			    InputStream instream = entity.getContent();
			    
			    byte[] bData = new byte[1024];
			    instream.read(bData);
			    
			    String jsonData = new String (bData);
			    jsonData = jsonData.replace("\u0000", ""); // removes NUL chars
			    jsonData = jsonData.replace("\\u0000", ""); // removes backslash+u0000

//			    StringWriter writer = new StringWriter();
//			    IOUtils.copy(instream, writer, "UTF-8");
//			    String jsonData = writer.toString();
			    return jsonData;
			}
		} catch (IOException e2) {
			e2.printStackTrace();
		} 
		return null;
	}

	public static int doHttpPut(String url, String jsonData) {
		
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpPut httpPut = new HttpPut(url);
			StringEntity params = new StringEntity(jsonData);
			httpPut.setEntity(params);		
			HttpResponse response = client.execute(httpPut);
			
			return response.getStatusLine().getStatusCode();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 404;
	}

	public static String doHttpDelete(String url) {
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpDelete delete = new HttpDelete(url);
			HttpResponse response = client.execute(delete);
			if (response.getEntity() != null) {
				HttpEntity entity = response.getEntity();
			    InputStream instream = entity.getContent();

			    StringWriter writer = new StringWriter();
			    IOUtils.copy(instream, writer, "UTF-8");
			    String jsonData = writer.toString();
			    return jsonData;
			}
		} catch (IOException e2) {
			e2.printStackTrace();
		} 
		return null;
	}

}
