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

This java program scrapes the REI site for events around zip code 30306, 
starting with page 1, and going until there is no Next page.

The program outputs to file "rei.csv" with a header line.

If the file already exists before running the program, user is prompted for 
whether they want to overwrite the file.

Using the 3rd party Jsoup for scraping, if timeouts occur due to slow internet, 
increase the JSOUP_TIMEOUT. (If it's going to slowly, feel free to decrease it, 
but be aware that 6000 seems to be the lower limit.)

Using 3rd party Joda time: because it's better than Java's time libraries.

TODO: The date parsing logic doesn't take into account the possibility of 
an event that starts in one year and ends in another, or one-day events happening next year.
Solution: if event month < this month => next year

Lots of debugging statements have been commented out. 
Feel free to uncomment them if you want to troubleshoot something or just know what it's doing.

Any questions? Contact the author here:
dstrube@gmail.com

Compile:
Mac:
javac -cp bin/joda-time-2.9.9.jar -d bin IScraperRow.java 
javac -cp bin:bin/joda-time-2.9.9.jar:bin/jsoup-1.11.1.jar -d bin ReiScraper.java
Windowsx [unverified]:
javac -cp bin\joda-time-2.9.9.jar -d bin IScraperRow.java 
javac -cp bin;bin\joda-time-2.9.9.jar;bin\jsoup-1.11.1.jar; -d bin ReiScraper.java

Run:
Mac:
java -cp bin:bin/joda-time-2.9.9.jar:bin/jsoup-1.11.1.jar ReiScraper
Windows [unverified]:
java -cp bin;bin\joda-time-2.9.9.jar;bin\jsoup-1.11.1.jar; ReiScraper

*/


public class ReiScraper {

	private static final String URL1 = "https://www.rei.com/events/p/us-ga-atlanta/a/all-activities?page=";
	private static final String URL2 = "&previousLocation=30306";
	private static int pageNumber = 1;
	private static boolean isNextPageAvailable = true;
	private static final List<ReiScraperRow> reiScraperRows = new ArrayList<>();
	private static final int JSOUP_TIMEOUT = 8000;

	public static void main(String[] args) {

		//Verify the output file doesn't exist before wasting resources getting the data
		final String fileName = "rei.csv";
		final Path path = Paths.get(fileName);
		try{
			if (Files.exists(path)){
				System.out.println("File already exists. Overwrite? (y/n)");
				final Scanner input = new Scanner(System.in);
				final String answer = input.next().toLowerCase();
				if (!answer.equals("y")){
					System.out.println("I didn't get a 'y', so I'll let you sort this out before proceeding.");
					return;
				}
				//Don't delete the file just yet, in case something breaks while getting the data
				//Files.delete(path);
			}
		}catch(Exception e){
			System.out.println("Exception");
            e.printStackTrace();
            return;
		}
		
		//Get the data
		while (isNextPageAvailable){
			if (!getNextPage()) return;
			pageNumber++;
			//System.out.println("isNextPageAvailable: " + isNextPageAvailable);
		}

		//Verify we got something
		if (reiScraperRows.size() == 0){
			System.out.println("Error: No results found on site.");
			return;
		}

		//Format the data
		final StringBuilder sb = new StringBuilder();
		final String headerRow = "Organization/organizer,Title,Description (optional),URL of event,Location,"
			+ "\"Category (e.g. hiking, birding, volunteering, class)\",Start Date,End Date (if multi-day),"
			+ "Start Time,End Time,Free or paid?,RSVP info,Age group (if specified),Dog-friendly (if specified),"
			+ "Indoor or outdoor?,Imported to Google Calendar";
		sb.append(headerRow);
		sb.append("\n");
		for(ReiScraperRow row : reiScraperRows){
			sb.append(row.organizer);
			sb.append(",");
			sb.append(row.title);
			sb.append(",");
			sb.append(row.description);
			sb.append(",");
			sb.append(row.url);
			sb.append(",");
			sb.append(row.location);
			sb.append(",");
			//no category sb.append(row);
			sb.append(",");
			sb.append(row.startDate);
			sb.append(",");
			if (row.endDate != null) sb.append(row.endDate);
			sb.append(",");
			sb.append(row.startTime.toString("HH:mm"));
			sb.append(",");
			sb.append(row.endTime.toString("HH:mm"));
			sb.append(",");
			sb.append(row.cost);
			sb.append(",");
			sb.append(row.url); //RSVP Info
			sb.append(",");
			//no age group sb.append(row);
			sb.append(",");
			//no dog friendly sb.append(row);
			sb.append(",");
			//no indoor / outdoor sb.append(row);
			sb.append(",");
			sb.append("No");//imported to Google Calendar
			sb.append("\n");
		}
		
		//System.out.println("output: " + sb.toString());
		
		//Write the data to file		
		try{
			if (Files.exists(path)){
				Files.delete(path);
			}
			Files.createFile(path);
			final byte[] bytes = sb.toString().getBytes(); 
		    Files.write(path, bytes);
		    
		    System.out.println("Wrote output to " + fileName);
		    
		}catch(Exception e){
			System.out.println("Exception");
            e.printStackTrace();
		}
	}

