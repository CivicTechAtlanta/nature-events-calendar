const _ = require('lodash');
const fs = require('fs');
const moment = require('moment');
const parse = require('csv-parse');
const transform = require('stream-transform');

// TODO: atlanta botanical garden (not in repo)

/**
 * Keys are improper column names, values are proper column names
 */
const columnNameMap = {
  'Organization/organizer': 'Organizer',
  'Description (optional)': 'Description',
  'URL of event': 'URL',
  'Category (e.g. hiking, birding, volunteering, class': 'Category',
  'End Date (if multi-day)': 'End Date',
  'Free or paid?': 'Free or Paid',
  'RSVP info': 'RSVP Info',
  'Age group (if specified)': 'Age Group',
  'Dog-friendly (if specified)': 'Dog Friendly',
  'Indoor or outdoor?': 'Indoor or Outdoor'
}

/**
 * Find the column names used in the data and replace them with the preferred ones
 *
 * @param {*} data
 */
function cleanColumnNames(data) {
  let keys = _.keys(data[0]);
  _.forEach(keys, key => {
    let newColumnName = columnNameMap[key];
    if (newColumnName) {
      _.forEach(data, row => {
        row[newColumnName] = row[key];
        delete row[key];
      });
    }
  });

  return data;
}

function computeSubject(row) {
  if (row['Organizer']) {
    if (row['Title']) {
      return (row['Organizer']) + ' - ' + row['Title'];
    } else {
      return row['Organizer'];
    }
  } else if (row['Title']) {
    return row['Title'];
  }
}

function computeEndDate(row, startDate) {
  let endDate = row['End Date'];
  if (!endDate) {
    return startDate;
  }
  return moment(endDate, 'YYYY-MM-DD').format('MM/DD/YYYY');
}

function computeAllDayEvent(startTime, endTime) {
  return (startTime || endTime) ? false : true;
}

function computeDescription(row) {
  let description = '';
  if (row['Description']) {
    description += row['Description'];
  }
  if (row['Free or Paid']) {
    description += '\n\nCost: ';
    description += row['Free or Paid'];
  }
  if (row['Dog Friendly']) {
    description += '\n\nDog friendly?: ';
    description += row['Dog Friendly'];
  }
  if (row['RSVP Info']) {
    description += '\n\nHow to RSVP: ';
    description += row['RSVP Info'];
  }
  if (row['URL']) {
    description += '\n\n';
    description += row['URL'];
  }
}

function parseData(data) {
  data = cleanColumnNames(data);
  _.forEach(data, row => {
    let reformattedRow = {};
    reformattedRow['Subject'] = computeSubject(row);
    reformattedRow['Start Date'] = moment(row['Start Date'], 'YYYY-MM-DD').format('MM/DD/YYYY');
    reformattedRow['Start Time'] = moment(row['Start Time'], 'HH:mm').format('h:mm A');
    reformattedRow['End Date'] = computeEndDate(row, reformattedRow['Start Date']);
    reformattedRow['End Time'] = moment(row['End Time'], 'HH:mm').format('h:mm A');
    reformattedRow['All Day Event'] = computeAllDayEvent(reformattedRow['Start Time'], reformattedRow['End Time']);
    reformattedRow['Location'] = row['Location']; // FIXME: why is this not working??
    console.log(row);
    console.log(reformattedRow);
    console.log('---');
  });
}

var parser = parse({
  columns: true,
  delimiter: ','
}, function (err, data) {
  parseData(data);
});

fs.createReadStream('./java/rei.csv').pipe(parser);

