package jphome.hsmart;

public interface gConfig {
	public String html_head = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\" ><html xmlns=\"http://www.w3.org/1999/xhtml\" ><head><title>demo</title><link rel=\"stylesheet\" type=\"text/css\" href=\"http://192.168.2.168/css/demo.css\" /></head><body text=\"green\">";
	public String html_tail = "</body></html>";
	public String db_Name = "hSmartSensor.db";
	public String sensorTableName = "sensorTable";
	public String testTableName = "temperatureTable";
	public String baseURL_Resource ="file:///android_asset/images/";
	
	public class URL {
		public static final String BASE_URL = "http://192.168.1.168:8081/";
		public static final String VIDEO_URL = "http://192.168.1.1:8080/?action=snapshot";
	}
}
