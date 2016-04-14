import java.util.HashMap;
import java.util.Map;

public class DataMapper {
	
	private static DataMapper instance = null;
	Map<String, SM.OID> map = new HashMap<String, SM.OID>();
	Map<String, String> backup = new HashMap<String, String>();
	
	public static DataMapper getInstance() {
		if (instance == null) {
			instance = new DataMapper();
		}
		return instance;
	}
	
	private DataMapper() {		
	}
	
	public void put(String key, SM.OID oid) {
		map.put(key, oid);
	}
	
	public SM.OID get(String key) {
		return map.get(key);
	}

	public Map<String, String> getBackup() {
		return backup;
	}
	
	public String getBkpValue(String key) {
		return backup.get(key);
	}
	
	public void clearBackup() {
		backup.clear();
	}
	
	public Boolean isBackupAvailable() {
		return map.size() > 0;
	}
	
	public void addBackup(String key, String value) {
		backup.put(key, value);
	}
	

}
