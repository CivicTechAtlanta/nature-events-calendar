# -*- coding: utf-8 -*-
import urllib2
import csv,codecs,cStringIO
import re
from bs4 import BeautifulSoup
from pprint import pprint as pp

def wildapricot():
    url = 'https://uxatl.wildapricot.org/tours'
    page = urllib2.urlopen(url)
    raw = page.read()
    soup = BeautifulSoup(raw, 'html.parser')
    event_list = soup.select('li[id*=idUpcomingEvent]')
    event_detail_url_list = []  # a list of urls from which can be scraped event details
    for event in event_list:
        event_detail_url_list.append(event.find('a', {'class': 'footerEventDetailsLink'})['href'])

    organization = "Urban Explorers of Atlanta"

    events = []
    for _url in event_detail_url_list:
        _temp = {}
        page = urllib2.urlopen(_url)
        raw = page.read()
        soup = BeautifulSoup(raw, 'html.parser')

        _temp['Organizer'] = organization
        _temp['Title'] = soup.find('h1').text.strip()
        _temp['URL'] = _url
        _temp['Location'] = soup.find('li', 'eventInfoLocation').text.replace('Location', '').strip()
        _temp['Start Date'] = soup.find('li', {'class': 'eventInfoStartDate'}).text.replace('When', '').strip()
        time_span = soup.find('li', {'class': 'eventInfoStartTime'}).text.strip().split('-')
        _temp['Start Time'] = time_span[0].strip()
        try:
            _temp['End Time'] = time_span[1].strip()
        except IndexError:
            _temp['End Time'] = ''
        cost_info = soup.find('div', {'class': 'registrationInfoContainer'}).find_all('strong')
        cost_out = ''
        for cost in cost_info:
            if '$' in cost.text:
                base = cost.text.strip().split('$')
                the_type = base[0].split(' ')[0]
                the_cost = base[1]
                foo = the_type.strip() + ': $' + the_cost.strip()
                cost_out += foo + '  '

        _temp['Free or Paid'] = cost_out.strip()

        events.append(_temp)

    with open('urban_explorers_of_atlanta.csv', 'w') as csvfile:
        fieldnames = ['Title', 'Start Time', 'End Time', 'Free or Paid', 'Location', 'Organizer', 'Start Date', 'URL']
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)

        writer.writeheader()
        for event in events:
            writer.writerow(event)

    # f = open('urban_explorers_of_atlanta.csv', 'w')

wildapricot()
