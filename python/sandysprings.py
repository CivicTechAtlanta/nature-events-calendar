import urllib2
import csv,codecs,cStringIO
import re
import json
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

url = "https://www.visitsandysprings.org/includes/rest/plugins_events_events/find/?filter%5B%24or%5D%5B0%5D%5Bcategories.catId%5D=25&filter%5Bdates%5D%5B%24elemMatch%5D%5BeventDate%5D%5B%24gte%5D=2017-10-09T04%3A00%3A00.000Z&filter%5Bdates%5D%5B%24elemMatch%5D%5BeventDate%5D%5B%24lte%5D=2017-11-10T04%3A59%3A59.000Z&options%5Bskip%5D=0&options%5Blimit%5D=50&options%5Bhooks%5D%5B%5D=afterFind_listing&options%5Bhooks%5D%5B%5D=afterFind_host&options%5Bsort%5D%5BnextDate%5D=1&options%5Bsort%5D%5BstartDate%5D=1&options%5Bsort%5D%5Btitle%5D=1&options%5Bfields%5D%5Bcategories%5D=1&options%5Bfields%5D%5BendDate%5D=1&options%5Bfields%5D%5Bhost_id%5D=1&options%5Bfields%5D%5Bhost.recid%5D=1&options%5Bfields%5D%5Bhost.title%5D=1&options%5Bfields%5D%5Bhost.detailURL%5D=1&options%5Bfields%5D%5Blatitude%5D=1&options%5Bfields%5D%5Blisting_id%5D=1&options%5Bfields%5D%5Blisting.recid%5D=1&options%5Bfields%5D%5Blisting.title%5D=1&options%5Bfields%5D%5Blisting.detailURL%5D=1&options%5Bfields%5D%5Blocation%5D=1&options%5Bfields%5D%5Blongitude%5D=1&options%5Bfields%5D%5Bmedia_raw%5D=1&options%5Bfields%5D%5BnextDate%5D=1&options%5Bfields%5D%5Brank%5D=1&options%5Bfields%5D%5BrecId%5D=1&options%5Bfields%5D%5Brecid%5D=1&options%5Bfields%5D%5BrecurType%5D=1&options%5Bfields%5D%5Brecurrence%5D=1&options%5Bfields%5D%5BstartDate%5D=1&options%5Bfields%5D%5Btitle%5D=1&options%5Bfields%5D%5BtypeName%5D=1&options%5Bfields%5D%5Bloc%5D=1&options%5Bfields%5D%5Burl%5D=1&options%5Bfields%5D%5Bdate%5D=1&token=3a1208787a735b4d1634a2c60f22d044"

page = urllib2.urlopen(url)
raw = page.read()

data = json.loads(raw);
with open("sandysprings-events.csv", "wb") as f:
	writer = UnicodeWriter(f,quoting=csv.QUOTE_ALL)
	writer.writerow(['Event ID', 'Event Title', 'Event Start', 'Event End', 'Event URL', 'Event Location', 'Event Description']);

	for event in data['docs']:
		try:
			location = event['location']
		except KeyError:
			location = event['listing']['title']
		eventUrl = "https://www.visitsandysprings.org" + event['url']
		eventPage = urllib2.urlopen(eventUrl);
		eventPageRaw = eventPage.read();
		soup = BeautifulSoup(eventPageRaw, 'html.parser')
		description = soup.find(attrs={"name": "description"})
		writer.writerow([event['_id'], event['title'], event['startDate'], event['endDate'], eventUrl, location, description['content']])
f.close()