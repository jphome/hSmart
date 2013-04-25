package jphome.hsmart;

import jphome.utils.HttpUtil;

public class cmd {
	static boolean rc = true;
	static String url;

	public static boolean login(String username, String password) {
		url = gConfig.URL.BASE_URL + "login?username=" + username
				+ "&password=" + password;
		System.out.println("url=" + url);
		rc = HttpUtil.cmdForGet(url);
		if (false == rc) {
			System.out.println("login failed");
			return false;
		} else {
			System.out.println("login success");
			return true;
		}
	}

	public static void logout(String username) {
		url = gConfig.URL.BASE_URL + "logout?username=" + username;
		System.out.println("url=" + url);
		rc = HttpUtil.cmdForGet(url);
		if (false == rc) {
			System.out.println("logout failed");
		} else {
			System.out.println("logout success");
		}
	}

	public static void adduser(String username, String password) {
		url = gConfig.URL.BASE_URL + "adduser?username=" + username
				+ "&password=" + password;
		System.out.println("url=" + url);
		rc = HttpUtil.cmdForGet(url);
		if (false == rc) {
			System.out.println("adduser failed");
		} else {
			System.out.println("adduser success");
		}
	}

	public static void light(String deviceId, String sensorId, byte status) {
		url = gConfig.URL.BASE_URL + "cmd_light?status=";
		switch (status) {
		case 1:
			url += "on";			
			break;
		case 0:			
			url += "off";
			break;
		}
		url += "&deviceId=" + deviceId;
		url += "&sensorId=" + sensorId;
		System.out.println("url: " + url);
		rc = HttpUtil.cmdForGet(url);
		if (false == rc) {
			System.out.println("set light status failed");
		}
	}

	public static void addDevice(String deviceId, String sensorId, String type) {
		url = gConfig.URL.BASE_URL + "sensorRegister?deviceId=" + deviceId
				+ "&sensorId=" + sensorId + "&sensorType=" + type;
		System.out.println(url);
		rc = HttpUtil.cmdForGet(url);
		if (false == rc) {
			System.out.println("addDevice failed");
		}
	}

	public static void delDevice(String deviceId, String sensorId) {
		url = gConfig.URL.BASE_URL + "sensorUnregister?deviceId=" + deviceId
				+ "&sensorId=" + sensorId;
		System.out.println(url);
		rc = HttpUtil.cmdForGet(url);
		if (false == rc) {
			System.out.println("delDevice failed");
		}
	}

	public static void delNode(String deviceId) {
		url = gConfig.URL.BASE_URL + "nodeUnregister?deviceId=" + deviceId;
		System.out.println(url);
		rc = HttpUtil.cmdForGet(url);
		if (false == rc) {
			System.out.println("delNode failed");
		}
	}
}
