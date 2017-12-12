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

This java program scrapes the SORBA Atlanta site for events, 
starting with page 1, and going until there is no Next page.

The program outputs to file "sorba.csv" with a header line.

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
javac -cp bin:bin/joda-time-2.9.9.jar:bin/jsoup-1.11.1.jar -d bin SorbaScraper.java
Windows [unverified]:
javac -cp bin\joda-time-2.9.9.jar -d bin IScraperRow.java 
javac -cp bin;bin\joda-time-2.9.9.jar;bin\jsoup-1.11.1.jar; -d bin SorbaScraper.java

Run:
Mac:
java -cp bin:bin/joda-time-2.9.9.jar:bin/jsoup-1.11.1.jar SorbaScraper
Windows [unverified]:
java -cp bin;bin\joda-time-2.9.9.jar;bin\jsoup-1.11.1.jar; SorbaScraper

*/


public class SorbaScraper {
	//As of last commit, all the upcoming events are on the same page. 
	//If that changes, this logic will need to change.
	private static final String URL = "http://sorbaatlanta.org/events/list/?tribe_paged=1&tribe_event_display=list";
	private static int eventNumber = 1;
	private static boolean isNextEventAvailable = true;
	private static final List<SorbaScraperRow> sorbaScraperRows = new ArrayList<>();
	private static final int JSOUP_TIMEOUT = 8000;

