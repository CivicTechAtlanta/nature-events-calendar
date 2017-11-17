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

Ugh, linked text in the description:
https://uxatl.wildapricot.org/event-2662847
Inconsistent existence of price:
https://uxatl.wildapricot.org/event-2733109?CalendarViewType=0&SelectedDate=11/16/2017
Multiple date lists:
https://uxatl.wildapricot.org/page-18297
https://uxatl.wildapricot.org/calendar

Maybe later. Until then, this is a handy placeholder.

==========================================================================================

This program scrapes the Urban Explorers of Atlanta site for events.

Any questions? Contact the author here:
dstrube@gmail.com

Compile:
Mac:
javac -cp ".:jsoup-1.11.1.jar:joda-time-2.9.9.jar" UrbanExplorersAtlantaScraper.java
Windows:
javac -cp ".;jsoup-1.11.1.jar;joda-time-2.9.9.jar;" UrbanExplorersAtlantaScraper.java

Run:
Mac:
java -cp ".:jsoup-1.11.1.jar:joda-time-2.9.9.jar" UrbanExplorersAtlantaScraper
Windows:
java -cp ".;jsoup-1.11.1.jar;joda-time-2.9.9.jar;" UrbanExplorersAtlantaScraper

*/


public class UrbanExplorersAtlantaScraper {


	public static void main(String[] args) {

	}

}
