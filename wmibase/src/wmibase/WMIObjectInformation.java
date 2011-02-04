package wmibase;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WMIObjectInformation {
	private String path;
	private Map<String,Object> properties;

	public WMIObjectInformation ( String path, Map<String,Object> properties )
	{
		this.path = path;
		this.properties = new HashMap<String, Object> ( properties );
	}
	
	public String getPath() {
		return path;
	}
	
	public Map<String, Object> getProperties() {
		return Collections.unmodifiableMap( properties );
	}
}
