cd java
java -cp bin:bin/joda-time-2.9.9.jar:bin/jsoup-1.11.1.jar ReiScraper
java -cp bin:bin/joda-time-2.9.9.jar:bin/jsoup-1.11.1.jar SorbaScraper
cd ../
node javascript/bhnp.js
echo "BHNP done"
node javascript/state-parks.js
echo "State parks done"
python python/audobon.py
echo "Audubon done"
python python/rambo-mtb.py
echo "RAMBO done"
python python/sandysprings.py
echo "Sandy Springs done"
# TODO: node javascript/combine-calendars.js
