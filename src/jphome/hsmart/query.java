package jphome.hsmart;

import jphome.utils.HttpUtil;

public class query {
	public static String login(String username, String password) {
		String queryString = "username=" + username + "&password=" + password;
		String url = gConfig.URL.BASE_URL + "query_login?" + queryString;
		
		return null;
	}	
}
