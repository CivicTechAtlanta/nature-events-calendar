//import java.io.File;
import java.io.IOException;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*

Compile:
Mac:
javac -cp ".:jsoup-1.11.1.jar" BeltlineScraper.java
Windows:
javac -cp ".;jsoup-1.11.1.jar;" BeltlineScraper.java

Run:
Mac:
java -cp ".:jsoup-1.11.1.jar" BeltlineScraper
Windows:
java -cp ".;jsoup-1.11.1.jar;" BeltlineScraper

*/


public class BeltlineScraper {

	private static final String URL = "https://atlantabeltline.checkfront.com/reserve/?#D20171117";

	public static void main(String[] args) {
/*		Date date = new Date();
		System.out.println("Started testing at " + date.toString());
		boolean successful = false;
		Date successfulDate = null;
		final int timeout = 2000;
		date = new Date();
		if (ping(URL, timeout)) {
			System.out.println("ping worked at " + date.toString());
			successful = true;
			successfulDate = date;
		}else{
			System.out.println("ping failed at " + date.toString());
			if (successful){
				System.out.println("*******************************************");
				System.out.println("Last successful was "+successfulDate.toString());
				System.out.println("*******************************************");
			}
		}*/
		StringBuilder sb = new StringBuilder();

		jsoupTest();
	}

	// from
	// http://stackoverflow.com/questions/3584210/preferred-java-way-to-ping-a-http-url-for-availability/3584332#3584332
/*	private static boolean ping(String url, int timeout) {
		//url = url.replaceFirst("https", "http"); // Otherwise an exception may
													// be thrown on invalid SSL
													// certificates.
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url)
					.openConnection();
			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(timeout);
			connection.setRequestMethod("HEAD");
			int responseCode = connection.getResponseCode();
			System.out.println("responseCode: " + responseCode);
			return (200 <= responseCode && responseCode <= 399);
		} catch (IOException exception) {
			System.out.println("IOException: " + exception);
			return false;
		}
	}*/
	
	private static void jsoupTest(){
		try {

            Response res = Jsoup.connect(URL).timeout(7000).execute(); 
            //anything less than 6000 times out for me during normal network conditions; 
            //7000 is safer during heavy traffic

            Document doc = res.parse();
            /*
            Element ele = doc.select("div[class=cf-item-list]").first();
            if (ele == null){
            	System.out.println("ele is null");
            	return;
            }
            Elements lines = ele.select("div");
            for (Element elt : lines) {
                System.out.println(elt.text());
                System.out.println("------------------------");
            }
            */
            if (doc == null){
            	System.out.println("doc is null");
            	return;
            }
//            System.out.println("doc location: " + doc.location()); //url
//            System.out.println("doc baseUri : " + doc.baseUri()); //same
//            System.out.println("doc nodeName​: " + doc.nodeName()); //#document  
//            System.out.println("doc title​: " + doc.title()); //page title

//            System.out.println("doc data​: " + doc.data()); //style tags content
//            System.out.println("doc id​: " + doc.id()); //nothing
//            System.out.println("doc ownText​: " + doc.ownText()); //nothing
//            System.out.println("doc text​: " + doc.text()); //text of the page, no tags
            System.out.println("doc toString​: " + doc.toString()); //"Checking Availability..." redirect page
			
            /*
            Element content = doc.getElementById("cf-items");
            if (content == null){
            	System.out.println("content is null");
            	return;
            }
            
            System.out.println("content text: " + content.text());
			
            System.out.println("content html: " + content.html());
			
            System.out.println("content data: " + content.data());
			
			Elements divs = content.getElementsByTag("div");
            if (divs == null){
            	System.out.println("divs is null");
            	return;
            }
            for (Element elt : divs) {
                System.out.println(elt.text());
                System.out.println("------------------------");
            }
*/
        } catch (IOException c) {
			System.out.println("IOException");
            c.printStackTrace();
        }
	}

}