	private static boolean getNextPage(){
		try {
			final String URL = URL1 + pageNumber + URL2;
            final Response res = Jsoup.connect(URL).timeout(JSOUP_TIMEOUT).execute(); 
            //anything less than 6000 times out for me during normal network conditions; 
            //8000 is safer during heavy traffic

            final Document doc = res.parse();

            if (doc == null){
            	System.out.println("Error: doc is null on page " + pageNumber);
            	return false;
            }
			
            final Element content = doc.getElementById("course-results");
            if (content == null){
            	System.out.println("Error: content is null on page " + pageNumber);
            	return false;
            }
            
            //System.out.println("pageNumber: " + pageNumber);
            
            final Elements endElements = content.getElementsByClass("icon-rei-right-arrow");
            if (!verifyAtLeastOne(endElements, "endElements")){
            	return false;
            }
            final String endDisabled = endElements.get(0).attr("disabled");
            if (endDisabled != null && endDisabled.equals("disabled")){
            	isNextPageAvailable = false;
            }
            
			final Elements rows = content.getElementsByClass("card card--parent vertical-push");
            if (rows == null){
            	System.out.println("Error: rows is null on page " + pageNumber);
            	return false;
            }
//            System.out.println("rows size: " + rows.size());	
            		
            for (final Element row : rows) {
            	//Even though we're getting a list, it's expected to have only 1 element
            	final Elements titleElement = row.getElementsByClass("card-title");
            	if (!verifyOne(titleElement, "title")){
	            	return false;
            	}
            	final String title = titleElement.get(0).text();
                //System.out.println("title: " + title);
                
            	//Even though we're getting a list, we're only interested in the first element
                final Elements descriptionElement = row.getElementsByClass("card-text");
            	if (!verifyAtLeastOne(descriptionElement, "description")){
	            	return false;
            	}
            	
            	final String description = escapeCommasAndQuotes(descriptionElement.get(0).text());
            	
            	//System.out.println("description: " + description);
            	
            	//collection of date elements
            	final Elements dateCollectionElement = row.getElementsByClass("card-block date-card");
               	if (!verifyOne(dateCollectionElement, "dateCollectionElement")){
	            	return false;
            	}
            	
            	final Elements dateTileElements = dateCollectionElement.get(0).getElementsByClass("tile");
               	if (!verifyAtLeastOne(dateTileElements, "dates")){
	            	return false;
            	}
            	
            	for (final Element dateTileElement : dateTileElements){
            		final Elements dateElements = dateTileElement.getElementsByClass("text-muted");
            		if (!verifyAtLeastOne(dateElements, "date")){
	            		return false;
            		}
            		
            		final Elements urlElements = dateTileElement.getElementsByClass("link_header-explore");
            		if (!verifyAtLeastOne(urlElements, "url")){
	            		return false;
            		}
            		
            		final Elements locationElements = dateTileElement.getElementsByClass("event__location");
            		if (!verifyAtLeastOne(locationElements, "location")){
	            		return false;
            		}
            		
            		final Elements timesElements = dateTileElement.getElementsByClass("event__time");
            		if (!verifyAtLeastOne(timesElements, "times")){
	            		return false;
            		}
            		
            		final Elements costElements = dateTileElement.getElementsByClass("event__price");
            		if (!verifyAtLeastOne(costElements, "cost")){
	            		return false;
            		}
            		            							
					for(int i = 0; i < dateElements.size(); i++){
	            		
	            		final String location = escapeCommasAndQuotes(locationElements.get(i).text());

	            		//http://www.joda.org/joda-time/apidocs/org/joda/time/format/DateTimeFormat.html
						final LocalDate startDate = LocalDate.parse(dateElements.get(i).text(), 
							DateTimeFormat.forPattern("MMM d").withDefaultYear(new LocalDate().getYear()));
							//TODO: months < this month => next year
						//System.out.println("startDate: " + startDate);
						
						LocalDate endDate = null;
						String times = timesElements.get(i).text();
						if (containsNonTimeChar(times)){
							//Start and end times are followed by an end date
							//Set end date
							endDate = getEndDate(times);
							//Remove end date
							times = trimEndDate(times);
						}
						
						times = times.replace(" ","");
						final String[] timeArray = times.split("-");
						if (timeArray.length == 0){
			            	System.out.println("Error: No times found on page " + pageNumber);
        					return false;
						}

						final LocalTime startTime = LocalTime.parse(timeArray[0], DateTimeFormat.forPattern("h:ma"));
						//System.out.println("time: " + time.toString("H:m"));

						final LocalTime endTime;
						if (timeArray.length == 1){
			            	System.out.println("Warning: No end time found on page " + pageNumber);
		    	        	endTime = startTime.withHourOfDay(startTime.getHourOfDay() + 1);
						}else{
							endTime = LocalTime.parse(timeArray[1], DateTimeFormat.forPattern("h:ma"));
						}
												
						//System.out.println("date: " + dateElements.get(i).text());
						ReiScraperRow reiScraperRow = new ReiScraperRow();
						reiScraperRow.title = title;
						reiScraperRow.description = description;
						reiScraperRow.url = urlElements.get(i).attr("href");;
						reiScraperRow.location = location;
						reiScraperRow.startDate = startDate;
						reiScraperRow.endDate = endDate;
						reiScraperRow.startTime = startTime;
						reiScraperRow.endTime = endTime;
						reiScraperRow.cost = costElements.get(i).text();
						reiScraperRows.add(reiScraperRow);
					}
            	}
            }

        } catch (Exception e) {
			System.out.println("Exception on page " + pageNumber);
            e.printStackTrace();
            return false;
        }
        return true;
	}
	
