//import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
Problem: Getting the calendar data for this site returns a redirect page. 
Not sure how to handle that at this time.

==========================================================================================

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
		StringBuilder sb = new StringBuilder();

		jsoupTest();
	}

	private static void jsoupTest(){
		try {

            Response res = Jsoup.connect(URL).timeout(8000).execute(); 
            //anything less than 6000 times out for me during normal network conditions; 
            //8000 is safer during heavy traffic

            Document doc = res.parse();

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
			
        } catch (IOException c) {
			System.out.println("IOException");
            c.printStackTrace();
        }
	}

}
