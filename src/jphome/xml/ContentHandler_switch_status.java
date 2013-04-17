package jphome.xml;

import jphome.app.app_query_sub_Activity;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.widget.Toast;

public class ContentHandler_switch_status extends DefaultHandler {
	String count, time, deviceId, sensorId, status;
	String tagName;

	public void startDocument() throws SAXException {
		System.out.println("--- begin ---");
		app_query_sub_Activity.html_content = "";
		app_query_sub_Activity.html_content += "<h1>switch_status</h1><div>";
	}

	public void endDocument() throws SAXException {
		System.out.println("--- end ---");
		app_query_sub_Activity.html_content += "</div>";
	}

	// <abc:name id="dd">Mark</name>
	// 没有前缀
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes attr) throws SAXException {
		// 得到前缀 abc 属性
		tagName = localName;
		if (localName.equals("switch")) {
			// 获取标签的所有属性 属性个数：getLength()
			for (int i = 0; i < attr.getLength(); i++) {
				if (attr.getLocalName(i) == "status") {
					if (attr.getValue(i).equals("1")) {
						app_query_sub_Activity.html_content += "<img src=\"light_on.png\" style=\"vertical-align:right;\" />"
								+ "<a onclick=\"window.cmd.light(0)\" >"
								+ "<img src=\"switch_off.png\" alt=\"a ha\" />"
								+ "</a>";
						System.out.println("light_on");
					} else {
						app_query_sub_Activity.html_content += "<img src=\"light_off.png\" style=\"vertical-align:right;\" />"
								+ "<a onclick=\"window.cmd.light(1)\" >"
								+ "<img src=\"switch_on.png\" alt=\"a ha\" />"
								+ "</a>";
						System.out.println("light_off");
					}
				} else if (attr.getLocalName(i) == "deviceId") {
					String tmp = "<a>deviceId:";
					tmp += attr.getValue(i);
					tmp += "</a>";
					app_query_sub_Activity.html_content += tmp;
				} else if (attr.getLocalName(i) == "sensorId") {
					String tmp = "<a>sensorId:";
					tmp += attr.getValue(i);
					tmp += "</a>";
					app_query_sub_Activity.html_content += tmp;

					app_query_sub_Activity.html_content += "<br />";
				}
				// System.out.println(attr.getLocalName(i) + "=" +
				// attr.getValue(i));
			}
		}
	}

	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		tagName = "";
		// 在worker标签解析完之后，会打印出所有得到的数据
		if (localName.equals("switch")) {
			// System.out.println("switch end");
			// this.printout();
		}
	}

	// 得到标签内容 Mark
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (tagName.equals("count")) {
			count = new String(ch, start, length);
		}
		System.out.println("count: " + count);
		if (count.equals("0")) {
			app_query_sub_Activity.html_content += "</div>";
			// 中止xml解析
			throw new SAXException("count值为0");
		}
	}
}
