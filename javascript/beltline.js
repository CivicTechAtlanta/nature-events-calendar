/**
 * This file scrapes next month's events
 * For Atlanta BeltLine
 */

var axios = require('axios');
var moment = require('moment');
//var fs = require('fs');
//var json2csv = require('json2csv');

var today = new Date;
//console.log('today:' + today);
var firstDate = moment.utc(today).add(1, 'months').startOf('month').format('YYYYMMDD');
//console.log('firstDate: ' + firstDate);
var lastDate = moment.utc(today).add(2, 'months').startOf('month').format('YYYYMMDD');
//console.log('lastDate: ' + lastDate);
var url = `https://atlantabeltline.checkfront.com/reserve/?#D${firstDate}`;
//console.log('url: ' + url);

axios.get(url)
  .then(resp => {
    processData(resp.data);
  });

function processData(data) {
	console.log('data: ' + data); 
	//data shows html for a "Checking availability..."" page
	//ie, some redirect page. TODO: Come back to this and make it fixed.
	
//  var parsedData = [];
  //data.forEach(event => {
  //});
/*  
  var fields = ['Organizer', 'Title', 'Description', 'URL', 'Location', 'Category', 'Start Date', 'End Date', 'Start Time', 'End Time', 'Free or Paid', 'RSVP Info', 'Age Group', 'Dog Friendly', 'Indoor or Outdoor'];

  var csv = json2csv({ data: parsedData, fields: fields });

  fs.writeFile('beltline.csv', csv, (err) => {
    if (err) throw err;
    console.log('file saved');
  });
*/
}
