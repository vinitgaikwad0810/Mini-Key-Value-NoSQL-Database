

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

public class InternalDataHandler extends ServerResource{
	SM sm = SMFactory.getSM();
	DataMapper mapper = DataMapper.getInstance();

	@Get
	public String read() {
		String key = (String) this.getRequestAttributes().get(InstanceInfo.KEY);
		SM.OID oid = mapper.get(key);		
		Record fetched;
		try {
			fetched = (Record) sm.fetch(oid);
			String value = new String(fetched.getBytes(0, 0));
			System.out.println("Write from internal Data Handler");
			return value;
		} catch (Exception e) {
			e.printStackTrace();			
		}
		return null;		
	}
	
	@Post
	public String write(String jsonData) {
		try {
			JSONObject json = NodeHelper.stringToJSON(jsonData);
			String key = (String) json.get(InstanceInfo.KEY);
			String value = (String) json.get(InstanceInfo.VALUE);
		
			Record record = new Record(20);
			record.setBytes(value.getBytes());
		
			SM.OID oid = sm.store(record);				
			mapper.put(key, oid);
			
			int status = 404;
			if (ConfService.getInstance().isMaster()) {
				//TODO different behavior for AP & CP in case of parition
				String slave_ip1 = ConfService.getInstance().getSlave1_ip();
				String slave_ip2 = ConfService.getInstance().getSlave2_ip();
				int sPort1 = ConfService.getInstance().getSlave1_internal_port();
				int sPort2 = ConfService.getInstance().getSlave2_internal_port();
								
				if (NodeHelper.pingHost(slave_ip1, sPort1, 1000)) {
					 status = NodeHelper.doHttpPost("http://"+ slave_ip1 +":"+ sPort1 + "/restlet/test", jsonData);
					 if (mapper.isBackupAvailable()) {
						 for (Map.Entry<String, String> entry : mapper.getBackup().entrySet()) {
							 String rJson = "{\"key\":\"" + entry.getKey()+ "\",\"value\":\"" + entry.getValue() + "\"}";
							 status = NodeHelper.doHttpPost("http://"+ slave_ip1 +":"+ sPort1 + "/restlet/test", rJson);
						 }
						 mapper.clearBackup();
						 System.out.println("Replicated data from MASTER to SLAVE1");
					 }

				} else {
					mapper.addBackup(key, value);
					System.out.println("SLAVE1 can not be reached from MASTER");
				}
				
				if (NodeHelper.pingHost(slave_ip2, sPort2, 1000)) {
					 status = NodeHelper.doHttpPost("http://"+ slave_ip2 +":"+ sPort2 + "/restlet/test", jsonData);
					 if (mapper.isBackupAvailable()) {
						 for (Map.Entry<String, String> entry : mapper.getBackup().entrySet()) {
							 String rJson = "{\"key\":\"" + entry.getKey()+ "\",\"value\":\"" + entry.getValue() + "\"}";
							 status = NodeHelper.doHttpPost("http://"+ slave_ip2 +":"+ sPort2 + "/restlet/test", rJson);
						 }
						 mapper.clearBackup();
						 System.out.println("Replicated data from MASTER to SLAVE2");
					 }
				} else {
					mapper.addBackup(key, value);
					System.out.println("SLAVE2 can not be reached from MASTER");
				}				
			}
			System.out.println("Write from internal Data Handler");
			return status + "";

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Delete
	public void deleteData() {
		try {
			String key = (String) this.getRequestAttributes().get("key");
			SM.OID oid = mapper.get(key);		
			sm.delete(oid);
			if (ConfService.getInstance().isMaster()) {
				String slave_ip1 = ConfService.getInstance().getSlave1_ip();
				String slave_ip2 = ConfService.getInstance().getSlave2_ip();
				int sPort1 = ConfService.getInstance().getSlave1_internal_port();
				int sPort2 = ConfService.getInstance().getSlave2_internal_port();
				if (NodeHelper.pingHost(slave_ip1, sPort1, 1000)) {
					NodeHelper.doHttpDelete("http://"+ ConfService.getInstance().getSlave1_ip() +":"+ ConfService.getInstance().getSlave1_internal_port() + "/restlet/test/" + key);
				} else {
					System.out.println("SLAVE1 can not be reached from MASTER");
				}
				
				if (NodeHelper.pingHost(slave_ip2, sPort2, 1000)) {
					NodeHelper.doHttpDelete("http://"+ ConfService.getInstance().getSlave2_ip() +":"+ ConfService.getInstance().getSlave2_internal_port() + "/restlet/test/" + key);
				} else {
					System.out.println("SLAVE2 can not be reached from MASTER");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	@Put
	public String update(String jsonData) throws Exception {	
		JSONObject json = NodeHelper.stringToJSON(jsonData);
		String key = (String) json.get(InstanceInfo.KEY);
		String value = (String) json.get(InstanceInfo.VALUE);
		
		Record record = new Record(20);
		record.setBytes(value.getBytes());

		SM.OID oid = mapper.get(key);		
		oid = sm.update(oid, record);
		mapper.put(key, oid);
		if (ConfService.getInstance().isMaster()) {
			String slave_ip1 = ConfService.getInstance().getSlave1_ip();
			String slave_ip2 = ConfService.getInstance().getSlave2_ip();
			int sPort1 = ConfService.getInstance().getSlave1_internal_port();
			int sPort2 = ConfService.getInstance().getSlave2_internal_port();
			if (NodeHelper.pingHost(slave_ip1, sPort1, 1000)) {
				 NodeHelper.doHttpPut("http://"+ ConfService.getInstance().getSlave1_ip() +":"+ ConfService.getInstance().getSlave1_internal_port() + "/restlet/test", jsonData);
			} else {
				System.out.println("SLAVE1 can not be reached from MASTER");
			}
			if (NodeHelper.pingHost(slave_ip2, sPort2, 1000)) {
				 NodeHelper.doHttpPut("http://"+ ConfService.getInstance().getSlave2_ip() +":"+ ConfService.getInstance().getSlave2_internal_port() + "/restlet/test", jsonData);
			} else {
				System.out.println("SLAVE2 can not be reached from MASTER");
			}
		}
		return key;
	}	
}
