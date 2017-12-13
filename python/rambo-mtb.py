import urllib2
import csv,codecs,cStringIO
import re
import json
import time

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

filename = "rambo-events.csv"
months = ['October-2017', 'November-2017', 'December-2017']

with open(filename, "wb") as f:
	writer = UnicodeWriter(f,quoting=csv.QUOTE_ALL)
	#writer.writerow(['Event ID', 'Event Title', 'Event Start', 'Event End', 'Event URL', 'Event Location'])
	writer.writerow(['Organization/organizer', 'Title', 'Description (optional)', 'URL of event', 'Location', 'Category', 
		'Start Date', 'End Date', 'Start Time', 'End Time','Cost','RSVP info','Age group','Dog-friendly','Indoor or outdoor','Imported to Google Calendar']) 
	
	organization = "Roswell Alpharetta Mountain Bike Organization (RAMBO)"
	
	for month in months:
		url = "http://www.rambo-mtb.org/api/open/GetItemsByMonth?month=" + month + "&collectionId=53bdf2b4e4b0dd89ea2837a0"

		page = urllib2.urlopen(url)
		raw = page.read()

		data = json.loads(raw)

		for event in data:
			title = event['title']
			description = ""
			eventUrl = "http://www.rambo-mtb.org" + event['fullUrl']
			location = str(event['location']['mapLat']) + ',' + str(event['location']['mapLng'])
			category = ""
			start = time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(float(event['startDate']) / 1000))
			end = time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(float(event['endDate']) / 1000))
			startDate = start.split()[0]
			startTime = start.split()[1]
			endDate = end.split()[0]
			endTime = end.split()[1]
			cost = ""
			rsvp = ""
			ageGroup = ""
			dogFriendly = ""
			indoorOutdoor = ""
			imported = "No"
			#writer.writerow([event['id'], event['title'], start, end, eventUrl, location])
			writer.writerow([organization, title, description, eventUrl, location, category, startDate, 
				endDate, startTime, endTime, cost, rsvp, ageGroup, dogFriendly, indoorOutdoor, imported])
f.close()

print('Wrote output to ' + filename)