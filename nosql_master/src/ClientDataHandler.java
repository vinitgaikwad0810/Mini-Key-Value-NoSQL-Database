import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

public class ClientDataHandler extends ServerResource{
	SM sm = SMFactory.getSM();
	String localIP = ConfService.getInstance().getCurrentIP();
	int localPort = ConfService.getInstance().getCurrent_internal_port();
	private boolean isCP = ConfService.getInstance().isCP();
	private boolean isAP = ConfService.getInstance().isAP();
	
	@Get
	public String read() throws Exception{
		Map<String, String> map = new LinkedHashMap<String, String>();
		String key = (String) this.getRequestAttributes().get(InstanceInfo.KEY);
		map.put(NodeHelper.NODE_IP, ConfService.getInstance().getCurrentIP());
		map.put(NodeHelper.VERSION, "1");
		map.put(NodeHelper.ACTION, "READ (KEY)");
		String value = null;
		String masterIP = ConfService.getInstance().getMaster_ip();
		int masterInternalPort = ConfService.getInstance().getMaster_internal_port();

		if (isAP) {
			value = NodeHelper.doHttpGet("http://"+ localIP + ":" + localPort + "/restlet/test/"+key);
			if (value == null)
				map.put(NodeHelper.STATUS, "KEY NOT PRESENT");
			else {
				map.put(NodeHelper.STATUS, "200 SUCCESS");
				map.put(NodeHelper.KEY, key);
				map.put(NodeHelper.VALUE, value);
			}

		} else if (isCP && NodeHelper.pingHost(masterIP, masterInternalPort, 1000)){
			value = NodeHelper.doHttpGet("http://"+ masterIP + ":" + masterInternalPort + "/restlet/test/"+key);
			if (value == null)
				map.put(NodeHelper.STATUS, "KEY NOT PRESENT");
			else {
				map.put(NodeHelper.STATUS, "200 SUCCESS");
				map.put(NodeHelper.KEY, key);
				map.put(NodeHelper.VALUE, value);
			}
		} else {
//			map.put(NodeHelper.STATUS, "MAINTINANCE MODE, CONNECTION TO MASTER DOWN");
			throw new Exception("MAINTINANCE MODE, CONNECTION TO MASTER DOWN");
		}
		return NodeHelper.createJSONStr(map);
	}
	
	
	@Post
	public String write(String jsonData) {
		int status = 404;
		Map<String, String> map = new LinkedHashMap<String, String>();
		try {			
			String masterIP = ConfService.getInstance().getMaster_ip();
			int masterInternalPort = ConfService.getInstance().getMaster_internal_port();
			if (NodeHelper.pingHost(masterIP, masterInternalPort, 1000)) {
				status = NodeHelper.doHttpPost("http://"+ ConfService.getInstance().getMaster_ip() +":"+ ConfService.getInstance().getMaster_internal_port() + "/restlet/test", jsonData);
			} 
			
			JSONObject json = NodeHelper.stringToJSON(jsonData);
			String key = (String) json.get(InstanceInfo.KEY);
			String value = (String) json.get(InstanceInfo.VALUE);

			map.put(NodeHelper.NODE_IP, ConfService.getInstance().getCurrentIP());
			map.put(NodeHelper.VERSION, "1");
			map.put(NodeHelper.ACTION, "WRITE (KEY, VALUE)");
			if (status == 404) {
				map.put(NodeHelper.STATUS, "WRITE FAILED");
			} else {
				map.put(NodeHelper.STATUS, "200 SUCCESS");
				map.put(NodeHelper.KEY, key);
				map.put(NodeHelper.VALUE, value);
			}			
 
		} catch (ResourceException e) {
			System.out.println("ERROR: Write not successful");
		}
		return NodeHelper.createJSONStr(map);
	}
	
	
	@Delete
	public String deleteData() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		try {
			String key = (String) this.getRequestAttributes().get(InstanceInfo.KEY);
			String masterIP = ConfService.getInstance().getMaster_ip();
			int masterInternalPort = ConfService.getInstance().getMaster_internal_port();

			map.put(NodeHelper.NODE_IP, ConfService.getInstance().getCurrentIP());
			map.put(NodeHelper.VERSION, "1");					
			map.put(NodeHelper.ACTION, "DELETE (KEY)");

			if (NodeHelper.pingHost(masterIP, masterInternalPort, 1000)) {
				NodeHelper.doHttpDelete("http://"+  masterIP+":"+ masterInternalPort + "/restlet/test/" + key);
				map.put(NodeHelper.STATUS, "200 SUCCESS");
			} else {
				map.put(NodeHelper.STATUS, "DELETE FAILED");
			}
			map.put(NodeHelper.KEY, key);
		} catch (Exception e) {
			System.out.println("ERROR: Delete not successful");
			e.printStackTrace();			
		}
		return NodeHelper.createJSONStr(map);
	}	
	
	@Put
	public String update(String jsonData) throws Exception {		
		Map<String, String> map = new LinkedHashMap<String, String>();
		try {
			System.out.println("Write from Client Data Handler");
			int status = 404;
			String masterIP = ConfService.getInstance().getMaster_ip();
			int masterInternalPort = ConfService.getInstance().getMaster_internal_port();
			JSONObject json = NodeHelper.stringToJSON(jsonData);
			String key = (String) json.get(InstanceInfo.KEY);
			String value = (String) json.get(InstanceInfo.VALUE);

			map.put(NodeHelper.NODE_IP, ConfService.getInstance().getCurrentIP());
			map.put(NodeHelper.VERSION, "1");					
			map.put(NodeHelper.ACTION, "UPDATE (KEY, NEW VALUE)");
			if (NodeHelper.pingHost(masterIP, masterInternalPort, 1000)) {
				status = NodeHelper.doHttpPut("http://" + masterIP + ":" + masterInternalPort + "/restlet/test", jsonData);
				if (status == 200) {
					map.put(NodeHelper.STATUS, "200 SUCCESS");
					map.put(NodeHelper.KEY, key);
					map.put(NodeHelper.VALUE, value);
				} else {
					map.put(NodeHelper.STATUS, "UPDATE FAILED");
				}					
			}

		} catch (Exception e) {
			System.out.println("ERROR: Update not successful");
			e.printStackTrace();
		}
		return NodeHelper.createJSONStr(map);
	}	
}
