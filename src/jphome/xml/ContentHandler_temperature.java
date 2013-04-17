package jphome.xml;

import jphome.app.app_query_temperature_Activity;
import jphome.hsmart.gConfig;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.ContentValues;

public class ContentHandler_temperature extends DefaultHandler {
	String count, time, deviceId, sensorId, status;
	String tagName;

	public void startDocument() throws SAXException {
		System.out.println("--- begin ---");
	}

	public void endDocument() throws SAXException {
		System.out.println("--- end ---");
	}

	// <abc:name id="dd">Mark</name>
	// 标签属性
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes attr) throws SAXException {
		// 得到前缀 abc 属性
		tagName = localName;
		if (localName.equals("temp")) {
			String time = null;
			String value = null;
			ContentValues pair = new ContentValues();
			// 获取标签的所有属性 属性个数：getLength()
			for (int i = 0; i < attr.getLength(); i++) {
				if (attr.getLocalName(i) == "time") {
					time = attr.getValue(i);
					// System.out.println("time: " + time);
				} else if (attr.getLocalName(i) == "value") {
					value = attr.getValue(i);
					// System.out.println("value: " + value);
				}
			}
			pair.clear();
			pair.put("time", time);
			pair.put("value", value);
			System.out.println("insert to db");
			app_query_temperature_Activity.dbWriteHandle.insert(
					gConfig.testTableName, null, pair);
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
			// 中止xml解析
			throw new SAXException("count值为0");
		} else {
			System.out.println("delete all in temperatureTable");
			app_query_temperature_Activity.dbWriteHandle.delete(
					gConfig.testTableName, null, null);
		}
	}
}
