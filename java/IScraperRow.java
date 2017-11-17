import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

/*
This is the interface that each ScraperRow class must implement

Compile:
Mac:
javac -d bin -cp bin/joda-time-2.9.9.jar IScraperRow.java 
Windows:
javac -d bin -cp bin\joda-time-2.9.9.jar IScraperRow.java 

*/


public interface IScraperRow {

		public final String organizer = "";
		public String title = "";
		public String description = "";
		public String url = "";
		public String location = "";
		public LocalDate startDate = null;
		public LocalDate endDate = null;
		public LocalTime startTime = null;
		public LocalTime endTime = null;
		public String cost = "";
}