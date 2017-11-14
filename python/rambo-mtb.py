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

months = ['October-2017', 'November-2017', 'December-2017'];
with open("rambo-events.csv", "wb") as f:
	writer = UnicodeWriter(f,quoting=csv.QUOTE_ALL)
	writer.writerow(['Event ID', 'Event Title', 'Event Start', 'Event End', 'Event URL', 'Event Location']);
	for month in months:
		url = "http://www.rambo-mtb.org/api/open/GetItemsByMonth?month=" + month + "&collectionId=53bdf2b4e4b0dd89ea2837a0";

		page = urllib2.urlopen(url)
		raw = page.read()

		data = json.loads(raw);

		for event in data:
			eventUrl = "http://www.rambo-mtb.org" + event['fullUrl']
			start = time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(float(event['startDate']) / 1000))
			end = time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(float(event['endDate']) / 1000))
			location = str(event['location']['mapLat']) + ',' + str(event['location']['mapLng'])
			writer.writerow([event['id'], event['title'], start, end, eventUrl, location])
f.close()