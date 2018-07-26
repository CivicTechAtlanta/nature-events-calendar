# Atlanta Nature Events Scraper - Java

These java programs scrape the websites for Atlanta nature events data.

Each program outputs to a csv file with a header line.

Lots of debugging statements have been commented out. 
Feel free to uncomment them in your clone if you want to troubleshoot something or just know what it's doing.

Any questions? Contact the author here:
dstrube@gmail.com

Scrapers available in Java are:
* `ReiScraper`
* `SorbaScraper`


## Compile
Mac
* `javac -cp bin/joda-time-2.9.9.jar -d bin IScraperRow.java`
* `javac -cp bin:bin/joda-time-2.9.9.jar:bin/jsoup-1.11.1.jar -d bin [Scraper].java`

Windows [unverified]
* `javac -cp bin\joda-time-2.9.9.jar -d bin IScraperRow.java`
* `javac -cp bin;bin\joda-time-2.9.9.jar;bin\jsoup-1.11.1.jar; -d bin [Scraper].java`

## Run
Mac
* `java -cp bin:bin/joda-time-2.9.9.jar:bin/jsoup-1.11.1.jar [Scraper]`

Windows [unverified]
* `java -cp bin;bin\joda-time-2.9.9.jar;bin\jsoup-1.11.1.jar; [Scraper]`
