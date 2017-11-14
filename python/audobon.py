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
reg = re.compile('events: (\[.+?)}\);', re.MULTILINE|re.DOTALL);
rawjs = reg.findall(raw)
idreg = re.compile('id:\'(.+?)\', title:\'(.+?)\', start:\'(.+?)\'', re.MULTILINE);
ids = idreg.findall(rawjs[0])

soup = BeautifulSoup(raw, 'html.parser')

with open("audobon-events.csv", "wb") as f:
	writer = UnicodeWriter(f,quoting=csv.QUOTE_ALL)
	writer.writerow(['Event ID', 'Event Title', 'Event Start', 'Event URL', 'Event Location', 'Event Description']);

	for id in ids:
		div_id = "event_desc_" + id[0]
		desc = soup.find(id=div_id)
		links = desc.find_all('a');
		if len(links) > 1:
			location = links[1]
		else:
			location = ""
		desc = desc.find("div", class_="calendar-event-text")
		desc = desc.get_text()
		writer.writerow([id[0], id[1], id[2], url, location, desc])
f.close()