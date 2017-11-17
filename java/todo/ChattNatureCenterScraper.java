import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
INCOMPLETE

No single page for all events 
No url per event
TODO: Confirm:
I suspect moving from one month to another would have same redirect problem as BeltlineScraper

http://www.chattnaturecenter.org/calendar-events/

Maybe later. Until then, this is a handy placeholder.

==========================================================================================

This program scrapes the Chattahoochee Nature Center site for events.

Any questions? Contact the author here:
dstrube@gmail.com

Compile:
Mac:
javac -cp ".:jsoup-1.11.1.jar:joda-time-2.9.9.jar" ChattNatureCenterScraper.java
Windows:
javac -cp ".;jsoup-1.11.1.jar;joda-time-2.9.9.jar;" ChattNatureCenterScraper.java

Run:
Mac:
java -cp ".:jsoup-1.11.1.jar:joda-time-2.9.9.jar" ChattNatureCenterScraper
Windows:
java -cp ".;jsoup-1.11.1.jar;joda-time-2.9.9.jar;" ChattNatureCenterScraper

*/


public class ChattNatureCenterScraper {


	public static void main(String[] args) {

	}

}