	private static boolean verifyOne(final Elements element, final String fieldName){
        if (!verifyAtLeastOne(element, fieldName)) return false;
        
       	if (element.size() > 1){
            System.out.println("Error: " + fieldName + " is bigger than expected: " + element.size());
            for (Element e : element)
            	System.out.println(fieldName + " : " + e.text());
            System.out.println("on page " + pageNumber);
    		return false;
        }
        return true;
	}
	
	private static boolean verifyAtLeastOne(final Elements element, final String fieldName){
		if (element == null){
    		System.out.println("Error: " + fieldName + " is null on page " + pageNumber);
            return false;
       	}
       	if (element.size() == 0){
            System.out.println("Error: " + fieldName + " is empty on page " + pageNumber);
    		return false;
        }
        return true;
	}
	
	private static boolean containsNonTimeChar(final String times){
		final String timeChars = "1234567890:-amp ";
		for(char c : times.toCharArray()){
			if (timeChars.indexOf(c) == -1)
				return true;
			//else
			//	System.out.println("index of " + c + " : " + timeChars.indexOf(c));
		}
		return false;
	}
	
	private static LocalDate getEndDate(final String times){
		final int lastTimeCharIndex = lastTimeCharIndex(times);
		String endDateStr = times.substring(lastTimeCharIndex + 2);
		//System.out.println("endDateStr: " + endDateStr);
		String[] endDates = endDateStr.split("-");
		final LocalDate endDate = LocalDate.parse(endDates[1].trim(), 
			DateTimeFormat.forPattern("MMM d").withDefaultYear(new LocalDate().getYear()));
		//TODO: months < this month => next year
		return endDate;
	}
	
	private static String trimEndDate(String times){
		final int lastTimeCharIndex = lastTimeCharIndex(times);
		times = times.substring(0, lastTimeCharIndex + 2);
		//System.out.println("trimEndDate: " + times);
		return times;
	}
	
	private static int lastTimeCharIndex(final String times){
		final int indexOfLastAMChar = times.lastIndexOf("am");
		final int indexOfLastPMChar = times.lastIndexOf("pm");
		if (indexOfLastAMChar > indexOfLastPMChar){
			return indexOfLastAMChar;
		}
		else{
			return indexOfLastPMChar;
		}
	}
	
	private static String escapeCommasAndQuotes(String input){
		if (input.indexOf("\"") != -1){
			input = input.replace("\"", "\"\"");
		}
		if (input.indexOf(",") != -1){
			input = "\"" + input + "\"";
		}
		return input;
	}

	private static class ReiScraperRow implements IScraperRow{
		public final String organizer = "REI";
		public String title;
		public String description;
		public String url;
		public String location;
		public LocalDate startDate;
		public LocalDate endDate;
		public LocalTime startTime;
		public LocalTime endTime;
		public String cost;
		//public String rsvpInfo; // same as url
		public ReiScraperRow(){}
	}
}
