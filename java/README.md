# Atlanta Nature Events Scraper - Java

These java programs scrape the websites for Atlanta nature events data.

Each program outputs to a csv file with a header line.

Scrapers available in Java are:
* `AsfScraper`
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
