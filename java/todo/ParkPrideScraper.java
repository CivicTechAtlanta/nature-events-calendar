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

Inconsistent existence of price:
https://parkpride.org/event/small-change-grant-workshop/
https://parkpride.org/event/mayoral-forum-on-greenspace-runoff-edition-2/
[most pages I skimmed don't have a cost]

Maybe later. Until then, this is a handy placeholder.

==========================================================================================

This program scrapes the Park Pride site for events.

Any questions? Contact the author here:
dstrube@gmail.com

Compile:
Mac:
javac -cp ".:jsoup-1.11.1.jar:joda-time-2.9.9.jar" ParkPrideScraper.java
Windows:
javac -cp ".;jsoup-1.11.1.jar;joda-time-2.9.9.jar;" ParkPrideScraper.java

Run:
Mac:
java -cp ".:jsoup-1.11.1.jar:joda-time-2.9.9.jar" ParkPrideScraper
Windows:
java -cp ".;jsoup-1.11.1.jar;joda-time-2.9.9.jar;" ParkPrideScraper

*/


public class ParkPrideScraper {


	public static void main(String[] args) {

	}

}