	public static void main(String[] args) {
		//Verify the output file doesn't exist before wasting resources getting the data
		final String fileName = "sorba.csv";
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
		Elements content;
		try {
            final Response response = Jsoup.connect(URL).timeout(JSOUP_TIMEOUT).execute(); 
            //anything less than 6000 times out for me during normal network conditions; 
            //8000 is safer during heavy traffic

            final Document document = response.parse();

            if (document == null){
            	System.out.println("Error: document is null.");
            	return;
            }
			
            content = document.getElementsByClass("tribe-events-loop");
            if (!verifyOne(content, "content")){
            	return;
            }
        } catch (Exception e) {
			System.out.println("Exception at event " + eventNumber);
            e.printStackTrace();
            return;
        }

		while (isNextEventAvailable){
			if (!getNextEvent(content.get(0))) {
				//Any error encountered in getNextEvent will have already been printed by now
				return;
			}
			eventNumber++;
			//System.out.println("isNextEventAvailable: " + isNextEventAvailable);
		}

		//Verify we got something
		if (sorbaScraperRows.size() == 0){
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
		for(SorbaScraperRow row : sorbaScraperRows){
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
			if (row.startTime != null) sb.append(row.startTime.toString("HH:mm"));
			sb.append(",");
			if (row.endTime != null) sb.append(row.endTime.toString("HH:mm"));
			sb.append(",");
			sb.append(row.cost);
			sb.append(",");
			//No RSVP Info sb.append(row.url);
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
/*
		//Format the data
		final StringBuilder sb = new StringBuilder();
		final String headerRow = "Organization/organizer,Title,Description (optional),URL of event,Location,"
			+ "Category (e.g. hiking, birding, volunteering, class),Start Date,End Date (if multi-day),"
			+ "Start Time,End Time,Free or paid?,RSVP info,Age group (if specified),Dog-friendly (if specified)"
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
		}*/
	}

	private static boolean getNextEvent(final Element content){
		final Elements titleElements = content.getElementsByClass("tribe-events-list-event-title");
		if (!verifyAtLeastOne(titleElements, "titles")){
			return false;
		}
		if (titleElements.size() == eventNumber){
			isNextEventAvailable = false;
			//System.out.println("Getting final event, #" + eventNumber);
		}else{
			//System.out.println("Getting event #" + eventNumber);
		}
		
		final String title = titleElements.get(eventNumber - 1).text();
		
		final Elements urlElements = titleElements.get(eventNumber - 1).getElementsByClass("tribe-event-url");
    	if (!verifyOne(urlElements, "url")){
        	return false;
        }
        
        final String eventUrl = urlElements.get(0).attr("href");
        
        final Element parent = titleElements.get(eventNumber - 1).parent();
        
        //Date and times
        final Elements startElements = parent.getElementsByClass("tribe-event-date-start");
        if (!verifyOne(startElements, "start date and time")){
        	return false;
        }
        final String startDateAndTime = startElements.get(0).text();
        final String startDateInput = startDateAndTime.split("@")[0].trim();
        final String startTimeInput = startDateAndTime.split("@")[1].trim();
        //System.out.println("startDateInput:" + startDateInput + ";startTimeInput:" + startTimeInput+";");
        final Elements endElements = parent.getElementsByClass("tribe-event-time");
		if (!verifyOne(endElements, "end time")){
        	return false;
        }
        final String endTimeInput = endElements.get(0).text();
        final LocalDate startDate;
        if (startDateInput.indexOf(",") == -1){
        //http://www.joda.org/joda-time/apidocs/org/joda/time/format/DateTimeFormat.html
        	startDate  = LocalDate.parse(startDateInput, DateTimeFormat.forPattern("MMMM d").withDefaultYear(new LocalDate().getYear()));
        }else{
        	startDate  = LocalDate.parse(startDateInput, DateTimeFormat.forPattern("MMMM d, Y"));
        }
        
        final LocalDate endDate = null; //If there is an event with an end date, do it here
        final LocalTime startTime = LocalTime.parse(startTimeInput, DateTimeFormat.forPattern("h:m a"));
        //System.out.println("startTime:" + startTime.toString("HH:mm") + ";");
        final LocalTime endTime = LocalTime.parse(endTimeInput, DateTimeFormat.forPattern("h:m a"));
        
        //Location
        final Elements locationElements = parent.getElementsByClass("tribe-events-venue-details");
        if (!verifyOne(locationElements, "location")){
        	return false;
        }
        String location = null;
        if (locationElements.get(0).text().length() > 0){
        	final String locationTitle = locationElements.get(0).text().split(",")[0];
	        //System.out.println("location title:" + locationTitle + ";");
	        //Address
	        final Elements addressElements = parent.getElementsByClass("tribe-street-address");
	        if (!verifyOne(addressElements, "address")){
        		return false;
        	}
	        final String address = addressElements.get(0).text();
	        //City
	        final Elements cityElements = parent.getElementsByClass("tribe-locality");
	        if (!verifyOne(cityElements, "city")){
        		return false;
        	}
	        final String city = cityElements.get(0).text();
	        //State
	        final Elements stateElements = parent.getElementsByClass("tribe-region");
	        if (!verifyOne(stateElements, "state")){
        		return false;
        	}
	        final String state = stateElements.get(0).text();
	        
	        //Zip
	        final Elements zipElements = parent.getElementsByClass("tribe-postal-code");
	        if (!verifyOne(zipElements, "zip")){
        		return false;
        	}
	        final String zip = zipElements.get(0).text();
	        
	        location = escapeCommasAndQuotes(locationTitle + ", " + address + ", " + city + " " + state + " " + zip);
        } 
        else{
            System.out.println("no location listed for event " + eventNumber);
        }
        
        //Description
       	final Elements descriptionElements = parent.getElementsByClass("tribe-events-list-event-description");
		if (!verifyAtLeastOne(descriptionElements, "description")){
	        return false;
        }    	
        final String description;
        if (descriptionElements.get(0).text().indexOf(",") == -1){
        	description = descriptionElements.get(0).text();
        }
        else{
        	description = escapeCommasAndQuotes(descriptionElements.get(0).text());
        }

		SorbaScraperRow sorbaScraperRow = new SorbaScraperRow();
		sorbaScraperRow.title = title;
		sorbaScraperRow.description = description;
		sorbaScraperRow.url = eventUrl;
		sorbaScraperRow.location = (location != null) ? location : "" ;
		sorbaScraperRow.startDate = startDate;
		sorbaScraperRow.endDate = endDate;
		sorbaScraperRow.startTime = startTime;
		sorbaScraperRow.endTime = endTime;
//		sorbaScraperRow.cost = costElements.get(i).text();
		sorbaScraperRows.add(sorbaScraperRow);
	/*
        //Go to the page for each event to get full description 
        //(or is there enough info for each event on the list page?)
		try {
			final Response eventResponse = Jsoup.connect(eventUrl).timeout(JSOUP_TIMEOUT).execute(); 
			
			final Document eventDocument = eventResponse.parse();

            if (eventDocument == null){
            	System.out.println("Error: eventDocument is null at event " + eventNumber);
            	return false;
            }
            
            System.out.println("Got the page for event " + eventNumber);
            	

        } catch (Exception e) {
			System.out.println("Exception at event " + eventNumber);
            e.printStackTrace();
            return false;
        }
	*/
        return true;
	}
	
	private static boolean verifyOne(final Elements element, final String fieldName){
        if (!verifyAtLeastOne(element, fieldName)) return false;
        
       	if (element.size() > 1){
            System.out.println("Error: " + fieldName + " is bigger than expected: " + element.size());
            for (Element e : element)
            	System.out.println(fieldName + " : " + e.text());
            System.out.println("at event " + eventNumber);
    		return false;
        }
        return true;
	}
	
	private static boolean verifyAtLeastOne(final Elements element, final String fieldName){
		if (element == null){
    		System.out.println("Error: " + fieldName + " is null at event " + eventNumber);
            return false;
       	}
       	if (element.size() == 0){
            System.out.println("Error: " + fieldName + " is empty at event " + eventNumber);
    		return false;
        }
        return true;
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
	
	/*
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
	*/

	private static class SorbaScraperRow implements IScraperRow{
		public final String organizer = "SORBA Atlanta";
		public String title;
		public String description;
		public String url;
		public String location;
		public LocalDate startDate;
		public LocalDate endDate;
		public LocalTime startTime;
		public LocalTime endTime;
		public String cost = "IMBA membership (https://win.imba.com/join?chapter=SORBA%20Atlanta)";
		//public String rsvpInfo; // n/a
		public SorbaScraperRow(){}
	}
}
