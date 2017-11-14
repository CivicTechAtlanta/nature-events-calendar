/**
 * This file scrapes next month's events
 * For GA State Parks within 50 miles of downtown Atlanta
 */

var axios = require('axios');
var fs = require('fs');
var json2csv = require('json2csv');
var moment = require('moment');

var today = new Date;
var firstDate = moment.utc(today).add(1, 'months').startOf('month').format('MM/DD/YYYY');
var lastDate = moment.utc(today).add(2, 'months').startOf('month').format('MM/DD/YYYY');
var url = `http://explore.gastateparks.org/core/event/fullcalendarfeed.aspx?admin=1&json=1&sd=${firstDate}&ed=${lastDate}+11:59+PM&zip=30303&distance=50&forcal=1`;

axios.get(url)
  .then(resp => {
    processData(resp.data);
  });

function processData(data) {
  var parsedData = [];

  data.forEach(event => {
    var eventData = {};
    eventData['Organizer'] = 'Georgia State Parks';
    eventData['Title'] = event.title;
    eventData['Description'] = event.description;
    eventData['URL'] = `http://explore.gastateparks.org${event.url}`;
    eventData['Location'] = event.location;
    "1/1/2018 5:30:00 PM"
    eventData['Start Date'] = moment(event.start, 'M/D/YYYY H:mm:ss A').format("YYYY-MM-DD");
    eventData['Start Time'] = moment(event.start, 'M/D/YYYY H:mm:ss A').format("HH:mm");
    eventData['End Date'] = moment(event.end, 'M/D/YYYY H:mm:ss A').format("YYYY-MM-DD");
    eventData['End Time'] = moment(event.end, 'M/D/YYYY H:mm:ss A').format("HH:mm");
    parsedData.push(eventData);
  });

  var fields = ['Organizer', 'Title', 'Description', 'URL', 'Location', 'Category', 'Start Date', 'End Date', 'Start Time', 'End Time', 'Free or Paid', 'RSVP Info', 'Age Group', 'Dog Friendly', 'Indoor or Outdoor'];

  var csv = json2csv({ data: parsedData, fields: fields });

  fs.writeFile('state-parks.csv', csv, (err) => {
    if (err) throw err;
    console.log('file saved');
  });
}
