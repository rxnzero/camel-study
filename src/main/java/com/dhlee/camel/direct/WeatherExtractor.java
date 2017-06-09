package com.dhlee.camel.direct;

import javax.xml.transform.TransformerException;

import com.sun.org.apache.xpath.internal.XPathAPI;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

public class WeatherExtractor {
	
	 public void print(Document doc) throws TransformerException {
		 String xpath = "//wid/body/location[@city='11B10101']";
		 NodeIterator locations = XPathAPI.selectNodeIterator(doc, xpath);
		 
		 Node location;
		 while ((location = locations.nextNode()) != null) {
			 NodeIterator datas = XPathAPI.selectNodeIterator(location, "data");
			 Node data;
			 while ((data = datas.nextNode()) != null) {
				 System.err.println(getText(data, "numEf") + "일후 예보");
				 System.err.println("\t날짜: " + getText(data, "tmEf"));
				 System.err.println("\t날씨: " + getText(data, "wf"));
				 System.err.println("\t최저온도 : " + getText(data, "tmn") + " 도");
				 System.err.println("\t최고온도: " + getText(data, "tmx") + " 도");
				 System.err.println("\t신뢰도: " + getText(data, "reliability"));
			 }
		 }
	 }
	 
	 private String getText(Node data, String xpath) throws TransformerException {
		 return XPathAPI.selectSingleNode(data, xpath).getTextContent();
	 }
}
