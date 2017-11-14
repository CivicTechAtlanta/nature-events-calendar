/**
 * This file scrapes next month's events
 * For Blue Heron Nature Preserve
 */

var axios = require('axios');
var fs = require('fs');
var json2csv = require('json2csv');
var moment = require('moment');

var today = new Date;
var nextMonth = moment.utc(today).add(1, 'months').format('MMMM-YYYY');
var url = `https://bhnp.org/api/open/GetItemsByMonth?month=${nextMonth}&collectionId=58d022815016e1f1078e89b1&crumb=BUVD9clJILLQZjNkYTc1NjAxMmU3ZDk2NzlmNWI5NmZlZmIyNmVj`;

axios.get(url)
  .then(resp => {
    processData(resp.data);
  });

function processData(data) {
  var parsedData = [];

  data.forEach(event => {
    var eventData = {};
    eventData['Organizer'] = 'Blue Heron Nature Preserve';
    eventData['Title'] = event.title;
    eventData['Location'] = `${event.location.markerLat}, ${event.location.markerLng}`;
    eventData['Start Date'] = moment(event.startDate, 'x').format("YYYY-MM-DD");
    eventData['Start Time'] = moment(event.startDate, 'x').format("HH:mm");
    eventData['End Date'] = moment(event.endDate, 'x').format("YYYY-MM-DD");
    eventData['End Time'] = moment(event.endDate, 'x').format("HH:mm");
    eventData['URL'] = `https://bhnp.org${event.fullUrl}`;

    parsedData.push(eventData);
  });

  var fields = ['Organizer', 'Title', 'Description', 'URL', 'Location', 'Category', 'Start Date', 'End Date', 'Start Time', 'End Time', 'Free or Paid', 'RSVP Info', 'Age Group', 'Dog Friendly', 'Indoor or Outdoor'];


  var csv = json2csv({ data: parsedData, fields: fields });

  fs.writeFile('bhnp.csv', csv, (err) => {
    if (err) throw err;
    console.log('file saved');
  });
}
