import urllib2
import csv,codecs,cStringIO
import re
from bs4 import BeautifulSoup

class UnicodeWriter:
	def __init__(self, f, dialect=csv.excel, encoding="utf-8-sig", **kwds):
		self.queue = cStringIO.StringIO()
		self.writer = csv.writer(self.queue, dialect=dialect, **kwds)
		self.stream = f
		self.encoder = codecs.getincrementalencoder(encoding)()
	def writerow(self, row):
		'''writerow(unicode) -> None
		This function takes a Unicode string and encodes it to the output.
		'''
		self.writer.writerow([s.encode("utf-8") for s in row])
		data = self.queue.getvalue()
		data = data.decode("utf-8")
		data = self.encoder.encode(data)
		self.stream.write(data)
		self.queue.truncate(0)

url = "https://www.atlantaaudubon.org/field-trips"

page = urllib2.urlopen(url)
raw = page.read()
reg = re.compile('events: (\[.+?)}\);', re.MULTILINE|re.DOTALL) 
rawjs = reg.findall(raw)
idreg = re.compile('id:\'(.+?)\', title:\'(.+?)\', start:\'(.+?)\'', re.MULTILINE) 
ids = idreg.findall(rawjs[0])
filename = "audobon-events.csv"

soup = BeautifulSoup(raw, 'html.parser')

with open(filename, "wb") as f:
	writer = UnicodeWriter(f,quoting=csv.QUOTE_ALL)
	writer.writerow(['Organization/organizer', 'Title', 'Description (optional)', 'URL of event', 'Location', 'Category', 
		'Start Date', 'End Date', 'Start Time', 'End Time','Cost','RSVP info','Age group','Dog-friendly','Indoor or outdoor','Imported to Google Calendar']) 
	organization = "Atlanta Audubon Society"
	for id in ids:
		div_id = "event_desc_" + id[0]
		title = id[1]
		desc = soup.find(id=div_id)
		links = desc.find_all('a') 
		if len(links) > 1:
			location = links[1]
		else:
			location = ""
		category = ""
		desc = desc.find("div", class_="calendar-event-text")
		desc = desc.get_text()
		start = id[2]
		startDate = start.split()[0]
		startTime = start.split()[1]
		endDate = ""
		endTime = ""
		cost = ""
		rsvp = ""
		ageGroup = ""
		dogFriendly = ""
		indoorOutdoor = ""
		imported = "No"
		writer.writerow([organization, title, desc, url, location, category, startDate, endDate, 
			startTime, endTime, cost, rsvp, ageGroup, dogFriendly, indoorOutdoor, imported])
f.close()

print('Wrote output to ' + filename)
























