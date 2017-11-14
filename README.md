# Atlanta Nature Events Calendar

## How to suggest that an organization's events be added to the calendar

1. Open [the spreadsheet](https://docs.google.com/spreadsheets/d/18mFHR6cExJ0B0xD59A5TYfv5R6BYsO7UXLIeneWBCa8/edit#gid=0).
1. Look at the 'websites' sheet.
1. Check that the URL is not already listed.
1. Add the URL to the 'URL' column of the 'websites' sheet.


## How to "claim" a site (i.e. plan to scrape it)

1. Open [the spreadsheet](https://docs.google.com/spreadsheets/d/18mFHR6cExJ0B0xD59A5TYfv5R6BYsO7UXLIeneWBCa8/edit#gid=0).
1. In the 'websites' sheet, put your name next to the site you would like to write a scraper for, in the 'Claimed by (scraping)' column.
1. If you finish the scraper, replace your name with a link to the scraper.
1. If you no longer plan to finish writing the scraper for a site, please remove your name from the column.
1. Please just claim one site at a time.

## How to write scrapers

Feel free to use any language of your choice, as long as you include instructions on how to run it.

The scraper should output a CSV file.

That CSV should have the following columns (required):
* `Organizer`: This is the organization or group putting on the event. Generally this will be the same for all events from a site. Example: "Atlanta Botanical Garden"
* `Title`: This should be a fairly short title of the event. Example: "Halloween Archery & Ghost Stories"
* `Description`: This is freeform. Feel free to include any info in here that you think will be useful that isn't covered in one of the other columns.
* `URL`: This will ideally be a URL to this particular event, but if that's not available, the URL of the site's events calendar or list is fine. Example: `http://www.rambo-mtb.org/events/2017/10/7/rambo-fall-family-festival-and-take-a-kid-mtb-day`
* `Location`: Ideally, this will be a location that is both human-readable and interpretable by Google geocoding. It can be an address or the name of a location. Coordinates will also work, but are generally not preferred. Examples: `Don Carter State Park`; `50 Lodge Rd SE, Acworth, GA 30102`
* `Start Date`: The date the event starts, preferably in 'YYYY-MM-DD' format. Example: `2017-11-13`

The following columns are optional, but please include them where relevant:
* `End Date`: Assumed to be the same as `Start Date` unless included. Preferably in 'YYYY-MM-DD' format.
* `Start Time`: The time the event starts. If not included, the event is assumed to be a full-day event. Preferably in 'HH:mm' (24-hour clock) time. Example: `17:30`
* `End Time`: The time the event ends. If not included, the event is assumed to last one hour. Preferably in 'HH:mm' (24-hour clock) time. Example: `18:30`

The following columns are fully optional:
* `Category`: This is basically a placeholder for now, but if the events have categories clearly marked, it will be nice to include them so we can come up with a catgorization strategy later. Examples: `Hike`; `Arts & Crafts`; `Class`; `Volunteer`
* `Free or Paid`: The value of this should be `Free` if the event is free. If it's a paid event, information about the price can be included. Examples: `Free`; `$20 per campsite`; `$65 daily`; `$50`
* `RSVP Info`: This is freeform text about how to RSVP. Examples: `http://treesatlanta.doubleknot.com/registration/calendardetail.aspx?ActivityKey=2186202&OrgKey=3594`; `No RSVP required`; `Email southriverwatershedalliance@gmail.com`
* `Age Group`: This is freeform text. Examples: `Family`; `9 - 13`; `16+`
* `Dog Friendly`: This is freeform text.
* `Indoor or Outdoor`: This can be `Indoor`, `Outdoor`, `Both`, or empty.

If it makes things easier, only scrape the next two months of data. If it doesn't require any extra queries or work, go ahead and scrape all upcoming events.

Please document how to run the scraper (this can just be a few lines of comments in the scraper file itself).

## Existing Scrapers

| Site being scraped | Scraper | Status |
| --- | --- | --- |
| [Blue Heron Nature Preserve](https://bhnp.org/calendar) | [/javascript/bhnp.js](https://github.com/codeforatlanta/nature-events-calendar/blob/master/javascript/bhnp.js) | ![Working](https://img.shields.io/badge/scraper-working-brightgreen.svg) |
| [Georgia State Parks and Historic Sites](http://explore.gastateparks.org/events) | [/javascript/state-parks.js](https://github.com/codeforatlanta/nature-events-calendar/blob/master/javascript/state-parks.js) | ![Working](https://img.shields.io/badge/scraper-working-brightgreen.svg) |
| Placeholder | | ![Needs Standardization](https://img.shields.io/badge/scraper-needs%20standardization-yellow.svg) |
| Placeholder | | ![Broken](https://img.shields.io/badge/scraper-broken-red.svg) |
| Placeholder | | ![Incomplete](https://img.shields.io/badge/scraper-incomplete-lightgrey.svg) |
| [Atlanta Audubon Society](https://www.atlantaaudubon.org/field-trips) | [Gist](https://gist.github.com/saussy/54cbfda181a4318ad3a237f4350d444b) | ![Not in Repo](https://img.shields.io/badge/scraper-not%20in%20repo-orange.svg) |
| [RAMBO](http://www.rambo-mtb.org/events/?view=calendar&month=September-2017) | [Gist](https://gist.github.com/saussy/b314991f9797aa7df91f27c81613b756) | ![Not in Repo](https://img.shields.io/badge/scraper-not%20in%20repo-orange.svg) |
| [Atlanta Botanical Garden](http://atlantabg.org/calendar) | [Gist](https://gist.github.com/bollwyvl/4e2d9d601c4beb06f61252fc0617880e) | ![Not in Repo](https://img.shields.io/badge/scraper-not%20in%20repo-orange.svg) |
| [City of Sandy Springs](https://www.visitsandysprings.org/events/?categories%5B0%5D=25) | [Gist](https://gist.github.com/saussy/a997afc72583e644a13489c8a8f46835) | ![Not in Repo](https://img.shields.io/badge/scraper-not%20in%20repo-orange.svg) |
